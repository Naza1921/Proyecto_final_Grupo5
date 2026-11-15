package modelos.items;

public final class PowerUpVida extends PowerUpBase {
    private static final RecompensaPowerUp RECOMPENSA = new RecompensaPowerUp(1_000, false);
    private static final int CANTIDAD_CURACION = 200;

    public PowerUpVida(double x, double y) {
        super(x, y, CategoriaEfectoTemporal.SALUD, RECOMPENSA, CANTIDAD_CURACION);
    }
}
