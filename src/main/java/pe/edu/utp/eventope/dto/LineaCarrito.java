package pe.edu.utp.eventope.dto;

import java.io.Serializable;

public final class LineaCarrito implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int categoriaId;
    private final String zona;
    private final int cantidad;
    private final String precioUnitario;
    public LineaCarrito(int categoriaId, String zona, int cantidad, String precioUnitario) {
        if (categoriaId <= 0 || zona == null || cantidad < 1 || cantidad > 4 || precioUnitario == null) throw new IllegalArgumentException("Línea de carrito inválida");
        this.categoriaId=categoriaId; this.zona=zona; this.cantidad=cantidad; this.precioUnitario=precioUnitario;
    }
    public int getCategoriaId(){return categoriaId;} public String getZona(){return zona;} public int getCantidad(){return cantidad;} public String getPrecioUnitario(){return precioUnitario;}
}
