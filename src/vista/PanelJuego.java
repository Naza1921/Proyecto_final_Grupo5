package vista;
//java fx para que se cargue lo que
//mostrar en pantalla
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import javax.swing.*;
import controlador.GestorControlesRatas;
import controlador.GestorAnimacionSprite;
import modelos.enemigos.fantasma;
import modelos.personajes.Personaje;

//aqui nosotros estamos definimos el panel visual del juego
//mostramos la barrera de vida, estamina y mana del personaje, ademas de dibujar al personaje en el panel
public class PanelJuego extends JPanel {
    private static final long DURACION_FRAME_NANOS = 160_000_000L;

    private final Personaje jugador;
    private final fantasma enemigo;
    private final GestorControlesRatas gestorControles;
    private final PanelHUD hud;
    private boolean saltoPresionado;

    // Fondo fijo del nivel (por ahora un solo escenario, sin gestor de niveles).
    private Image fondoImagen;

    // El gestor mantiene el ritmo de la animación y entrega frames con un anclaje común.
    private final GestorAnimacionSprite animacionRata;
    private final GestorAnimacionSprite animacionFantasmaDerecha;
    private final GestorAnimacionSprite animacionFantasmaIzquierda;

    //definimos el panel de juego, con un personaje
    // y un panel de hud, ademas de un timer para actualizar el juego
    public PanelJuego(Personaje jugador, GestorControlesRatas gestorControles,
                      fantasma enemigo) {
        if (jugador == null || gestorControles == null || enemigo == null) {
            throw new IllegalArgumentException(
                "Se requieren un personaje, el gestor de controles y el enemigo"
            );
        }
        this.jugador = jugador;
        this.enemigo = enemigo;
        this.gestorControles = gestorControles;
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
        //determinamos el alto y ancho de la ventana
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.DARK_GRAY);
        setFocusable(true);

        configurarControles();
        cargarFondo("/assets/escenarios/Escenario_nivel3.jpeg");
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

    private void configurarControles() {
        vincularTecla("pressed LEFT", "izquierdaPresionada",
            gestorControles::presionarIzquierda);
        vincularTecla("released LEFT", "izquierdaLiberada",
            gestorControles::soltarIzquierda);
        vincularTecla("pressed RIGHT", "derechaPresionada",
            gestorControles::presionarDerecha);
        vincularTecla("released RIGHT", "derechaLiberada",
            gestorControles::soltarDerecha);
        vincularTecla("pressed SPACE", "saltar",
            () -> {
                if (!saltoPresionado) {
                    saltoPresionado = true;
                    gestorControles.presionarSalto();
                }
            });
        vincularTecla("released SPACE", "saltarLiberado",
            () -> {
                saltoPresionado = false;
                gestorControles.soltarSalto();
            });

        // Limpiamos las teclas activas si la ventana pierde el foco antes de liberarlas.
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent evento) {
                saltoPresionado = false;
                gestorControles.limpiarEntradas();
            }
        });
    }

    // Swing asocia cada pulsación y liberación con una acción independiente del teclado físico.
    private void vincularTecla(String secuencia, String nombreAccion, Runnable accion) {
        getInputMap(WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(secuencia), nombreAccion);
        getActionMap().put(nombreAccion, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evento) {
                accion.run();
            }
        });
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Fondo del escenario, escalado al tamaño del panel.
        if (fondoImagen != null) {
            g2.drawImage(fondoImagen, 0, 0, getWidth(), getHeight(), this);
        }

        GestorAnimacionSprite animacionFantasma = enemigo.estaMoviendoseDerecha()
            ? animacionFantasmaDerecha
            : animacionFantasmaIzquierda;
        BufferedImage cuadroFantasma = animacionFantasma.getFrameActual();
        g2.drawImage(cuadroFantasma, (int) enemigo.getX(), (int) enemigo.getY(),
            (int) enemigo.getAncho(), (int) enemigo.getAlto(), this);

        // Dibujamos el frame original; el contorno amarillo se reserva como halo de estamina del HUD.
        BufferedImage cuadroRata = animacionRata.getFrameActual();

        if (cuadroRata != null) {
            g2.drawImage(cuadroRata, (int) jugador.getX(), (int) jugador.getY(),
                cuadroRata.getWidth(), cuadroRata.getHeight(), this);
        }

        // dibujar el HUD encima de todo
        //en la esquina izquierda
        hud.dibujar(g2, jugador);
    }
}