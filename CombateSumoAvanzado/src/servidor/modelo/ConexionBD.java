package servidor.modelo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton que gestiona la conexion a MySQL.
 * Garantiza una sola instancia de conexion en todo el sistema.
 * Reconecta automaticamente si la conexion se cierra.
 * Credenciales cargadas desde PropertiesBD.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class ConexionBD {

    /** Unica instancia Singleton. */
    private static ConexionBD instancia;

    /** Conexion JDBC activa. */
    private Connection conexion;

    /** URL guardada para reconexion. */
    private String url;

    /** Usuario guardado para reconexion. */
    private String usuario;

    /** Contrasena guardada para reconexion. */
    private String contrasena;

    /**
     * Constructor privado que establece la conexion inicial.
     */
    private ConexionBD() {
        try {
            PropertiesBD props = new PropertiesBD();
            this.url        = props.getUrl();
            this.usuario    = props.getUsuario();
            this.contrasena = props.getContrasena();
            // Intentar driver 8.x primero, luego 5.x como fallback
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e1) {
                Class.forName("com.mysql.jdbc.Driver");
            }
            this.conexion = DriverManager.getConnection(url, usuario, contrasena);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver MySQL no encontrado: " + e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException("Error de conexion BD: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException("Error leyendo config.properties: " + e.getMessage());
        }
    }

    /**
     * Retorna la unica instancia de ConexionBD.
     *
     * @return Instancia Singleton.
     */
    public static ConexionBD getInstancia() {
        if (instancia == null) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    /**
     * Retorna la conexion JDBC activa.
     * Si la conexion esta cerrada o es nula, la recrea automaticamente.
     *
     * @return Connection activo.
     */
    public Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                // reconexion automatica
                conexion = DriverManager.getConnection(url, usuario, contrasena);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error reconectando a BD: " + e.getMessage());
        }
        return conexion;
    }
}
