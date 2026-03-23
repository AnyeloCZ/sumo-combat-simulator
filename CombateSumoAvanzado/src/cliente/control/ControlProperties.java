package cliente.control;

import cliente.modelo.ArchivoPropiedades;

import java.io.IOException;
import java.util.List;

/**
 * Controlador responsable de coordinar la carga de kimarites
 * desde el archivo de propiedades.
 * Recibe la ruta del archivo (seleccionada por JFileChooser en la vista),
 * instancia ArchivoPropiedades y retorna la lista de nombres.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class ControlProperties {

    /** Objeto de acceso al archivo de propiedades. */
    private ArchivoPropiedades archivoPropiedades;

    /**
     * Carga los kimarites desde el archivo ubicado en la ruta indicada.
     * La ruta es proporcionada por la vista mediante JFileChooser.
     *
     * @param ruta Ruta completa al archivo .properties.
     * @return Lista de nombres de kimarites leídos del archivo.
     * @throws IOException Si el archivo no existe o no puede leerse.
     */
    public List<String> cargarKimarites(String ruta) throws IOException {
        archivoPropiedades = new ArchivoPropiedades(ruta);
        return archivoPropiedades.leerKimarites();
    }

    /**
     * Obtiene la ruta del archivo de propiedades actualmente cargado.
     *
     * @return Ruta del archivo, o {@code null} si no se ha cargado ninguno.
     */
    public String getRutaActual() {
        if (archivoPropiedades == null) return null;
        return archivoPropiedades.getRuta();
    }
}
