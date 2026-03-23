package cliente.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Ventana principal del cliente de sumo.
 * Permite al usuario ingresar su nombre y peso, cargar su archivo de kimarites
 * mediante JFileChooser, seleccionar sus técnicas de forma creativa
 * y enviar los datos al servidor para iniciar el combate.
 * Diseño temático japonés oscuro con acentos dorados y rojos.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class VentanaCliente extends JFrame {

    // ==================== COLORES ====================
    /** Color de fondo principal. */
    private static final Color COLOR_FONDO    = new Color(12, 8, 8);
    /** Color rojo carmesí. */
    private static final Color COLOR_ROJO     = new Color(170, 15, 15);
    /** Color dorado. */
    private static final Color COLOR_DORADO   = new Color(215, 165, 45);
    /** Color texto principal. */
    private static final Color COLOR_TEXTO    = new Color(235, 220, 200);
    /** Color panel secundario. */
    private static final Color COLOR_PANEL    = new Color(28, 18, 18);
    /** Color verde victoria. */
    private static final Color COLOR_VICTORIA = new Color(60, 200, 90);
    /** Color azul derrota. */
    private static final Color COLOR_DERROTA  = new Color(80, 120, 220);

    // ==================== COMPONENTES ====================
    /** Campo de texto para el nombre del luchador. */
    private JTextField txtNombre;

    /** Campo de texto para el peso del luchador. */
    private JTextField txtPeso;

    /** Campo de texto para el host del servidor. */
    private JTextField txtHost;

    /** Campo de texto para el puerto del servidor. */
    private JTextField txtPuerto;

    /** Panel de checkboxes con las técnicas (selección creativa). */
    private JPanel panelTecnicas;

    /** Lista de checkboxes de kimarites. */
    private List<JCheckBox> checkBoxes;

    /** Botón para seleccionar el archivo .properties. */
    private JButton btnSeleccionarProperties;

    /** Botón para enviar los datos al servidor. */
    private JButton btnEnviar;

    /** Label que muestra el estado actual. */
    private JLabel lblEstado;

    /** Panel de resultado final (ganó/perdió). */
    private JPanel panelResultado;

    /** Label del resultado. */
    private JLabel lblResultado;

    /** ScrollPane de las técnicas. */
    private JScrollPane scrollTecnicas;

    /**
     * Constructor que construye la ventana del cliente.
     */
    public VentanaCliente() {
        checkBoxes = new ArrayList<>();
        inicializarComponentes();
    }

    /**
     * Inicializa y organiza todos los componentes de la ventana.
     */
    private void inicializarComponentes() {
        setTitle("🥋 SUMO — Registro de Luchador");
        setSize(580, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(COLOR_FONDO);
        setLayout(new BorderLayout(0, 0));

        add(crearPanelTitulo(), BorderLayout.NORTH);
        add(crearPanelCentral(), BorderLayout.CENTER);
        add(crearPanelSur(), BorderLayout.SOUTH);
    }

    /**
     * Crea el panel del título con diseño japonés.
     *
     * @return Panel título.
     */
    private JPanel crearPanelTitulo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_ROJO);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblKanji = new JLabel("力士", SwingConstants.LEFT);
        lblKanji.setFont(new Font("Serif", Font.BOLD, 36));
        lblKanji.setForeground(new Color(255, 220, 100, 100));

        JLabel lblTitulo = new JLabel("REGISTRO DE RIKISHI", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Serif", Font.BOLD, 22));
        lblTitulo.setForeground(COLOR_DORADO);

        JLabel lblSub = new JLabel("Ingresa tus datos y selecciona tus técnicas de combate",
                                    SwingConstants.CENTER);
        lblSub.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblSub.setForeground(COLOR_TEXTO);

        JPanel centro = new JPanel(new BorderLayout(0, 3));
        centro.setOpaque(false);
        centro.add(lblTitulo, BorderLayout.CENTER);
        centro.add(lblSub, BorderLayout.SOUTH);

        panel.add(lblKanji, BorderLayout.WEST);
        panel.add(centro, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Crea el panel central con formulario y selección de técnicas.
     *
     * @return Panel central con scroll.
     */
    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(new EmptyBorder(15, 20, 10, 20));

        // ---- Sección datos personales ----
        panel.add(crearSeparadorSeccion("⚔  DATOS DEL LUCHADOR"));
        panel.add(Box.createVerticalStrut(8));
        panel.add(crearPanelDatos());
        panel.add(Box.createVerticalStrut(15));

        // ---- Sección técnicas ----
        panel.add(crearSeparadorSeccion("📜  TÉCNICAS (KIMARITES)"));
        panel.add(Box.createVerticalStrut(8));
        panel.add(crearPanelBotonesProperties());
        panel.add(Box.createVerticalStrut(8));
        panel.add(crearPanelTecnicas());
        panel.add(Box.createVerticalStrut(12));

        // ---- Estado ----
        lblEstado = new JLabel("Carga tu archivo .properties para ver tus técnicas.",
                                SwingConstants.CENTER);
        lblEstado.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblEstado.setForeground(new Color(160, 150, 130));
        lblEstado.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblEstado);

        return panel;
    }

    /**
     * Crea un separador de sección con título decorativo.
     *
     * @param titulo Texto del separador.
     * @return JLabel decorado.
     */
    private JLabel crearSeparadorSeccion(String titulo) {
        JLabel lbl = new JLabel(titulo);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(COLOR_DORADO);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_ROJO));
        return lbl;
    }

    /**
     * Crea el panel con los campos nombre y peso.
     *
     * @return Panel de datos del luchador.
     */
    private JPanel crearPanelDatos() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 12, 8));
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_ROJO, 1),
            new EmptyBorder(12, 15, 12, 15)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblNombre = new JLabel("Nombre del Rikishi:");
        lblNombre.setForeground(COLOR_TEXTO);
        lblNombre.setFont(new Font("SansSerif", Font.PLAIN, 12));

        txtNombre = crearCampoTexto("Ej: Hakuho");

        JLabel lblPeso = new JLabel("Peso (kg):");
        lblPeso.setForeground(COLOR_TEXTO);
        lblPeso.setFont(new Font("SansSerif", Font.PLAIN, 12));

        txtPeso = crearCampoTexto("Ej: 155.0");

        JLabel lblHost = new JLabel("Host del servidor:");
        lblHost.setForeground(COLOR_TEXTO);
        lblHost.setFont(new Font("SansSerif", Font.PLAIN, 12));

        txtHost = crearCampoTexto("Ej: localhost");
        txtHost.setText("localhost");

        JLabel lblPuerto = new JLabel("Puerto:");
        lblPuerto.setForeground(COLOR_TEXTO);
        lblPuerto.setFont(new Font("SansSerif", Font.PLAIN, 12));

        txtPuerto = crearCampoTexto("Ej: 9090");
        txtPuerto.setText("9090");

        panel.add(lblNombre);
        panel.add(txtNombre);
        panel.add(lblPeso);
        panel.add(txtPeso);
        panel.add(lblHost);
        panel.add(txtHost);
        panel.add(lblPuerto);
        panel.add(txtPuerto);

        return panel;
    }

    /**
     * Crea un campo de texto con placeholder y estilo japonés.
     *
     * @param placeholder Texto de ayuda.
     * @return JTextField configurado.
     */
    private JTextField crearCampoTexto(String placeholder) {
        JTextField campo = new JTextField();
        campo.setBackground(new Color(35, 22, 22));
        campo.setForeground(COLOR_TEXTO);
        campo.setCaretColor(COLOR_DORADO);
        campo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(90, 50, 50), 1),
            new EmptyBorder(4, 8, 4, 8)
        ));
        return campo;
    }

    /**
     * Crea el panel con el botón para seleccionar el archivo .properties.
     *
     * @return Panel con botón de selección.
     */
    private JPanel crearPanelBotonesProperties() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setBackground(COLOR_FONDO);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnSeleccionarProperties = new JButton("📂  Cargar Técnicas (.properties)");
        btnSeleccionarProperties.setBackground(new Color(60, 35, 10));
        btnSeleccionarProperties.setForeground(COLOR_DORADO);
        btnSeleccionarProperties.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnSeleccionarProperties.setFocusPainted(false);
        btnSeleccionarProperties.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_DORADO, 1),
            new EmptyBorder(7, 14, 7, 14)
        ));
        btnSeleccionarProperties.setCursor(new Cursor(Cursor.HAND_CURSOR));

        panel.add(btnSeleccionarProperties);
        return panel;
    }

    /**
     * Crea el panel de selección de técnicas con checkboxes creativos.
     *
     * @return ScrollPane con los checkboxes de técnicas.
     */
    private JScrollPane crearPanelTecnicas() {
        panelTecnicas = new JPanel();
        panelTecnicas.setLayout(new GridLayout(0, 3, 6, 6));
        panelTecnicas.setBackground(COLOR_PANEL);
        panelTecnicas.setBorder(new EmptyBorder(8, 10, 8, 10));

        JLabel lblVacio = new JLabel("Carga un archivo .properties para ver las técnicas disponibles.");
        lblVacio.setForeground(new Color(120, 110, 100));
        lblVacio.setFont(new Font("SansSerif", Font.ITALIC, 11));
        panelTecnicas.add(lblVacio);

        scrollTecnicas = new JScrollPane(panelTecnicas);
        scrollTecnicas.setPreferredSize(new Dimension(0, 200));
        scrollTecnicas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        scrollTecnicas.setBorder(BorderFactory.createLineBorder(COLOR_ROJO, 1));
        scrollTecnicas.getViewport().setBackground(COLOR_PANEL);
        scrollTecnicas.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollTecnicas.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        return scrollTecnicas;
    }

    /**
     * Crea el panel inferior con el botón enviar y el panel de resultado.
     *
     * @return Panel sur.
     */
    private JPanel crearPanelSur() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(new EmptyBorder(5, 20, 15, 20));

        btnEnviar = new JButton("⚔  ENTRAR AL DOHYO");
        btnEnviar.setBackground(COLOR_ROJO);
        btnEnviar.setForeground(Color.WHITE);
        btnEnviar.setFont(new Font("Serif", Font.BOLD, 16));
        btnEnviar.setFocusPainted(false);
        btnEnviar.setBorder(new EmptyBorder(12, 0, 12, 0));
        btnEnviar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        panelResultado = new JPanel(new BorderLayout());
        panelResultado.setBackground(COLOR_FONDO);
        panelResultado.setVisible(false);
        panelResultado.setPreferredSize(new Dimension(0, 70));

        lblResultado = new JLabel("", SwingConstants.CENTER);
        lblResultado.setFont(new Font("Serif", Font.BOLD, 24));
        panelResultado.add(lblResultado, BorderLayout.CENTER);

        panel.add(btnEnviar, BorderLayout.NORTH);
        panel.add(panelResultado, BorderLayout.CENTER);

        return panel;
    }

    // ==================== MÉTODOS PÚBLICOS ====================

    /**
     * Carga la lista de kimarites en el panel de técnicas como checkboxes creativos.
     * Cada técnica aparece como un checkbox con estilo de "pergamino".
     *
     * @param kimarites Lista de nombres de técnicas a mostrar.
     */
    public void cargarKimarites(List<String> kimarites) {
        panelTecnicas.removeAll();
        checkBoxes.clear();

        for (String nombre : kimarites) {
            JCheckBox cb = new JCheckBox(nombre);
            cb.setBackground(new Color(45, 28, 18));
            cb.setForeground(COLOR_TEXTO);
            cb.setFont(new Font("SansSerif", Font.PLAIN, 12));
            cb.setFocusPainted(false);
            cb.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 65, 30), 1),
                new EmptyBorder(5, 10, 5, 10)
            ));
            cb.setCursor(new Cursor(Cursor.HAND_CURSOR));
            cb.setSelected(true);

            // Hover effect
            cb.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    cb.setBackground(new Color(80, 45, 20));
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    cb.setBackground(new Color(45, 28, 18));
                }
            });

            checkBoxes.add(cb);
            panelTecnicas.add(cb);
        }

        panelTecnicas.revalidate();
        panelTecnicas.repaint();
    }

    /**
     * Retorna la lista de técnicas seleccionadas (checkboxes marcados).
     *
     * @return Lista de nombres de kimarites seleccionados.
     */
    public List<String> getKimaritesSeleccionados() {
        List<String> seleccionados = new ArrayList<>();
        for (JCheckBox cb : checkBoxes) {
            if (cb.isSelected()) {
                seleccionados.add(cb.getText());
            }
        }
        return seleccionados;
    }

    /**
     * Muestra el resultado final del combate en la ventana.
     *
     * @param gano   {@code true} si el luchador ganó.
     * @param nombre Nombre del luchador.
     */
    public void mostrarResultado(boolean gano, String nombre) {
        panelResultado.setVisible(true);
        if (gano) {
            lblResultado.setText("🏆 ¡" + nombre.toUpperCase() + " GANÓ EL COMBATE!");
            lblResultado.setForeground(COLOR_VICTORIA);
            panelResultado.setBackground(new Color(10, 40, 15));
        } else {
            lblResultado.setText("💀 " + nombre.toUpperCase() + " fue derrotado.");
            lblResultado.setForeground(COLOR_DERROTA);
            panelResultado.setBackground(new Color(10, 15, 40));
        }
        mostrarEstado("Combate finalizado. Puedes cerrar la ventana.");
    }

    /**
     * Muestra un mensaje de estado en la parte inferior del formulario.
     *
     * @param mensaje Texto de estado.
     */
    public void mostrarEstado(String mensaje) {
        lblEstado.setText(mensaje);
    }

    /**
     * Habilita o deshabilita el botón de enviar.
     *
     * @param habilitado {@code true} para habilitar.
     */
    public void setBtnEnviarHabilitado(boolean habilitado) {
        btnEnviar.setEnabled(habilitado);
    }

    /**
     * Obtiene el nombre ingresado por el usuario.
     *
     * @return Texto del campo nombre.
     */
    public String getNombre() { return txtNombre.getText(); }

    /**
     * Obtiene el peso ingresado por el usuario.
     *
     * @return Texto del campo peso.
     */
    public String getPeso() { return txtPeso.getText(); }

    /**
     * Obtiene el host del servidor ingresado por el usuario.
     *
     * @return Texto del campo host.
     */
    public String getHost() { return txtHost.getText(); }

    /**
     * Obtiene el puerto del servidor ingresado por el usuario.
     *
     * @return Texto del campo puerto.
     */
    public String getPuerto() { return txtPuerto.getText(); }

    /**
     * Obtiene el botón de seleccionar properties.
     *
     * @return Botón seleccionar properties.
     */
    public JButton getBtnSeleccionarProperties() { return btnSeleccionarProperties; }

    /**
     * Obtiene el botón de enviar datos al servidor.
     *
     * @return Botón enviar.
     */
    public JButton getBtnEnviar() { return btnEnviar; }
}
