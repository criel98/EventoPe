package pe.edu.utp.eventope.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.edu.utp.eventope.config.ConexionBD;
import pe.edu.utp.eventope.config.Configuracion;
import pe.edu.utp.eventope.config.ProveedorConexion;
import pe.edu.utp.eventope.dto.QrRespuestaDTO;
import pe.edu.utp.eventope.dto.SesionUsuario;
import pe.edu.utp.eventope.service.ServicioQr;
import pe.edu.utp.eventope.util.CifradoSecretos;
import pe.edu.utp.eventope.util.Csrf;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Clock;
import java.time.Duration;
import java.util.Locale;

@WebServlet("/entradas/qr")
public final class QrServlet extends HttpServlet {
    private ServicioQr servicio;

    @Override
    public void init() throws ServletException {
        try {
            var config = Configuracion.cargar();
            ProveedorConexion db = new ConexionBD(config);
            CifradoSecretos cifrado = new CifradoSecretos(config.requerido("totp.masterKey"));
            servicio = new ServicioQr(db, cifrado, Clock.systemUTC());
        } catch (RuntimeException e) {
            throw new ServletException("Error al configurar ServicioQr", e);
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
        if (!(o instanceof SesionUsuario actor) || actor.getTipo() != SesionUsuario.Tipo.CLIENTE) {
            responderError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Inicia sesión como cliente para ver tu código.");
            return;
        }

        String paramId = req.getParameter("entradaId");
        int entradaId;
        try {
            entradaId = Integer.parseInt(paramId);
            if (entradaId <= 0) throw new NumberFormatException();
        } catch (Exception e) {
            responderError(resp, HttpServletResponse.SC_BAD_REQUEST, "Identificador de entrada inválido.");
            return;
        }

        try {
            QrRespuestaDTO r = servicio.obtener(actor, entradaId);
            long segundos = Math.max(0, Duration.between(r.getHoraServidor(), r.getExpiraEn()).toSeconds());
            String json = String.format(Locale.ROOT,
                    "{\"ok\":true,\"contenido\":\"%s\",\"horaServidor\":\"%s\",\"expiraEn\":\"%s\",\"segundosRestantes\":%d}",
                    escaparJson(r.getContenido()),
                    r.getHoraServidor().toString(),
                    r.getExpiraEn().toString(),
                    segundos);
            resp.getWriter().write(json);
        } catch (SecurityException e) {
            responderError(resp, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (IllegalStateException e) {
            responderError(resp, HttpServletResponse.SC_CONFLICT, e.getMessage());
        } catch (SQLException e) {
            responderError(resp, HttpServletResponse.SC_SERVICE_UNAVAILABLE, "El servicio de validación no está disponible temporalmente.");
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
