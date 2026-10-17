package modelos.personajes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

public class Hitbox {

    private Rectangle2D.Float hitbox;

    // Creamos la hitbox con su posición y tamaño
    public Hitbox(float x, float y, float width, float height) {
        hitbox = new Rectangle2D.Float(x, y, width, height);
    }

    // Actualizamos la posición de la hitbox
    public void actualizar(float x, float y) {
        hitbox.x = x;
        hitbox.y = y;
    }

    // Dibujamos la hitbox en pantalla
    public void dibujar(Graphics g) {
        g.setColor(Color.PINK);

        g.drawRect(
            (int) hitbox.x,
            (int) hitbox.y,
            (int) hitbox.width,
            (int) hitbox.height
        );
    }

    // Devolvemos el rectángulo para comprobar colisiones
    public Rectangle2D.Float getHitbox() {
        return hitbox;
    }
}
