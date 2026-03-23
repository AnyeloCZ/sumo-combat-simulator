package cliente.control;

/**
 * Clase principal que inicia el cliente de sumo.
 * Unicamente instancia el ControlGeneral y llama a iniciar().
 * No contiene ninguna logica adicional.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class LauncherCliente {

    /**
     * Punto de entrada del cliente de sumo.
     *
     * @param args Argumentos de linea de comandos (no se utilizan).
     */
    public static void main(String[] args) {
        new ControlGeneral().iniciar();
    }
}
