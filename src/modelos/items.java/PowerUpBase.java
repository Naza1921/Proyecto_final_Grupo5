package modelos.items;

import modelos.personajes.Personaje;

public abstract class PowerUpBase implements PowerUp {
    private final long duracion;
    private final double x;
    private final double y;

    public PowerUpBase(long duracion, double x, double y) {
        this.duracion = duracion;
        this.x = x;
        this.y = y;
    }

    @Override
    public long getDuracion() {
        return duracion;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public boolean colisionaCon(Personaje personaje) {
        double distancia = Math.sqrt(Math.pow(personaje.getX() - x, 2) + Math.pow(personaje.getY() - y, 2));
        return distancia < 20; // Radio de colisión
    }

    @Override
    public void revertirEfecto(Personaje personaje) {
        // Por defecto, no hace nada. Las subclases pueden sobrescribir este método.
    }
}