package cliente.modelo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Clase que gestiona la conexion por socket con el servidor de sumo.
 * Es la unica clase del paquete cliente.modelo.
 * Maneja la conexion, el envio y la recepcion de datos.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class ConexionSocket {

    /** Direccion IP o hostname del servidor. */
    private String host;

    /** Puerto del servidor. */
    private int puerto;

    /** Socket de conexion. */
    private Socket socket;

    /** Flujo de salida hacia el servidor. */
    private DataOutputStream salida;

    /** Flujo de entrada desde el servidor. */
    private DataInputStream entrada;

    /**
     * Constructor que configura host y puerto.
     *
     * @param host   Direccion del servidor.
     * @param puerto Puerto del servidor.
     */
    public ConexionSocket(String host, int puerto) {
        this.host   = host;
        this.puerto = puerto;
    }

    /**
     * Establece la conexion con el servidor e inicializa los flujos.
     *
     * @throws IOException Si no se puede conectar.
     */
    public void conectar() throws IOException {
        socket  = new Socket(host, puerto);
        salida  = new DataOutputStream(socket.getOutputStream());
        entrada = new DataInputStream(socket.getInputStream());
    }

    /**
     * Envia datos al servidor.
     *
     * @param datos Cadena a enviar.
     * @throws IOException Si ocurre error al enviar.
     */
    public void enviarDatos(String datos) throws IOException {
        salida.writeUTF(datos);
    }

    /**
     * Espera y recibe la respuesta del servidor (bloqueante).
     *
     * @return Respuesta del servidor.
     * @throws IOException Si ocurre error al leer.
     */
    public String recibirRespuesta() throws IOException {
        return entrada.readUTF();
    }

    /**
     * Envia senal de cierre y cierra el socket.
     *
     * @throws IOException Si ocurre error al cerrar.
     */
    public void cerrar() throws IOException {
        salida.writeUTF("CERRADO");
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    /**
     * Indica si el socket esta conectado.
     *
     * @return true si esta conectado.
     */
    public boolean isConectado() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }
}
