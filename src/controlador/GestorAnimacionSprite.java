package controlador;

import java.awt.image.BufferedImage;

// Controla el avance temporal de una secuencia y expone su frame ya alineado.
public final class GestorAnimacionSprite {
    private final BufferedImage[] frames;
    private final long duracionFrameNanos;
    private int indiceActual;
    private long inicioFrameNanos;

    public GestorAnimacionSprite(BufferedImage[] frames, long duracionFrameNanos) {
        if (frames == null || frames.length == 0 || duracionFrameNanos <= 0) {
            throw new IllegalArgumentException("La animación requiere frames y una duración válida");
        }
        this.frames = frames.clone();
        this.duracionFrameNanos = duracionFrameNanos;
        this.inicioFrameNanos = System.nanoTime();

        BufferedImage primerFrame = this.frames[0];
        if (primerFrame == null) {
            throw new IllegalArgumentException("La animación no puede contener frames nulos");
        }
        for (BufferedImage frame : this.frames) {
            if (frame == null || frame.getWidth() != primerFrame.getWidth()
                    || frame.getHeight() != primerFrame.getHeight()) {
                throw new IllegalArgumentException(
                    "Todos los frames deben compartir el mismo lienzo para evitar saltos"
                );
            }
        }
    }

    // Avanza por tiempo transcurrido y conserva la cadencia aunque Swing se retrase.
    public void actualizar() {
        long ahora = System.nanoTime();
        long tiempoTranscurrido = ahora - inicioFrameNanos;
        if (tiempoTranscurrido >= duracionFrameNanos) {
            long framesTranscurridos = tiempoTranscurrido / duracionFrameNanos;
            indiceActual = (int) ((indiceActual + framesTranscurridos) % frames.length);
            inicioFrameNanos += framesTranscurridos * duracionFrameNanos;
        }
    }

    public BufferedImage getFrameActual() {
        return frames[indiceActual];
    }

    public int getAncho() {
        return frames[0].getWidth();
    }

    public int getAlto() {
        return frames[0].getHeight();
    }
}
