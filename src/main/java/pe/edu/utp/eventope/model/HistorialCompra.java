package pe.edu.utp.eventope.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_compra")
public class HistorialCompra implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "cliente_id")
    private int clienteId;

    @Column(name = "total_boletos_comprados")
    private int totalBoletosComprados;

    @Column(name = "ultima_compra_fecha")
    private LocalDateTime ultimaCompraFecha;

    public HistorialCompra() {
    }

    public HistorialCompra(int id, int clienteId, int totalBoletosComprados, LocalDateTime ultimaCompraFecha) {
        this.id = id;
        this.clienteId = clienteId;
        this.totalBoletosComprados = totalBoletosComprados;
        this.ultimaCompraFecha = ultimaCompraFecha;
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

    public int getTotalBoletosComprados() {
        return totalBoletosComprados;
    }

    public void setTotalBoletosComprados(int totalBoletosComprados) {
        this.totalBoletosComprados = totalBoletosComprados;
    }

    public LocalDateTime getUltimaCompraFecha() {
        return ultimaCompraFecha;
    }

    public void setUltimaCompraFecha(LocalDateTime ultimaCompraFecha) {
        this.ultimaCompraFecha = ultimaCompraFecha;
    }
}
