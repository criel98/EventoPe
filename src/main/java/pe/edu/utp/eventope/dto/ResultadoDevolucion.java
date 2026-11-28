package pe.edu.utp.eventope.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ResultadoDevolucion(int transaccionId, String codigoCompra,
                                  BigDecimal importeReembolso, LocalDateTime fechaDevolucion,
                                  boolean yaDevuelta) {}
