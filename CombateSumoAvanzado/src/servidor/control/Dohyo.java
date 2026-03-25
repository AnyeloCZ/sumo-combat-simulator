package servidor.control;

import servidor.modelo.Rikishi;

import java.util.List;
import java.util.Random;

/**
 * El Dohyo es el control del juego de sumo.
 * Sincroniza los luchadores, gestiona los turnos, selecciona kimarites
 * aleatoriamente, determina si un luchador es sacado del ring y
 * obtiene el ganador. No es un modelo — ES la logica del juego.
 * Usa wait/notifyAll para sincronizar los turnos entre hilos.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class Dohyo {

    /** Primer luchador registrado. */
    private Rikishi rikishi1;
    /** Segundo luchador registrado. */
    private Rikishi rikishi2;
    /** Turno actual: 1 o 2. */
    private int turno;
    /** true si el combate termino. */
    private boolean combateTerminado;
    /** Ganador del combate. */
    private Rikishi ganador;
    /** Ultima kimarite ejecutada. */
    private String ultimaKimarite;
    /** Nombre del ultimo atacante. */
    private String ultimoAtacante;
    /** Cantidad de luchadores registrados. */
    private int rikishisListos;
    /** Generador aleatorio para logica del combate. */
    private Random random;

    /**
     * Constructor que inicializa el Dohyo vacio.
     */
    public Dohyo() {
        this.turno            = 1;
        this.combateTerminado = false;
        this.rikishisListos   = 0;
        this.random           = new Random();
    }

    /**
     * Registra un luchador en el Dohyo.
     * Al llegar dos luchadores notifica a todos los hilos en espera.
     *
     * @param rikishi Luchador que ingresa al combate.
     */
    public synchronized void registrarRikishi(Rikishi rikishi) {
        if (rikishi1 == null) rikishi1 = rikishi;
        else                  rikishi2 = rikishi;
        rikishisListos++;
        if (rikishisListos == 2) notifyAll();
    }

    /**
     * Bloquea al hilo hasta que el rival este registrado.
     *
     * @throws InterruptedException Si el hilo es interrumpido.
     */
    public synchronized void esperarRival() throws InterruptedException {
        while (rikishisListos < 2) wait();
    }

    /**
     * Bloquea al luchador hasta que sea su turno.
     *
     * @param rikishi Luchador que espera su turno.
     * @throws InterruptedException Si el hilo es interrumpido.
     */
    public synchronized void esperarTurno(Rikishi rikishi) throws InterruptedException {
        int miTurno = (rikishi == rikishi1) ? 1 : 2;
        while (!combateTerminado && turno != miTurno) wait();
    }

    /**
     * Ejecuta el turno del luchador: selecciona kimarite aleatoria,
     * espera hasta 500ms y determina con 15% de probabilidad si saca al rival.
     *
     * @param atacante Rikishi que ejecuta su tecnica.
     * @throws InterruptedException Si el hilo es interrumpido.
     */
    public synchronized void ejecutarTurno(Rikishi atacante) throws InterruptedException {
        if (combateTerminado) return;

        List<String> kimarites = atacante.getKimarites();
        ultimaKimarite  = kimarites.get(random.nextInt(kimarites.size()));
        ultimoAtacante  = atacante.getNombre();

        // Espera aleatoria entre 100 y 500ms
        long espera = 100 + random.nextInt(401);
        wait(espera);

        // 15% de probabilidad de sacar al rival
        boolean sacaRival = random.nextInt(100) < 15;

        if (sacaRival) {
            Rikishi rival = (atacante == rikishi1) ? rikishi2 : rikishi1;
            rival.setEnDohyo(false);
            ganador = atacante;
            ganador.incrementarVictorias();
            combateTerminado = true;
        }

        // Cambiar turno
        turno = (turno == 1) ? 2 : 1;
        notifyAll();
    }

    /**
     * Indica si el combate ha terminado.
     *
     * @return true si hay ganador.
     */
    public boolean isCombateTerminado() { return combateTerminado; }

    /**
     * Obtiene el ganador del combate.
     *
     * @return Rikishi ganador.
     */
    public Rikishi getGanador() { return ganador; }

    /**
     * Obtiene el nombre de la ultima kimarite ejecutada.
     *
     * @return Nombre del kimarite.
     */
    public String getUltimaKimarite() { return ultimaKimarite; }

    /**
     * Obtiene el nombre del ultimo atacante.
     *
     * @return Nombre del atacante.
     */
    public String getUltimoAtacante() { return ultimoAtacante; }

    /**
     * Obtiene el primer luchador registrado.
     *
     * @return Rikishi 1.
     */
    public Rikishi getRikishi1() { return rikishi1; }

    /**
     * Obtiene el segundo luchador registrado.
     *
     * @return Rikishi 2.
     */
    public Rikishi getRikishi2() { return rikishi2; }
}
