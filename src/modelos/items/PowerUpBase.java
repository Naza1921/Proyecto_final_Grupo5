package modelos.items;

import modelos.personajes.Personaje;

public abstract class PowerUpBase implements PowerUp {
    private final long duracion;
    private final double x;
    private final double y;
    private final EfectoTemporal efecto;

    protected PowerUpBase(long duracion, double x, double y, EfectoTemporal efecto) {
        if (duracion < 0 || (duracion == 0 && (efecto == null || !efecto.esInstantaneo()))
                || !Double.isFinite(x) || !Double.isFinite(y) || efecto == null) {
            throw new IllegalArgumentException("El power-up requiere duración, posición y efecto válidos");
        }
        this.duracion = duracion;
        this.x = x;
        this.y = y;
        this.efecto = efecto;
    }

    @Override
    public EfectoTemporal getEfecto() {
        return efecto;
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
        if (personaje == null) {
            throw new IllegalArgumentException("Se requiere un personaje para comprobar la colisión");
        }
        double distancia = Math.sqrt(Math.pow(personaje.getX() - x, 2) + Math.pow(personaje.getY() - y, 2));
        return distancia < 20; // Radio de colisión
    }
}