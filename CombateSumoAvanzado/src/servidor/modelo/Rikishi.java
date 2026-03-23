package servidor.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa un luchador de sumo (Rikishi) en el servidor.
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

    /** Numero de combates ganados. */
    private int victorias;

    /** Lista de kimarites (nombres de tecnicas) del luchador. */
    private List<String> kimarites;

    /** Referencia al luchador rival asignado. */
    private Rikishi rival;

    /** Estado: true si esta dentro del dohyo, false si fue sacado. */
    private boolean enDohyo;

    /**
     * Constructor que inicializa un Rikishi con nombre y peso.
     *
     * @param nombre Nombre del luchador.
     * @param peso   Peso en kg.
     */
    public Rikishi(String nombre, double peso) {
        this.nombre    = nombre;
        this.peso      = peso;
        this.victorias = 0;
        this.kimarites = new ArrayList<>();
        this.enDohyo   = true;
    }

    /**
     * Incrementa en uno el contador de victorias.
     */
    public void incrementarVictorias() { this.victorias++; }

    /**
     * Obtiene el nombre del luchador.
     * @return Nombre.
     */
    public String getNombre() { return nombre; }

    /**
     * Establece el nombre del luchador.
     * @param nombre Nuevo nombre.
     */
    public void setNombre(String nombre) { this.nombre = nombre; }

    /**
     * Obtiene el peso del luchador.
     * @return Peso en kg.
     */
    public double getPeso() { return peso; }

    /**
     * Establece el peso del luchador.
     * @param peso Nuevo peso.
     */
    public void setPeso(double peso) { this.peso = peso; }

    /**
     * Obtiene el numero de victorias.
     * @return Victorias acumuladas.
     */
    public int getVictorias() { return victorias; }

    /**
     * Obtiene la lista de kimarites.
     * @return Lista de nombres de tecnicas.
     */
    public List<String> getKimarites() { return kimarites; }

    /**
     * Establece la lista de kimarites.
     * @param kimarites Lista de nombres de tecnicas.
     */
    public void setKimarites(List<String> kimarites) { this.kimarites = kimarites; }

    /**
     * Obtiene el rival asignado.
     * @return Rikishi rival.
     */
    public Rikishi getRival() { return rival; }

    /**
     * Asigna el rival del luchador.
     * @param rival Rikishi contrincante.
     */
    public void setRival(Rikishi rival) { this.rival = rival; }

    /**
     * Indica si el luchador esta dentro del dohyo.
     * @return true si esta dentro.
     */
    public boolean isEnDohyo() { return enDohyo; }

    /**
     * Establece el estado del luchador dentro del dohyo.
     * @param enDohyo true si esta dentro.
     */
    public void setEnDohyo(boolean enDohyo) { this.enDohyo = enDohyo; }
}
