package vista;

import java.awt.image.BufferedImage;
import java.util.Map;
import controlador.utilz.loadsave;
import modelos.items.EfectoInvulnerabilidad;
import modelos.items.EfectoLento;
import modelos.items.EfectoTemporal;
import modelos.items.EfectoVelocidad;
import modelos.items.PowerUp;

public final class SpritesPowerUps {
    private static final int TAMANO_SPRITE = 30;
    private static final Map<Class<? extends EfectoTemporal>, BufferedImage> SPRITES = Map.of(
        EfectoVelocidad.class, cargar("/assets/powerUp/LataRedBull(powerUpvelocidad).jpeg"),
        EfectoLento.class, cargar("/assets/powerUp/QuesoconMoho(powerUplento).jpeg"),
        EfectoInvulnerabilidad.class,cargar("/assets/powerUp/Queso(powerupinvulnerabilidad).jpeg")
        //EfectoInvulnerabilidad.class,cargar("/assets/powerUp/Queso(powerupinvulnerabilidad).jpeg"),
        //EfectoLento.class, cargar("/assets/powerUp/HamburgesaconMoho(powerUplento).png"),
        //EfectoInvulnerabilidad.class,cargar("/assets/powerUp/Pizza(powerupinvulnerabilidad).png")
    );

    private SpritesPowerUps() {
    }

    public static BufferedImage cargar(PowerUp powerUp) {
        if (powerUp == null || powerUp.getEfecto() == null) {
            throw new IllegalArgumentException("Se requiere un power-up con efecto");
        }
        BufferedImage sprite = SPRITES.get(powerUp.getEfecto().getClass());
        if (sprite == null) {
            throw new IllegalArgumentException(
                "No hay un sprite configurado para " + powerUp.getEfecto().getClass().getSimpleName()
            );
        }
        return sprite;
    }

    private static BufferedImage cargar(String ruta) {
        BufferedImage original = loadsave.GetSpriteatlas(ruta.substring(1));
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
