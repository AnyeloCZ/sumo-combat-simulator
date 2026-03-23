package servidor.modelo;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Clase del modelo que lee un archivo .properties de kimarites.
 * Solo lee y retorna datos como Strings. No crea objetos del modelo.
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
     * @param ruta Ruta absoluta del archivo .properties.
     */
    public ArchivoPropiedades(String ruta) {
        this.ruta = ruta;
    }

    /**
     * Lee la lista de kimarites del archivo .properties.
     *
     * @return Lista de nombres de tecnicas.
     * @throws IOException Si ocurre error al leer.
     */
    public List<String> leerKimarites() throws IOException {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(ruta)) {
            props.load(fis);
        }
        List<String> lista = new ArrayList<>();
        int total = Integer.parseInt(props.getProperty("kimarite.total", "0"));
        for (int i = 1; i <= total; i++) {
            String k = props.getProperty("kimarite." + i);
            if (k != null && !k.trim().isEmpty()) lista.add(k.trim());
        }
        return lista;
    }

    /**
     * Retorna la ruta del archivo.
     *
     * @return Ruta.
     */
    public String getRuta() { return ruta; }

    /**
     * Establece la ruta del archivo.
     *
     * @param ruta Nueva ruta.
     */
    public void setRuta(String ruta) { this.ruta = ruta; }
}
