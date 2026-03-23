package cliente.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa un luchador de sumo (Rikishi) en el cliente.
 * Los kimarites se almacenan directamente como Strings.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class Rikishi {

    /** Nombre del luchador. */
    private String nombre;

    /** Peso del luchador en kg. */
    private double peso;

    /** Lista de kimarites (nombres de tecnicas) del luchador. */
    private List<String> kimarites;

    /**
     * Constructor que inicializa el Rikishi con nombre y peso.
     *
     * @param nombre Nombre del luchador.
     * @param peso   Peso en kg.
     */
    public Rikishi(String nombre, double peso) {
        this.nombre    = nombre;
        this.peso      = peso;
        this.kimarites = new ArrayList<>();
    }

    /**
     * Obtiene el nombre del luchador.
     *
     * @return Nombre.
     */
    public String getNombre() { return nombre; }

    /**
     * Establece el nombre del luchador.
     *
     * @param nombre Nuevo nombre.
     */
    public void setNombre(String nombre) { this.nombre = nombre; }

    /**
     * Obtiene el peso del luchador.
     *
     * @return Peso en kg.
     */
    public double getPeso() { return peso; }

    /**
     * Establece el peso del luchador.
     *
     * @param peso Nuevo peso.
     */
    public void setPeso(double peso) { this.peso = peso; }

    /**
     * Obtiene la lista de kimarites del luchador.
     *
     * @return Lista de nombres de tecnicas.
     */
    public List<String> getKimarites() { return kimarites; }

    /**
     * Establece la lista de kimarites del luchador.
     *
     * @param kimarites Lista de nombres de tecnicas.
     */
    public void setKimarites(List<String> kimarites) { this.kimarites = kimarites; }

    /**
     * Serializa el Rikishi como cadena para enviar por socket.
     * Formato: "NOMBRE|PESO|kimarite1,kimarite2,..."
     *
     * @return Cadena serializada.
     */
    public String serializar() {
        return nombre + "|" + peso + "|" + String.join(",", kimarites);
    }
}
