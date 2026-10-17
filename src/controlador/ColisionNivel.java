package controlador;


import modelos.escenario.levels.level;
import modelos.personajes.Personaje;

public class ColisionNivel {
    private final level nivel;
    private final int tile, ancho, alto;
    private double xSeguro, ySeguro;
    private boolean iniciado;

    public ColisionNivel(level nivel, int tile, int ancho, int alto) {
        this.nivel = nivel; this.tile = tile; this.ancho = ancho; this.alto = alto;
    }

    // Llamar una vez por tick, DESPUÉS de mover al personaje y aplicar la gravedad.
    public void resolver(Personaje p) {
        if (!iniciado) { xSeguro = p.getX(); ySeguro = p.getY(); iniciado = true; }
        double x = p.getX(), y = p.getY();

        // Eje X: si la nueva x choca (con la y anterior), se vuelve a la x anterior
        if (nivel.haySolidoEn(x, ySeguro, ancho, alto, tile)) {
            x = xSeguro;
            p.setX(x);
        }

        // Eje Y
        if (nivel.haySolidoEn(x, y, ancho, alto, tile)) {
            if (p.getVelocidadY() >= 0) { // cayendo: apoyar sobre el tile
                int filaPies = (int) Math.floor((y + alto - 1) / tile);
                y = filaPies * tile - alto;
                p.aterrizar(y);
            } else {                      // subiendo: golpea el techo
                int filaCabeza = (int) Math.floor(y / tile);
                y = (filaCabeza + 1) * tile;
                p.corregirPosicion(x, y);
                p.setVelocidadY(0);
            }
        } else if (!p.estaEnElAire() && !nivel.haySolidoEn(x, y + 1, ancho, alto, tile)) {
            // Caminó fuera de la plataforma: empieza a caer
            p.setEnElAire(true);
            p.setVelocidadY(0);
        }

        xSeguro = p.getX();
        ySeguro = p.getY();
    }
}
