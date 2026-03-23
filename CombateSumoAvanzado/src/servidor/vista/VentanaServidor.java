package servidor.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

/**
 * Ventana principal del servidor de sumo.
 * Muestra el dohyo, los luchadores, el log del combate y los controles del servidor.
 * Incluye campo de puerto, boton iniciar y boton luchar de nuevo.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class VentanaServidor extends JFrame {

    // ==================== COLORES ====================
    /** Color de fondo principal. */
    private static final Color COLOR_FONDO    = new Color(15, 10, 10);
    /** Color rojo carmes. */
    private static final Color COLOR_ROJO     = new Color(180, 20, 20);
    /** Color dorado. */
    private static final Color COLOR_DORADO   = new Color(220, 170, 50);
    /** Color texto. */
    private static final Color COLOR_TEXTO    = new Color(240, 230, 210);
    /** Color panel. */
    private static final Color COLOR_PANEL    = new Color(30, 22, 22);
    /** Color tecnica. */
    private static final Color COLOR_TECNICA  = new Color(100, 180, 220);
    /** Color victoria. */
    private static final Color COLOR_VICTORIA = new Color(80, 200, 100);

    // ==================== COMPONENTES ====================
    /** Panel del dohyo animado. */
    private PanelDohyo panelDohyo;
    /** Label de estado. */
    private JLabel lblEstado;
    /** Label del ganador. */
    private JLabel lblGanador;
    /** Area de log. */
    private JTextPane areaLog;
    /** Labels de luchadores. */
    private JLabel lblNombre1, lblNombre2, lblPeso1, lblPeso2;
    /** Campo de puerto. */
    private JTextField txtPuerto;
    /** Boton iniciar servidor. */
    private JButton btnIniciar;
    /** Boton limpiar log. */
    private JButton btnLimpiar;
    /** Contador de turnos. */
    private int turnoActual = 0;

    /**
     * Constructor que construye la ventana del servidor.
     */
    public VentanaServidor() {
        inicializarComponentes();
    }

    /**
     * Inicializa y organiza todos los componentes de la ventana.
     */
    private void inicializarComponentes() {
        setTitle("DOHYO — Servidor de Combate de Sumo");
        setSize(920, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(COLOR_FONDO);
        setLayout(new BorderLayout(10, 10));

        add(crearPanelTitulo(), BorderLayout.NORTH);
        add(crearPanelCentral(), BorderLayout.CENTER);
        add(crearPanelSur(), BorderLayout.SOUTH);
    }

    /**
     * Crea el panel de titulo con campo de puerto y botones de control.
     *
     * @return Panel titulo.
     */
    private JPanel crearPanelTitulo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_ROJO);
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel lblTitulo = new JLabel("SUMO — Servidor", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Serif", Font.BOLD, 24));
        lblTitulo.setForeground(COLOR_DORADO);

        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelControles.setOpaque(false);

        JLabel lblPuertoLbl = new JLabel("Puerto:");
        lblPuertoLbl.setForeground(COLOR_TEXTO);
        lblPuertoLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));

        txtPuerto = new JTextField("9090", 6);
        txtPuerto.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtPuerto.setBackground(new Color(35, 22, 22));
        txtPuerto.setForeground(COLOR_TEXTO);
        txtPuerto.setCaretColor(COLOR_DORADO);

        btnIniciar = crearBoton("▶ Iniciar", new Color(50, 130, 50));
        btnLimpiar = crearBoton("Limpiar", new Color(100, 60, 20));

        panelControles.add(lblPuertoLbl);
        panelControles.add(txtPuerto);
        panelControles.add(btnIniciar);
        panelControles.add(btnLimpiar);

        lblEstado = new JLabel("Presiona Iniciar para arrancar el servidor.", SwingConstants.CENTER);
        lblEstado.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblEstado.setForeground(COLOR_TEXTO);

        panel.add(lblTitulo, BorderLayout.WEST);
        panel.add(panelControles, BorderLayout.EAST);
        panel.add(lblEstado, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Crea un boton con estilo para el panel de control.
     *
     * @param texto Texto del boton.
     * @param color Color de fondo.
     * @return JButton configurado.
     */
    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 11));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * Crea el panel central con el dohyo, informacion de luchadores y log.
     *
     * @return Panel central.
     */
    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(new EmptyBorder(8, 12, 8, 12));

        JPanel panelArena = new JPanel(new BorderLayout(8, 0));
        panelArena.setBackground(COLOR_FONDO);

        panelArena.add(crearPanelInfoLuchador("LUCHADOR 1", true), BorderLayout.WEST);
        panelDohyo = new PanelDohyo();
        panelDohyo.setPreferredSize(new Dimension(320, 270));
        panelArena.add(panelDohyo, BorderLayout.CENTER);
        panelArena.add(crearPanelInfoLuchador("LUCHADOR 2", false), BorderLayout.EAST);

        areaLog = new JTextPane();
        areaLog.setEditable(false);
        areaLog.setBackground(new Color(20, 14, 14));
        areaLog.setForeground(COLOR_TEXTO);
        areaLog.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scroll = new JScrollPane(areaLog);
        scroll.setPreferredSize(new Dimension(0, 210));
        scroll.setBorder(BorderFactory.createLineBorder(COLOR_ROJO, 1));
        scroll.getViewport().setBackground(new Color(20, 14, 14));

        panel.add(panelArena, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Crea el panel de informacion de un luchador.
     *
     * @param titulo  Titulo del panel.
     * @param esUno   true si es el luchador 1.
     * @return Panel de informacion.
     */
    private JPanel crearPanelInfoLuchador(String titulo, boolean esUno) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_ROJO, 2),
            new EmptyBorder(10, 15, 10, 15)
        ));
        panel.setPreferredSize(new Dimension(190, 270));

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Serif", Font.BOLD, 13));
        lblTitulo.setForeground(COLOR_DORADO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblNombreVal = new JLabel("---", SwingConstants.CENTER);
        lblNombreVal.setFont(new Font("Serif", Font.BOLD, 15));
        lblNombreVal.setForeground(COLOR_TEXTO);
        lblNombreVal.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblPesoVal = new JLabel("--- kg", SwingConstants.CENTER);
        lblPesoVal.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblPesoVal.setForeground(COLOR_TEXTO);
        lblPesoVal.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalStrut(5));
        panel.add(lblTitulo);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lblNombreVal);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblPesoVal);

        if (esUno) {
            lblNombre1 = lblNombreVal;
            lblPeso1   = lblPesoVal;
        } else {
            lblNombre2 = lblNombreVal;
            lblPeso2   = lblPesoVal;
        }

        return panel;
    }

    /**
     * Crea el panel inferior con el label del ganador.
     *
     * @return Panel sur.
     */
    private JPanel crearPanelSur() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(new EmptyBorder(0, 12, 10, 12));

        lblGanador = new JLabel("", SwingConstants.CENTER);
        lblGanador.setFont(new Font("Serif", Font.BOLD, 18));
        lblGanador.setForeground(COLOR_DORADO);

        panel.add(lblGanador, BorderLayout.CENTER);
        return panel;
    }

    // ==================== METODOS PUBLICOS ====================

    /**
     * Muestra la llegada de un luchador al servidor.
     *
     * @param nombre Nombre del luchador.
     * @param peso   Peso del luchador.
     */
    public void mostrarLlegada(String nombre, double peso) {
        if ("---".equals(lblNombre1.getText())) {
            lblNombre1.setText(nombre);
            lblPeso1.setText(peso + " kg");
        } else {
            lblNombre2.setText(nombre);
            lblPeso2.setText(peso + " kg");
        }
        agregarLog("Luchador " + nombre + " (" + peso + " kg) ha llegado al Dohyo.");
        lblEstado.setText("Esperando al segundo luchador...");
        panelDohyo.repaint();
    }

    /**
     * Muestra el inicio del combate.
     *
     * @param nombre1 Nombre del primer luchador.
     * @param nombre2 Nombre del segundo luchador.
     */
    public void mostrarCombateIniciado(String nombre1, String nombre2) {
        agregarLog("TACHI-AI! Combate entre " + nombre1 + " y " + nombre2 + " ha comenzado.");
        lblEstado.setText("COMBATE EN CURSO");
        panelDohyo.setCombateActivo(true);
        panelDohyo.repaint();
    }

    /**
     * Muestra el resultado de un turno.
     *
     * @param atacante Nombre del atacante.
     * @param kimarite Tecnica utilizada.
     */
    public void mostrarTurno(String atacante, String kimarite) {
        turnoActual++;
        agregarLogColoreado("[Turno " + turnoActual + "] " + atacante + " ejecuta: ",
                            COLOR_TEXTO, kimarite, COLOR_TECNICA);
        panelDohyo.setUltimaKimarite(kimarite);
        panelDohyo.repaint();
    }

    /**
     * Muestra el ganador del combate.
     *
     * @param ganador   Nombre del ganador.
     * @param peso      Peso del ganador.
     * @param victorias Victorias acumuladas.
     */
    public void mostrarGanador(String ganador, double peso, int victorias) {
        agregarLog("GANADOR: " + ganador + " — Victorias: " + victorias);
        lblGanador.setText("GANADOR: " + ganador.toUpperCase() + " — Victorias: " + victorias);
        lblEstado.setText("Combate finalizado.");
        panelDohyo.setGanador(ganador);
        panelDohyo.repaint();
    }

    /**
     * Muestra un error en el log.
     *
     * @param mensaje Mensaje de error.
     */
    public void mostrarError(String mensaje) {
        agregarLog("ERROR: " + mensaje);
    }

    /**
     * Agrega un mensaje al log del combate.
     *
     * @param mensaje Texto a agregar.
     */
    public void agregarLog(String mensaje) {
        StyledDocument doc = areaLog.getStyledDocument();
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setForeground(attrs, COLOR_TEXTO);
        try {
            doc.insertString(doc.getLength(), mensaje + "\n", attrs);
            areaLog.setCaretPosition(doc.getLength());
        } catch (Exception e) {
            // Silenciado en vista
        }
    }

    /**
     * Agrega al log texto en dos colores.
     *
     * @param texto1 Primera parte.
     * @param color1 Color de la primera parte.
     * @param texto2 Segunda parte.
     * @param color2 Color de la segunda parte.
     */
    private void agregarLogColoreado(String texto1, Color color1, String texto2, Color color2) {
        StyledDocument doc = areaLog.getStyledDocument();
        SimpleAttributeSet a1 = new SimpleAttributeSet();
        StyleConstants.setForeground(a1, color1);
        SimpleAttributeSet a2 = new SimpleAttributeSet();
        StyleConstants.setForeground(a2, color2);
        StyleConstants.setBold(a2, true);
        try {
            doc.insertString(doc.getLength(), texto1, a1);
            doc.insertString(doc.getLength(), texto2 + "\n", a2);
            areaLog.setCaretPosition(doc.getLength());
        } catch (Exception e) {
            // Silenciado en vista
        }
    }

    /**
     * Limpia el log y reinicia la informacion de luchadores.
     */
    public void limpiar() {
        areaLog.setText("");
        turnoActual = 0;
        lblGanador.setText("");
        lblEstado.setText("Listo para nuevo combate.");
        lblNombre1.setText("---");
        lblNombre2.setText("---");
        lblPeso1.setText("--- kg");
        lblPeso2.setText("--- kg");
        panelDohyo.setCombateActivo(false);
        panelDohyo.setGanador(null);
        panelDohyo.repaint();
    }

    /**
     * Obtiene el texto del campo puerto.
     *
     * @return Puerto ingresado por el usuario.
     */
    public String getPuerto() { return txtPuerto.getText(); }

    /**
     * Obtiene el boton iniciar.
     *
     * @return Boton iniciar.
     */
    public JButton getBtnIniciar() { return btnIniciar; }

    /**
     * Obtiene el boton limpiar.
     *
     * @return Boton limpiar.
     */
    public JButton getBtnLimpiar() { return btnLimpiar; }
}
