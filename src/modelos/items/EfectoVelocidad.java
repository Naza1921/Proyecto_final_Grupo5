package modelos.items;

import modelos.personajes.EstadisticasRata;
import modelos.personajes.MejoraVelocidad;

public final class EfectoVelocidad implements EfectoTemporal {
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
    public EstadisticasRata decorarEstadisticas(EstadisticasRata estadisticas) {
        return new MejoraVelocidad(estadisticas);
    }
}
