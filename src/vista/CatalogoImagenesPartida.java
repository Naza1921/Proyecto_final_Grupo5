package vista;

import java.util.List;
import java.util.stream.Stream;

public final class CatalogoImagenesPartida {
    private CatalogoImagenesPartida() { }

    public static List<String> rutasPrecarga() {
        return Stream.of(
            CatalogoSpritesRatas.rutasImagenes(),
            RenderizadorNivel.rutasImagenes(),
            SpritesFantasma.rutasImagenes(),
            SpriteRataInfestada.rutasImagenes(),
            SpriteProyectilStun.rutasImagenes(),
            SpriteAcido.rutasImagenes(),
            SpriteAcidoCharco.rutasImagenes(),
            SpriteLamparaFuego.rutasImagenes(),
            SpriteMoneda.rutasImagenes(),
            PanelHUD.rutasImagenes(),
            SpritesPowerUps.rutasImagenes()
        ).flatMap(List::stream).distinct().toList();
    }
}
