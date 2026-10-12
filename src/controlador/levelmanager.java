package controlador;

import modelos.escenario.utilz.loadsave;
import modelos.escenario.levels.level;
import java.awt.image.BufferedImage;
import java.awt.Graphics;

public class levelmanager {

   // private PrincipalControlador juego;
    private BufferedImage[] nivelsprite;
    private level niveluno;

    public levelmanager() {
 //       this.juego = juego;
 //       nivelsprite = loadsave.GetPlayeratlas(loadsave.NIVEL_ATLAS);
        importoutsidesprites();
    }
    private void importoutsidesprites() {
        BufferedImage img = loadsave.GetSpriteatlas(loadsave.NIVEL_ATLAS);
        nivelsprite = new BufferedImage[48];
        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 12; i++) {
                int index = j * 12 + i;
                nivelsprite[index] = img.getSubimage(i * 32, j * 32, 32, 32);
            }
        }
    }

    public void draw(Graphics g) {
        for (int j = 0; j < 10; j++) {
            for (int i = 0; i < 10; i++) {
                int index = niveluno.getspriteindex(i, j);
                g.drawImage(nivelsprite[index], i * 32, j * 32, 10, 10, null);
            }
        }
    }

    public void update() {
    }

}
