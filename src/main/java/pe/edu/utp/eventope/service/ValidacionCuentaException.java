package pe.edu.utp.eventope.service;
import java.util.Map;
public final class ValidacionCuentaException extends IllegalArgumentException {
    private final Map<String,String> errores;
    public ValidacionCuentaException(Map<String,String> errores) {
        super("Revisa los campos indicados."); this.errores=Map.copyOf(errores);
    }
    public Map<String,String> getErrores(){return errores;}
}
