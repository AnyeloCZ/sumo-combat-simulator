package cliente.control;

import cliente.modelo.ConexionSocket;
import cliente.modelo.PropertiesCliente;
import cliente.vista.VentanaCliente;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

/**
 * Controlador de vista del cliente de sumo.
 * Crea su propia VentanaCliente internamente.
 * Implementa ActionListener directamente.
 * Lee host y puerto desde Data/cliente.properties via PropertiesCliente.
 * El JFileChooser esta en la Vista — el control solo coordina la logica.
 * El cliente NO tiene hilos: el socket hace el envio/recepcion directo.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class ControlVista implements ActionListener {

    /** Ventana del cliente. */
    private VentanaCliente ventana;

    /** Controlador de creacion de Rikishi. */
    private servidor.control.ControlRikishi controlRikishi;

    /** Controlador de propiedades de kimarites. */
    private servidor.control.ControlProperties controlProperties;

    /** Host del servidor desde properties. */
    private String host;

    /** Puerto del servidor desde properties. */
    private int puerto;

    /**
     * Constructor: crea ventana, lee properties y registra listeners.
     */
    public ControlVista() {
        this.ventana           = new VentanaCliente();
        this.controlRikishi    = new servidor.control.ControlRikishi();
        this.controlProperties = new servidor.control.ControlProperties();
        cargarConfiguracion();
        registrarListeners();
    }

    /**
     * Carga host y puerto desde Data/cliente.properties.
     * Si falla usa valores por defecto y avisa en la vista.
     */
    private void cargarConfiguracion() {
        try {
            PropertiesCliente props = new PropertiesCliente();
            this.host   = props.getHost();
            this.puerto = props.getPuerto();
            ventana.setHost(host);
            ventana.setPuerto(String.valueOf(puerto));
        } catch (IOException e) {
            this.host   = "localhost";
            this.puerto = 9090;
            ventana.mostrarEstado("Advertencia: cliente.properties no encontrado. Usando valores por defecto.");
        }
    }

    /**
     * Hace visible la ventana.
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
     * Pide a la vista abrir el JFileChooser y devolver la ruta seleccionada,
     * luego carga los kimarites con ControlProperties.
     *
     * @param e Evento de accion.
     */
    private void performedSeleccionarProperties(ActionEvent e) {
        String ruta = ventana.abrirSelectorProperties();
        if (ruta != null) {
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
     * Valida datos, construye la cadena del luchador y la envia al servidor
     * usando ConexionSocket directamente — sin hilos.
     * Espera la respuesta bloqueante del servidor.
     *
     * @param e Evento de accion.
     */
    private void performedEnviar(ActionEvent e) {
        String nombre  = ventana.getNombre().trim();
        String pesoStr = ventana.getPeso().trim();
        List<String> kimarites = ventana.getKimaritesSeleccionados();

        if (nombre.isEmpty()) {
            ventana.mostrarEstado("Ingresa tu nombre de luchador.");
            return;
        }

        double peso;
        try {
            peso = Double.parseDouble(pesoStr);
            if (peso <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            ventana.mostrarEstado("El peso debe ser un numero positivo.");
            return;
        }

        if (kimarites == null || kimarites.isEmpty()) {
            ventana.mostrarEstado("Selecciona al menos una tecnica.");
            return;
        }

        servidor.modelo.Rikishi rikishi = controlRikishi.crearRikishi(nombre, peso);
        controlRikishi.asignarKimarites(rikishi, kimarites);
        String datos = controlRikishi.convertirACadena(rikishi);

        ventana.setBtnEnviarHabilitado(false);
        ventana.mostrarEstado("Conectando a " + host + ":" + puerto + "...");

        // El cliente NO tiene hilos propios.
        // La conexion, envio y espera se hacen directamente con ConexionSocket.
        // Esto bloquea la UI mientras espera — es el comportamiento esperado
        // ya que el cliente solo envia datos y espera el resultado.
        ConexionSocket conexion = new ConexionSocket(host, puerto);
        try {
            conexion.conectar();
            ventana.mostrarEstado("Conectado. Esperando resultado del combate...");
            conexion.enviarDatos(datos);
            String respuesta = conexion.recibirRespuesta();
            conexion.cerrar();
            ventana.mostrarResultado("GANASTE".equals(respuesta), nombre);
        } catch (IOException ex) {
            ventana.mostrarEstado("Error de conexion: " + ex.getMessage());
            ventana.setBtnEnviarHabilitado(true);
        }
    }
}
