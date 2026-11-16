package aplicacion;

public record MonedaVista(double x, double y) {
    public MonedaVista {
        if (!Double.isFinite(x) || !Double.isFinite(y)) {
            throw new IllegalArgumentException("La posición de la moneda debe ser válida");
        }
    }
}
