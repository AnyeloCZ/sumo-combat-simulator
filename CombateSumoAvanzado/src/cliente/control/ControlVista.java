package cliente.control;

import cliente.modelo.ConexionSocket;
import cliente.vista.VentanaCliente;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Controlador de vista del cliente de sumo.
 * Crea su propia VentanaCliente internamente.
 * Implementa ActionListener directamente.
 * Lee host y puerto directamente desde Data/cliente.properties en el control.
 * El JFileChooser esta en la Vista — el control solo coordina la logica.
 * El cliente NO tiene hilos: el socket hace el envio/recepcion directo.
 * Formato de envio al servidor: "nombre|peso|tecnica1,tecnica2,..."
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class ControlVista implements ActionListener {

    /** Ventana del cliente. */
    private VentanaCliente ventana;

    /** Host del servidor desde properties. */
    private String host;

    /** Puerto del servidor desde properties. */
    private int puerto;

    /**
     * Constructor: crea ventana, lee properties y registra listeners.
     */
    public ControlVista() {
        this.ventana = new VentanaCliente();
        cargarConfiguracion();
        registrarListeners();
    }

    /**
     * Carga host y puerto leyendo directamente Data/cliente.properties.
     * Si falla usa valores por defecto sin mostrar advertencia.
     */
    private void cargarConfiguracion() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("Data/cliente.properties")) {
            props.load(fis);
            this.host   = props.getProperty("servidor.host", "localhost");
            this.puerto = Integer.parseInt(props.getProperty("servidor.puerto", "9090"));
        } catch (IOException e) {
            this.host   = "localhost";
            this.puerto = 9090;
        }
        ventana.setHost(host);
        ventana.setPuerto(String.valueOf(puerto));
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
     * luego carga los kimarites leyendo el .properties directamente en el cliente.
     *
     * @param e Evento de accion.
     */
    private void performedSeleccionarProperties(ActionEvent e) {
        String ruta = ventana.abrirSelectorProperties();
        if (ruta != null) {
            try {
                List<String> kimarites = cargarKimaritesDesdeArchivo(ruta);
                ventana.cargarKimarites(kimarites);
                ventana.mostrarEstado("Tecnicas cargadas: " + kimarites.size());
            } catch (IOException ex) {
                ventana.mostrarEstado("Error al leer el archivo: " + ex.getMessage());
            }
        }
    }

    /**
     * Lee un archivo .properties y retorna los valores como lista de kimarites.
     * Lee usando las claves kimarite.1, kimarite.2, etc. segun kimarite.total.
     * Si no tiene kimarite.total toma todos los valores del archivo.
     *
     * @param ruta Ruta absoluta del archivo .properties.
     * @return Lista de nombres de kimarites.
     * @throws IOException Si el archivo no se puede leer.
     */
    private List<String> cargarKimaritesDesdeArchivo(String ruta) throws IOException {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(ruta)) {
            props.load(fis);
        }
        List<String> kimarites = new ArrayList<>();
        String totalStr = props.getProperty("kimarite.total");
        if (totalStr != null) {
            // Formato con kimarite.total, kimarite.1, kimarite.2...
            int total = Integer.parseInt(totalStr.trim());
            for (int i = 1; i <= total; i++) {
                String k = props.getProperty("kimarite." + i);
                if (k != null && !k.trim().isEmpty()) kimarites.add(k.trim());
            }
        } else {
            // Formato generico: tomar todos los valores
            for (String key : props.stringPropertyNames()) {
                String valor = props.getProperty(key).trim();
                if (!valor.isEmpty()) kimarites.add(valor);
            }
        }
        return kimarites;
    }

    /**
     * Valida datos, construye la cadena del luchador con formato pipe
     * y la envia al servidor usando ConexionSocket directamente.
     * Formato enviado: "nombre|peso|tecnica1,tecnica2,..."
     * El servidor en HiloRikishi espera exactamente ese formato con split("|").
     * Usa finally para garantizar el cierre del socket.
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

        // Formato esperado por HiloRikishi: "nombre|peso|tecnica1,tecnica2,..."
        String datos = nombre + "|" + peso + "|" + String.join(",", kimarites);

        ventana.setBtnEnviarHabilitado(false);
        ventana.mostrarEstado("Conectando a " + host + ":" + puerto + "...");

        ConexionSocket conexion = new ConexionSocket(host, puerto);
        try {
            conexion.conectar();
            ventana.mostrarEstado("Conectado. Esperando resultado del combate...");
            conexion.enviarDatos(datos);
            String respuesta = conexion.recibirRespuesta();
            ventana.mostrarResultado("GANASTE".equals(respuesta), nombre);
        } catch (IOException ex) {
            ventana.mostrarEstado("Error de conexion: " + ex.getMessage());
            ventana.setBtnEnviarHabilitado(true);
        } finally {
            // Garantiza cierre del socket aunque haya error
            try {
                if (conexion.isConectado()) conexion.cerrar();
            } catch (IOException ignore) {}
        }
    }
}