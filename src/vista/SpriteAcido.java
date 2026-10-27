package vista;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import modelos.enemigos.ProyectilAcido;
import recursos.CargadorRecursos;

public final class SpriteAcido {
    private static final String RUTA_HOJA = "assets/arte_enemigos/acido_volando_5frames_transparente.png";
    private static final int CANTIDAD_FRAMES = 5;
    private static final int TAMANO_DIBUJO = 18;
    private static final int TAMANO_HITBOX = 12;
    private static final long DURACION_FRAME_NANOS = 90_000_000L;

    private final BufferedImage[] frames;
    private int indiceFrame;
    private long inicioFrameNanos = System.nanoTime();

    public SpriteAcido() {
        frames = cargarFrames();
    }

    public void actualizar() {
        long ahora = System.nanoTime();
        long transcurrido = ahora - inicioFrameNanos;
        if (transcurrido >= DURACION_FRAME_NANOS) {
            long framesTranscurridos = transcurrido / DURACION_FRAME_NANOS;
            indiceFrame = (int) ((indiceFrame + framesTranscurridos) % frames.length);
            inicioFrameNanos += framesTranscurridos * DURACION_FRAME_NANOS;
        }
    }

    public void dibujar(Graphics2D graficos, ProyectilAcido proyectil) {
        if (graficos == null || proyectil == null) {
            throw new IllegalArgumentException("Se requieren los gráficos y el proyectil");
        }

        int desplazamiento = (TAMANO_DIBUJO - TAMANO_HITBOX) / 2;
        SpritesRatas.dibujarAjustado(
            graficos,
            frames[indiceFrame],
            (int) Math.round(proyectil.getX()) - desplazamiento,
            (int) Math.round(proyectil.getY()) - desplazamiento,
            TAMANO_DIBUJO,
            TAMANO_DIBUJO
        );
    }

    private BufferedImage[] cargarFrames() {
        BufferedImage hoja = CargadorRecursos.cargarImagen(RUTA_HOJA);
        if (hoja.getWidth() % CANTIDAD_FRAMES != 0) {
            throw new IllegalArgumentException(
                "La hoja de ácido debe dividirse en cinco frames iguales"
            );
        }

        int anchoFrame = hoja.getWidth() / CANTIDAD_FRAMES;
        BufferedImage[] recortados = new BufferedImage[CANTIDAD_FRAMES];
        int anchoLienzo = 0;
        int altoLienzo = 0;
        for (int indice = 0; indice < CANTIDAD_FRAMES; indice++) {
            int x = indice * anchoFrame;
            BufferedImage frame = hoja.getSubimage(x, 0, anchoFrame, hoja.getHeight());
            BufferedImage recortado = recortarTransparencia(frame);
            recortados[indice] = recortado;
            anchoLienzo = Math.max(anchoLienzo, recortado.getWidth());
            altoLienzo = Math.max(altoLienzo, recortado.getHeight());
        }

        BufferedImage[] alineados = new BufferedImage[CANTIDAD_FRAMES];
        for (int indice = 0; indice < CANTIDAD_FRAMES; indice++) {
            BufferedImage frame = recortados[indice];
            BufferedImage alineado = new BufferedImage(
                anchoLienzo, altoLienzo, BufferedImage.TYPE_INT_ARGB
            );
            Graphics2D graficos = alineado.createGraphics();
            graficos.drawImage(
                frame,
                (anchoLienzo - frame.getWidth()) / 2,
                (altoLienzo - frame.getHeight()) / 2,
                null
            );
            graficos.dispose();
            alineados[indice] = alineado;
        }
        return alineados;
    }

    private BufferedImage recortarTransparencia(BufferedImage imagen) {
        int izquierda = imagen.getWidth();
        int superior = imagen.getHeight();
        int derecha = -1;
        int inferior = -1;
        for (int y = 0; y < imagen.getHeight(); y++) {
            for (int x = 0; x < imagen.getWidth(); x++) {
                if ((imagen.getRGB(x, y) >>> 24) != 0) {
                    izquierda = Math.min(izquierda, x);
                    superior = Math.min(superior, y);
                    derecha = Math.max(derecha, x);
                    inferior = Math.max(inferior, y);
                }
            }
        }
        if (derecha < izquierda || inferior < superior) {
            throw new IllegalArgumentException("La hoja de ácido contiene un frame vacío");
        }
        return imagen.getSubimage(
            izquierda, superior, derecha - izquierda + 1, inferior - superior + 1
        );
    }
}
