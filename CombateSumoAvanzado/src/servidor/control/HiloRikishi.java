package servidor.control;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Hilo que atiende a un cliente conectado por socket.
 * Recibe datos, los registra en BD, espera el resultado del combate
 * y lo notifica al cliente. Usa lock para sincronizacion.
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
    /** Listener de eventos. */
    private IEventosCombate listener;
    /** Nombre del luchador. */
    private String nombre;
    /** Peso del luchador. */
    private double peso;
    /** Kimarites del luchador. */
    private List<String> kimarites;
    /** Controlador general. */
    private ControlGeneral controlGeneral;
    /** Lock para esperar el resultado. */
    private final Object lock = new Object();
    /** Resultado: GANASTE o PERDISTE. */
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
     * Flujo completo: recibe datos, registra en BD,
     * espera resultado y notifica al cliente.
     */
    @Override
    public void run() {
        try {
            entrada = new DataInputStream(socket.getInputStream());
            salida  = new DataOutputStream(socket.getOutputStream());

            // 1. Recibir datos
            String linea    = entrada.readUTF();
            String[] partes = linea.split("\\|");
            nombre          = partes[0].trim();
            peso            = Double.parseDouble(partes[1].trim());
            kimarites       = new ArrayList<>();

            if (partes.length > 2) {
                for (String k : partes[2].split(",")) {
                    if (!k.trim().isEmpty()) kimarites.add(k.trim());
                }
            }

            // 2. Registrar en BD
            boolean registrado = false;
            try {
                registrado = controlBD.registrarLuchador(nombre, peso, kimarites);
            } catch (Exception e) {
                System.err.println("Error BD al registrar " + nombre + ": " + e.getMessage());
            }

            if (!registrado) {
                System.err.println("No se pudo registrar " + nombre + " en la BD.");
            }

            // 3. Notificar llegada a la vista
            listener.onLuchadorLlego(nombre, peso);

            // 4. Notificar al ControlGeneral (siempre, aunque BD falle)
            controlGeneral.luchadorRegistrado(this);

            // 5. Esperar resultado del combate
            synchronized (lock) {
                while (resultado == null) {
                    lock.wait();
                }
            }

            // 6. Enviar resultado al cliente
            salida.writeUTF(resultado);

            // 7. Esperar confirmacion de cierre
            try {
                entrada.readUTF();
            } catch (IOException e) {
                // cliente cerro sin confirmar
            }
            socket.close();

        } catch (IOException | InterruptedException e) {
            System.err.println("Error hilo " +
                (nombre != null ? nombre : "desconocido") + ": " + e.getMessage());
        }
    }

    /**
     * Desbloquea el hilo con el resultado del combate.
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

    /**
     * Obtiene los kimarites del luchador.
     *
     * @return Lista de tecnicas.
     */
    public List<String> getKimarites() { return kimarites; }

    /**
     * Obtiene el peso del luchador.
     *
     * @return Peso en kg.
     */
    public double getPeso() { return peso; }
}
