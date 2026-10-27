package vista;

import java.awt.image.BufferedImage;
import controlador.utilz.loadsave;

public final class SpritesFantasma {
    private static final String RUTA_HOJA = "assets/arte_enemigos/fantasma.jpeg";
    private static final int INICIO_FILA_PATRULLA = 315;
    private static final int ALTO_FILA_PATRULLA = 140;
    private static final int CANTIDAD_CUADROS_PATRULLA = 3;
    private static final int X_PATRULLA_IZQUIERDA = 230;
    private static final int ANCHO_PATRULLA_IZQUIERDA = 306;
    private static final int X_PATRULLA_DERECHA = 1018;
    private static final int ANCHO_PATRULLA_DERECHA = 288;
    private static final int INICIO_FILA_MUERTE = 565;
    private static final int ALTO_FILA_MUERTE = 150;
    private static final int X_PRIMER_FRAME_MUERTE = 1152;
    private static final int ANCHO_FILA_MUERTE = 384;
    private static final int CANTIDAD_CUADROS_MUERTE = 3;

    private SpritesFantasma() {
    }

    public static BufferedImage[] cargarPatrulla(boolean haciaDerecha) {
        int x = haciaDerecha ? X_PATRULLA_DERECHA : X_PATRULLA_IZQUIERDA;
        int ancho = haciaDerecha
            ? ANCHO_PATRULLA_DERECHA
            : ANCHO_PATRULLA_IZQUIERDA;
        SpriteSheet hoja = new SpriteSheet(loadsave.GetSpriteatlas(RUTA_HOJA),
            CANTIDAD_CUADROS_PATRULLA,
            x, INICIO_FILA_PATRULLA, ancho, ALTO_FILA_PATRULLA);
        return SpritesRatas.crearFramesAlineados(hoja, 0);
    }

    public static BufferedImage[] cargarMuerte() {
        SpriteSheet hoja = new SpriteSheet(loadsave.GetSpriteatlas(RUTA_HOJA),
            CANTIDAD_CUADROS_MUERTE,
            X_PRIMER_FRAME_MUERTE, INICIO_FILA_MUERTE,
            ANCHO_FILA_MUERTE, ALTO_FILA_MUERTE);
        return SpritesRatas.crearFramesAlineados(hoja, 0);
    }
}
