package servidor.control;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Hilo que acepta conexiones de clientes continuamente
 * hasta que haya suficientes luchadores registrados.
 * Genera un HiloRikishi por cada cliente conectado.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class HiloCombate extends Thread {

    /** Puerto del servidor. */
    private int puerto;
    /** Controlador de BD. */
    private ControlBD controlBD;
    /** Controlador de vista. */
    private ControlVista controlVista;
    /** Controlador general. */
    private ControlGeneral controlGeneral;

    /**
     * Constructor del hilo de aceptacion de conexiones.
     * @param puerto         Puerto del servidor.
     * @param controlBD      Controlador de BD.
     * @param controlVista   Controlador de vista.
     * @param controlGeneral Controlador principal.
     */
    public HiloCombate(int puerto, ControlBD controlBD,
                        ControlVista controlVista, ControlGeneral controlGeneral) {
        this.puerto         = puerto;
        this.controlBD      = controlBD;
        this.controlVista   = controlVista;
        this.controlGeneral = controlGeneral;
    }

    /**
     * Acepta conexiones de clientes generando un HiloRikishi por cada uno.
     * El servidor sigue aceptando hasta que ControlGeneral detenga el ServerSocket.
     */
    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(puerto);
            controlGeneral.setServerSocket(serverSocket);
            controlVista.mostrarMensaje("Servidor escuchando en puerto " + puerto + "...");

            IEventosCombate listener = controlVista.getListener();

            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                controlVista.mostrarMensaje("Nuevo cliente conectado.");
                HiloRikishi hilo = new HiloRikishi(socket, controlBD, listener, controlGeneral);
                hilo.start();
            }

        } catch (IOException e) {
            if (!e.getMessage().contains("closed")) {
                controlVista.mostrarMensaje("Error del servidor: " + e.getMessage());
            }
        }
    }
}
