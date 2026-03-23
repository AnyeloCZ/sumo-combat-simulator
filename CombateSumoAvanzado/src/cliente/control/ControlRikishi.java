package cliente.control;

import cliente.modelo.Rikishi;

import java.util.List;

/**
 * Controlador responsable de crear y configurar objetos Rikishi.
 * Es el unico que instancia objetos Rikishi.
 * Los kimarites se manejan directamente como Strings.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class ControlRikishi {

    /**
     * Crea un objeto Rikishi con nombre y peso.
     *
     * @param nombre Nombre del luchador.
     * @param peso   Peso del luchador en kg.
     * @return Nuevo objeto Rikishi.
     */
    public Rikishi crearRikishi(String nombre, double peso) {
        return new Rikishi(nombre, peso);
    }

    /**
     * Asigna la lista de kimarites (Strings) a un Rikishi dado.
     *
     * @param rikishi  Luchador al que se le asignan las tecnicas.
     * @param kimarites Lista de nombres de tecnicas.
     */
    public void asignarKimarites(Rikishi rikishi, List<String> kimarites) {
        rikishi.setKimarites(kimarites);
    }

    /**
     * Serializa un Rikishi para enviarlo por socket.
     * Formato: "NOMBRE|PESO|kimarite1,kimarite2,..."
     *
     * @param rikishi Luchador a serializar.
     * @return Cadena serializada lista para enviar.
     */
    public String serializarRikishi(Rikishi rikishi) {
        return rikishi.serializar();
    }
}
