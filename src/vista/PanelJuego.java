package vista;
//java fx para que se cargue lo que
//mostrar en pantalla
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;

import controlador.levelmanager;
import controlador.GestorAnimacionSprite;
import controlador.utilz.loadsave;
import modelos.enemigos.fantasma;
import modelos.partida.ConfiguracionPartida;
import modelos.partida.Puntaje;
import modelos.personajes.Personaje;
import modelos.items.PowerUp;

//aqui nosotros estamos definimos el panel visual del juego
//mostramos la barrera de vida, estamina y mana del personaje, ademas de dibujar al personaje en el panel
public class PanelJuego extends JPanel {
    private static final long DURACION_FRAME_NANOS = 160_000_000L;
    private static final double ZOOM_CAMARA = 2.0;
    private static final double ZOOM_CAMARA_PANTALLA_COMPLETA = 2.5;
    private static final double SUAVIZADO_CAMARA_SEGUNDOS = 0.12;

    private final Personaje jugador;
    private final fantasma enemigo;
    private final levelmanager manejadornivel;
    private final Camara camara = new Camara(ZOOM_CAMARA, SUAVIZADO_CAMARA_SEGUNDOS);
    private final PanelHUD hud;
    private List<PowerUp> powerUps = Collections.emptyList();
    private long tiempoPartidaMillis;
    private int puntajePresentado;
    private static final double AJUSTE_BASE_RATA_VERDE = 1.0;
    private boolean nivelCompletado;

    // Fondo fijo del nivel (por ahora un solo escenario, sin gestor de niveles).
    private Image fondoImagen;

    // El gestor mantiene el ritmo de la animación y entrega frames con un anclaje común.
    private final Map<SpritesRatas.Accion, GestorAnimacionSprite> animacionesRata =
        new EnumMap<>(SpritesRatas.Accion.class);
    private SpritesRatas.Accion accionAnimacionRata = SpritesRatas.Accion.QUIETA;
    private double ultimaXPresentada;
    private boolean mirandoIzquierda;
    private BufferedImage frameBaseCalculada;
    private int ultimaFilaVisibleRata;
    private final BufferedImage[] framesMuerte;
    private final Timer temporizadorAnimacionMuerte;
    private int indiceFrameMuerte;
    private final BufferedImage[] framesDanio;
    private final Timer temporizadorAnimacionDanio;
    private int indiceFrameDanio;
    private final GestorAnimacionSprite animacionFantasmaDerecha;
    private final GestorAnimacionSprite animacionFantasmaIzquierda;

    //definimos el panel de juego, con un personaje
    // y un panel de hud, ademas de un timer para actualizar el juego
public PanelJuego(Personaje jugador, Puntaje modeloPuntaje, fantasma enemigo,
                  levelmanager manejadornivel) {
    if (jugador == null || modeloPuntaje == null || enemigo == null || manejadornivel == null) {
        throw new IllegalArgumentException("Se requieren el estado de partida y el nivel");
    }
    this.jugador = jugador;
    this.enemigo = enemigo;
    this.manejadornivel = manejadornivel;
    this.puntajePresentado = modeloPuntaje.getTotal();

    this.hud = new PanelHUD();
    for (SpritesRatas.Accion accion : SpritesRatas.Accion.values()) {
        if (accion == SpritesRatas.Accion.MUERTE
                || accion == SpritesRatas.Accion.RECIBIR_DANIO) {
            continue;
        }
        this.animacionesRata.put(accion, new GestorAnimacionSprite(
            SpritesRatas.cargarFrames(jugador.getTipoRata(), accion), DURACION_FRAME_NANOS
        ));
    }
    this.framesMuerte = SpritesRatas.cargarFrames(
        jugador.getTipoRata(), SpritesRatas.Accion.MUERTE
    );
    this.temporizadorAnimacionMuerte = new Timer(
        (int) (DURACION_FRAME_NANOS / 1_000_000L), evento -> avanzarAnimacionMuerte()
    );
    this.temporizadorAnimacionMuerte.setCoalesce(true);
    this.framesDanio = SpritesRatas.cargarFrames(
        jugador.getTipoRata(), SpritesRatas.Accion.RECIBIR_DANIO
    );
    this.temporizadorAnimacionDanio = new Timer(
        (int) (DURACION_FRAME_NANOS / 1_000_000L), evento -> avanzarAnimacionDanio()
    );
    this.temporizadorAnimacionDanio.setCoalesce(true);
    this.ultimaXPresentada = jugador.getX();
    this.animacionFantasmaDerecha = new GestorAnimacionSprite(
        SpritesFantasma.cargarPatrulla(true), 180_000_000L
    );
    this.animacionFantasmaIzquierda = new GestorAnimacionSprite(
        SpritesFantasma.cargarPatrulla(false), 180_000_000L
    );

    setPreferredSize(new Dimension(
        ConfiguracionPartida.COLS_NIVEL * ConfiguracionPartida.TAM_TILE,
        ConfiguracionPartida.FILAS_NIVEL * ConfiguracionPartida.TAM_TILE
    ));
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
        if (accionAnimacionRata == SpritesRatas.Accion.MUERTE) {
            return framesMuerte[0].getWidth();
        }
        if (accionAnimacionRata == SpritesRatas.Accion.RECIBIR_DANIO) {
            return framesDanio[0].getWidth();
        }
        return animacionesRata.get(accionAnimacionRata).getAncho();
    }

    public double getAltoSprite() {
        if (accionAnimacionRata == SpritesRatas.Accion.MUERTE) {
            return framesMuerte[0].getHeight();
        }
        if (accionAnimacionRata == SpritesRatas.Accion.RECIBIR_DANIO) {
            return framesDanio[0].getHeight();
        }
        return animacionesRata.get(accionAnimacionRata).getAlto();
    }

    public void actualizarPresentacion() {
        double xActual = jugador.getX();
        double desplazamientoX = xActual - ultimaXPresentada;
        if (Math.abs(desplazamientoX) > 0.01) {
            mirandoIzquierda = desplazamientoX < 0;
        }
        ultimaXPresentada = xActual;

        if (accionAnimacionRata == SpritesRatas.Accion.MUERTE
                || accionAnimacionRata == SpritesRatas.Accion.RECIBIR_DANIO) {
            animacionFantasmaDerecha.actualizar();
            animacionFantasmaIzquierda.actualizar();
            repaint();
            return;
        }

        SpritesRatas.Accion accionActual = obtenerAccionAnimacion(desplazamientoX);
        if (accionActual != accionAnimacionRata) {
            accionAnimacionRata = accionActual;
            animacionesRata.get(accionAnimacionRata).reiniciar();
        }
        animacionesRata.get(accionAnimacionRata).actualizar();
        animacionFantasmaDerecha.actualizar();
        animacionFantasmaIzquierda.actualizar();
        repaint();
    }

    public void mostrarAnimacionMuerte() {
        if (accionAnimacionRata == SpritesRatas.Accion.MUERTE) {
            return;
        }
        temporizadorAnimacionDanio.stop();
        accionAnimacionRata = SpritesRatas.Accion.MUERTE;
        indiceFrameMuerte = 0;
        frameBaseCalculada = null;
        temporizadorAnimacionMuerte.start();
        repaint();
    }

    public void mostrarAnimacionDanio() {
        if (accionAnimacionRata == SpritesRatas.Accion.MUERTE) {
            return;
        }
        accionAnimacionRata = SpritesRatas.Accion.RECIBIR_DANIO;
        indiceFrameDanio = 0;
        frameBaseCalculada = null;
        temporizadorAnimacionDanio.restart();
        repaint();
    }

    private void avanzarAnimacionMuerte() {
        if (indiceFrameMuerte >= framesMuerte.length - 1) {
            temporizadorAnimacionMuerte.stop();
            return;
        }
        indiceFrameMuerte++;
        repaint();
    }

    private void avanzarAnimacionDanio() {
        if (indiceFrameDanio >= framesDanio.length - 1) {
            temporizadorAnimacionDanio.stop();
            accionAnimacionRata = SpritesRatas.Accion.QUIETA;
            frameBaseCalculada = null;
            repaint();
            return;
        }
        indiceFrameDanio++;
        repaint();
    }

    private SpritesRatas.Accion obtenerAccionAnimacion(double desplazamientoX) {
        if (jugador.estaEnElAire()) {
            return SpritesRatas.Accion.SALTAR;
        }
        if (jugador.estaAgachada()) {
            return SpritesRatas.Accion.AGACHADA;
        }
        if (Math.abs(desplazamientoX) > 0.01) {
            return SpritesRatas.Accion.CAMINAR;
        }
        return SpritesRatas.Accion.QUIETA;
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
double anchoMundo = manejadornivel.getNivel().getAncho() * ConfiguracionPartida.TAM_TILE;
double altoMundo = manejadornivel.getNivel().getAlto() * ConfiguracionPartida.TAM_TILE;
java.awt.Window ventana = SwingUtilities.getWindowAncestor(this);
boolean pantallaCompleta = ventana instanceof JFrame && ((JFrame) ventana).isUndecorated();
camara.setZoom(pantallaCompleta ? ZOOM_CAMARA_PANTALLA_COMPLETA : ZOOM_CAMARA);
g2.transform(camara.actualizar(
    jugador.getX() + ConfiguracionPartida.ANCHO_JUGADOR / 2.0,
    jugador.getY() + ConfiguracionPartida.ALTO_JUGADOR / 2.0,
    getWidth(),
    getHeight(),
    anchoMundo,
    altoMundo
));

g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
if (fondoImagen != null) g2.drawImage(fondoImagen, 0, 0, (int) anchoMundo, (int) altoMundo, this);
manejadornivel.draw(g2, ConfiguracionPartida.TAM_TILE);
dibujarPowerUps(g2);
        // Dibujamos el frame original; el contorno amarillo se reserva como halo de estamina del HUD.

GestorAnimacionSprite animacionFantasma = enemigo.estaMoviendoseDerecha()
    ? animacionFantasmaDerecha
    : animacionFantasmaIzquierda;
BufferedImage cuadroFantasma = animacionFantasma.getFrameActual();
g2.drawImage(cuadroFantasma, (int) enemigo.getX(), (int) enemigo.getY(),
    (int) enemigo.getAncho(), (int) enemigo.getAlto(), this);

BufferedImage cuadroRata;
if (accionAnimacionRata == SpritesRatas.Accion.MUERTE) {
    cuadroRata = framesMuerte[indiceFrameMuerte];
} else if (accionAnimacionRata == SpritesRatas.Accion.RECIBIR_DANIO) {
    cuadroRata = framesDanio[indiceFrameDanio];
} else {
    cuadroRata = animacionesRata.get(accionAnimacionRata).getFrameActual();
}

if (cuadroRata != null) {
    g2.setRenderingHint(
        RenderingHints.KEY_INTERPOLATION,
        RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
    );

    double escala = Math.min(
        (double) ConfiguracionPartida.ANCHO_JUGADOR / cuadroRata.getWidth(),
        (double) ConfiguracionPartida.ALTO_JUGADOR / cuadroRata.getHeight()
    );

    int anchoDibujo = (int) Math.round(cuadroRata.getWidth() * escala);
    int altoDibujo = (int) Math.round(cuadroRata.getHeight() * escala);

    if (cuadroRata != frameBaseCalculada) {
        ultimaFilaVisibleRata = obtenerUltimaFilaVisible(cuadroRata);
        frameBaseCalculada = cuadroRata;
    }

    int xDibujo = (int) jugador.getX();
    double ajusteBase = jugador.getTipoRata() == Personaje.TipoRata.VERDE
        ? AJUSTE_BASE_RATA_VERDE
        : 0;
    int yDibujo = (int) Math.round(
        jugador.getY() + ConfiguracionPartida.ALTO_JUGADOR
            - (ultimaFilaVisibleRata + 1) * (double) altoDibujo / cuadroRata.getHeight()
            + ajusteBase
    );
    if (mirandoIzquierda) {
        AffineTransform transformacionRata = g2.getTransform();
        g2.translate(xDibujo + anchoDibujo, yDibujo);
        g2.scale(-1, 1);
        g2.drawImage(cuadroRata, 0, 0, anchoDibujo, altoDibujo, this);
        g2.setTransform(transformacionRata);
    } else {
        g2.drawImage(cuadroRata, xDibujo, yDibujo, anchoDibujo, altoDibujo, this);
    }
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

    private int obtenerUltimaFilaVisible(BufferedImage imagen) {
        for (int y = imagen.getHeight() - 1; y >= 0; y--) {
            for (int x = 0; x < imagen.getWidth(); x++) {
                if ((imagen.getRGB(x, y) >>> 24) != 0) {
                    return y;
                }
            }
        }
        throw new IllegalStateException("El frame de la rata no contiene píxeles visibles");
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

}