package modelos.items;

import modelos.personajes.Personaje;

public interface EfectoTemporal {
    CategoriaEfectoTemporal getCategoria();
    RecompensaPowerUp getRecompensa();
    void aplicar(Personaje personaje);
    void revertir(Personaje personaje);

    default boolean esInstantaneo() {
        return false;
    }
}
