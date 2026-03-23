package cliente.control;

import cliente.modelo.ConexionSocket;

import javax.swing.SwingUtilities;
import java.io.IOException;

/**
 * Hilo que gestiona la conexion con el servidor desde el cliente.
 * Usa ConexionSocket (unica clase de cliente.modelo).
 * Envia datos del luchador y espera el resultado del combate.
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
     * @param controlVista Controlador de vista.
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
     * Conecta al servidor, envia datos y espera el resultado.
     */
    @Override
    public void run() {
        ConexionSocket conexion = new ConexionSocket(host, puerto);
        try {
            conexion.conectar();
            actualizarEstado("Conectado. Esperando resultado...");
            conexion.enviarDatos(datos);
            String respuesta = conexion.recibirRespuesta();
            conexion.cerrar();
            mostrarResultado("GANASTE".equals(respuesta));
        } catch (IOException ex) {
            actualizarEstado("Error de conexion: " + ex.getMessage());
            habilitarEnviar();
        }
    }

    /**
     * Actualiza el estado en el hilo de Swing.
     *
     * @param mensaje Mensaje a mostrar.
     */
    private void actualizarEstado(String mensaje) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() { controlVista.notificarEstado(mensaje); }
        });
    }

    /**
     * Muestra el resultado en el hilo de Swing.
     *
     * @param gano true si gano.
     */
    private void mostrarResultado(boolean gano) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() { controlVista.notificarResultado(gano, nombre); }
        });
    }

    /**
     * Habilita el boton enviar en caso de error.
     */
    private void habilitarEnviar() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() { controlVista.habilitarEnviar(); }
        });
    }
}
