package aplicacion;

public record PowerUpVista(TipoPowerUpVista tipo, double x, double y) {
    public PowerUpVista {
        if (tipo == null || !Double.isFinite(x) || !Double.isFinite(y)) {
            throw new IllegalArgumentException("Los datos del power-up deben ser válidos");
        }
    }
}
