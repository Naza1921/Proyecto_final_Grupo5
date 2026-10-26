package vista;

import javax.swing.*;
import java.awt.*;
import controlador.utilz.loadsave;

public class MenuPrincipal extends JPanel {
 //botones para el menu principal, jugar, instrucciones y salir
    private BotonEstilizado btnJugar;
    private BotonEstilizado btnOpciones;
    private BotonEstilizado btnTablero;
    private BotonEstilizado btnSalir;
    private Image fondo;
    private Image titulo;
 // Constructor del panel del menú principal
    public MenuPrincipal() {
        setPreferredSize(new Dimension(800, 600));
        setLayout(new GridBagLayout());

        fondo = loadsave.GetSpriteatlas("assets/Portada_rata.png");
        titulo = loadsave.GetSpriteatlas("assets/TITULO 2.png");
        


        // Crear y configurar los botones y el título
        // Crear y configurar los botones y el título
        
        //JLabel titulo = new JLabel("Ghosts and Rats");
        //titulo.setFont(new Font("Serif", Font.BOLD, 34));
        //titulo.setForeground(Color.BLACK);

        btnJugar = new BotonEstilizado("Jugar");
        btnOpciones = new BotonEstilizado("Opciones");
        btnTablero = new BotonEstilizado("Tablero");
        btnSalir = new BotonEstilizado("Salir");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(12, 0, 12, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        //add(titulo, gbc);
        add(btnJugar, gbc);
        add(btnOpciones, gbc);
        add(btnTablero, gbc);
        add(btnSalir, gbc);
    }
    // Sobrescribir el método paintComponent para dibujar el fondo del menú
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (fondo != null) {
            g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);

        }
        if (titulo != null) {
            g.drawImage(titulo, 0, 0, getWidth(), getHeight(), this);
        }

    }
    // Getters para los botones del menú principal
    public JButton getBtnJugar() { return btnJugar; }
    public JButton getBtnOpciones() { return btnOpciones; }
    public JButton getBtnTablero() { return btnTablero; }
    public JButton getBtnSalir() { return btnSalir; }
}