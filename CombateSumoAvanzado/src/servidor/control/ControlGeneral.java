package servidor.control;

import servidor.modelo.Rikishi;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador principal del servidor del parcial.
 * Orquesta el registro de luchadores, el inicio de combates
 * y el cierre ordenado al terminar.
 * Minimo 6 luchadores registrados antes de iniciar los combates.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class ControlGeneral {

    /** Minimo de luchadores requeridos para iniciar combates. */
    private static final int MIN_LUCHADORES = 6;

    /** Controlador de vista. */
    private ControlVista controlVista;
    /** Controlador de BD. */
    private ControlBD controlBD;
    /** Controlador del RAF. */
    private ControlRAF controlRAF;
    /** Controlador de combates. */
    private ControlCombates controlCombates;
    /** ServerSocket para cerrar cuando se alcancen los 6 luchadores. */
    private ServerSocket serverSocket;
    /** Hilos de luchadores registrados (para notificarles el resultado). */
    private List<HiloRikishi> hilosRegistrados;
    /** Contador de luchadores registrados. */
    private int totalRegistrados;

    /**
     * Inicia la aplicacion servidor creando el ControlVista.
     */
    public void iniciar() {
        controlVista       = new ControlVista(this);
        controlBD          = new ControlBD();
        controlRAF         = new ControlRAF();
        hilosRegistrados   = new ArrayList<>();
        totalRegistrados   = 0;
        controlVista.mostrarVentana();
    }

    /**
     * Arranca el servidor en el puerto indicado.
     * @param puerto Puerto del servidor.
     */
    public void iniciarServidor(int puerto) {
        controlCombates = new ControlCombates(controlBD, controlRAF, controlVista.getListener());
        HiloCombate hiloCombate = new HiloCombate(puerto, controlBD, controlVista, this);
        hiloCombate.start();
    }

    /**
     * Llamado por cada HiloRikishi cuando un luchador se registra en la BD.
     * Cuando se alcanzan MIN_LUCHADORES, cierra el ServerSocket e inicia combates.
     * @param hilo HiloRikishi del luchador registrado.
     */
    public synchronized void luchadorRegistrado(HiloRikishi hilo) {
        hilosRegistrados.add(hilo);
        totalRegistrados++;
        controlVista.mostrarMensaje("Luchadores registrados: " + totalRegistrados + "/" + MIN_LUCHADORES);

        if (totalRegistrados >= MIN_LUCHADORES) {
            cerrarServerSocket();
            iniciarCombates();
        }
    }

    /**
     * Cierra el ServerSocket para dejar de aceptar nuevas conexiones.
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
     * Asigna el ServerSocket para poder cerrarlo cuando sea necesario.
     * @param serverSocket ServerSocket activo.
     */
    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    /**
     * Ejecuta los tres combates en secuencia y al finalizar
     * notifica a todos los clientes y muestra el RAF.
     */
    private void iniciarCombates() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    controlCombates.cargarLuchadores();

                    while (controlCombates.hayCombatesPendientes()) {
                        controlCombates.ejecutarSiguienteCombate();
                    }

                    // Notificar resultado a cada cliente
                    List<Rikishi> todos = controlBD.obtenerTodos();
                    for (HiloRikishi hilo : hilosRegistrados) {
                        String nombre = hilo.getNombreLuchador();
                        boolean gano  = todos.stream()
                            .anyMatch(r -> r.getNombre().equals(nombre) && r.getVictorias() > 0);
                        try {
                            hilo.notificarResultado(gano ? "GANASTE" : "PERDISTE");
                        } catch (IOException e) {
                            System.err.println("Error notificando a " + nombre);
                        }
                    }

                    // Mostrar RAF por consola cuando todos los clientes terminen
                    String contenidoRAF = controlRAF.leerResultados(
                        controlCombates.getTotalCombatesRealizados()
                    );
                    System.out.println(contenidoRAF);
                    controlVista.mostrarMensaje("Todos los combates finalizados. Ver consola para resultados.");

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }
}
