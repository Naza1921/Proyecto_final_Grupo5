package modelos.items;

public final class PowerUpInvulnerabilidad extends PowerUpBase {
    public PowerUpInvulnerabilidad(long duracionMs, double x, double y) {
        super(duracionMs, x, y, new EfectoInvulnerabilidad());
    }
}
