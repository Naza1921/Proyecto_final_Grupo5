package modelos.items;

import modelos.personajes.Personaje;
import modelos.personajes.EstadisticasRata;
import modelos.personajes.Invulnerabilidad;

public final class EfectoInvulnerabilidad implements EfectoTemporal {
    private static final RecompensaPowerUp RECOMPENSA = new RecompensaPowerUp(1_000, false);

    @Override
    public CategoriaEfectoTemporal getCategoria() {
        return CategoriaEfectoTemporal.DEFENSA;
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
        return new Invulnerabilidad(estadisticas);
    }
}
