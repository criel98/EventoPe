package pe.edu.utp.eventope.dto;

import pe.edu.utp.eventope.util.FormatoFecha;
import pe.edu.utp.eventope.util.MotivosAcceso;

import java.io.Serializable;
import java.time.LocalDateTime;

/** Incidencia de acceso lista para mostrar. El DNI del asistente solo se expone enmascarado. */
public final class IncidenciaDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int id;
    private final int controlId;
    private final String tipo;
    private final String descripcion;
    private final String severidad;
    private final String estado;
    private final String fechaIntento;
    private final String evento;
    private final String puerta;
    private final String guardia;
    private final String motivoControl;
    private final String estadoControl;
    private final Integer entradaId;
    private final String entradaEstado;
    private final String asistente;
    private final String dniEnmascarado;
    private final String resueltaPor;
    private final String fechaResolucion;
    private final String motivoResolucion;
    private final boolean ingresoManualPosible;
    private final String razonSinIngresoManual;

    /** Datos crudos tal como salen de la consulta. */
    public record Datos(int id, int controlId, String tipo, String descripcion, String severidad, String estado,
                        LocalDateTime fechaResolucion, String motivoResolucion, String resueltaPor,
                        LocalDateTime horaIntento, String estadoControl, String motivoControl, Integer entradaId,
                        int eventoControlId, Integer autorizadoPorId, String evento, String puerta, String guardia,
                        String entradaEstado, Integer entradaEventoId, String nombres, String apellidos, String dni) {}

    public IncidenciaDTO(Datos d) {
        this.id = d.id();
        this.controlId = d.controlId();
        this.tipo = d.tipo();
        this.descripcion = d.descripcion();
        this.severidad = d.severidad();
        this.estado = d.estado();
        this.fechaIntento = FormatoFecha.lima(d.horaIntento());
        this.evento = d.evento();
        this.puerta = d.puerta();
        this.guardia = d.guardia();
        this.motivoControl = d.motivoControl();
        this.estadoControl = d.estadoControl();
        this.entradaId = d.entradaId();
        this.entradaEstado = d.entradaEstado();
        this.asistente = d.nombres() == null ? null : d.nombres() + " " + d.apellidos();
        this.dniEnmascarado = d.dni() == null || d.dni().length() < 4 ? null : "****" + d.dni().substring(d.dni().length() - 4);
        this.resueltaPor = d.resueltaPor();
        this.fechaResolucion = FormatoFecha.lima(d.fechaResolucion());
        this.motivoResolucion = d.motivoResolucion();
        String razon = razonSinIngresoManual(d);
        this.razonSinIngresoManual = razon;
        this.ingresoManualPosible = razon == null;
    }

    /** null cuando el ingreso manual es posible. Es solo orientación para la pantalla: el servicio lo vuelve a comprobar bajo bloqueo. */
    private static String razonSinIngresoManual(Datos d) {
        if (!"ABIERTA".equals(d.estado())) return "La incidencia ya fue " + (d.estado().equals("RESUELTA") ? "resuelta." : "descartada.");
        if (!"DENEGADO".equals(d.estadoControl())) return "El intento no fue denegado.";
        if (d.entradaId() == null) return "El intento no está asociado a una entrada.";
        if (d.autorizadoPorId() != null) return "El intento ya tiene una autorización.";
        if ("ANULADA".equals(d.entradaEstado())) return "La entrada está anulada: no se puede recuperar.";
        if ("UTILIZADA".equals(d.entradaEstado())) return "La entrada ya fue utilizada: no se permite un segundo ingreso.";
        if (!MotivosAcceso.admiteIngresoManual(d.motivoControl())) return "Este motivo (" + MotivosAcceso.descripcion(d.motivoControl()) + ") no admite ingreso manual.";
        if (d.entradaEventoId() == null || d.entradaEventoId() != d.eventoControlId()) return "La entrada pertenece a otro evento.";
        return null;
    }

    public int getId() { return id; }
    public int getControlId() { return controlId; }
    public String getTipo() { return tipo; }
    public String getTipoTexto() { return MotivosAcceso.descripcion(tipo); }
    public String getDescripcion() { return descripcion; }
    public String getSeveridad() { return severidad; }
    public String getEstado() { return estado; }
    public boolean isAbierta() { return "ABIERTA".equals(estado); }
    public String getFechaIntento() { return fechaIntento; }
    public String getEvento() { return evento; }
    public String getPuerta() { return puerta; }
    public String getGuardia() { return guardia; }
    public String getMotivoControl() { return motivoControl; }
    public String getEstadoControl() { return estadoControl; }
    public Integer getEntradaId() { return entradaId; }
    public String getEntradaEstado() { return entradaEstado; }
    public String getAsistente() { return asistente; }
    public String getDniEnmascarado() { return dniEnmascarado; }
    public String getResueltaPor() { return resueltaPor; }
    public String getFechaResolucion() { return fechaResolucion; }
    public String getMotivoResolucion() { return motivoResolucion; }
    public boolean isIngresoManualPosible() { return ingresoManualPosible; }
    public String getRazonSinIngresoManual() { return razonSinIngresoManual; }
}
