package vista;
//java fx para que se cargue lo que
//mostrar en pantalla
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

import controlador.levelmanager;
import controlador.GestorAnimacionSprite;
import controlador.utilz.loadsave;
import modelos.partida.Puntaje;
import modelos.personajes.Personaje;
import modelos.personajes.Hitbox;
import controlador.ColisionNivel;

//aqui nosotros estamos definimos el panel visual del juego
//mostramos la barrera de vida, estamina y mana del personaje, ademas de dibujar al personaje en el panel
public class PanelJuego extends JPanel {
    private static final long DURACION_FRAME_NANOS = 160_000_000L;

    private final Personaje jugador;
    private final Puntaje modeloPuntaje;
    private final Hitbox hitboxJugador;
    private final PanelHUD hud;
    private levelmanager manejadornivel = new levelmanager(); // Inicializar sin parámetros
    private static final int ANCHO_RATA = 25;
    private static final int ALTO_RATA = 20;
    private static final int TAM_TILE = 30;          // 960x540 / 32x18 tiles = 30 px exactos
    private final ColisionNivel colision;       // ver nota abajo
    


    // Fondo fijo del nivel (por ahora un solo escenario, sin gestor de niveles).
    private Image fondoImagen;

    // El gestor mantiene el ritmo de la animación y entrega frames con un anclaje común.
    private final GestorAnimacionSprite animacionRata;

    //definimos el panel de juego, con un personaje
    // y un panel de hud, ademas de un timer para actualizar el juego
    public PanelJuego(Personaje jugador, Puntaje modeloPuntaje) {
        if (jugador == null || modeloPuntaje == null) {
            throw new IllegalArgumentException("Se requieren un personaje y el modelo de puntaje");
        }
        this.jugador = jugador;
        this.modeloPuntaje = modeloPuntaje;
        this.hitboxJugador = new Hitbox(
        (float) jugador.getX(),
        (float) jugador.getY(),
        ANCHO_RATA,
        ALTO_RATA
);
        this.hud = new PanelHUD();
        SpriteSheet spriteRata = SpritesRatas.cargar(jugador.getTipoRata());
        this.animacionRata = new GestorAnimacionSprite(
            SpritesRatas.crearFramesAlineados(spriteRata, 0), DURACION_FRAME_NANOS
        );
        //determinamos el alto y ancho de la ventana
        setPreferredSize(new Dimension(960, 540));
        setBackground(Color.DARK_GRAY);
        setFocusable(true);

        cargarFondo("/assets/escenarios/Nivel_1_Top_Floor X.png");
        this.colision = new ColisionNivel(manejadornivel.getNivel(), TAM_TILE, ANCHO_RATA, ALTO_RATA);
    }

    public double getAnchoAreaVisual() {
        return getWidth();
    }

    public double getAltoAreaVisual() {
        return getHeight();
    }

    public double getAnchoSprite() {
        return animacionRata.getAncho();
    }

    public double getAltoSprite() {
        return animacionRata.getAlto();
    }

    public void actualizarPresentacion() {
        animacionRata.actualizar();
        repaint();
    }

    public int getAnchoHitbox() { 
        return ANCHO_RATA; 
    }

    //Carga la imagen de fondo del escenario desde los recursos.
    private void cargarFondo(String ruta) {
        try {
            String nombreRecurso = ruta.startsWith("/") ? ruta.substring(1) : ruta;
            fondoImagen = loadsave.GetSpriteatlas(nombreRecurso);
        } catch (IllegalArgumentException error) {
            fondoImagen = null;
            System.err.println("No se pudo cargar el fondo " + ruta + ": " + error.getMessage());
        }
    }
    public void resolverColisiones() {
    colision.resolver(jugador);
    hitboxJugador.actualizar((float) jugador.getX(), (float) jugador.getY());
}

@Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;

    // 1. Fondo, estirado al tamaño del panel
    if (fondoImagen != null) {
        g2.drawImage(fondoImagen, 0, 0, getWidth(), getHeight(), this);
    }

    // 2. Nivel encima (los tiles de aire no se dibujan, así el fondo se ve a través)
    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    manejadornivel.draw(g2, TAM_TILE);

        // Dibujamos el frame original; el contorno amarillo se reserva como halo de estamina del HUD.

BufferedImage cuadroRata = animacionRata.getFrameActual();

if (cuadroRata != null) {
    g2.setRenderingHint(
        RenderingHints.KEY_INTERPOLATION,
        RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
    );

    double escala = Math.min(
        (double) ANCHO_RATA / cuadroRata.getWidth(),
        (double) ALTO_RATA / cuadroRata.getHeight()
    );

    int anchoDibujo = (int) Math.round(cuadroRata.getWidth() * escala);
    int altoDibujo = (int) Math.round(cuadroRata.getHeight() * escala);

    g2.drawImage(
        cuadroRata,
        (int) jugador.getX(),
        (int) jugador.getY() + (ALTO_RATA - altoDibujo),
        anchoDibujo,
        altoDibujo,
        this
    );
    // Actualizamos la posición de la hitbox con la del personaje.
    hitboxJugador.actualizar(
    (float) jugador.getX(),
    (float) jugador.getY()
    );
    // Dibujamos el rectángulo rosa encima del escenario y la rata.
    hitboxJugador.dibujar(g2);
    }



        // Dibujar el HUD en las esquinas superiores de la pantalla.
        hud.dibujar(g2, jugador, modeloPuntaje.getTotal(), getWidth());
    }
}