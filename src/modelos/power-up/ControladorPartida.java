package controlador;

import javax.swing.Timer;
import modelos.items.PowerUp;
import vista.PanelJuego;

// Coordina el ciclo temporal de la partida y solicita el repintado de la vista.
public class ControladorPartida {
    private static final int INTERVALO_ACTUALIZACION_MS = 16;

    private final ControladorRata controladorRata;
    private final GestorControlesRatas gestorControles;
    private final GestorEfectosTemporales gestorEfectos;
    private final PanelJuego panelJuego;
    private final Timer temporizador;

    public ControladorPartida(ControladorRata controladorRata,
                            GestorControlesRatas gestorControles,
                              GestorEfectosTemporales gestorEfectos,
                              PanelJuego panelJuego) {
        if (controladorRata == null || gestorControles == null
                || gestorEfectos == null || panelJuego == null) {
            throw new IllegalArgumentException(
                "Se requieren controladores, gestor de efectos y vista de juego"
            );
        }
        this.controladorRata = controladorRata;
        this.gestorControles = gestorControles;
        this.gestorEfectos = gestorEfectos;
        this.panelJuego = panelJuego;
        this.temporizador = new Timer(INTERVALO_ACTUALIZACION_MS, evento -> actualizar());
    }

    public boolean recogerPowerUp(PowerUp powerUp) {
        return gestorEfectos.recogerSiColisiona(powerUp);
    }

    public void iniciar() {
        temporizador.start();
    }

    public void detener() {
        temporizador.stop();
    }

    private void actualizar() {
        double xMinimo = 0;
        double xMaximo = Math.max(0,
            panelJuego.getAnchoAreaVisual() - panelJuego.getAnchoSprite()
        );
        gestorControles.actualizarMovimiento(xMinimo, xMaximo);

        gestorEfectos.actualizar();
        double yPiso = Math.max(
            0, panelJuego.getAltoAreaVisual() - panelJuego.getAltoSprite()
        );
        controladorRata.actualizar(yPiso);
        panelJuego.actualizarPresentacion();
    }
}
