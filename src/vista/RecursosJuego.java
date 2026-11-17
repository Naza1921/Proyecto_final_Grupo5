package vista;

import java.util.List;
import aplicacion.JugadorVista;

public final class RecursosJuego {
    private final RenderizadorNivel renderizadorNivel;
    private final SpriteMoneda spriteMoneda;
    private final RenderizadorEntidades renderizadorEntidades;
    private final PanelHUD hud;

    private RecursosJuego(
            RenderizadorNivel renderizadorNivel,
            SpriteMoneda spriteMoneda,
            RenderizadorEntidades renderizadorEntidades,
            PanelHUD hud) {
        this.renderizadorNivel = renderizadorNivel;
        this.spriteMoneda = spriteMoneda;
        this.renderizadorEntidades = renderizadorEntidades;
        this.hud = hud;
    }

    public static RecursosJuego preparar(
            JugadorVista jugador,
            Runnable alTerminarAnimacionMuerte) {
        if (jugador == null || alTerminarAnimacionMuerte == null) {
            throw new IllegalArgumentException("Se requieren los datos visuales del jugador");
        }

        SpriteRataInfestada.precargar();
        SpritesPowerUps.precargar();
        return new RecursosJuego(
            new RenderizadorNivel(),
            new SpriteMoneda(),
            new RenderizadorEntidades(
                jugador, List.of(), alTerminarAnimacionMuerte, () -> { }
            ),
            new PanelHUD()
        );
    }

    RenderizadorNivel renderizadorNivel() {
        return renderizadorNivel;
    }

    SpriteMoneda spriteMoneda() {
        return spriteMoneda;
    }

    RenderizadorEntidades renderizadorEntidades() {
        return renderizadorEntidades;
    }

    PanelHUD hud() {
        return hud;
    }
}
