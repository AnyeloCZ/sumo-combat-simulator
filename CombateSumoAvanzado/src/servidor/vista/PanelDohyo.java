package servidor.vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.*;
import java.util.Random;

/**
 * Panel ultra-animado del Dohyo con efectos cinematograficos.
 * Particulas de sal, fuego, sombras dinamicas, publico, antorchas,
 * luchadores con fisica, ondas de impacto y celebracion explosiva.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class PanelDohyo extends JPanel {

    // ==================== ESTADO ====================
    private boolean combateActivo = false;
    private String  ultimaKimarite = "";
    private String  ganador = null;

    // ==================== ANIMACION ====================
    private Timer  timer;
    private int    frame        = 0;
    private float  alphaKim     = 0f;
    private boolean fadeIn      = false;
    private int    frameImpacto = 0;
    private int    frameGanador = 0;
    private double pulso        = 1.0;
    private boolean pulsoSube   = true;
    private double anguloDecor  = 0;
    private double offsetL1     = 0;
    private double offsetL2     = 0;
    private Random rng          = new Random();

    // Particulas de sal [x, y, vx, vy, tam, alpha, tipo]
    private float[][] particulas = new float[60][7];
    // Particulas de fuego de antorchas [x, y, vx, vy, tam, alpha, calor]
    private float[][] fuego      = new float[80][7];
    // Estrellas del fondo [x, y, tam, brillo]
    private float[][] estrellas  = new float[120][4];
    // Ondas de impacto [x, y, radio, alpha]
    private float[][] ondas      = new float[8][4];
    // Particulas de celebracion [x, y, vx, vy, tam, alpha, r, g, b]
    private float[][] confeti    = new float[100][9];
    // Rayos de energia durante combate [angulo, longitud, alpha]
    private float[][] rayos      = new float[16][3];

    // Posiciones de antorchas
    private static final int[][] ANTORCHAS = {{60,60},{240,60},{60,200},{240,200}};

    // Colores
    private static final Color C_FONDO   = new Color(8,4,4);
    private static final Color C_ARENA   = new Color(218,188,132);
    private static final Color C_ARENA2  = new Color(185,152,88);
    private static final Color C_BORDE   = new Color(128,75,25);
    private static final Color C_BORDE2  = new Color(95,50,12);
    private static final Color C_ROJO    = new Color(195,22,22);
    private static final Color C_DORADO  = new Color(232,182,55);
    private static final Color C_DORADO2 = new Color(255,220,80);
    private static final Color C_L1      = new Color(55,105,215);
    private static final Color C_L1D     = new Color(25,55,130);
    private static final Color C_L2      = new Color(215,50,50);
    private static final Color C_L2D     = new Color(130,22,22);
    private static final Color C_SAL     = new Color(245,245,255);

    /**
     * Constructor del panel animado.
     */
    public PanelDohyo() {
        setBackground(C_FONDO);
        setOpaque(true);
        inicEstrellas();
        inicParticulas();
        inicFuego();
        inicOndas();
        timer = new Timer(14, new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { tick(); repaint(); }
        });
        timer.start();
    }

    private void inicEstrellas() {
        for (int i = 0; i < estrellas.length; i++) {
            estrellas[i][0] = rng.nextFloat() * 320;
            estrellas[i][1] = rng.nextFloat() * 280;
            estrellas[i][2] = rng.nextFloat() * 2.5f + 0.5f;
            estrellas[i][3] = rng.nextFloat();
        }
    }

    private void inicParticulas() {
        for (int i = 0; i < particulas.length; i++) resetSal(i);
    }

    private void resetSal(int i) {
        double a = rng.nextDouble() * Math.PI * 2;
        float r  = 85 + rng.nextFloat() * 25;
        particulas[i][0] = 155 + (float)(Math.cos(a) * r);
        particulas[i][1] = 130 + (float)(Math.sin(a) * r);
        particulas[i][2] = (rng.nextFloat() - 0.5f) * 0.8f;
        particulas[i][3] = -rng.nextFloat() * 1.8f - 0.3f;
        particulas[i][4] = rng.nextFloat() * 4 + 1;
        particulas[i][5] = rng.nextFloat() * 0.9f + 0.1f;
        particulas[i][6] = rng.nextInt(3); // 0=sal, 1=polvo, 2=brillo
    }

    private void inicFuego() {
        int[] xs = {60,240,60,240};
        int[] ys = {60,60,200,200};
        for (int i = 0; i < fuego.length; i++) {
            int ant = i % 4;
            resetFuego(i, xs[ant], ys[ant]);
        }
    }

    private void resetFuego(int i, float bx, float by) {
        fuego[i][0] = bx + (rng.nextFloat() - 0.5f) * 6;
        fuego[i][1] = by;
        fuego[i][2] = (rng.nextFloat() - 0.5f) * 0.6f;
        fuego[i][3] = -rng.nextFloat() * 2.5f - 1f;
        fuego[i][4] = rng.nextFloat() * 5 + 2;
        fuego[i][5] = rng.nextFloat() * 0.9f + 0.1f;
        fuego[i][6] = rng.nextFloat(); // calor 0=amarillo, 1=rojo
    }

    private void inicOndas() {
        for (int i = 0; i < ondas.length; i++) ondas[i][3] = 0;
    }

    private void lanzarOnda(float x, float y) {
        for (int i = 0; i < ondas.length; i++) {
            if (ondas[i][3] <= 0) {
                ondas[i][0] = x; ondas[i][1] = y;
                ondas[i][2] = 5; ondas[i][3] = 1f;
                break;
            }
        }
    }

    private void inicConfeti() {
        for (int i = 0; i < confeti.length; i++) {
            confeti[i][0] = rng.nextFloat() * 320;
            confeti[i][1] = rng.nextFloat() * -50;
            confeti[i][2] = (rng.nextFloat() - 0.5f) * 2f;
            confeti[i][3] = rng.nextFloat() * 3 + 1;
            confeti[i][4] = rng.nextFloat() * 8 + 3;
            confeti[i][5] = 1f;
            confeti[i][6] = rng.nextFloat() * 255;
            confeti[i][7] = rng.nextFloat() * 255;
            confeti[i][8] = rng.nextFloat() * 255;
        }
    }

    private void tick() {
        frame++;
        anguloDecor += 0.006;

        // Pulso ring
        if (combateActivo) {
            if (pulsoSube) { pulso += 0.004; if (pulso >= 1.018) pulsoSube = false; }
            else           { pulso -= 0.004; if (pulso <= 0.982) pulsoSube = true;  }
        } else pulso = 1.0;

        // Estrellas parpadeantes
        for (float[] e : estrellas)
            e[3] = (float)(0.4 + Math.sin(frame * 0.04 + e[0]) * 0.6);

        // Particulas de sal
        for (int i = 0; i < particulas.length; i++) {
            particulas[i][0] += particulas[i][2];
            particulas[i][1] += particulas[i][3];
            particulas[i][5] -= 0.01f;
            if (particulas[i][5] <= 0) resetSal(i);
        }

        // Fuego antorchas
        int[] xs = {60,240,60,240};
        int[] ys = {60,60,200,200};
        for (int i = 0; i < fuego.length; i++) {
            int ant = i % 4;
            fuego[i][0] += fuego[i][2] + (float)Math.sin(frame*0.1+i)*0.3f;
            fuego[i][1] += fuego[i][3];
            fuego[i][5] -= 0.025f;
            fuego[i][4] *= 0.98f;
            if (fuego[i][5] <= 0 || fuego[i][4] < 0.5f)
                resetFuego(i, xs[ant], ys[ant]);
        }

        // Ondas de impacto
        for (float[] o : ondas) {
            if (o[3] > 0) { o[2] += 4; o[3] -= 0.04f; }
        }

        // Fade kimarite
        if (fadeIn)         { alphaKim += 0.06f; if (alphaKim >= 1f) { alphaKim = 1f; fadeIn = false; } }
        else if (alphaKim > 0 && !ultimaKimarite.isEmpty()) { alphaKim -= 0.007f; if (alphaKim < 0) alphaKim = 0; }

        // Impacto
        if (frameImpacto > 0) frameImpacto--;

        // Oscilacion luchadores
        if (combateActivo) {
            offsetL1 = Math.sin(frame * 0.09) * 4;
            offsetL2 = Math.sin(frame * 0.09 + Math.PI) * 4;
        }

        // Confeti ganador
        if (ganador != null) {
            frameGanador++;
            if (frameGanador == 1) inicConfeti();
            for (int i = 0; i < confeti.length; i++) {
                confeti[i][0] += confeti[i][2];
                confeti[i][1] += confeti[i][3];
                confeti[i][2] += (rng.nextFloat()-0.5f)*0.1f;
                confeti[i][5] -= 0.004f;
                if (confeti[i][1] > 300 || confeti[i][5] <= 0) {
                    confeti[i][0] = rng.nextFloat()*320;
                    confeti[i][1] = -10;
                    confeti[i][5] = 1f;
                    confeti[i][6] = rng.nextFloat()*255;
                    confeti[i][7] = rng.nextFloat()*255;
                    confeti[i][8] = rng.nextFloat()*255;
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);

        int w = getWidth(), h = getHeight();
        int cx = w/2, cy = h/2 - 10;
        int radio = Math.min(w,h)/3 + 5;

        pintarFondo(g2, w, h, cx, cy);
        pintarEstrellas(g2, w, h);
        pintarAntorchas(g2, cx, cy, w, h);
        pintarPublico(g2, cx, cy, radio, w, h);
        pintarDecorExterior(g2, cx, cy, radio);
        pintarDohyo(g2, cx, cy, radio);
        pintarSal(g2, cx, cy);
        pintarOndas(g2, cx, cy);

        if (!combateActivo && ganador == null) pintarEspera(g2, cx, cy, radio);
        else if (combateActivo)               pintarCombate(g2, cx, cy, radio);

        if (ganador != null) pintarGanador(g2, cx, cy, radio, w, h);
        pintarFuego(g2, cx, cy, w, h);
        pintarTitulo(g2, cx, h);
        g2.dispose();
    }

    private void pintarFondo(Graphics2D g2, int w, int h, int cx, int cy) {
        RadialGradientPaint rg = new RadialGradientPaint(cx, cy, Math.max(w,h)*0.8f,
            new float[]{0f,0.4f,1f},
            new Color[]{new Color(45,20,20), new Color(22,10,10), new Color(4,2,2)});
        g2.setPaint(rg);
        g2.fillRect(0, 0, w, h);
        // Vigneta
        RadialGradientPaint vig = new RadialGradientPaint(cx, cy, Math.max(w,h)*0.7f,
            new float[]{0.5f,1f},
            new Color[]{new Color(0,0,0,0), new Color(0,0,0,180)});
        g2.setPaint(vig);
        g2.fillRect(0,0,w,h);
    }

    private void pintarEstrellas(Graphics2D g2, int w, int h) {
        for (float[] e : estrellas) {
            int alpha = (int)(e[3] * 180);
            if (alpha <= 0) continue;
            g2.setColor(new Color(255,240,200,alpha));
            int tam = (int)e[2];
            g2.fillOval((int)e[0]-tam/2, (int)e[1]-tam/2, tam, tam);
        }
    }

    private void pintarAntorchas(Graphics2D g2, int cx, int cy, int w, int h) {
        int[][] pos = {
            {cx-w/3, cy-h/4}, {cx+w/3, cy-h/4},
            {cx-w/3, cy+h/4}, {cx+w/3, cy+h/4}
        };
        for (int[] p : pos) {
            // Palo de antorcha
            g2.setColor(new Color(80,50,20));
            g2.setStroke(new BasicStroke(4f));
            g2.drawLine(p[0], p[1]+5, p[0], p[1]+25);
            // Base
            g2.setColor(new Color(100,65,25));
            g2.fillRect(p[0]-5, p[1]+20, 10, 8);
            // Halo de luz
            RadialGradientPaint halo = new RadialGradientPaint(p[0], p[1], 40,
                new float[]{0f,0.4f,1f},
                new Color[]{new Color(255,180,50,60), new Color(255,100,20,25), new Color(0,0,0,0)});
            g2.setPaint(halo);
            g2.fillOval(p[0]-40, p[1]-40, 80, 80);
        }
    }

    private void pintarFuego(Graphics2D g2, int cx, int cy, int w, int h) {
        int[][] pos = {
            {cx-w/3, cy-h/4}, {cx+w/3, cy-h/4},
            {cx-w/3, cy+h/4}, {cx+w/3, cy+h/4}
        };
        for (int i = 0; i < fuego.length; i++) {
            int ant = i % 4;
            float dx = fuego[i][0] - 155 + pos[ant][0] - (cx - w/3);
            float dy = fuego[i][1] - 60  + pos[ant][1] - (cy - h/4);
            float calor = fuego[i][6];
            int alpha = (int)(fuego[i][5] * 220);
            if (alpha <= 0) continue;
            Color cf;
            if (calor < 0.3f)       cf = new Color(255,255,100,alpha);
            else if (calor < 0.6f)  cf = new Color(255,160,30,alpha);
            else                     cf = new Color(220,40,10,alpha);
            g2.setColor(cf);
            int tam = (int)fuego[i][4];
            g2.fillOval((int)dx-tam/2, (int)dy-tam/2, tam, tam);
        }
    }

    private void pintarPublico(Graphics2D g2, int cx, int cy, int radio, int w, int h) {
        // Filas de siluetas del publico
        for (int fila = 0; fila < 3; fila++) {
            int y = cy + radio + 15 + fila * 12;
            int onda = (int)(Math.sin(frame * 0.03 + fila) * 3);
            for (int x = 20; x < w - 20; x += 14) {
                double oscil = Math.sin(frame * 0.05 + x * 0.3) * 2;
                int alpha = 40 + fila * 20;
                g2.setColor(new Color(80,40,40,alpha));
                g2.fillOval(x-5, y+(int)oscil+onda-12, 10, 12);
                g2.fillRect(x-5, y+(int)oscil+onda, 10, 10);
            }
        }
        // Arriba del ring
        for (int fila = 0; fila < 2; fila++) {
            int y = cy - radio - 8 - fila * 10;
            for (int x = cx - radio + 10; x < cx + radio - 10; x += 14) {
                double oscil = Math.sin(frame * 0.05 + x * 0.3) * 2;
                int alpha = 30 + fila * 15;
                g2.setColor(new Color(80,40,40,alpha));
                g2.fillOval(x-4, y+(int)oscil-10, 8, 10);
                g2.fillRect(x-4, y+(int)oscil, 8, 8);
            }
        }
    }

    private void pintarDecorExterior(Graphics2D g2, int cx, int cy, int radio) {
        // Anillo de banderas/ornamentos
        int n = 32;
        for (int i = 0; i < n; i++) {
            double a  = anguloDecor + (Math.PI*2/n)*i;
            float alpha = (float)(0.5 + Math.sin(a*2+frame*0.04)*0.5);
            int px = (int)(cx + Math.cos(a)*(radio+28));
            int py = (int)(cy + Math.sin(a)*(radio+28));
            boolean esGrande = (i % 4 == 0);
            g2.setColor(esGrande
                ? new Color(200,50,50,(int)(alpha*200))
                : new Color(210,165,45,(int)(alpha*150)));
            int tam = esGrande ? 7 : 4;
            g2.fillOval(px-tam/2, py-tam/2, tam, tam);
            // Linea al anillo
            g2.setColor(new Color(160,130,40,(int)(alpha*80)));
            g2.setStroke(new BasicStroke(0.8f));
            int bx = (int)(cx + Math.cos(a)*(radio+6));
            int by = (int)(cy + Math.sin(a)*(radio+6));
            g2.drawLine(bx,by,px,py);
        }
        // Anillo doble externo
        g2.setStroke(new BasicStroke(1.5f));
        g2.setColor(new Color(180,140,40,50));
        g2.drawOval(cx-radio-18, cy-radio-18, (radio+18)*2, (radio+18)*2);
        g2.setColor(new Color(180,140,40,25));
        g2.drawOval(cx-radio-24, cy-radio-24, (radio+24)*2, (radio+24)*2);
    }

    private void pintarDohyo(Graphics2D g2, int cx, int cy, int radio) {
        // Sombra
        g2.setColor(new Color(0,0,0,120));
        g2.fillOval(cx-radio-4, cy-radio+14, (radio+4)*2, (radio+4)*2);

        // Borde paja de arroz con textura
        for (int r = radio+10; r >= radio; r--) {
            float t = (float)(r - radio) / 10f;
            Color bc = new Color(
                (int)(C_BORDE2.getRed()  + (C_BORDE.getRed()  - C_BORDE2.getRed()) *t),
                (int)(C_BORDE2.getGreen()+ (C_BORDE.getGreen()- C_BORDE2.getGreen())*t),
                (int)(C_BORDE2.getBlue() + (C_BORDE.getBlue() - C_BORDE2.getBlue())*t)
            );
            g2.setColor(bc);
            g2.drawOval(cx-r, cy-r, r*2, r*2);
        }

        // Arena con pulso
        int rp = (int)(radio * pulso);
        RadialGradientPaint arena = new RadialGradientPaint(
            cx-rp/4f, cy-rp/4f, rp*1.5f,
            new float[]{0f,0.35f,0.7f,1f},
            new Color[]{
                new Color(238,215,165), new Color(225,195,140),
                C_ARENA, C_ARENA2
            }
        );
        g2.setPaint(arena);
        g2.fillOval(cx-rp, cy-rp, rp*2, rp*2);

        // Textura de tierra (lineas de arena)
        g2.setStroke(new BasicStroke(0.6f));
        for (int i = -rp+4; i < rp; i += 7) {
            double dx2 = Math.sqrt(Math.max(0, (double)rp*rp - (double)i*i));
            g2.setColor(new Color(160,125,70, 18 + (int)(Math.abs(i)%3)*5));
            g2.drawLine((int)(cx-dx2), cy+i, (int)(cx+dx2), cy+i);
        }

        // Circulos concentricos de polvo
        for (int r2 = rp/4; r2 < rp; r2 += rp/4) {
            g2.setColor(new Color(140,110,60,15));
            g2.drawOval(cx-r2, cy-r2, r2*2, r2*2);
        }

        // Shikiri-sen con detalle
        g2.setColor(new Color(95,58,18));
        g2.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(cx-20, cy, cx-20, cy+12);
        g2.drawLine(cx-20, cy+6, cx+20, cy+6);
        g2.drawLine(cx+20, cy, cx+20, cy+12);

        // Borde del ring brillante
        g2.setStroke(new BasicStroke(3f));
        g2.setColor(C_BORDE);
        g2.drawOval(cx-radio, cy-radio, radio*2, radio*2);
        g2.setStroke(new BasicStroke(1f));
        g2.setColor(new Color(200,160,80,60));
        g2.drawOval(cx-radio+3, cy-radio+3, (radio-3)*2, (radio-3)*2);

        // Efecto de luz en el ring
        RadialGradientPaint luz = new RadialGradientPaint(
            cx-rp*0.3f, cy-rp*0.4f, rp*0.8f,
            new float[]{0f,1f},
            new Color[]{new Color(255,250,220,60), new Color(255,220,150,0)}
        );
        g2.setPaint(luz);
        g2.fillOval(cx-rp, cy-rp, rp*2, rp*2);

        // Flash de impacto
        if (frameImpacto > 0) {
            float a = frameImpacto/22f;
            int ri1 = (int)(radio*(1+(22-frameImpacto)*0.025));
            int ri2 = (int)(radio*(1+(22-frameImpacto)*0.045));
            g2.setColor(new Color(255,220,80,(int)(a*100)));
            g2.setStroke(new BasicStroke(5f));
            g2.drawOval(cx-ri1, cy-ri1, ri1*2, ri1*2);
            g2.setColor(new Color(255,150,30,(int)(a*60)));
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(cx-ri2, cy-ri2, ri2*2, ri2*2);
        }
    }

    private void pintarSal(Graphics2D g2, int cx, int cy) {
        for (float[] p : particulas) {
            float a = Math.max(0, Math.min(1, p[5]));
            int tam = (int)p[4];
            int px  = cx - 155 + (int)p[0];
            int py  = cy - 130 + (int)p[1];
            if (p[6] == 0) {
                // Sal blanca
                g2.setColor(new Color(245,245,255,(int)(a*190)));
                g2.fillOval(px, py, tam, tam);
            } else if (p[6] == 1) {
                // Polvo dorado
                g2.setColor(new Color(220,185,80,(int)(a*140)));
                g2.fillOval(px, py, tam+1, tam+1);
            } else {
                // Brillo
                g2.setColor(new Color(255,255,255,(int)(a*220)));
                g2.drawLine(px, py, px+(int)p[2]*3, py+(int)p[3]*3);
            }
        }
    }

    private void pintarOndas(Graphics2D g2, int cx, int cy) {
        for (float[] o : ondas) {
            if (o[3] <= 0) continue;
            int alpha = (int)(o[3]*150);
            g2.setStroke(new BasicStroke(3f));
            g2.setColor(new Color(255,200,50,alpha));
            int r = (int)o[2];
            g2.drawOval(cx-r, cy-r, r*2, r*2);
            g2.setColor(new Color(255,120,20,alpha/2));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(cx-r-6, cy-r-6, (r+6)*2, (r+6)*2);
        }
    }

    private void pintarEspera(Graphics2D g2, int cx, int cy, int radio) {
        // Kanji central pulsante con glow
        float pulsoA = (float)(0.55 + Math.sin(frame*0.04)*0.45);
        g2.setFont(new Font("Serif", Font.BOLD, 46));
        FontMetrics fm = g2.getFontMetrics();
        String k = "相撲";
        int kx = cx - fm.stringWidth(k)/2, ky = cy+10;
        // Glow capas oscuras (visibles sobre arena dorada)
        for (int d = 8; d >= 1; d--) {
            g2.setColor(new Color(60,20,0,(int)(pulsoA*25*d/8)));
            g2.drawString(k, kx+d, ky+d); g2.drawString(k, kx-d, ky-d);
            g2.drawString(k, kx+d, ky-d); g2.drawString(k, kx-d, ky+d);
        }
        // Texto kanji en rojo oscuro visible sobre arena
        g2.setColor(new Color(150,20,20,(int)(pulsoA*240)));
        g2.drawString(k, kx, ky);

        // Texto de espera — fondo semitransparente oscuro para legibilidad
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        fm = g2.getFontMetrics();
        String msg = "Esperando luchadores...";
        int tx = cx - fm.stringWidth(msg)/2;
        int ty = cy + radio - 14;
        // Fondo del texto
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRoundRect(tx - 8, ty - fm.getAscent() - 2, fm.stringWidth(msg) + 16, fm.getHeight() + 4, 6, 6);
        // Texto blanco sobre fondo oscuro
        g2.setColor(Color.WHITE);
        g2.drawString(msg, tx, ty);
    }

    private void pintarCombate(Graphics2D g2, int cx, int cy, int radio) {
        // Rayos de energia entre luchadores
        float energiaA = (float)(0.3+Math.sin(frame*0.15)*0.3);
        g2.setStroke(new BasicStroke(1.2f));
        for (int i = 0; i < 5; i++) {
            double sinV = Math.sin(frame*0.12+i*1.2);
            int ym = cy + (int)(sinV*8);
            g2.setColor(new Color(255,200,50,(int)(energiaA*(100-i*15))));
            g2.drawLine(cx-radio/2+15, ym, cx+radio/2-15, ym);
        }

        // Polvo bajo los luchadores (impacto suelo)
        for (int side = -1; side <= 1; side += 2) {
            int lx = cx + side*(radio/2);
            for (int d = 5; d >= 1; d--) {
                g2.setColor(new Color(200,170,110,20*d));
                g2.fillOval(lx-d*5, cy+20, d*10, d*4);
            }
        }

        // Luchador 1 (azul, izquierda)
        pintarLuchador(g2, cx-radio/2, cy+(int)offsetL1, C_L1, C_L1D, "L1", true);
        // Luchador 2 (rojo, derecha)
        pintarLuchador(g2, cx+radio/2, cy+(int)offsetL2, C_L2, C_L2D, "L2", false);

        // Kimarite con glow
        if (!ultimaKimarite.isEmpty() && alphaKim > 0) {
            pintarKimarite(g2, cx, cy-radio+20);
        }
    }

    private void pintarLuchador(Graphics2D g2, int x, int y,
                                  Color c, Color cd, String etiq, boolean derecha) {
        // Sombra
        g2.setColor(new Color(0,0,0,70));
        g2.fillOval(x-18, y+24, 36, 10);

        // Piernas
        g2.setStroke(new BasicStroke(7f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        GradientPaint gp = new GradientPaint(x-20,y,c,x+20,y+40,cd);
        g2.setPaint(gp);
        double legSwing = Math.sin(frame*0.09)*6;
        g2.drawLine(x-7, y+20, x-9+(int)legSwing, y+38);
        g2.drawLine(x+7, y+20, x+9-(int)legSwing, y+38);

        // Cuerpo
        RadialGradientPaint body = new RadialGradientPaint(x-8,y-8,28,
            new float[]{0f,1f}, new Color[]{c,cd});
        g2.setPaint(body);
        g2.fillOval(x-20, y-22, 40, 42);

        // Musculatura (detalle)
        g2.setStroke(new BasicStroke(1f));
        g2.setColor(new Color(c.getRed(),c.getGreen(),c.getBlue(),60));
        g2.drawArc(x-14,y-18,28,28,30,120);

        // Brazos
        g2.setStroke(new BasicStroke(6f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        g2.setPaint(gp);
        double armSwing = Math.sin(frame*0.09+Math.PI)*5;
        if (derecha) {
            g2.drawLine(x+14, y-10, x+30, y-2+(int)armSwing);
            g2.drawLine(x-14, y-10, x-25, y+5);
        } else {
            g2.drawLine(x-14, y-10, x-30, y-2+(int)armSwing);
            g2.drawLine(x+14, y-10, x+25, y+5);
        }

        // Cabeza con detalle facial
        RadialGradientPaint cabeza = new RadialGradientPaint(x-5,y-52,14,
            new float[]{0f,1f}, new Color[]{c.brighter(),cd});
        g2.setPaint(cabeza);
        g2.fillOval(x-12, y-58, 24, 24);
        // Cara
        g2.setColor(new Color(255,220,180));
        g2.fillOval(x-9, y-55, 18, 18);
        // Ojos
        g2.setColor(new Color(30,20,10));
        if (derecha) { g2.fillOval(x,y-51,3,3); g2.fillOval(x+5,y-51,3,3); }
        else         { g2.fillOval(x-8,y-51,3,3); g2.fillOval(x-3,y-51,3,3); }
        // Expresion agresiva
        g2.setStroke(new BasicStroke(1.2f));
        g2.setColor(new Color(30,20,10));
        if (derecha) g2.drawArc(x,y-46,8,4,0,-180);
        else         g2.drawArc(x-8,y-46,8,4,0,-180);

        // Topknot (moño)
        g2.setColor(cd);
        g2.fillOval(x-4, y-62, 8, 8);

        // Mawashi (cinturon)
        g2.setStroke(new BasicStroke(5f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        GradientPaint maw = new GradientPaint(x-20,y-8,C_DORADO2,x+20,y+8,C_DORADO);
        g2.setPaint(maw);
        g2.drawArc(x-20, y-12, 40, 26, 0, -180);
        // Nudo del mawashi
        g2.setColor(C_DORADO2);
        g2.fillOval(x-6, y-14, 12, 12);

        // Etiqueta
        g2.setFont(new Font("SansSerif", Font.BOLD, 10));
        g2.setColor(new Color(255,255,255,200));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(etiq, x-fm.stringWidth(etiq)/2, y+52);
    }

    private void pintarKimarite(Graphics2D g2, int x, int y) {
        g2.setFont(new Font("Serif", Font.BOLD|Font.ITALIC, 16));
        FontMetrics fm = g2.getFontMetrics();
        int tx = x - fm.stringWidth(ultimaKimarite)/2;
        // Glow multicapa
        for (int d = 5; d >= 1; d--) {
            g2.setColor(new Color(255,185,30,(int)(alphaKim*30*d/5)));
            g2.drawString(ultimaKimarite, tx+d, y+d);
            g2.drawString(ultimaKimarite, tx-d, y-d);
        }
        g2.setColor(new Color(255,230,90,(int)(alphaKim*255)));
        g2.drawString(ultimaKimarite, tx, y);
    }

    private void pintarGanador(Graphics2D g2, int cx, int cy, int radio, int w, int h) {
        // Confeti
        g2.setStroke(new BasicStroke(1f));
        for (float[] cf : confeti) {
            if (cf[5] <= 0) continue;
            g2.setColor(new Color((int)cf[6],(int)cf[7],(int)cf[8],(int)(cf[5]*220)));
            int tam = (int)cf[4];
            // Formas variadas
            if ((int)cf[6] % 3 == 0) g2.fillOval((int)cf[0],(int)cf[1],tam,tam/2);
            else if ((int)cf[7] % 3 == 0) g2.fillRect((int)cf[0],(int)cf[1],tam,tam);
            else g2.drawLine((int)cf[0],(int)cf[1],(int)cf[0]+tam,(int)cf[1]+tam/2);
        }

        // Rayos dorados desde el centro
        int nr = 20;
        for (int i = 0; i < nr; i++) {
            double a = (Math.PI*2/nr)*i + frameGanador*0.04;
            float alpha = (float)(0.4+Math.sin(a*2+frameGanador*0.06)*0.3);
            g2.setStroke(new BasicStroke(2.5f));
            g2.setColor(new Color(255,200,50,(int)(alpha*140)));
            int x1 = (int)(cx+Math.cos(a)*(radio-15));
            int y1 = (int)(cy+Math.sin(a)*(radio-15));
            int x2 = (int)(cx+Math.cos(a)*(radio+22));
            int y2 = (int)(cy+Math.sin(a)*(radio+22));
            g2.drawLine(x1,y1,x2,y2);
        }

        // Luchador ganador en el centro con brillo
        pintarLuchador(g2, cx, cy, C_DORADO, new Color(175,115,20), "🏆", true);

        // Aura dorada alrededor del ganador
        float aura = (float)(0.5+Math.sin(frameGanador*0.08)*0.5);
        RadialGradientPaint au = new RadialGradientPaint(cx, cy, 55,
            new float[]{0f,0.6f,1f},
            new Color[]{
                new Color(255,215,50,(int)(aura*80)),
                new Color(255,140,20,(int)(aura*40)),
                new Color(0,0,0,0)
            });
        g2.setPaint(au);
        g2.fillOval(cx-55,cy-55,110,110);

        // Texto del ganador pulsante con glow
        float puls = (float)(0.88+Math.sin(frameGanador*0.1)*0.12);
        g2.setFont(new Font("Serif", Font.BOLD, (int)(15*puls)));
        FontMetrics fm = g2.getFontMetrics();
        String txt = "¡" + ganador.toUpperCase() + "!";
        int tx = cx-fm.stringWidth(txt)/2, ty = cy+radio-10;
        for (int d = 4; d >= 1; d--) {
            g2.setColor(new Color(200,100,10,(int)(puls*40*d)));
            g2.drawString(txt,tx+d,ty+d); g2.drawString(txt,tx-d,ty-d);
        }
        GradientPaint gp = new GradientPaint(tx,ty-15,C_DORADO2,tx,ty,C_DORADO);
        g2.setPaint(gp);
        g2.drawString(txt, tx, ty);
    }

    private void pintarTitulo(Graphics2D g2, int cx, int h) {
        g2.setFont(new Font("Serif", Font.BOLD, 13));
        String t = "土俵   D O H Y O   土俵";
        FontMetrics fm = g2.getFontMetrics();
        // Sombra
        g2.setColor(new Color(0,0,0,180));
        g2.drawString(t, cx-fm.stringWidth(t)/2+1, h-5);
        // Texto
        g2.setColor(new Color(190,30,30,200));
        g2.drawString(t, cx-fm.stringWidth(t)/2, h-6);
    }

    /** Activa/desactiva el combate. @param activo true si hay combate. */
    public void setCombateActivo(boolean activo) { this.combateActivo = activo; }

    /** Establece la ultima kimarite con efectos. @param k Nombre de la tecnica. */
    public void setUltimaKimarite(String k) {
        ultimaKimarite = k; alphaKim = 0f; fadeIn = true; frameImpacto = 22;
        lanzarOnda(155, 130);
    }

    /** Establece el ganador. null reinicia. @param g Nombre del ganador. */
    public void setGanador(String g) {
        ganador = g; frameGanador = 0;
        if (g != null) combateActivo = false;
    }
}
