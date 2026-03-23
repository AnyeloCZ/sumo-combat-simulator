package servidor.control;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Hilo que gestiona la aceptacion de conexiones y el ciclo del combate.
 * Espera exactamente dos conexiones, ejecuta el combate y al finalizar
 * espera que ambos clientes confirmen el cierre antes de terminar el servidor.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class HiloCombate extends Thread {

    /** Puerto en el que escuchara el servidor. */
    private int puerto;

    /** Controlador del dohyo con la logica del combate. */
    private ControlDohyo controlDohyo;

    /** Controlador de vista para notificar eventos. */
    private ControlVista controlVista;

    /**
     * Constructor del hilo de combate.
     *
     * @param puerto       Puerto del servidor.
     * @param controlDohyo Controlador del dohyo.
     * @param controlVista Controlador de vista para notificar eventos.
     */
    public HiloCombate(int puerto, ControlDohyo controlDohyo, ControlVista controlVista) {
        this.puerto       = puerto;
        this.controlDohyo = controlDohyo;
        this.controlVista = controlVista;
    }

    /**
     * Acepta exactamente dos conexiones, lanza los HiloRikishi,
     * espera que el combate termine y que ambos clientes confirmen cierre.
     * Al finalizar, el servidor muestra el resultado y termina su ejecucion.
     */
    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(puerto);
            controlVista.mostrarMensaje("Servidor escuchando en puerto " + puerto + "...");

            IEventosCombate listener = controlVista.getListener();

            Socket socket1 = serverSocket.accept();
            controlVista.mostrarMensaje("Luchador 1 conectado.");
            HiloRikishi hilo1 = new HiloRikishi(socket1, controlDohyo, listener);

            Socket socket2 = serverSocket.accept();
            controlVista.mostrarMensaje("Luchador 2 conectado.");
            HiloRikishi hilo2 = new HiloRikishi(socket2, controlDohyo, listener);

            // Cerrar inmediatamente: no se aceptan mas conexiones
            serverSocket.close();

            hilo1.start();
            hilo2.start();

            // Esperar a que ambos hilos terminen
            // (cada hilo espera confirmacion de cierre del cliente antes de morir)
            hilo1.join();
            hilo2.join();

            controlVista.mostrarMensaje("Ambos clientes han cerrado. Servidor finalizado.");

        } catch (IOException | InterruptedException e) {
            controlVista.mostrarMensaje("Error del servidor: " + e.getMessage());
        }
    }
}
