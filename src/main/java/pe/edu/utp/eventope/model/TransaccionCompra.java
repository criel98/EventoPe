package pe.edu.utp.eventope.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaccion_compra")
public class TransaccionCompra implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "cliente_id")
    private int clienteId;

    @Column(name = "cupon_id")
    private int cuponId;

    @Column(name = "fecha_transaccion")
    private LocalDateTime fechaTransaccion;

    @Column(name = "total_calculado")
    private BigDecimal totalCalculado;

    public TransaccionCompra() {
    }

    public TransaccionCompra(int id, int clienteId, int cuponId, LocalDateTime fechaTransaccion, BigDecimal totalCalculado) {
        this.id = id;
        this.clienteId = clienteId;
        this.cuponId = cuponId;
        this.fechaTransaccion = fechaTransaccion;
        this.totalCalculado = totalCalculado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClienteId() {
        return clienteId;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }

    public int getCuponId() {
        return cuponId;
    }

    public void setCuponId(int cuponId) {
        this.cuponId = cuponId;
    }

    public LocalDateTime getFechaTransaccion() {
        return fechaTransaccion;
    }

    public void setFechaTransaccion(LocalDateTime fechaTransaccion) {
        this.fechaTransaccion = fechaTransaccion;
    }

    public BigDecimal getTotalCalculado() {
        return totalCalculado;
    }

    public void setTotalCalculado(BigDecimal totalCalculado) {
        this.totalCalculado = totalCalculado;
    }
}
