package aplicacion;

public record HabilidadVista(
        long id,
        Tipo tipo,
        Estado estado,
        double x,
        double y,
        double ancho,
        double alto) {
    public enum Tipo {
        CHARCO_ACIDO, LAMPARA_FUEGO
    }

    public enum Estado {
        ACTIVA, PREPARANDO, VOLANDO, FUEGO
    }

    public HabilidadVista {
        if (id < 0 || tipo == null || estado == null
                || !Double.isFinite(x) || !Double.isFinite(y)
                || !Double.isFinite(ancho) || ancho <= 0
                || !Double.isFinite(alto) || alto <= 0) {
            throw new IllegalArgumentException("Los datos de la habilidad deben ser válidos");
        }
    }
}
