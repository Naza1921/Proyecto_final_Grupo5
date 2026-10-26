package vista;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import modelos.enemigos.ProyectilAcido;

public final class SpriteAcido {
    private static final int TAMANO_DIBUJO = 18;
    private static final int TAMANO_HITBOX = 12;

    private final BufferedImage imagen;

    public SpriteAcido(BufferedImage imagen) {
        if (imagen == null) {
            throw new IllegalArgumentException("Se requiere la imagen del ácido");
        }
        this.imagen = imagen;
    }

    public void dibujar(Graphics2D graficos, ProyectilAcido proyectil) {
        if (graficos == null || proyectil == null) {
            throw new IllegalArgumentException("Se requieren los gráficos y el proyectil");
        }

        int desplazamiento = (TAMANO_DIBUJO - TAMANO_HITBOX) / 2;
        SpritesRatas.dibujarAjustado(
            graficos,
            imagen,
            (int) Math.round(proyectil.getX()) - desplazamiento,
            (int) Math.round(proyectil.getY()) - desplazamiento,
            TAMANO_DIBUJO,
            TAMANO_DIBUJO
        );
    }
}
