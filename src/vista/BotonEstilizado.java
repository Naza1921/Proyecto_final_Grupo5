package vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
// Clase que representa un botón estilizado con colores personalizados y bordes redondeados
public class BotonEstilizado extends JButton {
    // Colores para los diferentes estados del botón
    private Color colorNormal = new Color(60, 60, 90);
    private Color colorHover = new Color(90, 90, 140);
    private Color colorPresionado = new Color(40, 40, 70);
    private Color colorActual;
    // Constructor del botón estilizado
    public BotonEstilizado(String texto) {
        super(texto);
        colorActual = colorNormal;
         // Configuración del botón
        setFont(new Font("Arial", Font.BOLD, 18));
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(220, 45));
        // Agregar un MouseListener para cambiar el color del botón según el estado del mouse
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                colorActual = colorHover;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                colorActual = colorNormal;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                colorActual = colorPresionado;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                colorActual = getModel().isRollover() ? colorHover : colorNormal;
                repaint();
            }
        });
    }
    // Sobrescribir el método paintComponent para dibujar el botón con colores personalizados 
    // y bordes redondeados
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(colorActual);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

        g2.setColor(new Color(255, 255, 255, 40));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);

        g2.dispose();
        super.paintComponent(g);
    }
}