package pe.edu.utp.eventope.service;

import pe.edu.utp.eventope.config.ProveedorConexion;
import pe.edu.utp.eventope.dao.DevolucionDAO;
import pe.edu.utp.eventope.dto.ResultadoDevolucion;

import java.sql.Connection;
import java.sql.SQLException;

public final class ServicioDevolucion {
    private final ProveedorConexion conexiones;
    private final DevolucionDAO dao;

    public ServicioDevolucion(ProveedorConexion conexiones) {
        this(conexiones,new DevolucionDAO());
    }
    public ServicioDevolucion(ProveedorConexion conexiones, DevolucionDAO dao) {
        this.conexiones=conexiones; this.dao=dao;
    }

    public ResultadoDevolucion devolver(int clienteId,int transaccionId) throws SQLException {
        try(Connection c=conexiones.abrir()) {
            c.setAutoCommit(false);
            try {
                ResultadoDevolucion r=dao.devolver(c,clienteId,transaccionId);
                c.commit();
                return r;
            } catch(SQLException|RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }
}
