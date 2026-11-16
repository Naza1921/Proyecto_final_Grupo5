package aplicacion;

public record ProyectilVista(double x, double y, double ancho, double alto) {
    public ProyectilVista {
        if (!Double.isFinite(x) || !Double.isFinite(y)
                || !Double.isFinite(ancho) || ancho <= 0
                || !Double.isFinite(alto) || alto <= 0) {
            throw new IllegalArgumentException("Los datos del proyectil deben ser válidos");
        }
    }
}
