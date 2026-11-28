package pe.edu.utp.eventope.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ResultadoDevolucion(int transaccionId, String codigoCompra,
                                  BigDecimal importeReembolso, LocalDateTime fechaDevolucion,
                                  boolean yaDevuelta) {
    public int getTransaccionId(){return transaccionId;}
    public String getCodigoCompra(){return codigoCompra;}
    public BigDecimal getImporteReembolso(){return importeReembolso;}
    public LocalDateTime getFechaDevolucion(){return fechaDevolucion;}
    public boolean isYaDevuelta(){return yaDevuelta;}
}
