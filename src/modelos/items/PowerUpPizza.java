package modelos.items;

public final class PowerUpPizza extends PowerUpBase {
    public PowerUpPizza(long duracionMs, double x, double y) {
        super(duracionMs, x, y, new EfectoInvulnerabilidad());
    }
}
