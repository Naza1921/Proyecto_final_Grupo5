package modelos.items;

public final class PowerUpVelocidad extends PowerUpBase {
    private static final RecompensaPowerUp RECOMPENSA = new RecompensaPowerUp(1_000, false);

    public PowerUpVelocidad(long duracionMs, double x, double y) {
        super(
            duracionMs,
            x,
            y,
            CategoriaEfectoTemporal.VELOCIDAD,
            RECOMPENSA,
            DecoradorVelocidad::new
        );
    }
}
