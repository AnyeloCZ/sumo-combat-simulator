package servidor.control;

import servidor.modelo.Rikishi;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Hilo de control que atiende a un cliente (luchador) conectado por socket.
 * Recibe datos, registra el rikishi en ControlDohyo, ejecuta turnos y notifica resultado.
 * Solo conoce a ControlDohyo, nunca al modelo Dohyo directamente.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class HiloRikishi extends Thread {

    /** Socket de conexion con el cliente. */
    private Socket socket;

    /** Controlador del dohyo con la logica del combate. */
    private ControlDohyo controlDohyo;

    /** Rikishi construido a partir de los datos recibidos. */
    private Rikishi rikishi;

    /** Flujo de entrada desde el cliente. */
    private DataInputStream entrada;

    /** Flujo de salida hacia el cliente. */
    private DataOutputStream salida;

    /** Listener que notifica eventos a la vista del servidor. */
    private IEventosCombate listener;

    /**
     * Constructor que inicializa el hilo.
     *
     * @param socket       Socket del cliente conectado.
     * @param controlDohyo Controlador del dohyo.
     * @param listener     Listener para notificar eventos a la vista.
     */
    public HiloRikishi(Socket socket, ControlDohyo controlDohyo, IEventosCombate listener) {
        this.socket       = socket;
        this.controlDohyo = controlDohyo;
        this.listener     = listener;
    }

    /**
     * Metodo principal del hilo. Recibe datos, registra, ejecuta turnos y notifica resultado.
     */
    @Override
    public void run() {
        try {
            entrada = new DataInputStream(socket.getInputStream());
            salida  = new DataOutputStream(socket.getOutputStream());

            recibirDatos();

            controlDohyo.registrarRikishi(rikishi);
            listener.onLuchadorLlego(rikishi.getNombre(), rikishi.getPeso());

            controlDohyo.esperarRival();
            listener.onCombateIniciado(
                controlDohyo.getRikishi1().getNombre(),
                controlDohyo.getRikishi2().getNombre()
            );

            while (!controlDohyo.isCombateTerminado()) {
                controlDohyo.ejecutarTurno(rikishi);
                if (!controlDohyo.isCombateTerminado()) {
                    listener.onTurnoEjecutado(
                        controlDohyo.getUltimoAtacante(),
                        controlDohyo.getUltimaKimarite()
                    );
                }
            }

            Rikishi ganador = controlDohyo.getGanador();
            boolean gane    = ganador.getNombre().equals(rikishi.getNombre());
            notificarResultado(gane ? "GANASTE" : "PERDISTE");

            listener.onCombateTerminado(
                ganador.getNombre(),
                ganador.getPeso(),
                ganador.getVictorias()
            );

            entrada.readUTF(); // esperar confirmacion de cierre
            socket.close();

        } catch (IOException | InterruptedException e) {
            listener.onError("Error en hilo de " +
                (rikishi != null ? rikishi.getNombre() : "desconocido") +
                ": " + e.getMessage());
        }
    }

    /**
     * Lee los datos del cliente y construye el Rikishi con sus kimarites como Strings.
     * Formato: "NOMBRE|PESO|kimarite1,kimarite2,..."
     *
     * @throws IOException Si ocurre un error al leer el socket.
     */
    private void recibirDatos() throws IOException {
        String linea    = entrada.readUTF();
        String[] partes = linea.split("\\|");
        String nombre   = partes[0];
        double peso     = Double.parseDouble(partes[1]);

        rikishi = new Rikishi(nombre, peso);

        if (partes.length > 2) {
            String[] tecnicas = partes[2].split(",");
            List<String> lista = new ArrayList<>();
            for (String t : tecnicas) {
                if (!t.trim().isEmpty()) lista.add(t.trim());
            }
            rikishi.setKimarites(lista);
        }
    }

    /**
     * Envia el resultado al cliente por socket.
     *
     * @param mensaje "GANASTE" o "PERDISTE".
     * @throws IOException Si ocurre un error al enviar.
     */
    public void notificarResultado(String mensaje) throws IOException {
        salida.writeUTF(mensaje);
    }

    /**
     * Obtiene el rikishi de este hilo.
     *
     * @return Objeto Rikishi del luchador.
     */
    public Rikishi getRikishi() { return rikishi; }
}
