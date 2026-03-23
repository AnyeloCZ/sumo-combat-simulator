package cliente.modelo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Clase que gestiona la conexion por socket con el servidor de sumo.
 * Maneja la conexion, el envio y la recepcion de datos mediante
 * DataInputStream y DataOutputStream.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class ClienteSocket {

    /** Direccion IP o hostname del servidor. */
    private String host;

    /** Puerto del servidor. */
    private int puerto;

    /** Socket de conexion con el servidor. */
    private Socket socket;

    /** Flujo de salida hacia el servidor. */
    private DataOutputStream salida;

    /** Flujo de entrada desde el servidor. */
    private DataInputStream entrada;

    /**
     * Constructor que configura el host y puerto del servidor.
     *
     * @param host   Direccion del servidor.
     * @param puerto Puerto del servidor.
     */
    public ClienteSocket(String host, int puerto) {
        this.host   = host;
        this.puerto = puerto;
    }

    /**
     * Establece la conexion con el servidor e inicializa los flujos.
     *
     * @throws IOException Si no se puede conectar al servidor.
     */
    public void conectar() throws IOException {
        socket  = new Socket(host, puerto);
        salida  = new DataOutputStream(socket.getOutputStream());
        entrada = new DataInputStream(socket.getInputStream());
    }

    /**
     * Envia los datos del luchador al servidor por el socket.
     * Formato: "NOMBRE|PESO|kimarite1,kimarite2,..."
     *
     * @param datos Cadena serializada del Rikishi.
     * @throws IOException Si ocurre un error al enviar.
     */
    public void enviarDatos(String datos) throws IOException {
        salida.writeUTF(datos);
    }

    /**
     * Espera y recibe la respuesta del servidor (bloqueante).
     *
     * @return "GANASTE" o "PERDISTE".
     * @throws IOException Si ocurre un error al leer.
     */
    public String recibirRespuesta() throws IOException {
        return entrada.readUTF();
    }

    /**
     * Envia senal de cierre al servidor y cierra el socket.
     *
     * @throws IOException Si ocurre un error al cerrar.
     */
    public void cerrar() throws IOException {
        salida.writeUTF("CERRADO");
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    /**
     * Indica si el socket esta actualmente conectado.
     *
     * @return true si esta conectado.
     */
    public boolean isConectado() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }
}
