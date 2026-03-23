package servidor.control;

import servidor.modelo.Rikishi;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Orquesta los tres combates del parcial.
 * Selecciona luchadores aleatoriamente de la BD, gestiona el turno del
 * ganador para el siguiente combate, actualiza victorias y participacion
 * en BD, y coordina con ControlRAF para persistir cada resultado.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class ControlCombates {

    /** Total de combates a realizar. */
    public static final int TOTAL_COMBATES = 3;

    /** Numero del combate actual. */
    private int numeroCombate;

    /** Ganador del ultimo combate (pelea en el siguiente). */
    private Rikishi ganadorActual;

    /** Luchadores pendientes de combatir. */
    private List<Rikishi> pendientes;

    /** Controlador de BD. */
    private ControlBD controlBD;

    /** Controlador del RAF. */
    private ControlRAF controlRAF;

    /** Listener para notificar eventos a la vista. */
    private IEventosCombate listener;

    /** Generador aleatorio. */
    private Random random;

    /**
     * Constructor que inicializa los controladores necesarios.
     * @param controlBD  Controlador de base de datos.
     * @param controlRAF Controlador del archivo de acceso aleatorio.
     * @param listener   Listener de eventos para la vista.
     */
    public ControlCombates(ControlBD controlBD, ControlRAF controlRAF, IEventosCombate listener) {
        this.controlBD     = controlBD;
        this.controlRAF    = controlRAF;
        this.listener      = listener;
        this.numeroCombate = 0;
        this.random        = new Random();
        this.pendientes    = new ArrayList<>();
    }

    /**
     * Carga los luchadores disponibles desde la BD.
     */
    public void cargarLuchadores() {
        pendientes = controlBD.obtenerDisponibles();
    }

    /**
     * Indica si aun quedan combates por realizar.
     * @return true si hay combates pendientes.
     */
    public boolean hayCombatesPendientes() {
        return numeroCombate < TOTAL_COMBATES && !pendientes.isEmpty();
    }

    /**
     * Ejecuta el siguiente combate.
     * Combate 1: dos luchadores aleatorios.
     * Combates 2-3: ganador anterior vs nuevo aleatorio.
     * Al terminar: actualiza BD y guarda en RAF con datos de la BD.
     * @throws InterruptedException Si el hilo es interrumpido.
     */
    public void ejecutarSiguienteCombate() throws InterruptedException {
        numeroCombate++;

        Rikishi luchador1 = (ganadorActual == null) ? seleccionarAleatorio() : ganadorActual;
        Rikishi luchador2 = seleccionarAleatorio();

        luchador1.setEnDohyo(true);
        luchador2.setEnDohyo(true);

        ControlDohyo controlDohyo = new ControlDohyo();

        listener.onCombateIniciado(luchador1.getNombre(), luchador2.getNombre());

        controlDohyo.registrarRikishi(luchador1);
        controlDohyo.registrarRikishi(luchador2);

        HiloCombateInterno hilo1 = new HiloCombateInterno(luchador1, controlDohyo);
        HiloCombateInterno hilo2 = new HiloCombateInterno(luchador2, controlDohyo);

        hilo1.start();
        hilo2.start();
        hilo1.join();
        hilo2.join();

        Rikishi ganador  = controlDohyo.getGanador();
        Rikishi perdedor = (ganador == luchador1) ? luchador2 : luchador1;

        ganador.incrementarVictorias();
        controlBD.actualizarVictorias(ganador.getId(), ganador.getVictorias());
        controlBD.marcarParticipacion(luchador1.getId());
        controlBD.marcarParticipacion(luchador2.getId());

        // Datos del RAF vienen de la BD (consulta directa)
        Rikishi ganadorBD  = controlBD.obtenerPorId(ganador.getId());
        Rikishi perdedorBD = controlBD.obtenerPorId(perdedor.getId());
        controlRAF.guardarResultado(numeroCombate, ganadorBD, perdedorBD);

        listener.onCombateTerminado(
            ganador.getNombre(), ganador.getPeso(), ganador.getVictorias()
        );

        ganadorActual = ganador;
    }

    /**
     * Selecciona y elimina un luchador aleatorio de la lista de pendientes.
     * @return Rikishi seleccionado.
     */
    private Rikishi seleccionarAleatorio() {
        return pendientes.remove(random.nextInt(pendientes.size()));
    }

    /**
     * Retorna el numero del combate actual.
     * @return Numero de combate.
     */
    public int getNumeroCombate() { return numeroCombate; }

    /**
     * Retorna el total de combates realizados.
     * @return Total combates.
     */
    public int getTotalCombatesRealizados() { return numeroCombate; }

    // ======================== HILO INTERNO ========================

    /**
     * Hilo que ejecuta los turnos de un luchador dentro del combate.
     */
    private class HiloCombateInterno extends Thread {

        /** Luchador que ejecuta sus turnos. */
        private Rikishi rikishi;
        /** Controlador del dohyo. */
        private ControlDohyo controlDohyo;

        /**
         * Constructor del hilo interno.
         * @param rikishi      Luchador a ejecutar.
         * @param controlDohyo Controlador del dohyo.
         */
        public HiloCombateInterno(Rikishi rikishi, ControlDohyo controlDohyo) {
            this.rikishi      = rikishi;
            this.controlDohyo = controlDohyo;
        }

        /**
         * Ejecuta los turnos del luchador hasta que el combate termine.
         */
        @Override
        public void run() {
            try {
                controlDohyo.esperarRival();
                while (!controlDohyo.isCombateTerminado()) {
                    controlDohyo.ejecutarTurno(rikishi);
                    if (!controlDohyo.isCombateTerminado()) {
                        listener.onTurnoEjecutado(
                            controlDohyo.getUltimoAtacante(),
                            controlDohyo.getUltimaKimarite()
                        );
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
