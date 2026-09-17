package vista;
//java fx para que se cargue lo que 
//mostrar en pantalla
import java.awt.*;
import javax.swing.*;
import modelos.Personaje;
//aqui nosotros estamos definimos el panel visual del juego 
//mostramos la barrera de vida, estamina y mana del personaje, ademas de dibujar al personaje en el panel
public class PanelJuego extends JPanel {
    private final Personaje jugador;
    private final PanelHUD hud;
    private Timer timer;
    //definimos el panel de juego, con un personaje 
    // y un panel de hud, ademas de un timer para actualizar el juego
    public PanelJuego(Personaje jugador) {
        this.jugador = jugador;
        this.hud = new PanelHUD();
        //determinamos el alto y ancho de la ventana
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.DARK_GRAY);
        setFocusable(true);

        // Loop del juego: ~60 fps 
        // determinamos el timer del juego
        timer = new Timer(16, e -> {
            jugador.actualizar();
            jugador.recargarEstamina();
            repaint(); // dispara paintComponent
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;

    //dibujar el nombre del personaje 
    //en un punto en cierta posicion
    //alpha : 0.0f estamos determinando que tan opaco es el punto 
    //que mostraria el nombre
    Composite compositeOriginal = g2.getComposite();
    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.0f)); // 1% opaco
    g2.setColor(Color.WHITE);
    g2.fillOval((int) jugador.getX(), (int) jugador.getY(), 30, 30);
    g2.setComposite(compositeOriginal); // restaurar, para que el HUD se dibuje normal

    // dibujar el HUD encima de todo
    //en la esquina izquierda
    hud.dibujar(g2, jugador);
}
}