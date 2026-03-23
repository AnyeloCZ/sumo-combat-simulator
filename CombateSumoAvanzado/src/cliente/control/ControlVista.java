package cliente.control;

import cliente.modelo.ConexionSocket;
import cliente.vista.VentanaCliente;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

/**
 * Controlador de vista del cliente de sumo.
 * Crea su propia VentanaCliente internamente.
 * Implementa ActionListener directamente.
 * Usa ConexionSocket (unica clase de cliente.modelo) para comunicarse.
 * ControlRikishi y ControlProperties estan en servidor.control pero
 * se instancian aqui para preparar los datos antes de enviarlos.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class ControlVista implements ActionListener {

    /** Ventana del cliente. */
    private VentanaCliente ventana;

    /** Controlador de creacion de Rikishi. */
    private servidor.control.ControlRikishi controlRikishi;

    /** Controlador de propiedades. */
    private servidor.control.ControlProperties controlProperties;

    /**
     * Constructor que crea la ventana y registra listeners.
     */
    public ControlVista() {
        this.ventana            = new VentanaCliente();
        this.controlRikishi     = new servidor.control.ControlRikishi();
        this.controlProperties  = new servidor.control.ControlProperties();
        registrarListeners();
    }

    /**
     * Hace visible la ventana del cliente.
     */
    public void mostrarVentana() {
        ventana.setVisible(true);
    }

    /**
     * Registra este controlador como listener de los botones.
     */
    private void registrarListeners() {
        ventana.getBtnSeleccionarProperties().addActionListener(this);
        ventana.getBtnEnviar().addActionListener(this);
    }

    /**
     * Despacha eventos segun el boton presionado.
     *
     * @param e Evento de accion.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ventana.getBtnSeleccionarProperties()) {
            performedSeleccionarProperties(e);
        } else if (e.getSource() == ventana.getBtnEnviar()) {
            performedEnviar(e);
        }
    }

    /**
     * Abre JFileChooser para seleccionar el .properties y carga los kimarites.
     *
     * @param e Evento de accion.
     */
    private void performedSeleccionarProperties(ActionEvent e) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Seleccionar archivo de kimarites (.properties)");
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Archivos de propiedades (*.properties)", "properties"
        ));
        if (fc.showOpenDialog(ventana) == JFileChooser.APPROVE_OPTION) {
            String ruta = fc.getSelectedFile().getAbsolutePath();
            try {
                List<String> kimarites = controlProperties.cargarKimarites(ruta);
                ventana.cargarKimarites(kimarites);
                ventana.mostrarEstado("Tecnicas cargadas: " + kimarites.size());
            } catch (IOException ex) {
                ventana.mostrarEstado("Error al leer el archivo: " + ex.getMessage());
            }
        }
    }

    /**
     * Valida datos, crea el Rikishi y lanza HiloConexionCliente.
     *
     * @param e Evento de accion.
     */
    private void performedEnviar(ActionEvent e) {
        String nombre    = ventana.getNombre().trim();
        String pesoStr   = ventana.getPeso().trim();
        String host      = ventana.getHost().trim();
        String puertoStr = ventana.getPuerto().trim();
        List<String> kimarites = ventana.getKimaritesSeleccionados();

        if (nombre.isEmpty()) { ventana.mostrarEstado("Ingresa tu nombre."); return; }

        double peso;
        try {
            peso = Double.parseDouble(pesoStr);
            if (peso <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            ventana.mostrarEstado("El peso debe ser un numero positivo.");
            return;
        }

        if (host.isEmpty()) { ventana.mostrarEstado("Ingresa el host."); return; }

        int puerto;
        try {
            puerto = Integer.parseInt(puertoStr);
            if (puerto <= 0 || puerto > 65535) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            ventana.mostrarEstado("Puerto invalido (1-65535).");
            return;
        }

        if (kimarites == null || kimarites.isEmpty()) {
            ventana.mostrarEstado("Selecciona al menos una tecnica.");
            return;
        }

        servidor.modelo.Rikishi rikishi = controlRikishi.crearRikishi(nombre, peso);
        controlRikishi.asignarKimarites(rikishi, kimarites);
        String datos = controlRikishi.serializarRikishi(rikishi);

        ventana.setBtnEnviarHabilitado(false);
        ventana.mostrarEstado("Conectando a " + host + ":" + puerto + "...");

        HiloConexionCliente hilo = new HiloConexionCliente(datos, host, puerto, nombre, this);
        hilo.start();
    }

    /**
     * Notifica el resultado del combate en la vista.
     *
     * @param gano   true si gano.
     * @param nombre Nombre del luchador.
     */
    public void notificarResultado(boolean gano, String nombre) {
        ventana.mostrarResultado(gano, nombre);
    }

    /**
     * Actualiza el estado en la vista.
     *
     * @param mensaje Mensaje a mostrar.
     */
    public void notificarEstado(String mensaje) {
        ventana.mostrarEstado(mensaje);
    }

    /**
     * Habilita el boton enviar.
     */
    public void habilitarEnviar() {
        ventana.setBtnEnviarHabilitado(true);
    }
}
