package modelos.items;

import modelos.personajes.Personaje;

public interface PowerUp {
    EfectoTemporal getEfecto();
    // The duration is expressed in milliseconds.
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