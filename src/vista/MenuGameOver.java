package vista;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JButton;
import javax.swing.JPanel;
import controlador.utilz.loadsave;

public final class MenuGameOver extends JPanel {
    private static final int ANCHO_BOTON = 252;
    private static final int ALTO_BOTON = 104;
    private static final int SEPARACION_BOTONES = 4;
    private static final int MARGEN_INFERIOR = 18;

    private final BufferedImage fondo;
    private final BotonImagen btnVolver;
    private final BotonImagen btnSalir;

    public MenuGameOver() {
        fondo = loadsave.GetSpriteatlas("assets/GAME_OVER.png");
        btnVolver = new BotonImagen(
            "Volver al menú", "assets/botones/boton_volver.png",
            ANCHO_BOTON, ALTO_BOTON
        );
        btnSalir = new BotonImagen(
            "Salir", "assets/botones/boton_salir.png",
            ANCHO_BOTON, ALTO_BOTON
        );

        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.BLACK);
        setOpaque(true);
        setLayout(null);
        add(btnVolver);
        add(btnSalir);
    }

    @Override
    public void doLayout() {
        int x = (getWidth() - ANCHO_BOTON) / 2;
        int ySalir = getHeight() - MARGEN_INFERIOR - ALTO_BOTON;
        int yVolver = ySalir - SEPARACION_BOTONES - ALTO_BOTON;
        btnVolver.setBounds(x, yVolver, ANCHO_BOTON, ALTO_BOTON);
        btnSalir.setBounds(x, ySalir, ANCHO_BOTON, ALTO_BOTON);
    }

    @Override
    protected void paintComponent(Graphics grafico) {
        super.paintComponent(grafico);
        Graphics2D g = (Graphics2D) grafico.create();
        g.setRenderingHint(
            RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BILINEAR
        );
        double escala = Math.max(
            (double) getWidth() / fondo.getWidth(),
            (double) getHeight() / fondo.getHeight()
        );
        int ancho = (int) Math.round(fondo.getWidth() * escala);
        int alto = (int) Math.round(fondo.getHeight() * escala);
        g.drawImage(
            fondo,
            (getWidth() - ancho) / 2,
            (getHeight() - alto) / 2,
            ancho,
            alto,
            this
        );
        g.dispose();
    }

    public JButton getBtnVolver() {
        return btnVolver;
    }

    public JButton getBtnSalir() {
        return btnSalir;
    }
}
