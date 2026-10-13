package pe.edu.utp.eventope.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.edu.utp.eventope.config.ConexionBD;
import pe.edu.utp.eventope.config.Configuracion;
import pe.edu.utp.eventope.config.ProveedorConexion;
import pe.edu.utp.eventope.dto.ResultadoAccesoDTO;
import pe.edu.utp.eventope.dto.SesionUsuario;
import pe.edu.utp.eventope.service.ServicioAcceso;
import pe.edu.utp.eventope.util.CifradoSecretos;
import pe.edu.utp.eventope.util.Csrf;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Clock;
import java.util.Locale;
import java.util.UUID;

@WebServlet("/seguridad/validar")
public final class ValidarAccesoServlet extends HttpServlet {
    private ServicioAcceso servicio;
    private Configuracion config;

    @Override
    public void init() throws ServletException {
        try {
            config = Configuracion.cargar();
            ProveedorConexion db = new ConexionBD(config);
            CifradoSecretos cifrado = new CifradoSecretos(config.requerido("totp.masterKey"));
            servicio = new ServicioAcceso(db, cifrado, Clock.systemUTC());
        } catch (RuntimeException e) {
            throw new ServletException("Error al configurar ServicioAcceso", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        resp.setContentType("application/json; charset=UTF-8");

        if (!Csrf.valido(req.getSession(false), req.getParameter("csrf"))) {
            responderError(resp, HttpServletResponse.SC_FORBIDDEN, "Token de seguridad inválido o sesión expirada.");
            return;
        }

        Object o = req.getSession().getAttribute("sesionUsuario");
        if (!(o instanceof SesionUsuario actor) || actor.getTipo() != SesionUsuario.Tipo.PERSONAL || actor.getRol() != SesionUsuario.Rol.VALIDACION) {
            responderError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Acceso exclusivo para personal de validación.");
            return;
        }

        int dispositivoId;
        try {
            dispositivoId = Integer.parseInt(req.getParameter("dispositivoId"));
            if (dispositivoId <= 0) throw new NumberFormatException();
        } catch (Exception e) {
            responderError(resp, HttpServletResponse.SC_BAD_REQUEST, "Identificador de dispositivo no válido.");
            return;
        }

        String solicitudId = req.getParameter("solicitudId");
        if (solicitudId == null || solicitudId.isBlank()) {
            responderError(resp, HttpServletResponse.SC_BAD_REQUEST, "Falta el identificador de solicitud.");
            return;
        }

        String contenido = req.getParameter("contenido");
        String dniPresentado = req.getParameter("dni");
        if (dniPresentado != null) dniPresentado = dniPresentado.trim();

        // Resolución del token del dispositivo registrado en el servidor
        String tokenDispositivo = config.opcional("seguridad.dispositivoToken." + dispositivoId)
                .orElse(null);
        if (tokenDispositivo == null) {
            responderError(resp, 503, "El dispositivo necesita su credencial en la configuración del servidor.");
            return;
        }

        try {
            ResultadoAccesoDTO res = servicio.validar(actor, dispositivoId, tokenDispositivo, solicitudId, contenido, dniPresentado);
            String siguienteSolicitudId = UUID.randomUUID().toString();
            String json = String.format(Locale.ROOT,
                    "{\"ok\":true,\"permitido\":%b,\"motivo\":\"%s\",\"recuperado\":%b,\"controlId\":%d,\"siguienteSolicitudId\":\"%s\"}",
                    res.isPermitido(),
                    escaparJson(res.getMotivo()),
                    res.isRecuperado(),
                    res.getControlId(),
                    siguienteSolicitudId);
            resp.getWriter().write(json);
        } catch (SecurityException e) {
            responderError(resp, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (IllegalArgumentException e) {
            responderError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            responderError(resp, HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Error temporal al procesar la validación.");
        }
    }

    private static void responderError(HttpServletResponse resp, int status, String mensaje) throws IOException {
        resp.setStatus(status);
        resp.getWriter().write(String.format("{\"ok\":false,\"error\":\"%s\"}", escaparJson(mensaje)));
    }

    private static String escaparJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}
