package modelos.items;

import modelos.personajes.Personaje;

public final class EfectoCuracion implements EfectoTemporal {
    private static final int CANTIDAD_CURACION = 20;
    private static final RecompensaPowerUp RECOMPENSA = new RecompensaPowerUp(1_000, false);

    @Override
    public CategoriaEfectoTemporal getCategoria() {
        return CategoriaEfectoTemporal.SALUD;
    }

    @Override
    public RecompensaPowerUp getRecompensa() {
        return RECOMPENSA;
    }

    @Override
    public void aplicar(Personaje personaje) {
        if (personaje == null) {
            throw new IllegalArgumentException("Se requiere un personaje para recuperar vida");
        }
        personaje.recuperarVida(CANTIDAD_CURACION);
    }

    @Override
    public void revertir(Personaje personaje) {
        if (personaje == null) {
            throw new IllegalArgumentException("Se requiere un personaje para revertir el efecto");
        }
    }

    @Override
    public boolean esInstantaneo() {
        return true;
    }
}
