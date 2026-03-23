package servidor.control;

import servidor.vista.VentanaServidor;

import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controlador de vista del servidor de sumo.
 * Crea su propia VentanaServidor internamente.
 * Implementa ActionListener e IEventosCombate directamente.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class ControlVista implements ActionListener, IEventosCombate {

    /** Ventana del servidor creada internamente. */
    private VentanaServidor ventana;

    /** Referencia al controlador general para iniciar el combate. */
    private ControlGeneral controlGeneral;

    /**
     * Constructor que recibe el ControlGeneral, crea la ventana y registra listeners.
     *
     * @param controlGeneral Controlador principal del servidor.
     */
    public ControlVista(ControlGeneral controlGeneral) {
        this.controlGeneral = controlGeneral;
        this.ventana        = new VentanaServidor();
        registrarListeners();
    }

    /**
     * Hace visible la ventana del servidor.
     */
    public void mostrarVentana() {
        ventana.setVisible(true);
    }

    /**
     * Registra este controlador como ActionListener de los botones de la ventana.
     */
    private void registrarListeners() {
        ventana.getBtnIniciar().addActionListener(this);
        ventana.getBtnLimpiar().addActionListener(this);
    }

    /**
     * Despacha los eventos segun la fuente del boton.
     *
     * @param e Evento de accion recibido.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ventana.getBtnIniciar()) {
            performedIniciar(e);
        } else if (e.getSource() == ventana.getBtnLimpiar()) {
            performedLimpiar(e);
        }
    }

    // ======================== PERFORMED ========================

    /**
     * Inicia el servidor con el puerto ingresado por el usuario.
     * El boton se deshabilita para que no se pueda iniciar dos veces.
     *
     * @param e Evento de accion.
     */
    private void performedIniciar(ActionEvent e) {
        int puerto = validarPuerto();
        if (puerto == -1) return;
        ventana.getBtnIniciar().setEnabled(false);
        controlGeneral.iniciarCombate(puerto);
    }

    /**
     * Limpia el log del servidor.
     *
     * @param e Evento de accion.
     */
    private void performedLimpiar(ActionEvent e) {
        ventana.limpiar();
    }

    /**
     * Valida el puerto ingresado por el usuario.
     *
     * @return Puerto valido como entero, o -1 si es invalido.
     */
    private int validarPuerto() {
        String puertoStr = ventana.getPuerto().trim();
        try {
            int puerto = Integer.parseInt(puertoStr);
            if (puerto <= 0 || puerto > 65535) throw new NumberFormatException();
            return puerto;
        } catch (NumberFormatException ex) {
            ventana.agregarLog("Puerto invalido. Debe ser un numero entre 1 y 65535.");
            return -1;
        }
    }

    // ======================== EVENTOS DEL COMBATE ========================

    /** {@inheritDoc} */
    @Override
    public void onLuchadorLlego(String nombre, double peso) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ventana.mostrarLlegada(nombre, peso);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onCombateIniciado(String nombre1, String nombre2) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ventana.mostrarCombateIniciado(nombre1, nombre2);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onTurnoEjecutado(String atacante, String kimarite) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ventana.mostrarTurno(atacante, kimarite);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onCombateTerminado(String ganador, double peso, int victorias) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ventana.mostrarGanador(ganador, peso, victorias);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onError(String mensaje) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ventana.mostrarError(mensaje);
            }
        });
    }

    /**
     * Muestra un mensaje general en el log de la ventana.
     *
     * @param mensaje Texto a mostrar.
     */
    public void mostrarMensaje(String mensaje) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ventana.agregarLog(mensaje);
            }
        });
    }

    /**
     * Retorna este controlador como listener de eventos del combate.
     *
     * @return Esta instancia que implementa IEventosCombate.
     */
    public IEventosCombate getListener() {
        return this;
    }
}
