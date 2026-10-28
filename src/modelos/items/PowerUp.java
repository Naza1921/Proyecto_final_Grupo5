package modelos.items;

import modelos.personajes.Personaje;

public interface PowerUp {
    EfectoTemporal getEfecto();
    long getDuracion();
    double getX();
    double getY();
    boolean colisionaCon(Personaje personaje);

    default void aplicarEfecto(Personaje personaje) {
        getEfecto().aplicar(personaje);
    }

    default void revertirEfecto(Personaje personaje) {
        getEfecto().revertir(personaje);
    }
}