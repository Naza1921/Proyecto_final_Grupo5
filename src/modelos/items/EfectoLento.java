package modelos.items;

import modelos.personajes.Personaje;
import modelos.personajes.EstadisticasRata;
import modelos.personajes.LentitudTemporal;

public final class EfectoLento implements EfectoTemporal {
    private static final RecompensaPowerUp RECOMPENSA = new RecompensaPowerUp(300, true);

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
    }

    @Override
    public void revertir(Personaje personaje) {
    }

    @Override
    public EstadisticasRata decorarEstadisticas(EstadisticasRata estadisticas) {
        return new LentitudTemporal(estadisticas);
    }
}
