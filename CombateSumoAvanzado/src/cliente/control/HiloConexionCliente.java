package cliente.control;

import cliente.modelo.ClienteSocket;

import javax.swing.SwingUtilities;
import java.io.IOException;

/**
 * Hilo que gestiona la conexion con el servidor de sumo desde el cliente.
 * Envia los datos del luchador, espera el resultado y actualiza la vista.
 * Separa la logica de red del hilo principal de la interfaz grafica.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class HiloConexionCliente extends Thread {

    /** Datos serializados del luchador. */
    private String datos;

    /** Host del servidor. */
    private String host;

    /** Puerto del servidor. */
    private int puerto;

    /** Nombre del luchador. */
    private String nombre;

    /** Controlador de vista para notificar resultados. */
    private ControlVista controlVista;

    /**
     * Constructor del hilo de conexion.
     *
     * @param datos        Datos serializados del Rikishi.
     * @param host         Host del servidor.
     * @param puerto       Puerto del servidor.
     * @param nombre       Nombre del luchador.
     * @param controlVista Controlador de vista para notificar resultados.
     */
    public HiloConexionCliente(String datos, String host, int puerto,
                                String nombre, ControlVista controlVista) {
        this.datos        = datos;
        this.host         = host;
        this.puerto       = puerto;
        this.nombre       = nombre;
        this.controlVista = controlVista;
    }

    /**
     * Ejecuta la conexion al servidor, envia los datos del luchador
     * y espera la respuesta. Actualiza la vista en el hilo de Swing.
     */
    @Override
    public void run() {
        ClienteSocket clienteSocket = new ClienteSocket(host, puerto);
        try {
            clienteSocket.conectar();
            actualizarEstado("Conectado. Esperando rival y resultado...");
            clienteSocket.enviarDatos(datos);
            String respuesta = clienteSocket.recibirRespuesta();
            clienteSocket.cerrar();
            mostrarResultadoEnSwing("GANASTE".equals(respuesta));
        } catch (IOException ex) {
            actualizarEstado("Error de conexion: " + ex.getMessage());
            habilitarEnviarEnSwing();
        }
    }

    /**
     * Actualiza el estado de la vista en el hilo de Swing.
     *
     * @param mensaje Mensaje a mostrar en la vista.
     */
    private void actualizarEstado(String mensaje) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                controlVista.notificarEstado(mensaje);
            }
        });
    }

    /**
     * Muestra el resultado final del combate en el hilo de Swing.
     *
     * @param gano true si el luchador gano el combate.
     */
    private void mostrarResultadoEnSwing(boolean gano) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                controlVista.notificarResultado(gano, nombre);
            }
        });
    }

    /**
     * Habilita el boton enviar en el hilo de Swing.
     */
    private void habilitarEnviarEnSwing() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                controlVista.habilitarEnviar();
            }
        });
    }
}
