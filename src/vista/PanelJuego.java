package vista;
//java fx para que se cargue lo que
//mostrar en pantalla
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;

import controlador.levelmanager;
import controlador.GestorAnimacionSprite;
import controlador.utilz.loadsave;
import modelos.enemigos.fantasma;
import modelos.partida.Puntaje;
import modelos.personajes.Personaje;
import modelos.personajes.Hitbox;
import modelos.items.PowerUp;
import controlador.ColisionNivel;
import controlador.ColisionNivel;
import controlador.GestorSecciones;

//aqui nosotros estamos definimos el panel visual del juego
//mostramos la barrera de vida, estamina y mana del personaje, ademas de dibujar al personaje en el panel
public class PanelJuego extends JPanel {
    private static final long DURACION_FRAME_NANOS = 160_000_000L;
    private static final double ZOOM_CAMARA = 2.0;
    private static final double SUAVIZADO_CAMARA_SEGUNDOS = 0.12;

    private final Personaje jugador;
    private final fantasma enemigo;
    private final Camara camara = new Camara(ZOOM_CAMARA, SUAVIZADO_CAMARA_SEGUNDOS);
    private final Hitbox hitboxJugador;
    private final PanelHUD hud;
    private List<PowerUp> powerUps = Collections.emptyList();
    private long tiempoPartidaMillis;
    private int puntajePresentado;
    private levelmanager manejadornivel = new levelmanager(); // Inicializar sin parámetros   
    private static final int TAM_TILE = 30;
    private static final int COLS_NIVEL = 42;
    private static final int FILAS_NIVEL = 23;
    private static final int ANCHO_RATA = 25;
    private static final int ALTO_RATA = 20;
    private ColisionNivel colision;
    private final GestorSecciones secciones;
    private boolean nivelCompletado;

    // Fondo fijo del nivel (por ahora un solo escenario, sin gestor de niveles).
    private Image fondoImagen;

    // El gestor mantiene el ritmo de la animación y entrega frames con un anclaje común.
    private final GestorAnimacionSprite animacionRata;
    private final GestorAnimacionSprite animacionFantasmaDerecha;
    private final GestorAnimacionSprite animacionFantasmaIzquierda;

    //definimos el panel de juego, con un personaje
    // y un panel de hud, ademas de un timer para actualizar el juego
public PanelJuego(Personaje jugador, Puntaje modeloPuntaje, fantasma enemigo) {
    if (jugador == null || modeloPuntaje == null || enemigo == null) {
        throw new IllegalArgumentException("Se requieren un personaje, puntaje y enemigo");
    }
    this.jugador = jugador;
    this.enemigo = enemigo;
    this.puntajePresentado = modeloPuntaje.getTotal();

    // Posición inicial primero (esa celda debe ser aire en el PNG de la sección 1)
    jugador.setX(2 * TAM_TILE);
    jugador.setY(1 * TAM_TILE);

    this.hitboxJugador = new Hitbox(
        (float) jugador.getX(), (float) jugador.getY(), ANCHO_RATA, ALTO_RATA);

    this.hud = new PanelHUD();
    SpriteSheet spriteRata = SpritesRatas.cargar(jugador.getTipoRata());
    this.animacionRata = new GestorAnimacionSprite(
        SpritesRatas.crearFramesAlineados(spriteRata, 0), DURACION_FRAME_NANOS
    );
    this.animacionFantasmaDerecha = new GestorAnimacionSprite(
        SpritesFantasma.cargarPatrulla(true), 180_000_000L
    );
    this.animacionFantasmaIzquierda = new GestorAnimacionSprite(
        SpritesFantasma.cargarPatrulla(false), 180_000_000L
    );

    this.colision = new ColisionNivel(
        manejadornivel.getNivel(), TAM_TILE, ANCHO_RATA, ALTO_RATA);
    this.secciones = new GestorSecciones(
        manejadornivel, colision, TAM_TILE, ANCHO_RATA, ALTO_RATA);

    setPreferredSize(new Dimension(COLS_NIVEL * TAM_TILE, FILAS_NIVEL * TAM_TILE));
    setBackground(Color.DARK_GRAY);
    setFocusable(true);

    cargarFondo("/assets/escenarios/Nivel_1_Top_Floor X.png");
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
        animacionFantasmaDerecha.actualizar();
        animacionFantasmaIzquierda.actualizar();
        repaint();
    }

    public void actualizarPowerUps(List<PowerUp> powerUps) {
        if (powerUps == null) {
            throw new IllegalArgumentException("La lista de power-ups debe ser válida");
        }
        for (PowerUp powerUp : powerUps) {
            if (powerUp == null) {
                throw new IllegalArgumentException("La lista de power-ups debe ser válida");
            }
        }
        this.powerUps = Collections.unmodifiableList(new ArrayList<>(powerUps));
        repaint();
    }

    public void actualizarTiempoPartida(long tiempoPartidaMillis) {
        if (tiempoPartidaMillis < 0) {
            throw new IllegalArgumentException("El tiempo de partida no puede ser negativo");
        }
        this.tiempoPartidaMillis = tiempoPartidaMillis;
        repaint();
    }

    public void actualizarPuntajePresentado(int puntaje) {
        if (puntaje < 0) {
            throw new IllegalArgumentException("El puntaje presentado no puede ser negativo");
        }
        puntajePresentado = puntaje;
        repaint();
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


@Override
protected void paintComponent(Graphics g) {
super.paintComponent(g);
Graphics2D g2 = (Graphics2D) g;

AffineTransform transformacionPantalla = g2.getTransform();
double anchoMundo = manejadornivel.getNivel().getAncho() * TAM_TILE;
double altoMundo = manejadornivel.getNivel().getAlto() * TAM_TILE;
g2.transform(camara.actualizar(
    jugador.getX() + ANCHO_RATA / 2.0,
    jugador.getY() + ALTO_RATA / 2.0,
    getWidth(),
    getHeight(),
    anchoMundo,
    altoMundo
));

g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
if (fondoImagen != null) g2.drawImage(fondoImagen, 0, 0, (int) anchoMundo, (int) altoMundo, this);
manejadornivel.draw(g2, TAM_TILE);
dibujarPowerUps(g2);
        // Dibujamos el frame original; el contorno amarillo se reserva como halo de estamina del HUD.

GestorAnimacionSprite animacionFantasma = enemigo.estaMoviendoseDerecha()
    ? animacionFantasmaDerecha
    : animacionFantasmaIzquierda;
BufferedImage cuadroFantasma = animacionFantasma.getFrameActual();
g2.drawImage(cuadroFantasma, (int) enemigo.getX(), (int) enemigo.getY(),
    (int) enemigo.getAncho(), (int) enemigo.getAlto(), this);

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
    //hitboxJugador.dibujar(g2);
    }



        // Dibujar el HUD en las esquinas superiores de la pantalla.
        g2.setTransform(transformacionPantalla);
        hud.dibujar(g2, jugador, puntajePresentado, tiempoPartidaMillis, getWidth());
    if (nivelCompletado) {
    g2.setColor(new Color(0, 0, 0, 170));
    g2.fillRect(0, 0, getWidth(), getHeight());
    g2.setColor(Color.WHITE);
    g2.setFont(g2.getFont().deriveFont(Font.BOLD, 40f));
    String texto = "¡Nivel completado!";
    FontMetrics fm = g2.getFontMetrics();
    g2.drawString(texto, (getWidth() - fm.stringWidth(texto)) / 2, getHeight() / 2);
}
    }
    public GestorSecciones getGestorSecciones() { return secciones; }
    public int getAnchoHitbox() { return ANCHO_RATA; }
    public int getAltoHitbox() { return ALTO_RATA; }
    public double getAnchoNivelPx() {
    return manejadornivel.getNivel().getAncho() * TAM_TILE;
    }

    public void mostrarNivelCompletado() {
    nivelCompletado = true;
    repaint();
    }  

    private void dibujarPowerUps(Graphics2D g2) {
        for (PowerUp powerUp : powerUps) {
            BufferedImage sprite = SpritesPowerUps.cargar(powerUp);
            g2.drawImage(
                sprite,
                (int) Math.round(powerUp.getX() - 15),
                (int) Math.round(powerUp.getY() - 15),
                30,
                30,
                this
            );
        }
    }

    public void resolverColisiones() {
    colision.resolver(jugador);
    secciones.actualizar(jugador);
    hitboxJugador.actualizar((float) jugador.getX(), (float) jugador.getY());
    }
}