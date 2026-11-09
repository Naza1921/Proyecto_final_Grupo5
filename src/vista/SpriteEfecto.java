package vista;

import java.awt.Graphics2D;
import modelos.personajes.EfectoHabilidad;

interface SpriteEfecto {
    String getClaveSprite();

    void actualizarAnimacion();

    void actualizar(EfectoHabilidad efecto);

    void dibujar(Graphics2D graficos, EfectoHabilidad efecto);
}
