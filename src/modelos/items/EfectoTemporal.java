package modelos.items;

import modelos.personajes.Personaje;
import modelos.personajes.EstadisticasRata;

public interface EfectoTemporal {
    CategoriaEfectoTemporal getCategoria();
    RecompensaPowerUp getRecompensa();

    default void aplicarInstantaneo(Personaje personaje) {
        throw new IllegalStateException("El efecto no es instantáneo");
    }

    default EstadisticasRata decorarEstadisticas(EstadisticasRata estadisticas) {
        return estadisticas;
    }

    default boolean esInstantaneo() {
        return false;
    }
}
