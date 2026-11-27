package pe.edu.utp.eventope.config;
import java.sql.Connection;
import java.sql.SQLException;
@FunctionalInterface
public interface ProveedorConexion { Connection abrir() throws SQLException; }
