package vista;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import controlador.utilz.loadsave;

public class MenuPausa extends JPanel {
    private static final int ANCHO_BOTON = 252;
    private static final int ALTO_BOTON = 104;
    private final BufferedImage fondo;

    private final BotonImagen btnReanudar = new BotonImagen(
        "Continuar", "assets/botones/boton_continuar.png", ANCHO_BOTON, ALTO_BOTON
    );
    private final BotonImagen btnVolverAlMenu = new BotonImagen(
        "Volver al menú", "assets/botones/boton_volver.png", ANCHO_BOTON, ALTO_BOTON
    );
    private final BotonImagen btnSalir = new BotonImagen(
        "Salir", "assets/botones/boton_salir.png", ANCHO_BOTON, ALTO_BOTON
    );

    public MenuPausa() {
        fondo = loadsave.GetSpriteatlas("assets/menu_pausa.png");
        setPreferredSize(new Dimension(800, 600));
        setBackground(new Color(35, 35, 45));
        setOpaque(true);
        setLayout(new GridBagLayout());

        JLabel titulo = new JLabel("Juego en pausa");
        titulo.setFont(new Font("Serif", Font.BOLD, 36));
        titulo.setForeground(Color.WHITE);

        GridBagConstraints restricciones = new GridBagConstraints();
        restricciones.gridwidth = GridBagConstraints.REMAINDER;
        restricciones.insets = new Insets(7, 0, 7, 0);
        restricciones.anchor = GridBagConstraints.CENTER;

        add(titulo, restricciones);
        add(btnReanudar, restricciones);
        add(btnVolverAlMenu, restricciones);
        add(btnSalir, restricciones);
    }

    @Override
    protected void paintComponent(Graphics grafico) {
        super.paintComponent(grafico);
        if (getWidth() <= 0 || getHeight() <= 0) {
            return;
        }

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

    public JButton getBtnReanudar() {
        return btnReanudar;
    }

    public JButton getBtnVolverAlMenu() {
        return btnVolverAlMenu;
    }

    public JButton getBtnSalir() {
        return btnSalir;
    }
}
