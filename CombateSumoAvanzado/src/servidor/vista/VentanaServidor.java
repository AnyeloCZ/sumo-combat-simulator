package servidor.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

/**
 * Ventana principal del servidor de sumo.
 * Muestra bienvenida, contador de luchadores registrados,
 * el dohyo, informacion de combatientes y el log del combate.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class VentanaServidor extends JFrame {

    // ==================== COLORES ====================
    private static final Color COLOR_FONDO    = new Color(15, 10, 10);
    private static final Color COLOR_ROJO     = new Color(180, 20, 20);
    private static final Color COLOR_DORADO   = new Color(220, 170, 50);
    private static final Color COLOR_TEXTO    = new Color(240, 230, 210);
    private static final Color COLOR_PANEL    = new Color(30, 22, 22);
    private static final Color COLOR_TECNICA  = new Color(100, 180, 220);
    private static final Color COLOR_VERDE    = new Color(80, 200, 100);
    private static final Color COLOR_NARANJA  = new Color(220, 140, 40);

    // ==================== COMPONENTES ====================
    /** Panel animado del dohyo. */
    private PanelDohyo panelDohyo;
    /** Estado general del servidor. */
    private JLabel lblEstado;
    /** Ganador del combate. */
    private JLabel lblGanador;
    /** Area de log coloreado. */
    private JTextPane areaLog;
    /** Labels de luchadores en combate. */
    private JLabel lblNombre1, lblNombre2, lblPeso1, lblPeso2;
    /** Campo de puerto. */
    private JTextField txtPuerto;
    /** Boton iniciar. */
    private JButton btnIniciar;
    /** Boton limpiar. */
    private JButton btnLimpiar;
    /** Label contador de luchadores registrados. */
    private JLabel lblContador;
    /** Label de bienvenida con instrucciones. */
    private JLabel lblBienvenida;
    /** Contador de turnos del combate actual. */
    private int turnoActual = 0;
    /** Total de luchadores registrados hasta ahora. */
    private int totalRegistrados = 0;

    /**
     * Constructor que inicializa la ventana del servidor.
     */
    public VentanaServidor() {
        inicializarComponentes();
    }

    /**
     * Inicializa y organiza todos los componentes.
     */
    private void inicializarComponentes() {
        setTitle("相撲 DOHYO — Servidor de Combate de Sumo");
        setSize(960, 780);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(COLOR_FONDO);
        setLayout(new BorderLayout(0, 0));

        add(crearPanelTitulo(),    BorderLayout.NORTH);
        add(crearPanelCentral(),   BorderLayout.CENTER);
        add(crearPanelSur(),       BorderLayout.SOUTH);
    }

    /**
     * Crea el panel superior con titulo, bienvenida, contador y controles.
     *
     * @return Panel titulo.
     */
    private JPanel crearPanelTitulo() {
        JPanel panelOuter = new JPanel(new BorderLayout());
        panelOuter.setBackground(COLOR_FONDO);

        // --- Franja roja con titulo ---
        JPanel panelRojo = new JPanel(new BorderLayout());
        panelRojo.setBackground(COLOR_ROJO);
        panelRojo.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel lblTitulo = new JLabel("相撲  SUMO — Servidor", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Serif", Font.BOLD, 26));
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

        btnIniciar = crearBoton("▶ Iniciar Servidor", new Color(50, 130, 50));
        btnLimpiar = crearBoton("Limpiar Log", new Color(100, 60, 20));

        panelControles.add(lblPuertoLbl);
        panelControles.add(txtPuerto);
        panelControles.add(btnIniciar);
        panelControles.add(btnLimpiar);

        panelRojo.add(lblTitulo,      BorderLayout.CENTER);
        panelRojo.add(panelControles, BorderLayout.EAST);

        // --- Panel bienvenida + contador ---
        JPanel panelInfo = new JPanel(new BorderLayout());
        panelInfo.setBackground(new Color(245, 235, 210));
        panelInfo.setBorder(new EmptyBorder(8, 20, 8, 20));

        lblBienvenida = new JLabel(
            "<html><center>⛩&nbsp;&nbsp;<b>Bienvenido al Servidor de Combate de Sumo</b>&nbsp;&nbsp;⛩" +
            "<br><i style='font-size:11px;'>Se necesitan mínimo <b>6 rikishi</b> registrados para iniciar los combates</i></center></html>",
            SwingConstants.CENTER
        );
        lblBienvenida.setFont(new Font("Serif", Font.PLAIN, 13));
        lblBienvenida.setForeground(new Color(80, 30, 10));

        lblContador = new JLabel(
            "🥋 Rikishi registrados: 0 / 6  —  Esperando luchadores...",
            SwingConstants.CENTER
        );
        lblContador.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblContador.setForeground(new Color(140, 60, 0));

        lblEstado = new JLabel(
            "Presiona ▶ Iniciar Servidor y luego conecta los clientes.",
            SwingConstants.CENTER
        );
        lblEstado.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblEstado.setForeground(new Color(60, 30, 10));

        JPanel panelLabels = new JPanel(new GridLayout(3, 1, 0, 2));
        panelLabels.setOpaque(false);
        panelLabels.add(lblBienvenida);
        panelLabels.add(lblContador);
        panelLabels.add(lblEstado);

        panelInfo.add(panelLabels, BorderLayout.CENTER);

        panelOuter.add(panelRojo, BorderLayout.NORTH);
        panelOuter.add(panelInfo, BorderLayout.CENTER);

        return panelOuter;
    }

    /**
     * Crea el panel central con el dohyo y el log.
     *
     * @return Panel central.
     */
    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(new EmptyBorder(8, 12, 8, 12));

        // Arena: luchador1 | dohyo | luchador2
        JPanel panelArena = new JPanel(new BorderLayout(8, 0));
        panelArena.setBackground(COLOR_FONDO);
        panelArena.add(crearPanelInfoLuchador("COMBATIENTE 1", true),  BorderLayout.WEST);

        panelDohyo = new PanelDohyo();
        panelDohyo.setPreferredSize(new Dimension(340, 270));
        panelArena.add(panelDohyo, BorderLayout.CENTER);

        panelArena.add(crearPanelInfoLuchador("COMBATIENTE 2", false), BorderLayout.EAST);

        // Log
        areaLog = new JTextPane();
        areaLog.setEditable(false);
        areaLog.setBackground(new Color(20, 14, 14));
        areaLog.setForeground(COLOR_TEXTO);
        areaLog.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scroll = new JScrollPane(areaLog);
        scroll.setPreferredSize(new Dimension(0, 200));
        scroll.setBorder(BorderFactory.createLineBorder(COLOR_ROJO, 1));
        scroll.getViewport().setBackground(new Color(20, 14, 14));

        JLabel lblLogTitulo = new JLabel("  📜 Registro del combate:", SwingConstants.LEFT);
        lblLogTitulo.setForeground(COLOR_DORADO);
        lblLogTitulo.setFont(new Font("Serif", Font.BOLD, 12));
        lblLogTitulo.setBackground(new Color(25, 15, 15));
        lblLogTitulo.setOpaque(true);

        JPanel panelLog = new JPanel(new BorderLayout());
        panelLog.setBackground(COLOR_FONDO);
        panelLog.add(lblLogTitulo, BorderLayout.NORTH);
        panelLog.add(scroll,       BorderLayout.CENTER);

        panel.add(panelArena, BorderLayout.NORTH);
        panel.add(panelLog,   BorderLayout.CENTER);

        return panel;
    }

    /**
     * Crea el panel de info de un luchador.
     *
     * @param titulo Titulo del panel.
     * @param esUno  true si es el primero.
     * @return Panel de informacion.
     */
    private JPanel crearPanelInfoLuchador(String titulo, boolean esUno) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_ROJO, 2),
            new EmptyBorder(10, 12, 10, 12)
        ));
        panel.setPreferredSize(new Dimension(185, 270));

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Serif", Font.BOLD, 12));
        lblTitulo.setForeground(COLOR_DORADO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sep = new JLabel("───────────", SwingConstants.CENTER);
        sep.setForeground(COLOR_ROJO);
        sep.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblNombreVal = new JLabel("---", SwingConstants.CENTER);
        lblNombreVal.setFont(new Font("Serif", Font.BOLD, 14));
        lblNombreVal.setForeground(COLOR_TEXTO);
        lblNombreVal.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblPesoLbl = new JLabel("Peso:", SwingConstants.CENTER);
        lblPesoLbl.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblPesoLbl.setForeground(COLOR_NARANJA);
        lblPesoLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblPesoVal = new JLabel("--- kg", SwingConstants.CENTER);
        lblPesoVal.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblPesoVal.setForeground(COLOR_TEXTO);
        lblPesoVal.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalStrut(5));
        panel.add(lblTitulo);
        panel.add(sep);
        panel.add(Box.createVerticalStrut(8));
        panel.add(lblNombreVal);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblPesoLbl);
        panel.add(lblPesoVal);
        panel.add(Box.createVerticalGlue());

        if (esUno) { lblNombre1 = lblNombreVal; lblPeso1 = lblPesoVal; }
        else        { lblNombre2 = lblNombreVal; lblPeso2 = lblPesoVal; }

        return panel;
    }

    /**
     * Crea el panel inferior con el label del ganador.
     *
     * @return Panel sur.
     */
    private JPanel crearPanelSur() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(20, 12, 12));
        panel.setBorder(new EmptyBorder(6, 20, 8, 20));

        lblGanador = new JLabel("", SwingConstants.CENTER);
        lblGanador.setFont(new Font("Serif", Font.BOLD, 20));
        lblGanador.setForeground(COLOR_DORADO);

        panel.add(lblGanador, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Crea un boton con estilo personalizado.
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

    // ==================== METODOS PUBLICOS ====================

    /**
     * Actualiza el contador de luchadores registrados.
     * Cambia el color a verde cuando se alcanza el minimo.
     *
     * @param registrados Cantidad actual de luchadores registrados.
     * @param minimo      Minimo requerido para iniciar.
     */
    public void actualizarContador(int registrados, int minimo) {
        String texto = "🥋 Rikishi registrados: " + registrados + " / " + minimo;
        if (registrados >= minimo) {
            texto += "  ✅ ¡Suficientes luchadores! Iniciando combates...";
            lblContador.setForeground(new Color(0, 100, 30));
        } else {
            int faltan = minimo - registrados;
            texto += "  — Faltan " + faltan + " luchador" + (faltan == 1 ? "" : "es");
            lblContador.setForeground(new Color(140, 60, 0));
        }
        lblContador.setText(texto);
    }

    /**
     * Muestra la llegada de un luchador al servidor.
     *
     * @param nombre Nombre del luchador.
     * @param peso   Peso del luchador.
     */
    public void mostrarLlegada(String nombre, double peso) {
        totalRegistrados++;
        if ("---".equals(lblNombre1.getText())) {
            lblNombre1.setText(nombre);
            lblPeso1.setText(peso + " kg");
        } else {
            lblNombre2.setText(nombre);
            lblPeso2.setText(peso + " kg");
        }
        agregarLog("⛩  " + nombre + " (" + peso + " kg) ha llegado al Dohyo.");
        lblEstado.setText("Luchador " + nombre + " registrado. Esperando más aspirantes...");
    }

    /**
     * Muestra el inicio de un combate.
     *
     * @param nombre1 Primer luchador.
     * @param nombre2 Segundo luchador.
     */
    public void mostrarCombateIniciado(String nombre1, String nombre2) {
        turnoActual = 0;
        lblNombre1.setText(nombre1);
        lblNombre2.setText(nombre2);
        agregarLog("⚔  TACHI-AI! " + nombre1 + " vs " + nombre2 + " — ¡Comienza el combate!");
        lblEstado.setText("COMBATE EN CURSO: " + nombre1 + " vs " + nombre2);
        panelDohyo.setCombateActivo(true);
        panelDohyo.setGanador(null);
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
        agregarLogColoreado(
            "[T" + turnoActual + "] " + atacante + " usa: ",
            COLOR_TEXTO, kimarite, COLOR_TECNICA
        );
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
        agregarLog("🏆 GANADOR: " + ganador + " — Victorias: " + victorias);
        lblGanador.setText("🏆  " + ganador.toUpperCase() + "  —  Victorias: " + victorias);
        lblEstado.setText("Combate finalizado. Ganador: " + ganador);
        panelDohyo.setGanador(ganador);
        panelDohyo.repaint();
    }

    /**
     * Muestra un error en el log.
     *
     * @param mensaje Mensaje de error.
     */
    public void mostrarError(String mensaje) {
        agregarLogColoreado("ERROR: ", new Color(220, 80, 80), mensaje, new Color(220, 80, 80));
    }

    /**
     * Agrega un mensaje al log.
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
     * Agrega texto en dos colores al log.
     *
     * @param t1 Primera parte.
     * @param c1 Color de la primera parte.
     * @param t2 Segunda parte.
     * @param c2 Color de la segunda parte.
     */
    private void agregarLogColoreado(String t1, Color c1, String t2, Color c2) {
        StyledDocument doc = areaLog.getStyledDocument();
        SimpleAttributeSet a1 = new SimpleAttributeSet();
        StyleConstants.setForeground(a1, c1);
        SimpleAttributeSet a2 = new SimpleAttributeSet();
        StyleConstants.setForeground(a2, c2);
        StyleConstants.setBold(a2, true);
        try {
            doc.insertString(doc.getLength(), t1, a1);
            doc.insertString(doc.getLength(), t2 + "\n", a2);
            areaLog.setCaretPosition(doc.getLength());
        } catch (Exception e) {
            // Silenciado en vista
        }
    }

    /**
     * Limpia el log y reinicia la interfaz.
     */
    public void limpiar() {
        areaLog.setText("");
        turnoActual = 0;
        lblGanador.setText("");
        lblEstado.setText("Log limpiado.");
        lblNombre1.setText("---");
        lblNombre2.setText("---");
        lblPeso1.setText("--- kg");
        lblPeso2.setText("--- kg");
        panelDohyo.setCombateActivo(false);
        panelDohyo.setGanador(null);
        panelDohyo.repaint();
    }

    /**
     * Retorna el puerto ingresado.
     *
     * @return Texto del campo puerto.
     */
    public String getPuerto() { return txtPuerto.getText(); }

    /**
     * Retorna el boton iniciar.
     *
     * @return Boton iniciar.
     */
    public JButton getBtnIniciar() { return btnIniciar; }

    /**
     * Retorna el boton limpiar.
     *
     * @return Boton limpiar.
     */
    public JButton getBtnLimpiar() { return btnLimpiar; }
}
