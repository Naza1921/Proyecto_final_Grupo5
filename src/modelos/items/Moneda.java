package modelos.items;

import modelos.personajes.Personaje;

public final class Moneda {
    private static final double RADIO_COLISION = 18;
    private final double x;
    private final double y;

    public Moneda(double x, double y) {
        if (!Double.isFinite(x) || x < 0 || !Double.isFinite(y) || y < 0) {
            throw new IllegalArgumentException("La posición de la moneda debe ser válida");
        }
        this.x = x;
        this.y = y;
    }

    public boolean colisionaCon(Personaje personaje) {
        if (personaje == null) {
            throw new IllegalArgumentException("Se requiere un personaje para recoger la moneda");
        }
        return Math.hypot(personaje.getX() - x, personaje.getY() - y) < RADIO_COLISION;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
