package modelos.items;

import modelos.personajes.Personaje;

public interface PowerUp {
    CategoriaEfectoTemporal getCategoria();
    RecompensaPowerUp getRecompensa();
    long getDuracion();
    double getX();
    double getY();
    boolean colisionaCon(Personaje personaje);
    boolean esInstantaneo();
    void aplicarEfecto(Personaje personaje);
    void revertirEfecto(Personaje personaje);
}