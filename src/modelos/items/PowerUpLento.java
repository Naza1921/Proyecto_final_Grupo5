package modelos.items;

public final class PowerUpLento extends PowerUpBase {
    private static final RecompensaPowerUp RECOMPENSA = new RecompensaPowerUp(1_000, true);

    public PowerUpLento(long duracionMs, double x, double y) {
        super(
            duracionMs,
            x,
            y,
            CategoriaEfectoTemporal.VELOCIDAD,
            RECOMPENSA,
            DecoradorLentitud::new
        );
    }
}
