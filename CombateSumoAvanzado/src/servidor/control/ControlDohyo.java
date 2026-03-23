package servidor.control;

import servidor.modelo.Dohyo;
import servidor.modelo.Rikishi;

import java.util.List;
import java.util.Random;

/**
 * Controlador del Dohyo. Contiene toda la logica del combate de sumo:
 * registrar luchadores, gestionar turnos, seleccionar kimarites aleatoriamente,
 * determinar si un luchador es sacado del ring y obtener el ganador.
 * ControlGeneral solo conoce a este controlador, nunca al modelo Dohyo directamente.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class ControlDohyo {

    /** Dohyo (estado y sincronizacion). */
    private Dohyo dohyo;

    /** Generador de numeros aleatorios para la logica del combate. */
    private Random random;

    /**
     * Constructor que crea el Dohyo internamente.
     * ControlGeneral no necesita conocer al modelo Dohyo.
     */
    public ControlDohyo() {
        this.dohyo  = new Dohyo();
        this.random = new Random();
    }

    /**
     * Registra un Rikishi en el Dohyo.
     *
     * @param rikishi Luchador que ingresa al combate.
     */
    public void registrarRikishi(Rikishi rikishi) {
        dohyo.registrarRikishi(rikishi);
    }

    /**
     * Bloquea al hilo hasta que el rival este registrado.
     *
     * @throws InterruptedException Si el hilo es interrumpido.
     */
    public void esperarRival() throws InterruptedException {
        dohyo.esperarRival();
    }

    /**
     * Ejecuta un turno del luchador dado:
     * 1. Espera su turno. 2. Selecciona kimarite aleatorio.
     * 3. Espera hasta 500ms. 4. Determina ~15% de probabilidad de sacar al rival.
     *
     * @param atacante Rikishi que ejecuta su tecnica.
     * @throws InterruptedException Si el hilo es interrumpido.
     */
    public void ejecutarTurno(Rikishi atacante) throws InterruptedException {
        dohyo.esperarTurno(atacante);
        if (dohyo.isCombateTerminado()) return;

        List<String> kimarites = atacante.getKimarites();
        int indice = random.nextInt(kimarites.size());
        String tecnica = kimarites.get(indice);

        long espera = 100 + random.nextInt(401);
        Thread.sleep(espera);

        boolean sacaRival = random.nextInt(100) < 15;
        dohyo.registrarTurno(atacante, tecnica, sacaRival);
    }

    /**
     * Indica si el combate ha terminado.
     *
     * @return true si hay ganador.
     */
    public boolean isCombateTerminado() { return dohyo.isCombateTerminado(); }

    /**
     * Obtiene el nombre de la ultima tecnica ejecutada.
     *
     * @return Nombre del ultimo kimarite.
     */
    public String getUltimaKimarite() { return dohyo.getUltimaKimarite(); }

    /**
     * Obtiene el nombre del luchador que ejecuto la ultima tecnica.
     *
     * @return Nombre del ultimo atacante.
     */
    public String getUltimoAtacante() { return dohyo.getUltimoAtacante(); }

    /**
     * Obtiene el Rikishi ganador del combate.
     *
     * @return Rikishi ganador.
     */
    public Rikishi getGanador() { return dohyo.getGanador(); }

    /**
     * Obtiene el primer rikishi registrado.
     *
     * @return Rikishi 1.
     */
    public Rikishi getRikishi1() { return dohyo.getRikishi1(); }

    /**
     * Obtiene el segundo rikishi registrado.
     *
     * @return Rikishi 2.
     */
    public Rikishi getRikishi2() { return dohyo.getRikishi2(); }
}
