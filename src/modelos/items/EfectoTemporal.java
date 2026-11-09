package modelos.items;

import modelos.personajes.Personaje;
import modelos.personajes.EstadisticasRata;

public interface EfectoTemporal {
    CategoriaEfectoTemporal getCategoria();
    RecompensaPowerUp getRecompensa();
    void aplicar(Personaje personaje);
    void revertir(Personaje personaje);

    default EstadisticasRata decorarEstadisticas(EstadisticasRata estadisticas) {
        return estadisticas;
    }

    default boolean esInstantaneo() {
        return false;
    }
}
