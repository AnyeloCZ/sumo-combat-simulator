package cliente.modelo;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Lee el archivo cliente.properties con el host y puerto del servidor.
 * Unica responsabilidad: leer propiedades de conexion del cliente.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class PropertiesCliente {

    /** Ruta del archivo de propiedades del cliente. */
    private static final String RUTA = "Data/cliente.properties";

    /** Propiedades cargadas. */
    private Properties props;

    /**
     * Constructor que carga el archivo de propiedades.
     *
     * @throws IOException Si el archivo no se puede leer.
     */
    public PropertiesCliente() throws IOException {
        props = new Properties();
        try (FileInputStream fis = new FileInputStream(RUTA)) {
            props.load(fis);
        }
    }

    /**
     * Retorna el host del servidor.
     *
     * @return Host configurado.
     */
    public String getHost() {
        return props.getProperty("servidor.host", "localhost");
    }

    /**
     * Retorna el puerto del servidor.
     *
     * @return Puerto configurado.
     */
    public int getPuerto() {
        return Integer.parseInt(props.getProperty("servidor.puerto", "9090"));
    }
}
