package cliente.control;

/**
 * Controlador principal del cliente de sumo.
 * Crea el ControlVista que gestiona toda la aplicacion cliente internamente.
 * Es el punto de arranque de la cadena de control.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class ControlGeneral {

    /**
     * Inicia la aplicacion cliente creando el ControlVista
     * y haciendo visible la ventana.
     */
    public void iniciar() {
        ControlVista controlVista = new ControlVista();
        controlVista.mostrarVentana();
    }
}
