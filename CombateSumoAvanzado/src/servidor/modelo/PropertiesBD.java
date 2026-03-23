package servidor.modelo;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Lee el archivo config.properties con las credenciales de la BD.
 * No crea objetos del modelo ni gestiona la conexion.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class PropertiesBD {

    /** Ruta del archivo de propiedades. */
    private static final String RUTA = "Data/config.properties";

    /** Propiedades cargadas. */
    private Properties props;

    /**
     * Constructor que carga el archivo de propiedades.
     * @throws IOException Si el archivo no se puede leer.
     */
    public PropertiesBD() throws IOException {
        props = new Properties();
        try (FileInputStream fis = new FileInputStream(RUTA)) {
            props.load(fis);
        }
    }

    /**
     * Obtiene la URL JDBC de conexion.
     * @return URL de la BD.
     */
    public String getUrl() { return props.getProperty("db.url"); }

    /**
     * Obtiene el usuario de la BD.
     * @return Usuario.
     */
    public String getUsuario() { return props.getProperty("db.usuario"); }

    /**
     * Obtiene la contrasena de la BD.
     * @return Contrasena.
     */
    public String getContrasena() { return props.getProperty("db.contrasena"); }
}
