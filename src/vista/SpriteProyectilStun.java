package vista;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import animacion.GestorAnimacionSprite;
import recursos.CargadorRecursos;
import modelos.personajes.ProyectilEspecial;

public final class SpriteProyectilStun {
    private static final String RUTA_HOJA = "assets/rata_blanca/hadouken_en_aire.jfif";
    private static final int CANTIDAD_FRAMES = 4;
    private static final int ALTO_REGION = 360;
    private static final int TAMANO_DIBUJO = 28;
    private static final long DURACION_FRAME_NANOS = 100_000_000L;

    private final GestorAnimacionSprite animacion;

    public SpriteProyectilStun() {
        BufferedImage hoja = CargadorRecursos.cargarImagen(RUTA_HOJA);
        if (hoja.getWidth() < CANTIDAD_FRAMES || hoja.getHeight() < ALTO_REGION) {
            throw new IllegalArgumentException("La hoja del proyectil STUN no tiene cuatro frames válidos");
        }
        SpriteSheet spriteSheet = new SpriteSheet(
            hoja,
            CANTIDAD_FRAMES,
            0,
            0,
            hoja.getWidth(),
            ALTO_REGION
        );
        BufferedImage[] frames = SpritesRatas.crearFramesAlineados(spriteSheet, 0);
        animacion = new GestorAnimacionSprite(frames, DURACION_FRAME_NANOS);
    }

    public void actualizar() {
        animacion.actualizar();
    }

    public void dibujar(Graphics2D graficos, ProyectilEspecial proyectil) {
        if (graficos == null || proyectil == null) {
            throw new IllegalArgumentException("Se requieren los gráficos y el proyectil STUN");
        }
        int x = (int) Math.round(proyectil.getX() + proyectil.getAncho() / 2.0 - TAMANO_DIBUJO / 2.0);
        int y = (int) Math.round(proyectil.getY() + proyectil.getAlto() / 2.0 - TAMANO_DIBUJO / 2.0);
        SpritesRatas.dibujarAjustado(
            graficos,
            animacion.getFrameActual(),
            x,
            y,
            TAMANO_DIBUJO,
            TAMANO_DIBUJO
        );
    }
}
