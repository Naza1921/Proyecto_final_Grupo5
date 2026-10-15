package modelos.items;

public final class PowerUpLento extends PowerUpBase {
    public PowerUpLento(long duracionMs, double x, double y) {
        super(duracionMs, x, y, new EfectoLento());
    }
}
