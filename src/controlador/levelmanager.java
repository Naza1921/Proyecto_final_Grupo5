package controlador;

import modelos.escenario.levels.level;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import controlador.utilz.loadsave;

public class levelmanager {

    private BufferedImage[] nivelsprite;
    private level niveluno;
    //public static final int TILE_AIRE = 6;

    public levelmanager() {
        importoutsidesprites();
        niveluno = new level(loadsave.obtenerdatonivel());
    }

private void importoutsidesprites() {
    BufferedImage img = loadsave.GetSpriteatlas(loadsave.NIVEL_ATLAS);

    int columnas = img.getWidth() / 32;
    int filas = img.getHeight() / 32;

    nivelsprite = new BufferedImage[columnas * filas];

    for (int j = 0; j < filas; j++) {
        for (int i = 0; i < columnas; i++) {
            int index = j * columnas + i;
            nivelsprite[index] =
                img.getSubimage(i * 32, j * 32, 32, 32);
        }
    }
}
 public level getNivel() { 
    return niveluno; }

public void draw(Graphics g, int tile) {
    if (niveluno == null) return;
    for (int j = 0; j < niveluno.getAlto(); j++) {
        for (int i = 0; i < niveluno.getAncho(); i++) {
            int index = niveluno.getspriteindex(i, j);
            if (index % level.COLUMNAS_ATLAS == level.COLUMNA_AIRE) continue; // aire
            if (index >= 0 && index < nivelsprite.length)
                g.drawImage(nivelsprite[index], i * tile, j * tile, tile, tile, null);
        }
    }
}
}