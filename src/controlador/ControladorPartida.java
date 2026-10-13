package controlador;

import javax.swing.Timer;
import vista.PanelJuego;

// Coordina el ciclo temporal de la partida y solicita el repintado de la vista.
public class ControladorPartida {
    private static final int INTERVALO_ACTUALIZACION_MS = 16;

    private final ControladorRata controladorRata;
    private final GestorControlesRatas gestorControles;
    private final PanelJuego panelJuego;
    private final Timer temporizador;

    public ControladorPartida(ControladorRata controladorRata,
                              GestorControlesRatas gestorControles,
                              PanelJuego panelJuego) {
        if (controladorRata == null || gestorControles == null || panelJuego == null) {
            throw new IllegalArgumentException(
                "Se requieren el controlador de rata, el gestor de controles y la vista de juego"
            );
        }
        this.controladorRata = controladorRata;
        this.gestorControles = gestorControles;
        this.panelJuego = panelJuego;
        this.temporizador = new Timer(INTERVALO_ACTUALIZACION_MS, evento -> actualizar());
    }

    public void iniciar() {
        temporizador.start();
    }

    public void detener() {
        temporizador.stop();
    }

    private void actualizar() {
        double xMaximo = Math.max(
            0, panelJuego.getAnchoAreaVisual() - panelJuego.getAnchoSprite()
        );
        gestorControles.actualizarMovimiento(xMaximo);

        double yPiso = Math.max(
            0, panelJuego.getAltoAreaVisual() - panelJuego.getAltoSprite()
        );
        controladorRata.actualizar(yPiso);
        panelJuego.actualizarPresentacion();
    }
}
