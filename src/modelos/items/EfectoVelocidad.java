package modelos.items;

import modelos.personajes.Personaje;

public final class EfectoVelocidad implements EfectoTemporal {
    private static final double MULTIPLICADOR = 1.5;
    private static final RecompensaPowerUp RECOMPENSA = new RecompensaPowerUp(475, false);

    @Override
    public CategoriaEfectoTemporal getCategoria() {
        return CategoriaEfectoTemporal.VELOCIDAD;
    }

    @Override
    public RecompensaPowerUp getRecompensa() {
        return RECOMPENSA;
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
