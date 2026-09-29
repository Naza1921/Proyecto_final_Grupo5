package vista;
//java fx para que se cargue lo que
//mostrar en pantalla
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import modelos.Personaje;

//aqui nosotros estamos definimos el panel visual del juego
//mostramos la barrera de vida, estamina y mana del personaje, ademas de dibujar al personaje en el panel
public class PanelJuego extends JPanel {
    private final Personaje jugador;
    private final PanelHUD hud;
    private Timer timer;

    // Fondo fijo del nivel (por ahora un solo escenario, sin gestor de niveles).
    private Image fondoImagen;

    // Sprite animado del personaje: 4 cuadros de caminata en fila.
    private final SpriteSheet spriteRata = new SpriteSheet("/assets/rata_idle_quieto.png", 4);
    // Cuadro actual de la animación y contador para controlar la velocidad del cambio de cuadro.
    private int frameActual = 0;
    private int contadorFrames = 0;

    //definimos el panel de juego, con un personaje
    // y un panel de hud, ademas de un timer para actualizar el juego
    public PanelJuego(Personaje jugador) {
        this.jugador = jugador;
        this.hud = new PanelHUD();
        //determinamos el alto y ancho de la ventana
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.DARK_GRAY);
        setFocusable(true);

        cargarFondo("/assets/Escenario_nivel1.jpeg");

        // Loop del juego: ~60 fps
        // determinamos el timer del juego
        timer = new Timer(16, e -> {
            jugador.actualizar();
            jugador.recargarEstamina();
            actualizarAnimacion();
            repaint(); // dispara paintComponent
        });
        timer.start();
    }

    // Carga la imagen de fondo del escenario desde los recursos.
    private void cargarFondo(String ruta) {
        java.net.URL urlFondo = getClass().getResource(ruta);
        if (urlFondo != null) {
            fondoImagen = new ImageIcon(urlFondo).getImage();
        } else {
            fondoImagen = null;
            System.err.println("No se encontró el fondo: " + ruta);
        }
    }

    // Avanza el cuadro de la animación de caminata cada cierta cantidad de ticks del timer.
    // El timer corre a ~60 fps (16 ms); cada 10 ticks (~160 ms) se pasa al siguiente cuadro.
    private void actualizarAnimacion() {
        contadorFrames++;
        if (contadorFrames >= 10) {
            contadorFrames = 0;
            frameActual = (frameActual + 1) % spriteRata.getCantidadFrames();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Fondo del escenario, escalado al tamaño del panel.
        if (fondoImagen != null) {
            g2.drawImage(fondoImagen, 0, 0, getWidth(), getHeight(), this);
        }

        // Dibujar primero el contorno negro (un poco más grande, detrás del sprite)
        // y encima el cuerpo del personaje, en la posición actual.
        BufferedImage contornoRata = spriteRata.getContorno(frameActual);
        BufferedImage cuadroRata = spriteRata.getFrame(frameActual);

        if (contornoRata != null) {
            g2.drawImage(contornoRata, (int) jugador.getX(), (int) jugador.getY(),
                contornoRata.getWidth(), contornoRata.getHeight(), this);
        }
        if (cuadroRata != null) {
            g2.drawImage(cuadroRata, (int) jugador.getX(), (int) jugador.getY(),
                cuadroRata.getWidth(), cuadroRata.getHeight(), this);
        }

        // dibujar el HUD encima de todo
        //en la esquina izquierda
        hud.dibujar(g2, jugador);
    }
}