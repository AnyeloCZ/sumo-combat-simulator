package servidor.control;

import servidor.modelo.Rikishi;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Controlador que orquesta los tres combates del parcial.
 * Selecciona luchadores aleatoriamente de la BD, usa el Dohyo directamente
 * (que es el control del juego), actualiza victorias en BD y guarda en RAF.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class ControlCombates {

    /** Total de combates a realizar. */
    public static final int TOTAL_COMBATES = 3;

    /** Numero del combate actual. */
    private int numeroCombate;

    /** Ganador del ultimo combate. */
    private Rikishi ganadorActual;

    /** Luchadores pendientes. */
    private List<Rikishi> pendientes;

    /** Controlador de BD. */
    private ControlBD controlBD;

    /** Controlador del RAF. */
    private ControlRAF controlRAF;

    /** Listener para eventos de la vista. */
    private IEventosCombate listener;

    /** Generador aleatorio para seleccion de luchadores. */
    private Random random;

    /**
     * Constructor que inicializa los controladores.
     *
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
     * Indica si quedan combates por realizar.
     *
     * @return true si hay combates pendientes.
     */
    public boolean hayCombatesPendientes() {
        return numeroCombate < TOTAL_COMBATES && !pendientes.isEmpty();
    }

    /**
     * Ejecuta el siguiente combate usando el Dohyo directamente.
     * El Dohyo es quien sincroniza los luchadores y dirige el combate.
     *
     * @throws InterruptedException Si el hilo es interrumpido.
     */
    public void ejecutarSiguienteCombate() throws InterruptedException {
        numeroCombate++;

        Rikishi luchador1 = (ganadorActual == null) ? seleccionarAleatorio() : ganadorActual;
        Rikishi luchador2 = seleccionarAleatorio();

        luchador1.setEnDohyo(true);
        luchador2.setEnDohyo(true);

        // El Dohyo ES el control del juego — se usa directamente
        Dohyo dohyo = new Dohyo();

        listener.onCombateIniciado(luchador1.getNombre(), luchador2.getNombre());

        dohyo.registrarRikishi(luchador1);
        dohyo.registrarRikishi(luchador2);

        // Turnos alternados hasta que haya ganador
        while (!dohyo.isCombateTerminado()) {
            dohyo.ejecutarTurno(luchador1);
            if (!dohyo.isCombateTerminado()) {
                listener.onTurnoEjecutado(dohyo.getUltimoAtacante(), dohyo.getUltimaKimarite());
                dohyo.ejecutarTurno(luchador2);
                if (!dohyo.isCombateTerminado()) {
                    listener.onTurnoEjecutado(dohyo.getUltimoAtacante(), dohyo.getUltimaKimarite());
                }
            }
        }

        Rikishi ganador  = dohyo.getGanador();
        Rikishi perdedor = (ganador == luchador1) ? luchador2 : luchador1;

        controlBD.actualizarVictorias(ganador.getId(), ganador.getVictorias());
        controlBD.marcarParticipacion(luchador1.getId());
        controlBD.marcarParticipacion(luchador2.getId());

        // Datos del RAF vienen de la BD
        Rikishi ganadorBD  = controlBD.obtenerPorId(ganador.getId());
        Rikishi perdedorBD = controlBD.obtenerPorId(perdedor.getId());
        controlRAF.guardarResultado(numeroCombate, ganadorBD, perdedorBD);

        listener.onCombateTerminado(ganador.getNombre(), ganador.getPeso(), ganador.getVictorias());

        ganadorActual = ganador;
    }

    /**
     * Selecciona y elimina un luchador aleatorio de los pendientes.
     *
     * @return Rikishi seleccionado.
     */
    private Rikishi seleccionarAleatorio() {
        return pendientes.remove(random.nextInt(pendientes.size()));
    }

    /**
     * Retorna el total de combates realizados.
     *
     * @return Total de combates.
     */
    public int getTotalCombatesRealizados() { return numeroCombate; }
}
