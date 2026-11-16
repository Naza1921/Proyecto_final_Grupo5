package vista;

import java.awt.image.BufferedImage;
import java.util.Map;
import aplicacion.PowerUpVista;
import aplicacion.TipoPowerUpVista;
import recursos.CargadorRecursos;

public final class SpritesPowerUps {
    private static final int TAMANO_SPRITE = 30;
    private static final Map<TipoPowerUpVista, BufferedImage> SPRITES = Map.of(
        TipoPowerUpVista.VELOCIDAD, cargar("/assets/powerUp/cafe.png"),
        TipoPowerUpVista.LENTO, cargar("/assets/powerUp/QuesoconMoho(powerUplento).jpeg"),
        TipoPowerUpVista.VIDA, cargar("/assets/powerUp/Queso(vida).jpeg"),
        TipoPowerUpVista.INVULNERABILIDAD,
            cargar("/assets/powerUp/Pizza(powerupinvulnerabilidad).png")
    );

    private SpritesPowerUps() {
    }

    public static BufferedImage cargar(PowerUpVista powerUp) {
        if (powerUp == null) {
            throw new IllegalArgumentException("Se requiere un power-up");
        }
        BufferedImage sprite = SPRITES.get(powerUp.tipo());
        if (sprite == null) {
            throw new IllegalArgumentException(
                "No hay un sprite configurado para " + powerUp.getClass().getSimpleName()
            );
        }
        return sprite;
    }

    private static BufferedImage cargar(String ruta) {
        BufferedImage original = CargadorRecursos.cargarImagen(ruta.substring(1));
        SpriteSheet spriteSheet = new SpriteSheet(original, 1);
        BufferedImage imagenOriginal = spriteSheet.getFrame(0);
        BufferedImage imagenEscalada = new BufferedImage(
            TAMANO_SPRITE, TAMANO_SPRITE, BufferedImage.TYPE_INT_ARGB
        );
        java.awt.Graphics2D graphics = imagenEscalada.createGraphics();
        try {
            SpritesRatas.dibujarAjustado(
                graphics, imagenOriginal, 0, 0, TAMANO_SPRITE, TAMANO_SPRITE
            );
        } finally {
            graphics.dispose();
        }
        return imagenEscalada;
    }
}
