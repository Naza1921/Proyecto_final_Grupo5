package controlador;

import modelos.escenario.levels.level;
import modelos.personajes.Personaje;

public class ColisionNivel {
    private level nivel;                       // ya no es final
    private final int tile, ancho, alto;
    private double xSeguro, ySeguro;
    private boolean iniciado;

    public ColisionNivel(level nivel, int tile, int ancho, int alto) {
        this.nivel = nivel; this.tile = tile; this.ancho = ancho; this.alto = alto;
    }

    public void setNivel(level nivel) {
        this.nivel = nivel;
        iniciado = false;
    }

    public void resolver(Personaje p) {
        if (!iniciado) { xSeguro = p.getX(); ySeguro = p.getY(); iniciado = true; }
        double x = p.getX(), y = p.getY();

        // Eje X
        if (nivel.haySolidoEn(x, ySeguro, ancho, alto, tile)) {
            if (x > xSeguro) {
                int colDer = (int) Math.floor((x + ancho - 1) / tile);
                x = colDer * tile - ancho;
            } else {
                int colIzq = (int) Math.floor(x / tile);
                x = (colIzq + 1) * tile;
            }
            p.setX(x);
        }

        // Eje Y
        if (nivel.haySolidoEn(x, y, ancho, alto, tile)) {
            if (p.getVelocidadY() >= 0) {
                int filaPies = (int) Math.floor((y + alto - 1) / tile);
                y = filaPies * tile - alto;
                p.aterrizar(y);
            } else {
                int filaCabeza = (int) Math.floor(y / tile);
                y = (filaCabeza + 1) * tile;
                p.corregirPosicion(x, y);
                p.setVelocidadY(0);
            }
        } else if (!p.estaEnElAire() && !nivel.haySolidoEn(x, y + 1, ancho, alto, tile)) {
            p.setEnElAire(true);
            p.setVelocidadY(0);
        }

        xSeguro = p.getX();
        ySeguro = p.getY();
    }
}