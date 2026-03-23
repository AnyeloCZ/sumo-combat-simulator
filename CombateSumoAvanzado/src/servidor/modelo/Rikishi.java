package servidor.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un luchador de sumo en el servidor.
 * Incluye id de BD, campo participo para control de combates
 * y kimarites como lista de Strings.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class Rikishi {

    /** Identificador unico en la base de datos. */
    private int id;
    /** Nombre del luchador. */
    private String nombre;
    /** Peso en kg. */
    private double peso;
    /** Victorias acumuladas. */
    private int victorias;
    /** Tecnicas del luchador. */
    private List<String> kimarites;
    /** Rival asignado en el combate actual. */
    private Rikishi rival;
    /** true si sigue dentro del dohyo. */
    private boolean enDohyo;
    /** true si ya participo en algun combate. */
    private boolean participo;

    /**
     * Constructor basico con nombre y peso.
     * @param nombre Nombre del luchador.
     * @param peso   Peso en kg.
     */
    public Rikishi(String nombre, double peso) {
        this.nombre    = nombre;
        this.peso      = peso;
        this.victorias = 0;
        this.kimarites = new ArrayList<>();
        this.enDohyo   = true;
        this.participo = false;
    }

    /**
     * Constructor completo para instancias recuperadas de la BD.
     * @param id        ID de la BD.
     * @param nombre    Nombre.
     * @param peso      Peso en kg.
     * @param victorias Victorias acumuladas.
     * @param kimarites Lista de tecnicas.
     * @param participo true si ya participo.
     */
    public Rikishi(int id, String nombre, double peso, int victorias,
                   List<String> kimarites, boolean participo) {
        this.id        = id;
        this.nombre    = nombre;
        this.peso      = peso;
        this.victorias = victorias;
        this.kimarites = kimarites;
        this.enDohyo   = true;
        this.participo = participo;
    }

    /** Incrementa en uno las victorias. */
    public void incrementarVictorias() { this.victorias++; }

    /** @return id de la BD. */
    public int getId() { return id; }
    /** @param id nuevo id. */
    public void setId(int id) { this.id = id; }
    /** @return nombre. */
    public String getNombre() { return nombre; }
    /** @param nombre nuevo nombre. */
    public void setNombre(String nombre) { this.nombre = nombre; }
    /** @return peso en kg. */
    public double getPeso() { return peso; }
    /** @param peso nuevo peso. */
    public void setPeso(double peso) { this.peso = peso; }
    /** @return victorias. */
    public int getVictorias() { return victorias; }
    /** @param victorias nuevo valor. */
    public void setVictorias(int victorias) { this.victorias = victorias; }
    /** @return lista de kimarites. */
    public List<String> getKimarites() { return kimarites; }
    /** @param kimarites nueva lista. */
    public void setKimarites(List<String> kimarites) { this.kimarites = kimarites; }
    /** @return rival. */
    public Rikishi getRival() { return rival; }
    /** @param rival contrincante. */
    public void setRival(Rikishi rival) { this.rival = rival; }
    /** @return true si esta en dohyo. */
    public boolean isEnDohyo() { return enDohyo; }
    /** @param enDohyo estado en dohyo. */
    public void setEnDohyo(boolean enDohyo) { this.enDohyo = enDohyo; }
    /** @return true si ya participo. */
    public boolean isParticipo() { return participo; }
    /** @param participo estado de participacion. */
    public void setParticipo(boolean participo) { this.participo = participo; }
}
