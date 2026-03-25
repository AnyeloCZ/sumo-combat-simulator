package servidor.control;

import servidor.modelo.Rikishi;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador principal del servidor de sumo.
 * Implementa IInicioServidor para que ControlVista pueda comunicarse
 * sin crear una dependencia ciclica directa.
 * Acepta conexiones con el ServerSocket y lanza un HiloRikishi por cliente.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class ControlGeneral implements ControlVista.IInicioServidor {

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
    /** ServerSocket activo. */
    private ServerSocket serverSocket;
    /** Hilos de luchadores registrados. */
    private List<HiloRikishi> hilosRegistrados;
    /** Total de luchadores registrados. */
    private int totalRegistrados;
    /** Evita iniciar combates dos veces. */
    private boolean combatesIniciados;

    /**
     * Inicia la aplicacion servidor creando el ControlVista.
     * ControlVista recibe this como IInicioServidor — sin acoplamiento directo.
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
     * Implementacion de IInicioServidor.
     * Abre el ServerSocket y lanza el HiloEscucha.
     *
     * @param puerto Puerto a escuchar.
     */
    @Override
    public void iniciarServidor(int puerto) {
        try {
            controlCombates = new ControlCombates(controlBD, controlRAF, controlVista.getListener());
            HiloEscucha hiloEscucha = new HiloEscucha(puerto);
            hiloEscucha.start();
        } catch (RuntimeException e) {
            controlVista.mostrarMensaje("Error al iniciar servidor: " + e.getMessage());
        }
    }

    /**
     * Llamado por cada HiloRikishi al registrar su luchador.
     * Al llegar al minimo inicia los combates.
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
     * Cierra el ServerSocket.
     */
    private void cerrarServerSocket() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            controlVista.mostrarMensaje("Error cerrando servidor: " + e.getMessage());
        }
    }

    /**
     * Ejecuta los combates en un hilo separado.
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
                } catch (RuntimeException e) {
                    controlVista.mostrarMensaje("Error en combates: " + e.getMessage());
                }
            }
        }).start();
    }

    /**
     * Notifica el resultado a cada cliente.
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
     * Hilo que acepta conexiones entrantes y lanza un HiloRikishi por cliente.
     */
    private class HiloEscucha extends Thread {

        /** Puerto del servidor. */
        private int puerto;

        /**
         * Constructor.
         *
         * @param puerto Puerto a escuchar.
         */
        public HiloEscucha(int puerto) {
            this.puerto = puerto;
        }

        /**
         * Abre el ServerSocket, acepta clientes y lanza HiloRikishi por cada uno.
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
                    controlVista.mostrarMensaje("Error servidor: " + e.getMessage());
                }
            }
        }
    }
}
