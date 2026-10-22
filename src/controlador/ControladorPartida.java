package controlador;

import javax.swing.Timer;
import modelos.enemigos.enemigo;
import modelos.items.PowerUp;
import vista.PanelJuego;

// Coordina el ciclo temporal de la partida y solicita el repintado de la vista.
public class ControladorPartida implements SeccionListener {
    private static final int INTERVALO_ACTUALIZACION_MS = 16;
    private static final long INTERVALO_RECARGA_ESTAMINA_NANOS = 250_000_000L;

    private final ControladorRata controladorRata;
    private final GestorControlesRatas gestorControles;
    private final GestorEfectosTemporales gestorEfectos;
    private final ControladorPuntaje controladorPuntaje;
    private final PanelJuego panelJuego;
    private final Timer temporizador;
    private long ultimaRecargaEstaminaNanos = System.nanoTime();

    public ControladorPartida(ControladorRata controladorRata,
                            GestorControlesRatas gestorControles,
                              GestorEfectosTemporales gestorEfectos,
                              ControladorPuntaje controladorPuntaje,
                              PanelJuego panelJuego) {
        if (controladorRata == null || gestorControles == null
                || gestorEfectos == null || controladorPuntaje == null || panelJuego == null) {
            throw new IllegalArgumentException(
                "Se requieren los controladores, el modelo de puntaje y la vista de juego"
            );
        }
        this.controladorRata = controladorRata;
        this.gestorControles = gestorControles;
        this.gestorEfectos = gestorEfectos;
        this.controladorPuntaje = controladorPuntaje;
        this.panelJuego = panelJuego;
        this.temporizador = new Timer(INTERVALO_ACTUALIZACION_MS, evento -> actualizar());
        panelJuego.getGestorSecciones().setListener(this);
    }

    public boolean recogerPowerUp(PowerUp powerUp) {
        return gestorEfectos.recogerSiColisiona(powerUp);
    }

    public boolean usarHabilidad(enemigo objetivo, double xDestino, double yDestino) {
        return gestorControles.usarHabilidad(objetivo, xDestino, yDestino);
    }

    public void agregarPuntos(int puntos) {
        controladorPuntaje.agregarPuntos(puntos);
        panelJuego.actualizarPresentacion();
    }

    public void iniciar() {
        temporizador.start();
    }

    public void detener() {
        temporizador.stop();
    }

private void actualizar() {
    long ahora = System.nanoTime();
    if (ahora - ultimaRecargaEstaminaNanos >= INTERVALO_RECARGA_ESTAMINA_NANOS) {
        controladorRata.recargarEstamina();
        ultimaRecargaEstaminaNanos = ahora;
    }

    double xMinimo = 0;
    double xMaximo = Math.max(0, panelJuego.getAnchoNivelPx() - panelJuego.getAnchoHitbox());
    gestorControles.actualizarMovimiento(xMinimo, xMaximo);

    gestorEfectos.actualizar();
    controladorRata.actualizar(Double.POSITIVE_INFINITY);
    panelJuego.resolverColisiones();
    panelJuego.actualizarPresentacion();
    
}
@Override
public void alCambiarSeccion(int seccion) { }

@Override
public void alTerminarNivel() {
    detener();
    panelJuego.mostrarNivelCompletado();
}
}