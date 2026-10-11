package pe.edu.utp.eventope.config;

import java.sql.*;
import java.util.Properties;

public final class ConexionBD implements ProveedorConexion {
    private final Configuracion config;
    
    public ConexionBD(Configuracion config) { 
        this.config = config; 
    }
    
    @Override 
    public Connection abrir() throws SQLException {
        
        // --- INICIO DE LA CORRECCIÓN ---
        // Obliga a Tomcat a cargar la clase del driver de MySQL en memoria 
        // explícitamente antes de que el DriverManager intente usarlo.
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver de MySQL no encontrado en memoria.", e);
        }
        // --- FIN DE LA CORRECCIÓN ---

        // Tu código original intacto:
        Properties p = new Properties(); 
        p.setProperty("user", config.requerido("db.user")); 
        p.setProperty("password", config.requerido("db.password"));
        
        return DriverManager.getConnection(config.requerido("db.url"), p);
    }
}