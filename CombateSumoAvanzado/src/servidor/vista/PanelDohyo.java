package servidor.vista;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * Panel que dibuja visualmente el Dohyo (ring de sumo).
 * Muestra el ring circular, los luchadores, el estado del combate
 * y la última técnica ejecutada. Se actualiza dinámicamente durante el combate.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class PanelDohyo extends JPanel {

    /** Indica si el combate está activo. */
    private boolean combateActivo = false;

    /** Última kimarite ejecutada (para mostrar en el ring). */
    private String ultimaKimarite = "";

    /** Nombre del ganador (cuando el combate termina). */
    private String ganador = null;

    /** Color de fondo del panel. */
    private static final Color COLOR_FONDO   = new Color(15, 10, 10);
    /** Color del ring (arena). */
    private static final Color COLOR_ARENA   = new Color(210, 180, 120);
    /** Color del borde del ring. */
    private static final Color COLOR_BORDE   = new Color(140, 90, 40);
    /** Color rojo del sumo. */
    private static final Color COLOR_ROJO    = new Color(180, 20, 20);
    /** Color dorado. */
    private static final Color COLOR_DORADO  = new Color(220, 170, 50);
    /** Color azul luchador 1. */
    private static final Color COLOR_L1      = new Color(70, 120, 200);
    /** Color rojo luchador 2. */
    private static final Color COLOR_L2      = new Color(200, 60, 60);

    /**
     * Constructor del panel del dohyo.
     */
    public PanelDohyo() {
        setBackground(COLOR_FONDO);
        setOpaque(true);
    }

    /**
     * Dibuja el dohyo, los luchadores y el estado del combate.
     *
     * @param g Contexto gráfico.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int cx = w / 2;
        int cy = h / 2 - 20;
        int radio = Math.min(w, h) / 3;

        // Fondo degradado
        GradientPaint bgGrad = new GradientPaint(0, 0, new Color(20, 12, 12),
                                                  w, h, new Color(40, 20, 20));
        g2.setPaint(bgGrad);
        g2.fillRect(0, 0, w, h);

        // Plataforma (sombra)
        g2.setColor(new Color(80, 50, 20, 80));
        g2.fillOval(cx - radio - 8, cy - radio + 12, (radio + 8) * 2, (radio + 8) * 2);

        // Ring exterior (borde de paja de arroz)
        g2.setColor(COLOR_BORDE);
        g2.fillOval(cx - radio - 6, cy - radio - 6, (radio + 6) * 2, (radio + 6) * 2);

        // Ring interior (arena)
        GradientPaint arenaGrad = new GradientPaint(
            cx - radio, cy - radio, new Color(220, 195, 140),
            cx + radio, cy + radio, new Color(190, 160, 100)
        );
        g2.setPaint(arenaGrad);
        g2.fillOval(cx - radio, cy - radio, radio * 2, radio * 2);

        // Líneas shikiri-sen (líneas de salida del centro)
        g2.setColor(new Color(140, 90, 40));
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(cx - 15, cy, cx + 15, cy);
        g2.drawLine(cx - 15, cy - 5, cx - 15, cy + 5);
        g2.drawLine(cx + 15, cy - 5, cx + 15, cy + 5);

        // Borde decorativo del ring
        g2.setColor(COLOR_BORDE);
        g2.setStroke(new BasicStroke(3));
        g2.drawOval(cx - radio, cy - radio, radio * 2, radio * 2);

        if (!combateActivo && ganador == null) {
            // Estado: esperando
            g2.setColor(COLOR_DORADO);
            g2.setFont(new Font("Serif", Font.BOLD, 14));
            String texto = "Esperando...";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(texto, cx - fm.stringWidth(texto) / 2, cy + 5);
        } else if (combateActivo && ganador == null) {
            // Luchador 1 (izquierda)
            dibujarLuchador(g2, cx - radio / 2, cy, COLOR_L1, "L1");
            // Luchador 2 (derecha)
            dibujarLuchador(g2, cx + radio / 2, cy, COLOR_L2, "L2");

            // Técnica en el centro
            if (!ultimaKimarite.isEmpty()) {
                g2.setFont(new Font("SansSerif", Font.ITALIC, 10));
                g2.setColor(new Color(255, 220, 100, 200));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(ultimaKimarite,
                    cx - fm.stringWidth(ultimaKimarite) / 2, cy - radio + 20);
            }
        } else if (ganador != null) {
            // Ganador en el centro
            dibujarLuchador(g2, cx, cy, COLOR_DORADO, "🏆");
            g2.setFont(new Font("Serif", Font.BOLD, 13));
            g2.setColor(COLOR_DORADO);
            FontMetrics fm = g2.getFontMetrics();
            String txt = "¡" + ganador + "!";
            g2.drawString(txt, cx - fm.stringWidth(txt) / 2, cy + radio - 10);
        }

        // Título del panel
        g2.setFont(new Font("Serif", Font.BOLD, 13));
        g2.setColor(COLOR_ROJO);
        String titulo = "土俵  DOHYO";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(titulo, cx - fm.stringWidth(titulo) / 2, h - 8);
    }

    /**
     * Dibuja un luchador en la posición indicada.
     *
     * @param g2    Contexto gráfico.
     * @param x     Posición X del centro.
     * @param y     Posición Y del centro.
     * @param color Color del luchador.
     * @param label Etiqueta del luchador.
     */
    private void dibujarLuchador(Graphics2D g2, int x, int y, Color color, String label) {
        // Cuerpo
        g2.setColor(color);
        g2.fillOval(x - 18, y - 30, 36, 36);
        // Cabeza
        g2.setColor(color.brighter());
        g2.fillOval(x - 10, y - 50, 20, 20);
        // Mawashi (cinturón)
        g2.setColor(COLOR_DORADO);
        g2.setStroke(new BasicStroke(3));
        g2.drawArc(x - 18, y - 14, 36, 20, 0, 180);
        // Label
        g2.setFont(new Font("SansSerif", Font.BOLD, 11));
        g2.setColor(Color.WHITE);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(label, x - fm.stringWidth(label) / 2, y + 20);
    }

    /**
     * Establece si el combate está activo para actualizar la visualización.
     *
     * @param activo {@code true} si el combate está en curso.
     */
    public void setCombateActivo(boolean activo) {
        this.combateActivo = activo;
    }

    /**
     * Establece la última técnica ejecutada para mostrarla en el ring.
     *
     * @param kimarite Nombre de la técnica.
     */
    public void setUltimaKimarite(String kimarite) {
        this.ultimaKimarite = kimarite;
    }

    /**
     * Establece el ganador para mostrar el estado final del ring.
     * Pasar null reinicia el estado del panel.
     *
     * @param ganador Nombre del ganador, o null para reiniciar.
     */
    public void setGanador(String ganador) {
        this.ganador = ganador;
        if (ganador != null) {
            this.combateActivo = false;
        }
    }
}
