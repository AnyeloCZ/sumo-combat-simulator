package servidor.control;

/**
 * Clase principal que inicia el servidor de sumo.
 * Únicamente instancia el ControlGeneral y llama a iniciar().
 * No contiene ninguna lógica adicional.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class LauncherServidor {

    /**
     * Punto de entrada del servidor de sumo.
     *
     * @param args Argumentos de línea de comandos (no se utilizan).
     */
    public static void main(String[] args) {
        new ControlGeneral().iniciar();
    }
}
