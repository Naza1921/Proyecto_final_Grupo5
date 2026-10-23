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
    private final BotonEstilizado btnReanudar = new BotonEstilizado("Reanudar");
    private final BotonEstilizado btnVolverAlMenu = new BotonEstilizado("Volver al menú");

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
    }

    public JButton getBtnReanudar() {
        return btnReanudar;
    }

    public JButton getBtnVolverAlMenu() {
        return btnVolverAlMenu;
    }
}
