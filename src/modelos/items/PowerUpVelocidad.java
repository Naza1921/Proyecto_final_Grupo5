package modelos.items;

public final class PowerUpVelocidad extends PowerUpBase {
    public PowerUpVelocidad(long duracionMs, double x, double y) {
        super(duracionMs, x, y, new EfectoVelocidad());
    }
}
