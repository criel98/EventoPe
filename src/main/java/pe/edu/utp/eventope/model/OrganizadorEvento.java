package pe.edu.utp.eventope.model;

import java.io.Serializable;

/** Datos de organizador_evento. Persistencia JDBC externa; no exponga entidades con secretos a JSP. */
public class OrganizadorEvento implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String ruc;
    private String razonSocial;
    private String emailContacto;
    private String telefono;

    public OrganizadorEvento() {}

    public OrganizadorEvento(int id, String ruc, String razonSocial, String emailContacto, String telefono) {
        this.id = id;
        this.ruc = ruc;
        this.razonSocial = razonSocial;
        this.emailContacto = emailContacto;
        this.telefono = telefono;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getEmailContacto() { return emailContacto; }
    public void setEmailContacto(String emailContacto) { this.emailContacto = emailContacto; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }


}
