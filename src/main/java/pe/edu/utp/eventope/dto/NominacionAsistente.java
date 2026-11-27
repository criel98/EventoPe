package pe.edu.utp.eventope.dto;

import java.io.Serializable;

public final class NominacionAsistente implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String dni;
    private final String nombres;
    private final String apellidos;

    public NominacionAsistente(String dni, String nombres, String apellidos) {
        this.dni = limpia(dni);
        this.nombres = limpia(nombres);
        this.apellidos = limpia(apellidos);
        if (!this.dni.matches("[0-9]{8}")) {
            throw new IllegalArgumentException("El DNI debe tener 8 dígitos.");
        }
        if (this.nombres.isBlank() || this.apellidos.isBlank() || this.nombres.length() > 100 || this.apellidos.length() > 100) {
            throw new IllegalArgumentException("Completa nombres y apellidos (máximo 100 caracteres).");
        }
    }

    private static String limpia(String s) {
        return s == null ? "" : s.trim().replaceAll("\\s+", " ");
    }

    public String getDni() { return dni; }
    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
}
