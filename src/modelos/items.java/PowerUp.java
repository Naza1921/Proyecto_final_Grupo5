package modelos.items;

import modelos.personajes.Personaje;

public interface PowerUp {
    void aplicarEfecto(Personaje personaje);
    void revertirEfecto(Personaje personaje);
    long getDuracion();
    double getX();
    double getY();
    boolean colisionaCon(Personaje personaje);
}