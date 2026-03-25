package servidor.control;

/**
 * Clase que representa el estado del Dohyo (ring de sumo).
 * Solo almacena el estado del combate y provee sincronización
 * mediante {@code wait()} y {@code notifyAll()}.
 * Toda la lógica del combate reside en {@code ControlDohyo}.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class Dohyo {

    /** Primer luchador registrado en el dohyo. */
    private Rikishi rikishi1;

    /** Segundo luchador registrado en el dohyo. */
    private Rikishi rikishi2;

    /** Número del turno actual (1 = rikishi1, 2 = rikishi2). */
    private int turno;

    /** Indica si el combate ha terminado. */
    private boolean combateTerminado;

    /** Luchador ganador del combate. */
    private Rikishi ganador;

    /** Nombre de la última técnica ejecutada (para mostrar en vista). */
    private String ultimaKimarite;

    /** Nombre del luchador que ejecutó la última técnica. */
    private String ultimoAtacante;

    /** Contador de luchadores registrados en el dohyo. */
    private int rikishisListos;

    /**
     * Constructor que inicializa el Dohyo vacío.
     */
    public Dohyo() {
        this.turno           = 1;
        this.combateTerminado = false;
        this.ganador         = null;
        this.rikishisListos  = 0;
    }

    /**
     * Registra un Rikishi en el dohyo.
     * Cuando llegan los dos, asigna los rivales y notifica los hilos en espera.
     *
     * @param rikishi Luchador que ingresa al dohyo.
     */
    public synchronized void registrarRikishi(Rikishi rikishi) {
        rikishisListos++;
        if (rikishisListos == 1) {
            rikishi1 = rikishi;
        } else {
            rikishi2 = rikishi;
            rikishi1.setRival(rikishi2);
            rikishi2.setRival(rikishi1);
            notifyAll();
        }
    }

    /**
     * Bloquea al hilo que llama hasta que los dos luchadores estén registrados.
     *
     * @throws InterruptedException Si el hilo es interrumpido.
     */
    public synchronized void esperarRival() throws InterruptedException {
        while (rikishisListos < 2) {
            wait();
        }
    }

    /**
     * Bloquea al hilo hasta que sea el turno del rikishi dado.
     *
     * @param rikishi Luchador que espera su turno.
     * @throws InterruptedException Si el hilo es interrumpido.
     */
    public synchronized void esperarTurno(Rikishi rikishi) throws InterruptedException {
        int miTurno = (rikishi == rikishi1) ? 1 : 2;
        while (turno != miTurno && !combateTerminado) {
            wait(500);
        }
    }

    /**
     * Registra el resultado de un turno: la técnica usada y si el rival fue sacado.
     * Luego alterna el turno y notifica a los hilos en espera.
     *
     * @param atacante      Rikishi que ejecutó la técnica.
     * @param nombreKimarite Nombre de la técnica utilizada.
     * @param sacaRival     {@code true} si el rival fue sacado del dohyo.
     */
    public synchronized void registrarTurno(Rikishi atacante, String nombreKimarite, boolean sacaRival) {
        this.ultimoAtacante = atacante.getNombre();
        this.ultimaKimarite = nombreKimarite;

        if (sacaRival) {
            atacante.getRival().setEnDohyo(false);
            ganador = atacante;
            ganador.incrementarVictorias();
            combateTerminado = true;
        }

        turno = (turno == 1) ? 2 : 1;
        notifyAll();
    }

    /**
     * Indica si el combate ha terminado.
     *
     * @return {@code true} si hay ganador.
     */
    public synchronized boolean isCombateTerminado() { return combateTerminado; }

    /**
     * Obtiene el luchador ganador.
     *
     * @return Rikishi ganador o {@code null} si aún no hay.
     */
    public synchronized Rikishi getGanador() { return ganador; }

    /**
     * Obtiene el nombre de la última técnica ejecutada.
     *
     * @return Nombre del último kimarite.
     */
    public synchronized String getUltimaKimarite() { return ultimaKimarite; }

    /**
     * Obtiene el nombre del luchador que ejecutó la última técnica.
     *
     * @return Nombre del último atacante.
     */
    public synchronized String getUltimoAtacante() { return ultimoAtacante; }

    /**
     * Obtiene el primer rikishi registrado.
     *
     * @return Rikishi 1.
     */
    public Rikishi getRikishi1() { return rikishi1; }

    /**
     * Obtiene el segundo rikishi registrado.
     *
     * @return Rikishi 2.
     */
    public Rikishi getRikishi2() { return rikishi2; }
}
