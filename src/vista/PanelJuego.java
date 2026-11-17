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

import aplicacion.EnemigoVista;
import aplicacion.JugadorVista;
import aplicacion.MonedaVista;
import aplicacion.NivelVista;
import aplicacion.PowerUpVista;
import aplicacion.VistaPartida;
import aplicacion.EstadoNivel;
import modelos.partida.ConfiguracionPartida;

public final class PanelJuego extends JPanel implements VistaPartida {
    private static final double ZOOM_CAMARA = 2.0;
    private static final double ZOOM_CAMARA_PANTALLA_COMPLETA = 2.5;
    private static final double SUAVIZADO_CAMARA_SEGUNDOS = 0.12;
    private static final int ANCHO_TUTORIAL_CONTROLES = 225;
    private static final int ALTO_TUTORIAL_CONTROLES = 127;
    private static final int TUTORIAL_CONTROLES_X = ConfiguracionPartida.POSICION_INICIAL_X;
    private static final int TUTORIAL_CONTROLES_Y =
        ConfiguracionPartida.POSICION_INICIAL_Y + 125;

    private JugadorVista jugador;
    private final EstadoNivel estadoNivel;
    private final RenderizadorNivel renderizadorNivel;
    private final Camara camara = new Camara(ZOOM_CAMARA, SUAVIZADO_CAMARA_SEGUNDOS);
    private final PanelHUD hud;
    private List<PowerUpVista> powerUps = Collections.emptyList();
    private List<MonedaVista> monedas = Collections.emptyList();
    private final SpriteMoneda spriteMoneda;
    private final RenderizadorEntidades renderizadorEntidades;
    private long tiempoPartidaMillis;
    private int puntajePresentado;
    private boolean nivelCompletado;
    private boolean pantallaCompleta;
    private Runnable alCerrarNivelCompletado;
    private final BufferedImage[] fondosPorPiso;
    private final BufferedImage tutorialControles;

public PanelJuego(JugadorVista jugador, int puntajeInicial, List<EnemigoVista> enemigos,
                  EstadoNivel estadoNivel,
                  Runnable alTerminarAnimacionMuerte, BufferedImage[] fondosPorPiso,
                  BufferedImage tutorialControles) {
    this(
        jugador, puntajeInicial, enemigos, estadoNivel, alTerminarAnimacionMuerte,
        fondosPorPiso, tutorialControles,
        RecursosJuego.preparar(jugador, alTerminarAnimacionMuerte)
    );
}

public PanelJuego(JugadorVista jugador, int puntajeInicial, List<EnemigoVista> enemigos,
                  EstadoNivel estadoNivel,
                  Runnable alTerminarAnimacionMuerte, BufferedImage[] fondosPorPiso,
                  BufferedImage tutorialControles, RecursosJuego recursos) {
    if (jugador == null || puntajeInicial < 0 || enemigos == null
            || enemigos.size() > 6
            || enemigos.stream().anyMatch(enemigo -> enemigo == null)
            || estadoNivel == null
            || alTerminarAnimacionMuerte == null
            || recursos == null
            || fondosPorPiso == null
            || fondosPorPiso.length == 0
            || java.util.Arrays.stream(fondosPorPiso).anyMatch(fondo -> fondo == null)
            || tutorialControles == null) {
        throw new IllegalArgumentException("Se requieren el estado de partida y el nivel");
    }
    this.jugador = jugador;
    this.estadoNivel = estadoNivel;
    this.renderizadorNivel = recursos.renderizadorNivel();
    this.puntajePresentado = puntajeInicial;
    this.fondosPorPiso = fondosPorPiso.clone();
    this.tutorialControles = tutorialControles;
    this.spriteMoneda = recursos.spriteMoneda();
    this.renderizadorEntidades = recursos.renderizadorEntidades();
    this.renderizadorEntidades.setSolicitarRepintado(this::repaint);
    this.renderizadorEntidades.actualizarEnemigosVisibles(enemigos);
    this.hud = recursos.hud();

    setPreferredSize(new Dimension(
        ConfiguracionPartida.COLS_NIVEL * ConfiguracionPartida.TAM_TILE,
        ConfiguracionPartida.FILAS_NIVEL * ConfiguracionPartida.TAM_TILE
    ));
    setBackground(Color.DARK_GRAY);
    setFocusable(true);

    getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "continuarNivel");
    getActionMap().put("continuarNivel", new AbstractAction() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent evento) {
            cerrarNivelCompletado();
        }
    });
    addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evento) {
            cerrarNivelCompletado();
        }
    });
}
    public double getAnchoSprite() {
        return renderizadorEntidades.getAnchoSprite();
    }

    public double getAltoSprite() {
        return renderizadorEntidades.getAltoSprite();
    }

    public void actualizarPresentacion() {
        spriteMoneda.actualizar();
        renderizadorEntidades.actualizar(jugador);
    }

    public void mostrarAnimacionHabilidad() {
        renderizadorEntidades.mostrarAnimacionHabilidad();
    }

    public void mostrarAnimacionMuerte() {
        renderizadorEntidades.mostrarAnimacionMuerte();
    }

    public void mostrarAnimacionDanio() {
        renderizadorEntidades.mostrarAnimacionDanio();
    }

    public void actualizarPowerUps(List<PowerUpVista> powerUps) {
        if (powerUps == null) {
            throw new IllegalArgumentException("La lista de power-ups debe ser válida");
        }
        for (PowerUpVista powerUp : powerUps) {
            if (powerUp == null) {
                throw new IllegalArgumentException("La lista de power-ups debe ser válida");
            }
        }
        this.powerUps = Collections.unmodifiableList(new ArrayList<>(powerUps));
        repaint();
    }

    public void actualizarMonedas(List<MonedaVista> monedas) {
        if (monedas == null || monedas.stream().anyMatch(moneda -> moneda == null)) {
            throw new IllegalArgumentException("La lista de monedas debe ser válida");
        }
        this.monedas = List.copyOf(monedas);
        repaint();
    }

    public void actualizarJugador(JugadorVista jugador) {
        if (jugador == null) {
            throw new IllegalArgumentException("Se requieren los datos visuales del jugador");
        }
        this.jugador = jugador;
        repaint();
    }

    public void actualizarEnemigosVisibles(List<EnemigoVista> enemigos) {
        renderizadorEntidades.actualizarEnemigosVisibles(enemigos);
    }

    public void mostrarMuerteFantasma(EnemigoVista enemigo) {
        renderizadorEntidades.mostrarMuerteFantasma(enemigo);
    }

    public void mostrarMuerteRataInfestada(EnemigoVista rata) {
        renderizadorEntidades.mostrarMuerteRataInfestada(rata);
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

        public void setPantallaCompleta(boolean pantallaCompleta) {
            this.pantallaCompleta = pantallaCompleta;
            repaint();
        }

    @Override
    protected void paintComponent(Graphics g) {
super.paintComponent(g);
Graphics2D g2 = (Graphics2D) g;

AffineTransform transformacionPantalla = g2.getTransform();
NivelVista nivelActual = estadoNivel.getNivelVista();
double anchoMundo = nivelActual.getAncho() * ConfiguracionPartida.TAM_TILE;
double altoMundo = nivelActual.getAlto() * ConfiguracionPartida.TAM_TILE;
camara.setZoom(pantallaCompleta ? ZOOM_CAMARA_PANTALLA_COMPLETA : ZOOM_CAMARA);
g2.transform(camara.actualizar(
    jugador.x() + ConfiguracionPartida.ANCHO_JUGADOR / 2.0,
    jugador.y() + ConfiguracionPartida.ALTO_JUGADOR / 2.0,
    getWidth(),
    getHeight(),
    anchoMundo,
    altoMundo
));

g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
BufferedImage fondoPiso = fondosPorPiso[estadoNivel.getPiso() - 1];
g2.drawImage(fondoPiso, 0, 0, (int) anchoMundo, (int) altoMundo, this);
renderizadorNivel.dibujar(g2, nivelActual);
if (estadoNivel.esPrimeraSeccion()) {
    g2.drawImage(
        tutorialControles,
        TUTORIAL_CONTROLES_X,
        TUTORIAL_CONTROLES_Y,
        ANCHO_TUTORIAL_CONTROLES,
        ALTO_TUTORIAL_CONTROLES,
        this
    );
}
dibujarPowerUps(g2);
dibujarMonedas(g2);
renderizadorEntidades.dibujar(g2);

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
    if (alCerrarNivelCompletado != null) {
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 20f));
        String aviso = "Enter o click para continuar";
        FontMetrics fmAviso = g2.getFontMetrics();
        g2.drawString(
            aviso,
            (getWidth() - fmAviso.stringWidth(aviso)) / 2,
            getHeight() / 2 + 40
        );
    }
}
    }

    public void mostrarNivelCompletado() {
    nivelCompletado = true;
    repaint();
    }  

    public void mostrarNivelCompletado(Runnable alCerrar) {
        if (alCerrar == null) {
            throw new IllegalArgumentException("Se requiere la acción para cerrar el aviso");
        }
        alCerrarNivelCompletado = alCerrar;
        mostrarNivelCompletado();
    }

    private void cerrarNivelCompletado() {
        if (!nivelCompletado || alCerrarNivelCompletado == null) {
            return;
        }
        Runnable accion = alCerrarNivelCompletado;
        alCerrarNivelCompletado = null;
        nivelCompletado = false;
        repaint();
        requestFocusInWindow();
        accion.run();
    }

    private void dibujarPowerUps(Graphics2D g2) {
        for (PowerUpVista powerUp : powerUps) {
            BufferedImage sprite = SpritesPowerUps.cargar(powerUp);
            g2.drawImage(
                sprite,
                (int) Math.round(powerUp.x() - 15),
                (int) Math.round(powerUp.y() - 15),
                30,
                30,
                this
            );
        }
    }

    private void dibujarMonedas(Graphics2D g2) {
        for (MonedaVista moneda : monedas) {
            spriteMoneda.dibujar(g2, moneda);
        }
    }

}
