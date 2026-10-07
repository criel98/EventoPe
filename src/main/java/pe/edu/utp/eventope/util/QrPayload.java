package pe.edu.utp.eventope.util;
import java.time.Instant;
import java.util.UUID;
/** No contiene DNI, contraseña ni secreto. El contador se valida contra la hora del servidor. */
public record QrPayload(String codigoEntrada,long intervalo,String otp) {
    public QrPayload {
        if(codigoEntrada==null||!UUID.fromString(codigoEntrada).toString().equals(codigoEntrada)||intervalo<0||otp==null||!otp.matches("[0-9]{8}"))throw new IllegalArgumentException("QR inválido");
    }
    public String codificar(){return "EP1:"+codigoEntrada+":"+intervalo+":"+otp;}
    public static QrPayload leer(String valor){
        if(valor==null||valor.length()>100)throw new IllegalArgumentException("QR inválido");
        String[] p=valor.split(":",-1);if(p.length!=4||!p[0].equals("EP1"))throw new IllegalArgumentException("QR inválido");
        return new QrPayload(p[1],Long.parseLong(p[2]),p[3]);
    }
    public boolean verificar(byte[] secreto,Instant ahora){return intervalo==Totp.intervalo(ahora)&&Totp.verificar(secreto,otp,ahora);}
}
