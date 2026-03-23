package servidor.control;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Hilo que atiende a un cliente conectado por socket.
 * Recibe los datos del luchador, los registra en BD y queda
 * bloqueado esperando que ControlGeneral le notifique el resultado
 * via notificarResultado(). Luego espera confirmacion de cierre.
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
    /** Nombre del luchador. */
    private String nombre;
    /** Controlador general. */
    private ControlGeneral controlGeneral;
    /** Objeto de sincronizacion para esperar el resultado. */
    private final Object lock = new Object();
    /** Resultado del combate: GANASTE o PERDISTE. */
    private String resultado;

    /**
     * Constructor del hilo.
     *
     * @param socket         Socket del cliente.
     * @param controlBD      Controlador de BD.
     * @param listener       Listener de eventos.
     * @param controlGeneral Controlador principal.
     */
    public HiloRikishi(Socket socket, ControlBD controlBD,
                        IEventosCombate listener, ControlGeneral controlGeneral) {
        this.socket         = socket;
        this.controlBD      = controlBD;
        this.listener       = listener;
        this.controlGeneral = controlGeneral;
    }

    /**
     * Recibe datos del cliente, registra en BD, espera el resultado
     * y lo envia de vuelta al cliente. Luego espera confirmacion de cierre.
     */
    @Override
    public void run() {
        try {
            entrada = new DataInputStream(socket.getInputStream());
            salida  = new DataOutputStream(socket.getOutputStream());

            // 1. Recibir datos del luchador
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

            // 2. Registrar en BD
            controlBD.registrarLuchador(nombre, peso, kimarites);
            listener.onLuchadorLlego(nombre, peso);

            // 3. Notificar al ControlGeneral
            controlGeneral.luchadorRegistrado(this);

            // 4. Esperar resultado — bloqueado aqui hasta que
            //    ControlGeneral llame a notificarResultado()
            synchronized (lock) {
                while (resultado == null) {
                    lock.wait();
                }
            }

            // 5. Enviar resultado al cliente
            salida.writeUTF(resultado);

            // 6. Esperar confirmacion de cierre del cliente
            try {
                entrada.readUTF(); // lee "CERRADO"
            } catch (IOException e) {
                // cliente cerro sin confirmar, ignorar
            }

            socket.close();

        } catch (IOException | InterruptedException e) {
            listener.onError("Error en hilo de " +
                (nombre != null ? nombre : "desconocido") + ": " + e.getMessage());
        }
    }

    /**
     * Llamado por ControlGeneral para notificar el resultado al hilo.
     * Desbloquea el wait() interno.
     *
     * @param resultado "GANASTE" o "PERDISTE".
     */
    public void notificarResultado(String resultado) {
        synchronized (lock) {
            this.resultado = resultado;
            lock.notifyAll();
        }
    }

    /**
     * Obtiene el nombre del luchador.
     *
     * @return Nombre.
     */
    public String getNombreLuchador() { return nombre; }
}
