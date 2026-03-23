package servidor.control;

/**
 * Controlador principal del servidor de sumo.
 * Orquesta el ControlVista y el HiloCombate.
 * El servidor acepta exactamente dos conexiones, ejecuta el combate y termina.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class ControlGeneral {

    /** Controlador de vista. */
    private ControlVista controlVista;

    /**
     * Inicia la aplicacion servidor creando el ControlVista.
     */
    public void iniciar() {
        controlVista = new ControlVista(this);
        controlVista.mostrarVentana();
    }

    /**
     * Arranca el combate en el puerto indicado.
     * Crea un ControlDohyo y lanza el HiloCombate.
     * Solo se llama una vez — el servidor no admite mas combates.
     *
     * @param puerto Puerto en el que escuchara el servidor.
     */
    public void iniciarCombate(int puerto) {
        ControlDohyo controlDohyo = new ControlDohyo();
        HiloCombate hiloCombate   = new HiloCombate(puerto, controlDohyo, controlVista);
        hiloCombate.start();
    }
}
