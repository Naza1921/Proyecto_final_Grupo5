package modelos.items;

public final class PowerUpVida extends PowerUpBase {
    public PowerUpVida(double x, double y) {
        super(0, x, y, new EfectoCuracion());
    }
}
