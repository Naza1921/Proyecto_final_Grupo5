package modelos.items;

import modelos.personajes.Personaje;

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
        personaje.setInvulnerable(true);
    }

    @Override
    public void revertir(Personaje personaje) {
        personaje.setInvulnerable(false);
    }
}
