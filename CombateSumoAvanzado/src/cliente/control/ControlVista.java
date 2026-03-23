package cliente.control;

import cliente.modelo.Rikishi;
import cliente.vista.VentanaCliente;

import javax.swing.JFileChooser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

/**
 * Controlador de vista del cliente de sumo.
 * Crea su propia VentanaCliente internamente.
 * Implementa ActionListener directamente para manejar todos los eventos.
 * Coordina ControlRikishi, ControlProperties y HiloConexionCliente.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class ControlVista implements ActionListener {

    /** Ventana del cliente creada internamente. */
    private VentanaCliente ventana;

    /** Controlador de creacion de Rikishi. */
    private ControlRikishi controlRikishi;

    /** Controlador de carga de propiedades. */
    private ControlProperties controlProperties;

    /**
     * Constructor que crea la ventana internamente y registra los listeners.
     */
    public ControlVista() {
        this.ventana           = new VentanaCliente();
        this.controlRikishi    = new ControlRikishi();
        this.controlProperties = new ControlProperties();
        registrarListeners();
    }

    /**
     * Hace visible la ventana del cliente.
     */
    public void mostrarVentana() {
        ventana.setVisible(true);
    }

    /**
     * Registra este controlador como ActionListener de los botones de la ventana.
     */
    private void registrarListeners() {
        ventana.getBtnSeleccionarProperties().addActionListener(this);
        ventana.getBtnEnviar().addActionListener(this);
    }

    /**
     * Despacha los eventos segun la fuente del boton.
     * Implementacion del metodo de ActionListener.
     *
     * @param e Evento de accion recibido.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ventana.getBtnSeleccionarProperties()) {
            performedSeleccionarProperties(e);
        } else if (e.getSource() == ventana.getBtnEnviar()) {
            performedEnviar(e);
        }
    }

    // ======================== PERFORMED ========================

    /**
     * Abre JFileChooser para seleccionar el archivo .properties y carga los kimarites.
     *
     * @param e Evento de accion.
     */
    private void performedSeleccionarProperties(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar archivo de kimarites (.properties)");
        fileChooser.setFileFilter(
            new javax.swing.filechooser.FileNameExtensionFilter(
                "Archivos de propiedades (*.properties)", "properties"
            )
        );

        int resultado = fileChooser.showOpenDialog(ventana);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            String ruta = fileChooser.getSelectedFile().getAbsolutePath();
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
     * Valida los datos del formulario, crea el Rikishi y lanza el HiloConexionCliente.
     * El HOST y PUERTO son tomados de los campos ingresados por el usuario.
     *
     * @param e Evento de accion.
     */
    private void performedEnviar(ActionEvent e) {
        String nombre    = ventana.getNombre().trim();
        String pesoStr   = ventana.getPeso().trim();
        String host      = ventana.getHost().trim();
        String puertoStr = ventana.getPuerto().trim();
        List<String> kimaritesSeleccionados = ventana.getKimaritesSeleccionados();

        if (nombre.isEmpty()) {
            ventana.mostrarEstado("Ingresa tu nombre de luchador.");
            return;
        }

        double peso;
        try {
            peso = Double.parseDouble(pesoStr);
            if (peso <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            ventana.mostrarEstado("El peso debe ser un numero positivo valido.");
            return;
        }

        if (host.isEmpty()) {
            ventana.mostrarEstado("Ingresa la direccion del servidor.");
            return;
        }

        int puerto;
        try {
            puerto = Integer.parseInt(puertoStr);
            if (puerto <= 0 || puerto > 65535) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            ventana.mostrarEstado("El puerto debe ser un numero entre 1 y 65535.");
            return;
        }

        if (kimaritesSeleccionados == null || kimaritesSeleccionados.isEmpty()) {
            ventana.mostrarEstado("Selecciona al menos una tecnica.");
            return;
        }

        Rikishi rikishi = controlRikishi.crearRikishi(nombre, peso);
        controlRikishi.asignarKimarites(rikishi, kimaritesSeleccionados);
        String datos = controlRikishi.serializarRikishi(rikishi);

        ventana.setBtnEnviarHabilitado(false);
        ventana.mostrarEstado("Conectando a " + host + ":" + puerto + "...");

        HiloConexionCliente hilo = new HiloConexionCliente(datos, host, puerto, nombre, this);
        hilo.start();
    }

    // ======================== METODOS DE NOTIFICACION ========================

    /**
     * Muestra el resultado final del combate en la ventana.
     * Llamado desde HiloConexionCliente via SwingUtilities.
     *
     * @param gano   true si el luchador gano.
     * @param nombre Nombre del luchador.
     */
    public void notificarResultado(boolean gano, String nombre) {
        ventana.mostrarResultado(gano, nombre);
    }

    /**
     * Actualiza el estado en la ventana.
     * Llamado desde HiloConexionCliente via SwingUtilities.
     *
     * @param mensaje Mensaje a mostrar.
     */
    public void notificarEstado(String mensaje) {
        ventana.mostrarEstado(mensaje);
    }

    /**
     * Habilita el boton enviar.
     * Llamado desde HiloConexionCliente en caso de error de conexion.
     */
    public void habilitarEnviar() {
        ventana.setBtnEnviarHabilitado(true);
    }
}
