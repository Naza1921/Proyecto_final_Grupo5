package vista;

import java.awt.*;
import javax.swing.*;

public class MenuPrincipal extends JPanel {
 //botones para el menu principal, jugar, instrucciones y salir
    private BotonEstilizado btnJugar;
    private BotonEstilizado btnOpciones;
    private BotonEstilizado btnSalir;
    private Image fondo;
 // Constructor del panel del menú principal
    public MenuPrincipal() {
        setPreferredSize(new Dimension(500, 400));
        setLayout(new GridBagLayout());

        java.net.URL urlImagen = getClass().getResource("/assets/Portada_rata.png");
        if (urlImagen == null) {
            System.err.println("No se encontró /assets/Portada_rata.png en el classpath.");
            fondo = null;
        } else {
            fondo = new ImageIcon(urlImagen).getImage();
        }
        // Crear y configurar los botones y el título
        JLabel titulo = new JLabel("Ghosts and Rats");
        titulo.setFont(new Font("Serif", Font.BOLD, 34));
        titulo.setForeground(Color.BLACK);

        btnJugar = new BotonEstilizado("Jugar");
        btnOpciones = new BotonEstilizado("Opciones");
        btnSalir = new BotonEstilizado("Salir");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(12, 0, 12, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        add(titulo, gbc);
        add(btnJugar, gbc);
        add(btnOpciones, gbc);
        add(btnSalir, gbc);
    }
    // Sobrescribir el método paintComponent para dibujar el fondo del menú
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (fondo != null) {
            g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
        }
    }
    // Getters para los botones del menú principal
    public JButton getBtnJugar() { return btnJugar; }
    public JButton getBtnOpciones() { return btnOpciones; }
    public JButton getBtnSalir() { return btnSalir; }
}