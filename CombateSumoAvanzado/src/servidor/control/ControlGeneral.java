package servidor.control;

import servidor.modelo.Rikishi;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador principal del servidor del parcial.
 * Registra luchadores hasta llegar al minimo requerido,
 * luego cierra el ServerSocket e inicia los 3 combates.
 * Al terminar notifica a cada cliente y muestra el RAF.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class ControlGeneral {

    /** Minimo de luchadores para iniciar combates. */
    private static final int MIN_LUCHADORES = 6;

    /** Controlador de vista. */
    private ControlVista controlVista;
    /** Controlador de BD. */
    private ControlBD controlBD;
    /** Controlador del RAF. */
    private ControlRAF controlRAF;
    /** Controlador de combates. */
    private ControlCombates controlCombates;
    /** ServerSocket activo. */
    private ServerSocket serverSocket;
    /** Hilos de luchadores registrados. */
    private List<HiloRikishi> hilosRegistrados;
    /** Total de luchadores registrados. */
    private int totalRegistrados;
    /** Bandera para no iniciar combates dos veces. */
    private boolean combatesIniciados;

    /**
     * Inicia la aplicacion servidor creando el ControlVista.
     */
    public void iniciar() {
        controlVista     = new ControlVista(this);
        controlBD        = new ControlBD();
        controlRAF       = new ControlRAF();
        hilosRegistrados = new ArrayList<>();
        totalRegistrados = 0;
        combatesIniciados = false;
        controlVista.mostrarVentana();
    }

    /**
     * Arranca el servidor en el puerto indicado.
     *
     * @param puerto Puerto del servidor.
     */
    public void iniciarServidor(int puerto) {
        controlCombates = new ControlCombates(controlBD, controlRAF, controlVista.getListener());
        HiloCombate hiloCombate = new HiloCombate(puerto, controlBD, controlVista, this);
        hiloCombate.start();
    }

    /**
     * Llamado por cada HiloRikishi cuando un luchador se registra en BD.
     * Cuando se alcanzan MIN_LUCHADORES, cierra ServerSocket e inicia combates.
     *
     * @param hilo HiloRikishi del luchador registrado.
     */
    public synchronized void luchadorRegistrado(HiloRikishi hilo) {
        hilosRegistrados.add(hilo);
        totalRegistrados++;
        controlVista.mostrarMensaje("Luchadores: " + totalRegistrados + "/" + MIN_LUCHADORES);

        if (totalRegistrados >= MIN_LUCHADORES && !combatesIniciados) {
            combatesIniciados = true;
            cerrarServerSocket();
            iniciarCombatesEnHilo();
        }
    }

    /**
     * Cierra el ServerSocket para no aceptar mas conexiones.
     */
    public void cerrarServerSocket() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error cerrando ServerSocket: " + e.getMessage());
        }
    }

    /**
     * Asigna el ServerSocket activo.
     *
     * @param serverSocket ServerSocket del servidor.
     */
    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    /**
     * Lanza los combates en un hilo separado para no bloquear.
     */
    private void iniciarCombatesEnHilo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    controlCombates.cargarLuchadores();
                    controlVista.mostrarMensaje("Iniciando combates...");

                    while (controlCombates.hayCombatesPendientes()) {
                        controlCombates.ejecutarSiguienteCombate();
                    }

                    // Notificar resultado a cada cliente
                    notificarResultadosAClientes();

                    // Mostrar RAF por consola
                    String raf = controlRAF.leerResultados(
                        controlCombates.getTotalCombatesRealizados()
                    );
                    System.out.println(raf);
                    controlVista.mostrarMensaje("Fin. Ver consola para resultados del RAF.");

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    /**
     * Notifica a cada HiloRikishi si su luchador gano o perdio.
     * Determina el resultado consultando victorias en la BD.
     */
    private void notificarResultadosAClientes() {
        List<Rikishi> todos = controlBD.obtenerTodos();
        for (HiloRikishi hilo : hilosRegistrados) {
            String nombre = hilo.getNombreLuchador();
            boolean gano  = todos.stream().anyMatch(r ->
                r.getNombre().equals(nombre) && r.getVictorias() > 0
            );
            hilo.notificarResultado(gano ? "GANASTE" : "PERDISTE");
        }
    }
}
