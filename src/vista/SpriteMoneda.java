package vista;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import controlador.GestorAnimacionSprite;
import controlador.utilz.loadsave;
import modelos.items.Moneda;

public final class SpriteMoneda {
    private static final String RUTA_HOJA = "assets/powerUp/monedas.png";
    private static final int[][] LIMITES_FRAMES = {
        {45, 111, 84, 202},
        {99, 111, 152, 202},
        {168, 111, 252, 202},
        {267, 111, 359, 202},
        {374, 111, 458, 202},
        {474, 111, 527, 202},
        {542, 111, 581, 202}
    };
    private static final int ANCHO_DIBUJO = 26;
    private static final int ALTO_DIBUJO = 30;
    private static final long DURACION_FRAME_NANOS = 110_000_000L;
    private static final int ANCHO_FRAME = 92;
    private static final int ALTO_FRAME = 91;

    private final GestorAnimacionSprite animacion;

    public SpriteMoneda() {
        BufferedImage hoja = loadsave.GetSpriteatlas(RUTA_HOJA);
        BufferedImage[] frames = new BufferedImage[LIMITES_FRAMES.length];
        for (int indice = 0; indice < LIMITES_FRAMES.length; indice++) {
            int[] limites = LIMITES_FRAMES[indice];
            if (limites[2] > hoja.getWidth() || limites[3] > hoja.getHeight()) {
                throw new IllegalArgumentException("La hoja de monedas no tiene el tamaño esperado");
            }
            BufferedImage recorte = hoja.getSubimage(
                limites[0], limites[1], limites[2] - limites[0], limites[3] - limites[1]
            );
            frames[indice] = new BufferedImage(
                ANCHO_FRAME, ALTO_FRAME, BufferedImage.TYPE_INT_ARGB
            );
            Graphics2D graficos = frames[indice].createGraphics();
            try {
                graficos.drawImage(
                    recorte, (ANCHO_FRAME - recorte.getWidth()) / 2, 0, null
                );
            } finally {
                graficos.dispose();
            }
        }
        animacion = new GestorAnimacionSprite(frames, DURACION_FRAME_NANOS);
    }

    public void actualizar() {
        animacion.actualizar();
    }

    public void dibujar(Graphics2D graficos, Moneda moneda) {
        if (graficos == null || moneda == null) {
            throw new IllegalArgumentException("Se requieren los gráficos y la moneda");
        }
        graficos.drawImage(
            animacion.getFrameActual(),
            (int) Math.round(moneda.getX() - ANCHO_DIBUJO / 2.0),
            (int) Math.round(moneda.getY() - ALTO_DIBUJO / 2.0),
            ANCHO_DIBUJO,
            ALTO_DIBUJO,
            null
        );
    }
}
