package controlador;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import controlador.utilz.loadsave;
import modelos.escenario.levels.level;

public class levelmanager {

    private static final String[][] RUTAS = {
        { "assets/escenarios/piso_1_1.png","assets/escenarios/piso_1_2.png", "assets/escenarios/piso_1_3.png" }
    };

    private BufferedImage[] nivelsprite;
    private final BufferedImage[] agujeroSprite = new BufferedImage[3];
    private level[][] niveles;
    private int piso = 0, seccion = 0;

    public levelmanager() {
        importoutsidesprites();

        BufferedImage tira = loadsave.GetSpriteatlas(loadsave.AGUJERO_ATLAS);
        for (int k = 0; k < 3; k++) agujeroSprite[k] = tira.getSubimage(k * 32, 0, 32, 32);

        niveles = new level[RUTAS.length][];
        for (int p = 0; p < RUTAS.length; p++) {
            niveles[p] = new level[RUTAS[p].length];
            for (int s = 0; s < RUTAS[p].length; s++)
                if (RUTAS[p][s] != null)
                    niveles[p][s] = new level(loadsave.obtenerdatonivel(RUTAS[p][s]));
        }
    }

    private void importoutsidesprites() {
        BufferedImage img = loadsave.GetSpriteatlas(loadsave.NIVEL_ATLAS);
        int columnas = img.getWidth() / 32;
        int filas = img.getHeight() / 32;
        nivelsprite = new BufferedImage[columnas * filas];
        for (int j = 0; j < filas; j++)
            for (int i = 0; i < columnas; i++)
                nivelsprite[j * columnas + i] = img.getSubimage(i * 32, j * 32, 32, 32);
    }

    public level getNivel() { return niveles[piso][seccion]; }
    public int getSeccion() { return seccion; }

    private boolean existe(int p, int s) {
        return p >= 0 && p < niveles.length && s >= 0 && s < niveles[p].length && niveles[p][s] != null;
    }

    public boolean esUltimaSeccion() { return !existe(piso, seccion + 1); }

    public boolean cambiarSeccion(int delta) {
        if (!existe(piso, seccion + delta)) return false;
        seccion += delta;
        return true;
    }

    public boolean cambiarPiso(int delta) {
        if (!existe(piso + delta, seccion)) return false;
        piso += delta;
        return true;
    }

    public void draw(Graphics g, int tile) {
        level nivel = getNivel();
        for (int j = 0; j < nivel.getAlto(); j++) {
            for (int i = 0; i < nivel.getAncho(); i++) {
                int index = nivel.getspriteindex(i, j);
                if (index >= level.AGUJERO_SIGUIENTE) {
                    dibujarAgujero(g, i * tile, j * tile, tile, index);
                    continue;
                }
                if (index % level.COLUMNAS_ATLAS == level.COLUMNA_AIRE) continue;
                if (index >= 0 && index < nivelsprite.length)
                    g.drawImage(nivelsprite[index], i * tile, j * tile, tile, tile, null);
            }
        }
    }

    private void dibujarAgujero(Graphics g, int x, int y, int t, int tipo) {
        int k = (tipo == level.AGUJERO_SIGUIENTE) ? (esUltimaSeccion() ? 2 : 0) : 1;
        g.drawImage(agujeroSprite[k], x, y, t, t, null);
    }

    public void update() { }
}