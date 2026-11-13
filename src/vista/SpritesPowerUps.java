package vista;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import recursos.CargadorRecursos;
import modelos.items.PowerUp;
import modelos.items.PowerUpLento;
import modelos.items.PowerUpPizza;
import modelos.items.PowerUpVelocidad;
import modelos.items.PowerUpVida;

public final class SpritesPowerUps {
    private static final int TAMANO_SPRITE = 30;
    private static final String RUTA_CAFE = "/assets/powerUp/cafe.png";
    private static final String RUTA_QUESO_CON_MOHO =
        "/assets/powerUp/QuesoconMoho(powerUplento).jpeg";
    private static final String RUTA_QUESO_VIDA = "/assets/powerUp/Queso(vida).jpeg";
    private static final String RUTA_PIZZA =
        "/assets/powerUp/Pizza(powerupinvulnerabilidad).png";

    private SpritesPowerUps() {
    }

    public static List<String> rutasImagenes() {
        return List.of(RUTA_CAFE, RUTA_QUESO_CON_MOHO, RUTA_QUESO_VIDA, RUTA_PIZZA);
    }

    public static BufferedImage cargar(PowerUp powerUp) {
        if (powerUp == null || powerUp.getEfecto() == null) {
            throw new IllegalArgumentException("Se requiere un power-up con efecto");
        }
        BufferedImage sprite = Catalogo.SPRITES.get(powerUp.getClass());
        if (sprite == null) {
            throw new IllegalArgumentException(
                "No hay un sprite configurado para " + powerUp.getClass().getSimpleName()
            );
        }
        return sprite;
    }

    private static final class Catalogo {
        private static final Map<Class<? extends PowerUp>, BufferedImage> SPRITES = Map.of(
            PowerUpVelocidad.class, cargar(RUTA_CAFE),
            PowerUpLento.class, cargar(RUTA_QUESO_CON_MOHO),
            PowerUpVida.class, cargar(RUTA_QUESO_VIDA),
            PowerUpPizza.class, cargar(RUTA_PIZZA)
        );
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
