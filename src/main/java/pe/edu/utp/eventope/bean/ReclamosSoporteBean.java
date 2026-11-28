package pe.edu.utp.eventope.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;
import pe.edu.utp.eventope.dto.SesionUsuario;
import pe.edu.utp.eventope.model.ReclamoSoporte;
import pe.edu.utp.eventope.service.ServicioSoporte;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("reclamosSoporteBean")
@ViewScoped
public class ReclamosSoporteBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private transient ServicioSoporte servicioSoporte = new ServicioSoporte();

    private List<ReclamoSoporte> reclamos = new ArrayList<>();
    private Integer incidenciaId;
    private String asunto;
    private String descripcion;

    private Integer reclamoSeleccionadoId;
    private String respuestaTexto;
    private String nuevoEstado = "RESUELTO";

    private boolean esStaffSoporte;
    private boolean esCliente;
    private String errorAcceso;

    @PostConstruct
    public void init() {
        cargarContextoYReclamos();
    }

    public void cargarContextoYReclamos() {
        reclamos = new ArrayList<>();
        errorAcceso = null;
        esStaffSoporte = false;
        esCliente = false;

        SesionUsuario actor = obtenerSesionActual();
        if (actor == null) {
            errorAcceso = "Debes iniciar sesión para acceder al módulo de soporte.";
            return;
        }

        try {
            if (actor.getTipo() == SesionUsuario.Tipo.PERSONAL
                    && (actor.getRol() == SesionUsuario.Rol.SOPORTE || actor.getRol() == SesionUsuario.Rol.ADMIN)) {
                esStaffSoporte = true;
                reclamos = servicio().listarReclamosGestion(actor);
            } else if (actor.getTipo() == SesionUsuario.Tipo.CLIENTE && actor.getRol() == SesionUsuario.Rol.CLIENTE) {
                esCliente = true;
                reclamos = servicio().listarReclamosCliente(actor);
            } else {
                errorAcceso = "Tu rol actual no tiene permisos para gestionar reclamos de soporte.";
            }
        } catch (SecurityException e) {
            errorAcceso = e.getMessage();
        } catch (IllegalStateException e) {
            errorAcceso = "No se pudieron cargar los reclamos: " + e.getMessage();
            agregarMensaje(FacesMessage.SEVERITY_ERROR, e.getMessage());
        }
    }

    public void crearReclamo() {
        SesionUsuario actor = obtenerSesionActual();
        if (actor == null) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Sesión no iniciada.");
            return;
        }
        try {
            servicio().registrarReclamo(actor, incidenciaId, asunto, descripcion);
            incidenciaId = null;
            asunto = null;
            descripcion = null;
            agregarMensaje(FacesMessage.SEVERITY_INFO, "Tu reclamo ha sido registrado correctamente.");
            cargarContextoYReclamos();
        } catch (IllegalArgumentException | SecurityException | IllegalStateException e) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, e.getMessage());
        }
    }

    public void prepararRespuesta(ReclamoSoporte r) {
        if (r == null) {
            return;
        }
        this.reclamoSeleccionadoId = r.getId();
        this.respuestaTexto = r.getRespuesta();
        this.nuevoEstado = r.getEstadoReclamo() != null ? r.getEstadoReclamo() : "RESUELTO";
    }

    public void guardarRespuesta() {
        SesionUsuario actor = obtenerSesionActual();
        if (actor == null) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Sesión no iniciada.");
            return;
        }
        if (reclamoSeleccionadoId == null) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "Selecciona un reclamo antes de responder.");
            return;
        }
        try {
            servicio().responderReclamo(actor, reclamoSeleccionadoId, respuestaTexto, nuevoEstado);
            reclamoSeleccionadoId = null;
            respuestaTexto = null;
            nuevoEstado = "RESUELTO";
            agregarMensaje(FacesMessage.SEVERITY_INFO, "Respuesta y estado guardados correctamente.");
            cargarContextoYReclamos();
        } catch (IllegalArgumentException | SecurityException | IllegalStateException e) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, e.getMessage());
        }
    }

    private ServicioSoporte servicio() {
        if (servicioSoporte == null) {
            servicioSoporte = new ServicioSoporte();
        }
        return servicioSoporte;
    }

    private SesionUsuario obtenerSesionActual() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        if (ctx == null || ctx.getExternalContext() == null) {
            return null;
        }
        Object rawSession = ctx.getExternalContext().getSession(false);
        if (!(rawSession instanceof HttpSession session)) {
            return null;
        }
        Object sesionObj = session.getAttribute("sesionUsuario");
        return (sesionObj instanceof SesionUsuario usuario) ? usuario : null;
    }

    private void agregarMensaje(FacesMessage.Severity severidad, String detalle) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        if (ctx != null) {
            ctx.addMessage(null, new FacesMessage(severidad, detalle, null));
        }
    }

    public List<ReclamoSoporte> getReclamos() {
        return reclamos;
    }

    public Integer getIncidenciaId() {
        return incidenciaId;
    }

    public void setIncidenciaId(Integer incidenciaId) {
        this.incidenciaId = incidenciaId;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getReclamoSeleccionadoId() {
        return reclamoSeleccionadoId;
    }

    public void setReclamoSeleccionadoId(Integer reclamoSeleccionadoId) {
        this.reclamoSeleccionadoId = reclamoSeleccionadoId;
    }

    public String getRespuestaTexto() {
        return respuestaTexto;
    }

    public void setRespuestaTexto(String respuestaTexto) {
        this.respuestaTexto = respuestaTexto;
    }

    public String getNuevoEstado() {
        return nuevoEstado;
    }

    public void setNuevoEstado(String nuevoEstado) {
        this.nuevoEstado = nuevoEstado;
    }

    public boolean isEsStaffSoporte() {
        return esStaffSoporte;
    }

    public boolean isEsCliente() {
        return esCliente;
    }

    public String getErrorAcceso() {
        return errorAcceso;
    }
}
