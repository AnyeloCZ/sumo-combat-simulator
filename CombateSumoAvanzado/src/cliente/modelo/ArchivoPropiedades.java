package cliente.modelo;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Clase que gestiona la lectura del archivo de propiedades con los kimarites.
 * Su única responsabilidad es leer el archivo y retornar los nombres
 * como lista de Strings. NO crea objetos del modelo.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class ArchivoPropiedades {

    /** Ruta del archivo de propiedades. */
    private String ruta;

    /**
     * Constructor que recibe la ruta del archivo.
     *
     * @param ruta Ruta completa al archivo .properties.
     */
    public ArchivoPropiedades(String ruta) {
        this.ruta = ruta;
    }

    /**
     * Lee el archivo de propiedades y retorna los nombres de los kimarites
     * como lista de Strings. No crea objetos del modelo.
     *
     * @return Lista de nombres de kimarites leídos del archivo.
     * @throws IOException Si el archivo no se puede leer.
     */
    public List<String> leerKimarites() throws IOException {
        List<String> lista = new ArrayList<>();
        Properties props = new Properties();

        try (FileInputStream fis = new FileInputStream(ruta)) {
            props.load(fis);
        }

        String totalStr = props.getProperty("kimarite.total", "0");
        int total = Integer.parseInt(totalStr.trim());

        for (int i = 1; i <= total; i++) {
            String nombre = props.getProperty("kimarite." + i);
            if (nombre != null && !nombre.trim().isEmpty()) {
                lista.add(nombre.trim());
            }
        }

        return lista;
    }

    /**
     * Obtiene la ruta del archivo de propiedades.
     *
     * @return Ruta del archivo.
     */
    public String getRuta() {
        return ruta;
    }

    /**
     * Establece una nueva ruta para el archivo de propiedades.
     *
     * @param ruta Nueva ruta del archivo.
     */
    public void setRuta(String ruta) {
        this.ruta = ruta;
    }
}
