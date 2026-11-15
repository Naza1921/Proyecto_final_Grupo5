package modelos.items;

public final class PowerUpPizza extends PowerUpBase {
    private static final RecompensaPowerUp RECOMPENSA = new RecompensaPowerUp(1_000, false);

    public PowerUpPizza(long duracionMs, double x, double y) {
        super(
            duracionMs,
            x,
            y,
            CategoriaEfectoTemporal.DEFENSA,
            RECOMPENSA,
            DecoradorInmortal::new
        );
    }
}
