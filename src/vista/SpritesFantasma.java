package vista;

import java.awt.image.BufferedImage;

public final class SpritesFantasma {
    private static final String RUTA_HOJA = "/assets/arte_enemigos/fantasma.jpeg";
    private static final int INICIO_FILA_PATRULLA = 315;
    private static final int ALTO_FILA_PATRULLA = 140;
    private static final int CANTIDAD_CUADROS_PATRULLA = 3;
    private static final int X_PATRULLA_IZQUIERDA = 230;
    private static final int ANCHO_PATRULLA_IZQUIERDA = 306;
    private static final int X_PATRULLA_DERECHA = 1018;
    private static final int ANCHO_PATRULLA_DERECHA = 288;

    private SpritesFantasma() {
    }

    public static BufferedImage[] cargarPatrulla(boolean haciaDerecha) {
        int x = haciaDerecha ? X_PATRULLA_DERECHA : X_PATRULLA_IZQUIERDA;
        int ancho = haciaDerecha
            ? ANCHO_PATRULLA_DERECHA
            : ANCHO_PATRULLA_IZQUIERDA;
        SpriteSheet hoja = new SpriteSheet(RUTA_HOJA, CANTIDAD_CUADROS_PATRULLA,
            x, INICIO_FILA_PATRULLA, ancho, ALTO_FILA_PATRULLA);
        return SpritesRatas.crearFramesAlineados(hoja, 0);
    }
}
