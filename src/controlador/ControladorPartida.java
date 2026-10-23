package controlador;

import javax.swing.Timer;
import modelos.enemigos.fantasma;
import modelos.personajes.Personaje;
import vista.PanelJuego;

// Coordina el ciclo temporal de la partida y solicita el repintado de la vista.
public class ControladorPartida {
    private static final int INTERVALO_ACTUALIZACION_MS = 16;

    private final ControladorRata controladorRata;
    private final GestorControlesRatas gestorControles;
    private final PanelJuego panelJuego;
    private final Personaje jugador;
    private final fantasma enemigo;
    private final Timer temporizador;

    public ControladorPartida(ControladorRata controladorRata,
                            GestorControlesRatas gestorControles,
                              PanelJuego panelJuego, Personaje jugador,
                              fantasma enemigo) {
        if (controladorRata == null || gestorControles == null || panelJuego == null
                || jugador == null || enemigo == null) {
            throw new IllegalArgumentException(
                "Se requieren los controladores, el personaje, el enemigo y la vista de juego"
            );
        }
        this.controladorRata = controladorRata;
        this.gestorControles = gestorControles;
        this.panelJuego = panelJuego;
        this.jugador = jugador;
        this.enemigo = enemigo;
        this.temporizador = new Timer(INTERVALO_ACTUALIZACION_MS, evento -> actualizar());
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

        double yPiso = Math.max(
            0, panelJuego.getAltoAreaVisual() - panelJuego.getAltoSprite()
        );
        controladorRata.actualizar(yPiso);
        double yFantasma = yPiso + panelJuego.getAltoSprite() - enemigo.getAlto();
        enemigo.actualizarPatrulla(Math.max(0, yFantasma));
        enemigo.atacar(jugador, panelJuego.getAnchoSprite(), panelJuego.getAltoSprite());
        panelJuego.actualizarPresentacion();
    }
}
