package cliente.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Ventana del cliente de sumo con efectos visuales cinematograficos.
 * Panel animado de fondo, particulas, luchador decorativo y UI tematica.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class VentanaCliente extends JFrame {

    // ==================== COLORES ====================
    private static final Color C_FONDO   = new Color(10, 6, 6);
    private static final Color C_ROJO    = new Color(175, 18, 18);
    private static final Color C_DORADO  = new Color(220, 172, 48);
    private static final Color C_DORADO2 = new Color(255, 215, 75);
    private static final Color C_TEXTO   = new Color(238, 222, 200);
    private static final Color C_PANEL   = new Color(22, 14, 14);
    private static final Color C_CAMPO   = new Color(32, 20, 20);
    private static final Color C_VERDE   = new Color(55, 195, 85);
    private static final Color C_AZUL    = new Color(70, 115, 215);

    // ==================== COMPONENTES ====================
    private JTextField txtNombre, txtPeso, txtHost, txtPuerto;
    private JPanel panelTecnicas;
    private List<JCheckBox> checkBoxes = new ArrayList<>();
    private JButton btnSeleccionarProperties, btnEnviar;
    private JLabel lblEstado;
    private JPanel panelResultado;
    private JLabel lblResultado;
    private JScrollPane scrollTecnicas;

    // ==================== ANIMACION FONDO ====================
    private PanelFondoAnimado panelFondo;

    /**
     * Constructor que inicializa la ventana.
     */
    public VentanaCliente() {
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setTitle("相撲  Registro de Rikishi — Cliente de Sumo");
        setSize(590, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        // Panel de fondo animado
        panelFondo = new PanelFondoAnimado();
        panelFondo.setLayout(new BorderLayout());
        setContentPane(panelFondo);
        panelFondo.setLayout(new BorderLayout(0, 0));

        panelFondo.add(crearPanelTitulo(),     BorderLayout.NORTH);
        panelFondo.add(crearPanelContenido(),  BorderLayout.CENTER);
        panelFondo.add(crearPanelEstado(),     BorderLayout.SOUTH);
    }

    // ==================== PANELES ====================

    private JPanel crearPanelTitulo() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);

        // Franja roja
        JPanel panelRojo = new JPanel(new BorderLayout());
        panelRojo.setBackground(C_ROJO);
        panelRojo.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel lblTitulo = new JLabel(
            "<html><center>" +
            "<span style='font-size:20px;'>相撲</span>  " +
            "<b style='font-size:18px;'>SUMO — Registro de Luchador</b>" +
            "</center></html>",
            SwingConstants.CENTER
        );
        lblTitulo.setForeground(C_DORADO);
        lblTitulo.setFont(new Font("Serif", Font.BOLD, 20));
        panelRojo.add(lblTitulo, BorderLayout.CENTER);

        // Subtitulo
        JPanel panelSub = new JPanel(new BorderLayout());
        panelSub.setBackground(new Color(18, 10, 10));
        panelSub.setBorder(new EmptyBorder(6, 0, 6, 0));
        JLabel lblSub = new JLabel(
            "⛩  Ingresa tus datos, carga tus técnicas y únete al combate  ⛩",
            SwingConstants.CENTER
        );
        lblSub.setForeground(new Color(180, 140, 50));
        lblSub.setFont(new Font("Serif", Font.ITALIC, 12));
        panelSub.add(lblSub, BorderLayout.CENTER);

        outer.add(panelRojo, BorderLayout.NORTH);
        outer.add(panelSub,  BorderLayout.CENTER);
        return outer;
    }

    private JPanel crearPanelContenido() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 18, 10, 18));

        panel.add(crearSeccion("⚔  DATOS DEL LUCHADOR", crearFormularioDatos()));
        panel.add(Box.createVerticalStrut(10));
        panel.add(crearSeccion("🌐  CONEXIÓN AL SERVIDOR", crearFormularioConexion()));
        panel.add(Box.createVerticalStrut(10));
        panel.add(crearSeccionTecnicas());
        panel.add(Box.createVerticalStrut(10));
        panel.add(crearPanelBotones());
        panel.add(Box.createVerticalStrut(10));

        panelResultado = new JPanel(new BorderLayout());
        panelResultado.setOpaque(false);
        panelResultado.setVisible(false);
        panelResultado.setPreferredSize(new Dimension(0, 85));
        panelResultado.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));

        lblResultado = new JLabel("", SwingConstants.CENTER);
        lblResultado.setFont(new Font("Serif", Font.BOLD, 14));
        panelResultado.add(lblResultado, BorderLayout.CENTER);
        panel.add(panelResultado);

        return panel;
    }

    private JPanel crearSeccion(String titulo, JPanel contenido) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 4));
        wrapper.setOpaque(false);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JLabel lbl = new JLabel(" " + titulo);
        lbl.setFont(new Font("Serif", Font.BOLD, 12));
        lbl.setForeground(C_DORADO);
        lbl.setOpaque(true);
        lbl.setBackground(new Color(35, 15, 15));
        lbl.setBorder(new EmptyBorder(4, 8, 4, 8));

        wrapper.add(lbl,      BorderLayout.NORTH);
        wrapper.add(contenido, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel crearFormularioDatos() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 8));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_ROJO, 1),
            new EmptyBorder(10, 12, 10, 12)
        ));

        panel.add(crearLabel("Nombre del Rikishi:"));
        txtNombre = crearCampo("Ej: Hakuho", false);
        panel.add(txtNombre);

        panel.add(crearLabel("Peso (kg):"));
        txtPeso = crearCampo("Ej: 155.0", false);
        panel.add(txtPeso);

        return panel;
    }

    private JPanel crearFormularioConexion() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 8));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_ROJO, 1),
            new EmptyBorder(10, 12, 10, 12)
        ));

        panel.add(crearLabel("Host del servidor:"));
        txtHost = crearCampo("localhost", false);
        txtHost.setText("localhost");
        panel.add(txtHost);

        panel.add(crearLabel("Puerto:"));
        txtPuerto = crearCampo("9090", false);
        txtPuerto.setText("9090");
        panel.add(txtPuerto);

        return panel;
    }

    private JPanel crearSeccionTecnicas() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 4));
        wrapper.setOpaque(false);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 230));

        JLabel lbl = new JLabel(" 🥋  TÉCNICAS (KIMARITES)");
        lbl.setFont(new Font("Serif", Font.BOLD, 12));
        lbl.setForeground(C_DORADO);
        lbl.setOpaque(true);
        lbl.setBackground(new Color(35, 15, 15));
        lbl.setBorder(new EmptyBorder(4, 8, 4, 8));

        JPanel panelBtn = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelBtn.setOpaque(false);
        panelBtn.setBorder(new EmptyBorder(0, 0, 6, 0));

        btnSeleccionarProperties = crearBoton("📂 Cargar Técnicas (.properties)", C_ROJO, 12);
        panelBtn.add(btnSeleccionarProperties);

        panelTecnicas = new JPanel(new GridLayout(0, 3, 6, 6));
        panelTecnicas.setBackground(C_PANEL);
        panelTecnicas.setBorder(new EmptyBorder(8, 10, 8, 10));

        JLabel lblVacio = new JLabel("Carga un archivo .properties primero", SwingConstants.CENTER);
        lblVacio.setForeground(new Color(130, 100, 70));
        lblVacio.setFont(new Font("SansSerif", Font.ITALIC, 11));
        panelTecnicas.add(lblVacio);

        scrollTecnicas = new JScrollPane(panelTecnicas);
        scrollTecnicas.setPreferredSize(new Dimension(0, 175));
        scrollTecnicas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 175));
        scrollTecnicas.setBorder(BorderFactory.createLineBorder(C_ROJO, 1));
        scrollTecnicas.getViewport().setBackground(C_PANEL);
        scrollTecnicas.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel inner = new JPanel(new BorderLayout(0, 4));
        inner.setOpaque(false);
        inner.setBorder(BorderFactory.createLineBorder(C_ROJO, 1));
        inner.add(panelBtn,      BorderLayout.NORTH);
        inner.add(scrollTecnicas, BorderLayout.CENTER);

        wrapper.add(lbl,   BorderLayout.NORTH);
        wrapper.add(inner, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        btnEnviar = crearBoton("⚔  ENTRAR AL DOHYO", new Color(140, 20, 20), 14);
        btnEnviar.setPreferredSize(new Dimension(220, 42));
        panel.add(btnEnviar);
        return panel;
    }

    private JPanel crearPanelEstado() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(15, 8, 8));
        panel.setBorder(new EmptyBorder(6, 15, 6, 15));

        lblEstado = new JLabel(
            "Completa tus datos y carga tus técnicas para unirte al combate.",
            SwingConstants.CENTER
        );
        lblEstado.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblEstado.setForeground(new Color(170, 140, 90));
        panel.add(lblEstado, BorderLayout.CENTER);
        return panel;
    }

    // ==================== HELPERS ====================

    private JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setForeground(C_TEXTO);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        return lbl;
    }

    private JTextField crearCampo(String placeholder, boolean disabled) {
        JTextField tf = new JTextField();
        tf.setBackground(C_CAMPO);
        tf.setForeground(C_TEXTO);
        tf.setCaretColor(C_DORADO);
        tf.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 40, 40), 1),
            new EmptyBorder(4, 8, 4, 8)
        ));
        return tf;
    }

    private JButton crearBoton(String texto, Color color, int fontSize) {
        JButton btn = new JButton(texto) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) g2.setColor(color.darker().darker());
                else if (getModel().isRollover()) g2.setColor(color.brighter());
                else g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                // Brillo superior
                GradientPaint shine = new GradientPaint(0, 0, new Color(255,255,255,40),
                    0, getHeight()/2, new Color(255,255,255,0));
                g2.setPaint(shine);
                g2.fillRoundRect(0, 0, getWidth(), getHeight()/2, 8, 8);
                // Texto
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                    (getWidth()-fm.stringWidth(getText()))/2,
                    (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(240, 36));
        return btn;
    }

    // ==================== METODOS PUBLICOS ====================

    /**
     * Carga los kimarites como checkboxes en el panel de tecnicas.
     * @param kimarites Lista de nombres de tecnicas.
     */
    public void cargarKimarites(List<String> kimarites) {
        panelTecnicas.removeAll();
        checkBoxes.clear();

        for (String k : kimarites) {
            JCheckBox cb = new JCheckBox(k);
            cb.setBackground(C_PANEL);
            cb.setForeground(C_TEXTO);
            cb.setFont(new Font("SansSerif", Font.PLAIN, 11));
            cb.setSelected(true);
            cb.setFocusPainted(false);
            checkBoxes.add(cb);
            panelTecnicas.add(cb);
        }
        panelTecnicas.revalidate();
        panelTecnicas.repaint();
    }

    /**
     * Muestra el resultado del combate con animacion.
     * @param gano   true si el luchador gano.
     * @param nombre Nombre del luchador.
     */
    public void mostrarResultado(boolean gano, String nombre) {
        panelResultado.setVisible(true);
        panelResultado.setPreferredSize(new Dimension(0, 95));
        panelResultado.setMaximumSize(new Dimension(Integer.MAX_VALUE, 95));

        if (gano) {
            panelResultado.setBackground(new Color(6, 32, 10));
            panelResultado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_VERDE, 2),
                new EmptyBorder(8, 12, 8, 12)
            ));
            panelResultado.setOpaque(true);
            lblResultado.setText(
                "<html><center>" +
                "<span style='font-size:24px;'>🏆</span><br>" +
                "<b style='font-size:15px;color:#50C878;'>¡" + nombre.toUpperCase() + " GANÓ EL COMBATE!</b><br>" +
                "<span style='font-size:10px;color:#90EE90;'>Victoria registrada — ¡Honor y gloria para ti!</span>" +
                "</center></html>"
            );
        } else {
            panelResultado.setBackground(new Color(8, 10, 32));
            panelResultado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_AZUL, 2),
                new EmptyBorder(8, 12, 8, 12)
            ));
            panelResultado.setOpaque(true);
            lblResultado.setText(
                "<html><center>" +
                "<span style='font-size:24px;'>⚡</span><br>" +
                "<b style='font-size:15px;color:#6495ED;'>" + nombre.toUpperCase() + " fue derrotado</b><br>" +
                "<span style='font-size:10px;color:#9999CC;'>El honor está en haber combatido con valentía</span>" +
                "</center></html>"
            );
        }
        panelResultado.revalidate();
        panelResultado.repaint();
        mostrarEstado("Combate finalizado. Puedes cerrar la ventana.");
    }

    /**
     * Muestra un mensaje de estado.
     * @param msg Mensaje a mostrar.
     */
    public void mostrarEstado(String msg) { lblEstado.setText(msg); }

    /**
     * Habilita o deshabilita el boton enviar.
     * @param h true para habilitar.
     */
    public void setBtnEnviarHabilitado(boolean h) { btnEnviar.setEnabled(h); }

    /** @return Texto del campo nombre. */
    public String getNombre() { return txtNombre.getText(); }
    /** @return Texto del campo peso. */
    public String getPeso()   { return txtPeso.getText(); }
    /** @return Texto del campo host. */
    public String getHost()   { return txtHost.getText(); }
    /** @return Texto del campo puerto. */
    public String getPuerto() { return txtPuerto.getText(); }

    /**
     * Establece el host leido desde cliente.properties (no editable por usuario).
     * @param host Host del servidor.
     */
    public void setHost(String host) {
        txtHost.setText(host);
        txtHost.setEditable(false);
        txtHost.setForeground(new Color(180, 150, 80));
    }

    /**
     * Establece el puerto leido desde cliente.properties (no editable por usuario).
     * @param puerto Puerto del servidor.
     */
    public void setPuerto(String puerto) {
        txtPuerto.setText(puerto);
        txtPuerto.setEditable(false);
        txtPuerto.setForeground(new Color(180, 150, 80));
    }

    /**
     * Retorna la lista de kimarites seleccionados.
     * @return Lista de nombres de tecnicas seleccionadas.
     */
    public List<String> getKimaritesSeleccionados() {
        List<String> sel = new ArrayList<>();
        for (JCheckBox cb : checkBoxes) if (cb.isSelected()) sel.add(cb.getText());
        return sel;
    }

    /** @return Boton seleccionar properties. */
    public JButton getBtnSeleccionarProperties() { return btnSeleccionarProperties; }
    /** @return Boton enviar. */
    public JButton getBtnEnviar() { return btnEnviar; }

    /**
     * Abre el JFileChooser para seleccionar el archivo .properties.
     * El FileChooser pertenece a la vista — no al control.
     *
     * @return Ruta absoluta del archivo seleccionado, o null si se cancelo.
     */
    public String abrirSelectorProperties() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Seleccionar archivo de kimarites (.properties)");
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Archivos de propiedades (*.properties)", "properties"
        ));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile().getAbsolutePath();
        }
        return null;
    }

    // ==================== PANEL FONDO ANIMADO ====================

    /**
     * Panel de fondo con particulas, brumas y kanji animados.
     */
    private class PanelFondoAnimado extends JPanel {

        private Timer timerFondo;
        private int frameFondo = 0;
        private float[][] pts = new float[50][6]; // particulas fondo
        private float[][] bruma = new float[8][4];
        private Random rngF = new Random();

        /**
         * Constructor del panel de fondo.
         */
        public PanelFondoAnimado() {
            setBackground(C_FONDO);
            setOpaque(true);
            for (int i = 0; i < pts.length; i++) resetPt(i);
            for (int i = 0; i < bruma.length; i++) resetBruma(i);
            timerFondo = new Timer(20, new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    frameFondo++;
                    for (int i = 0; i < pts.length; i++) {
                        pts[i][0] += pts[i][2]; pts[i][1] += pts[i][3];
                        pts[i][5] -= 0.006f;
                        if (pts[i][5] <= 0) resetPt(i);
                    }
                    for (int i = 0; i < bruma.length; i++) {
                        bruma[i][0] += bruma[i][2];
                        bruma[i][3] -= 0.003f;
                        if (bruma[i][3] <= 0) resetBruma(i);
                    }
                    repaint();
                }
            });
            timerFondo.start();
        }

        private void resetPt(int i) {
            pts[i][0] = rngF.nextFloat() * 600;
            pts[i][1] = rngF.nextFloat() * 800 + 800;
            pts[i][2] = (rngF.nextFloat()-0.5f)*0.4f;
            pts[i][3] = -rngF.nextFloat()*0.6f-0.2f;
            pts[i][4] = rngF.nextFloat()*3+1;
            pts[i][5] = rngF.nextFloat()*0.7f+0.1f;
        }

        private void resetBruma(int i) {
            bruma[i][0] = rngF.nextFloat()*600-100;
            bruma[i][1] = rngF.nextFloat()*800;
            bruma[i][2] = (rngF.nextFloat()-0.5f)*0.15f;
            bruma[i][3] = rngF.nextFloat()*0.12f+0.02f;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();

            // Fondo degradado
            RadialGradientPaint bg = new RadialGradientPaint(w/2f, h*0.3f, h*0.9f,
                new float[]{0f,0.5f,1f},
                new Color[]{new Color(40,18,18), new Color(18,8,8), new Color(5,2,2)});
            g2.setPaint(bg);
            g2.fillRect(0, 0, w, h);

            // Lineas decorativas japonesas (verticales sutiles)
            g2.setStroke(new BasicStroke(0.5f));
            for (int x = 0; x < w; x += 40) {
                float al = (float)(0.03+Math.sin(frameFondo*0.02+x*0.1)*0.02);
                g2.setColor(new Color(180,50,50,(int)(al*255)));
                g2.drawLine(x, 0, x, h);
            }
            for (int y2 = 0; y2 < h; y2 += 40) {
                float al = (float)(0.02+Math.sin(frameFondo*0.02+y2*0.1)*0.015);
                g2.setColor(new Color(180,50,50,(int)(al*255)));
                g2.drawLine(0, y2, w, y2);
            }

            // Kanji grandes en el fondo (decorativos)
            String[] kanjis = {"相","撲","力","道","勝","魂"};
            g2.setFont(new Font("Serif", Font.BOLD, 80));
            for (int i = 0; i < kanjis.length; i++) {
                float al = (float)(0.035+Math.sin(frameFondo*0.015+i*1.2)*0.025);
                g2.setColor(new Color(160,40,40,(int)(al*255)));
                int kx = (i%3)*200 + 40;
                int ky = (i/3)*300 + 180;
                g2.drawString(kanjis[i], kx, ky);
            }

            // Bruma
            for (float[] b : bruma) {
                int ba = (int)(b[3]*80);
                if (ba <= 0) continue;
                RadialGradientPaint br2 = new RadialGradientPaint(
                    b[0]+100, b[1]+60, 120,
                    new float[]{0f,1f},
                    new Color[]{new Color(180,80,80,ba), new Color(0,0,0,0)});
                g2.setPaint(br2);
                g2.fillOval((int)b[0], (int)b[1], 200, 120);
            }

            // Particulas ascendentes
            for (float[] p : pts) {
                int pa = (int)(p[5]*120);
                if (pa <= 0) continue;
                int tam = (int)p[4];
                g2.setColor(new Color(220,170,60,pa));
                g2.fillOval((int)p[0], (int)p[1], tam, tam);
            }

            // Pintar hijos encima
            paintChildren(g2);
        }
    }
}
