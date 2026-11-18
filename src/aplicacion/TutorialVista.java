package aplicacion;

public record TutorialVista(double x, double y, int ancho, int alto) {
    public TutorialVista {
        if (!Double.isFinite(x) || !Double.isFinite(y) || ancho <= 0 || alto <= 0) {
            throw new IllegalArgumentException("Los datos visuales del tutorial deben ser válidos");
        }
    }
}
