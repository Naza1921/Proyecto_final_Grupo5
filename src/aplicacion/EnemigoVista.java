package aplicacion;

import java.util.List;

public record EnemigoVista(
        long id,
        Tipo tipo,
        double x,
        double y,
        double ancho,
        double alto,
        boolean vivo,
        boolean moviendoseDerecha,
        List<ProyectilVista> proyectiles) {
    public enum Tipo {
        FANTASMA, RATA_INFESTADA
    }

    public EnemigoVista {
        if (id < 0 || tipo == null || !Double.isFinite(x) || !Double.isFinite(y)
                || !Double.isFinite(ancho) || ancho <= 0
                || !Double.isFinite(alto) || alto <= 0 || proyectiles == null
                || proyectiles.stream().anyMatch(proyectil -> proyectil == null)) {
            throw new IllegalArgumentException("Los datos del enemigo deben ser válidos");
        }
        proyectiles = List.copyOf(proyectiles);
    }
}
