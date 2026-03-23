package servidor.control;

import servidor.modelo.ArchivoPropiedades;

import java.io.IOException;
import java.util.List;

/**
 * Controlador que coordina la lectura del archivo de propiedades
 * de kimarites del luchador. Usa ArchivoPropiedades del modelo.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class ControlProperties {

    /** Instancia de lectura de propiedades. */
    private ArchivoPropiedades archivoPropiedades;

    /**
     * Carga los kimarites desde el archivo .properties indicado.
     *
     * @param ruta Ruta absoluta del archivo .properties.
     * @return Lista de nombres de kimarites.
     * @throws IOException Si ocurre error al leer el archivo.
     */
    public List<String> cargarKimarites(String ruta) throws IOException {
        archivoPropiedades = new ArchivoPropiedades(ruta);
        return archivoPropiedades.leerKimarites();
    }

    /**
     * Retorna la ruta del ultimo archivo cargado.
     *
     * @return Ruta del archivo actual.
     */
    public String getRutaActual() {
        return (archivoPropiedades != null) ? archivoPropiedades.getRuta() : "";
    }
}
