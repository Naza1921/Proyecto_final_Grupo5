package vista;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MenuPausa extends JPanel {
    private static final int ANCHO_BOTON = 300;
    private static final int ALTO_BOTON = 112;

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
        setPreferredSize(new Dimension(800, 600));
        setBackground(new Color(35, 35, 45));
        setLayout(new GridBagLayout());

        JLabel titulo = new JLabel("Juego en pausa");
        titulo.setFont(new Font("Serif", Font.BOLD, 36));
        titulo.setForeground(Color.WHITE);

        GridBagConstraints restricciones = new GridBagConstraints();
        restricciones.gridwidth = GridBagConstraints.REMAINDER;
        restricciones.insets = new Insets(12, 0, 12, 0);
        restricciones.anchor = GridBagConstraints.CENTER;

        add(titulo, restricciones);
        add(btnReanudar, restricciones);
        add(btnVolverAlMenu, restricciones);
        add(btnSalir, restricciones);
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
