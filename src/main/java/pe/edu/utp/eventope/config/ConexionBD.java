package pe.edu.utp.eventope.config;
import java.sql.*;
import java.util.Properties;
public final class ConexionBD implements ProveedorConexion {
    private final Configuracion config;
    public ConexionBD(Configuracion config) { this.config = config; }
    @Override public Connection abrir() throws SQLException {
        Properties p = new Properties(); p.setProperty("user", config.requerido("db.user")); p.setProperty("password", config.requerido("db.password"));
        return DriverManager.getConnection(config.requerido("db.url"), p);
    }
}
