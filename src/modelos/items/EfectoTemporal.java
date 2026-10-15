package modelos.items;

import modelos.personajes.Personaje;

public interface EfectoTemporal {
    CategoriaEfectoTemporal getCategoria();
    void aplicar(Personaje personaje);
    void revertir(Personaje personaje);
}
