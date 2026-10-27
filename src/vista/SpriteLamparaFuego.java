package vista;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import animacion.GestorAnimacionSprite;
import recursos.CargadorRecursos;
import modelos.personajes.LamparaFuego;

public final class SpriteLamparaFuego {
    private static final String RUTA_LAMPARA =
        "assets/rata_negra/lampara_cayendo_splash_fuego_8frames_transparente.png";
    private static final String RUTA_FUEGO =
        "assets/rata_negra/barrera_fuego_6frames_transparente.png";
    private static final long DURACION_FRAME_LAMPARA_NANOS = 100_000_000L;
    private static final long DURACION_FRAME_FUEGO_NANOS = 140_000_000L;
    private static final int CANTIDAD_FRAMES_LAMPARA = 5;
    private static final int ANCHO_DIBUJO_LAMPARA = 36;
    private static final int ALTO_DIBUJO_LAMPARA = 36;

    private final GestorAnimacionSprite animacionLampara;
    private final GestorAnimacionSprite animacionFuego;

    public SpriteLamparaFuego() {
        BufferedImage hojaLampara = CargadorRecursos.cargarImagen(RUTA_LAMPARA);
        int anchoRegionLampara = hojaLampara.getWidth() * CANTIDAD_FRAMES_LAMPARA / 8;
        SpriteSheet spritesLampara = new SpriteSheet(
            hojaLampara, CANTIDAD_FRAMES_LAMPARA, 0, 0,
            anchoRegionLampara, hojaLampara.getHeight()
        );
        animacionLampara = new GestorAnimacionSprite(
            SpritesRatas.crearFramesAlineados(spritesLampara, 0),
            DURACION_FRAME_LAMPARA_NANOS
        );

        BufferedImage hojaFuego = CargadorRecursos.cargarImagen(RUTA_FUEGO);
        SpriteSheet spritesFuego = new SpriteSheet(hojaFuego, 6);
        animacionFuego = new GestorAnimacionSprite(
            SpritesRatas.crearFramesAlineados(spritesFuego, 0),
            DURACION_FRAME_FUEGO_NANOS
        );
    }

    public void actualizar() {
        animacionLampara.actualizar();
        animacionFuego.actualizar();
    }

    public void dibujar(Graphics2D graficos, LamparaFuego habilidad) {
        if (graficos == null || habilidad == null) {
            throw new IllegalArgumentException("Se requieren los gráficos y la lámpara");
        }
        switch (habilidad.getEstado()) {
            case VOLANDO -> dibujarAjustado(
                graficos,
                animacionLampara.getFrameActual(),
                habilidad.getXLampara(),
                habilidad.getYLampara(),
                ANCHO_DIBUJO_LAMPARA,
                ALTO_DIBUJO_LAMPARA
            );
            case FUEGO -> dibujarAjustado(
                graficos,
                animacionFuego.getFrameActual(),
                habilidad.getXFuego(),
                habilidad.getYFuego(),
                LamparaFuego.ANCHO_FUEGO,
                LamparaFuego.ALTO_FUEGO
            );
            case PREPARANDO, TERMINADA -> { }
        }
    }

    private void dibujarAjustado(Graphics2D graficos, BufferedImage frame,
                                 double x, double y, double anchoMaximo, double altoMaximo) {
        double escala = Math.min(anchoMaximo / frame.getWidth(), altoMaximo / frame.getHeight());
        int ancho = (int) Math.round(frame.getWidth() * escala);
        int alto = (int) Math.round(frame.getHeight() * escala);
        int xDibujo = (int) Math.round(x + (anchoMaximo - ancho) / 2.0);
        int yDibujo = (int) Math.round(y + altoMaximo - alto);
        graficos.drawImage(frame, xDibujo, yDibujo, ancho, alto, null);
    }
}
