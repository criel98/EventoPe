package pe.edu.utp.eventope.dto;
/** Un resultado recuperado no es una segunda autorización física de ingreso. */
public final class ResultadoAccesoDTO {
    private final int controlId; private final boolean permitido; private final String motivo; private final boolean recuperado;
    public ResultadoAccesoDTO(int id,boolean permitido,String motivo,boolean recuperado){this.controlId=id;this.permitido=permitido;this.motivo=motivo;this.recuperado=recuperado;}
    public int getControlId(){return controlId;} public boolean isPermitido(){return permitido;} public String getMotivo(){return motivo;} public boolean isRecuperado(){return recuperado;}
}
