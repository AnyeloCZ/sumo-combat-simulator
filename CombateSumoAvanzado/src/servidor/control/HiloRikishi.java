package servidor.control;

import servidor.modelo.Rikishi;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Hilo que atiende a un cliente conectado por socket.
 * Recibe datos, los registra en BD via ControlBD y queda
 * esperando la notificacion del resultado del combate.
 * Solo conoce a ControlBD y al listener de eventos.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class HiloRikishi extends Thread {

    /** Socket del cliente. */
    private Socket socket;
    /** Controlador de BD. */
    private ControlBD controlBD;
    /** Flujo de entrada. */
    private DataInputStream entrada;
    /** Flujo de salida. */
    private DataOutputStream salida;
    /** Listener de eventos para la vista. */
    private IEventosCombate listener;
    /** Nombre del luchador recibido. */
    private String nombre;
    /** Controlador general para notificar registro. */
    private ControlGeneral controlGeneral;

    /**
     * Constructor del hilo.
     * @param socket         Socket del cliente.
     * @param controlBD      Controlador de BD.
     * @param listener       Listener de eventos.
     * @param controlGeneral Controlador principal para notificar registro.
     */
    public HiloRikishi(Socket socket, ControlBD controlBD,
                        IEventosCombate listener, ControlGeneral controlGeneral) {
        this.socket         = socket;
        this.controlBD      = controlBD;
        this.listener       = listener;
        this.controlGeneral = controlGeneral;
    }

    /**
     * Recibe datos del cliente, registra en BD y espera resultado.
     */
    @Override
    public void run() {
        try {
            entrada = new DataInputStream(socket.getInputStream());
            salida  = new DataOutputStream(socket.getOutputStream());

            // Recibir datos del luchador
            String linea    = entrada.readUTF();
            String[] partes = linea.split("\\|");
            nombre          = partes[0];
            double peso     = Double.parseDouble(partes[1]);

            List<String> kimarites = new ArrayList<>();
            if (partes.length > 2) {
                for (String k : partes[2].split(",")) {
                    if (!k.trim().isEmpty()) kimarites.add(k.trim());
                }
            }

            // Registrar en BD
            controlBD.registrarLuchador(nombre, peso, kimarites);
            listener.onLuchadorLlego(nombre, peso);

            // Notificar al ControlGeneral que hay un nuevo luchador registrado
            controlGeneral.luchadorRegistrado(this);

            // Esperar resultado (bloqueante hasta que el combate termine)
            // La respuesta la envia ControlGeneral cuando conoce el resultado
            entrada.readUTF(); // esperar confirmacion de cierre del cliente
            socket.close();

        } catch (IOException e) {
            listener.onError("Error en hilo de " + nombre + ": " + e.getMessage());
        }
    }

    /**
     * Envia el resultado al cliente.
     * @param resultado "GANASTE" o "PERDISTE".
     * @throws IOException Si ocurre error al enviar.
     */
    public void notificarResultado(String resultado) throws IOException {
        salida.writeUTF(resultado);
    }

    /**
     * Obtiene el nombre del luchador.
     * @return Nombre.
     */
    public String getNombreLuchador() { return nombre; }
}
