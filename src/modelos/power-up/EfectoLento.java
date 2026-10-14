package modelos.items;

import modelos.personajes.Personaje;

public final class EfectoLento implements EfectoTemporal {
    private static final double MULTIPLICADOR = 0.5;

    @Override
    public CategoriaEfectoTemporal getCategoria() {
        return CategoriaEfectoTemporal.VELOCIDAD;
    }

    @Override
    public void aplicar(Personaje personaje) {
        personaje.setMultiplicadorVelocidadTemporal(MULTIPLICADOR);
    }

    @Override
    public void revertir(Personaje personaje) {
        personaje.setMultiplicadorVelocidadTemporal(1.0);
    }
}
