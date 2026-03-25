package servidor.control;

import servidor.modelo.Rikishi;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador principal del servidor de sumo.
 * Es quien acepta conexiones con el ServerSocket y lanza un HiloRikishi
 * por cada cliente conectado. No delega esta responsabilidad a ningun hilo externo.
 * Al llegar al minimo de luchadores cierra el ServerSocket e inicia los combates.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class ControlGeneral {

    /** Minimo de luchadores requeridos. */
    private static final int MIN_LUCHADORES = 6;

    /** Controlador de vista. */
    private ControlVista controlVista;
    /** Controlador de BD. */
    private ControlBD controlBD;
    /** Controlador RAF. */
    private ControlRAF controlRAF;
    /** Controlador de combates. */
    private ControlCombates controlCombates;
    /** ServerSocket del servidor. */
    private ServerSocket serverSocket;
    /** Hilos de luchadores registrados. */
    private List<HiloRikishi> hilosRegistrados;
    /** Total de luchadores registrados. */
    private int totalRegistrados;
    /** Evita iniciar combates dos veces. */
    private boolean combatesIniciados;

    /**
     * Inicia la aplicacion servidor creando el ControlVista.
     */
    public void iniciar() {
        controlVista      = new ControlVista(this);
        controlBD         = new ControlBD();
        controlRAF        = new ControlRAF();
        hilosRegistrados  = new ArrayList<>();
        totalRegistrados  = 0;
        combatesIniciados = false;
        controlVista.mostrarVentana();
    }

    /**
     * Arranca el servidor: abre el ServerSocket y acepta clientes en un hilo
     * de escucha dedicado. Por cada cliente aceptado lanza un HiloRikishi.
     *
     * @param puerto Puerto en el que escuchara el servidor.
     */
    public void iniciarServidor(int puerto) {
        controlCombates = new ControlCombates(controlBD, controlRAF, controlVista.getListener());
        HiloEscucha hiloEscucha = new HiloEscucha(puerto);
        hiloEscucha.start();
    }

    /**
     * Llamado por cada HiloRikishi cuando registra su luchador.
     * Al llegar al minimo cierra el ServerSocket e inicia combates.
     *
     * @param hilo HiloRikishi del luchador registrado.
     */
    public synchronized void luchadorRegistrado(HiloRikishi hilo) {
        hilosRegistrados.add(hilo);
        totalRegistrados++;
        controlVista.mostrarMensaje(
            "Luchador \"" + hilo.getNombreLuchador() + "\" registrado. " +
            "Total: " + totalRegistrados + "/" + MIN_LUCHADORES
        );
        controlVista.actualizarContador(totalRegistrados, MIN_LUCHADORES);

        if (totalRegistrados >= MIN_LUCHADORES && !combatesIniciados) {
            combatesIniciados = true;
            cerrarServerSocket();
            iniciarCombatesEnHilo();
        }
    }

    /**
     * Cierra el ServerSocket para no aceptar mas conexiones.
     */
    private void cerrarServerSocket() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error cerrando ServerSocket: " + e.getMessage());
        }
    }

    /**
     * Ejecuta los tres combates en un hilo separado para no bloquear.
     */
    private void iniciarCombatesEnHilo() {
        controlVista.mostrarMensaje("6 luchadores registrados. Iniciando combates...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    controlCombates.cargarLuchadores();
                    while (controlCombates.hayCombatesPendientes()) {
                        controlCombates.ejecutarSiguienteCombate();
                    }
                    notificarResultadosAClientes();
                    String raf = controlRAF.leerResultados(
                        controlCombates.getTotalCombatesRealizados()
                    );
                    System.out.println(raf);
                    controlVista.mostrarMensaje("Fin. Resultados en consola.");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    /**
     * Notifica el resultado a cada HiloRikishi segun victorias en BD.
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

    // ======================== HILO DE ESCUCHA ========================

    /**
     * Hilo que abre el ServerSocket y acepta conexiones entrantes.
     * Por cada cliente aceptado lanza un HiloRikishi para atenderlo.
     * Este es el UNICO punto donde el servidor acepta clientes.
     */
    private class HiloEscucha extends Thread {

        /** Puerto del servidor. */
        private int puerto;

        /**
         * Constructor del hilo de escucha.
         *
         * @param puerto Puerto a escuchar.
         */
        public HiloEscucha(int puerto) {
            this.puerto = puerto;
        }

        /**
         * Abre el ServerSocket y lanza un HiloRikishi por cada cliente conectado.
         * Se detiene cuando el ServerSocket es cerrado por ControlGeneral.
         */
        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(puerto);
                controlVista.mostrarMensaje("Servidor escuchando en puerto " + puerto + "...");
                IEventosCombate listener = controlVista.getListener();

                while (!serverSocket.isClosed()) {
                    Socket socket = serverSocket.accept();
                    controlVista.mostrarMensaje("Nuevo cliente conectado.");
                    HiloRikishi hilo = new HiloRikishi(
                        socket, controlBD, listener, ControlGeneral.this
                    );
                    hilo.start();
                }
            } catch (IOException e) {
                if (!e.getMessage().contains("closed")) {
                    System.err.println("Error servidor: " + e.getMessage());
                }
            }
        }
    }
}
