package vista;
//java fx para que se cargue lo que
//mostrar en pantalla
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import javax.swing.*;

import controlador.GestorAnimacionSprite;
import controlador.ratacontroller;
import modelos.personajes.Personaje;

//aqui nosotros estamos definimos el panel visual del juego
//mostramos la barrera de vida, estamina y mana del personaje, ademas de dibujar al personaje en el panel
public class PanelJuego extends JPanel {
    private static final long DURACION_FRAME_NANOS = 160_000_000L;

    private final Personaje jugador;
    private final ratacontroller controladorRata;
    private final PanelHUD hud;
    private Timer timer;
    private boolean izquierdaPresionada;
    private boolean derechaPresionada;
    private boolean saltoPresionado;

    // Fondo fijo del nivel (por ahora un solo escenario, sin gestor de niveles).
    private Image fondoImagen;

    // El gestor mantiene el ritmo de la animación y entrega frames con un anclaje común.
    private final GestorAnimacionSprite animacionRata;

    //definimos el panel de juego, con un personaje
    // y un panel de hud, ademas de un timer para actualizar el juego
    public PanelJuego(Personaje jugador) {
        if (jugador == null) {
            throw new IllegalArgumentException("Se requiere un personaje para iniciar el juego");
        }
        this.jugador = jugador;
        this.controladorRata = new ratacontroller(jugador);
        this.hud = new PanelHUD();
        SpriteSheet spriteRata = SpritesRatas.cargar(jugador.getTipoRata());
        this.animacionRata = new GestorAnimacionSprite(
            SpritesRatas.crearFramesAlineados(spriteRata, 0), DURACION_FRAME_NANOS
        );
        //determinamos el alto y ancho de la ventana
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.DARK_GRAY);
        setFocusable(true);

        configurarControles();
        cargarFondo("/assets/escenarios/Escenario_nivel3.jpeg");

        // Loop del juego: ~60 fps
        // determinamos el timer del juego
        timer = new Timer(16, e -> {
            actualizarMovimientoHorizontal();
            controladorRata.actualizar();
            verificarAterrizaje();
            jugador.recargarEstamina();
            animacionRata.actualizar();
            repaint(); // dispara paintComponent
        });
        timer.start();
    }

    // Las flechas mantienen el movimiento mientras están pulsadas; Espacio inicia un salto.
    private void configurarControles() {
        vincularTecla("pressed LEFT", "izquierdaPresionada",
            () -> izquierdaPresionada = true);
        vincularTecla("released LEFT", "izquierdaLiberada",
            () -> izquierdaPresionada = false);
        vincularTecla("pressed RIGHT", "derechaPresionada",
            () -> derechaPresionada = true);
        vincularTecla("released RIGHT", "derechaLiberada",
            () -> derechaPresionada = false);
        vincularTecla("pressed SPACE", "saltar",
            () -> {
                if (!saltoPresionado) {
                    saltoPresionado = true;
                    controladorRata.saltar();
                }
            });
        vincularTecla("released SPACE", "saltarLiberado",
            () -> saltoPresionado = false);

        // Limpiamos las teclas activas si la ventana pierde el foco antes de liberarlas.
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent evento) {
                izquierdaPresionada = false;
                derechaPresionada = false;
                saltoPresionado = false;
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

    // Aplicamos el desplazamiento una vez por actualización y cancelamos direcciones opuestas.
    private void actualizarMovimientoHorizontal() {
        if (izquierdaPresionada != derechaPresionada) {
            if (izquierdaPresionada) {
                controladorRata.moverIzquierda();
            } else {
                controladorRata.moverDerecha();
            }
        }

        controladorRata.limitarPosicionHorizontal(
            0, Math.max(0, getWidth() - animacionRata.getAncho())
        );
    }

    // El nivel actual no tiene plataformas conectadas; usamos el borde inferior como piso.
    private void verificarAterrizaje() {
        if (jugador.estaEnElAire() && jugador.getVelocidadY() >= 0) {
            double yPiso = Math.max(0, getHeight() - animacionRata.getAlto());
            if (jugador.getY() >= yPiso) {
                jugador.aterrizar(yPiso);
            }
        }
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