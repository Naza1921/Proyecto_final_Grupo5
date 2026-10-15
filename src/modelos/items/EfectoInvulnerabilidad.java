package modelos.items;

import modelos.personajes.Personaje;

public final class EfectoInvulnerabilidad implements EfectoTemporal {
    @Override
    public CategoriaEfectoTemporal getCategoria() {
        return CategoriaEfectoTemporal.DEFENSA;
    }

    @Override
    public void aplicar(Personaje personaje) {
        personaje.setInvulnerable(true);
    }

    @Override
    public void revertir(Personaje personaje) {
        personaje.setInvulnerable(false);
    }
}
