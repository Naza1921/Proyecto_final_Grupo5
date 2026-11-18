package controlador;

import java.util.concurrent.TimeUnit;
import modelos.items.PowerUp;
import modelos.personajes.Personaje;

public final class GestorEfectosTemporales {
    private final Personaje personaje;
    private EfectoActivo efectoActivo;
    private long pausaIniciadaEnNanos;
    private boolean pausado;

    public GestorEfectosTemporales(Personaje personaje) {
        if (personaje == null) {
            throw new IllegalArgumentException("Se requiere un personaje para gestionar sus efectos");
        }
        this.personaje = personaje;
    }

    public boolean recogerSiColisiona(PowerUp powerUp) {
        if (powerUp == null) {
            throw new IllegalArgumentException("Se requiere un power-up");
        }
        if (!powerUp.colisionaCon(personaje)) {
            return false;
        }

        activar(powerUp);
        return true;
    }

    public void activar(PowerUp powerUp) {
        if (powerUp == null || powerUp.getRecompensa() == null
                || powerUp.getDuracion() < 0
                || (powerUp.getDuracion() == 0 && !powerUp.esInstantaneo())
                || (powerUp.getDuracion() > 0 && powerUp.esInstantaneo())) {
            throw new IllegalArgumentException("El power-up debe tener recompensa y duración válidas");
        }

        if (powerUp.esInstantaneo()) {
            powerUp.aplicarEfecto(personaje);
            return;
        }

        long ahora = System.nanoTime();
        long duracionNanos = TimeUnit.MILLISECONDS.toNanos(powerUp.getDuracion());

        if (efectoActivo != null
                && efectoActivo.powerUp.getClass() == powerUp.getClass()) {
            long transcurrido =
                Math.max(0, ahora - efectoActivo.iniciadoEnNanos);
            long restante =
                Math.max(0, efectoActivo.duracionNanos - transcurrido);
            efectoActivo.iniciadoEnNanos = ahora;
            efectoActivo.duracionNanos =
                sumarSaturando(restante, duracionNanos);
            return;
        }

        if (efectoActivo != null) {
            efectoActivo.powerUp.revertirEfecto(personaje);
        }
        powerUp.aplicarEfecto(personaje);
        efectoActivo = new EfectoActivo(powerUp, ahora, duracionNanos);
    }

    public void actualizar() {
        if (pausado) {
            return;
        }
        long ahora = System.nanoTime();
        if (efectoActivo != null
                && ahora - efectoActivo.iniciadoEnNanos
                    >= efectoActivo.duracionNanos) {
            efectoActivo.powerUp.revertirEfecto(personaje);
            efectoActivo = null;
        }
    }

    private static long sumarSaturando(long primerValor, long segundoValor) {
        return Long.MAX_VALUE - primerValor < segundoValor
            ? Long.MAX_VALUE
            : primerValor + segundoValor;
    }

    public void pausar() {
        if (!pausado) {
            pausaIniciadaEnNanos = System.nanoTime();
            pausado = true;
        }
    }

    public void reanudar() {
        if (!pausado) {
            return;
        }

        long duracionPausaNanos = System.nanoTime() - pausaIniciadaEnNanos;
        if (efectoActivo != null) {
            efectoActivo.iniciadoEnNanos += duracionPausaNanos;
        }
        pausaIniciadaEnNanos = 0;
        pausado = false;
    }

    public void limpiar() {
        if (efectoActivo != null) {
            efectoActivo.powerUp.revertirEfecto(personaje);
            efectoActivo = null;
        }
        pausaIniciadaEnNanos = 0;
        pausado = false;
    }

    public boolean estaActivo() {
        return efectoActivo != null;
    }

    private static final class EfectoActivo {
        private final PowerUp powerUp;
        private long iniciadoEnNanos;
        private long duracionNanos;

        private EfectoActivo(PowerUp powerUp, long iniciadoEnNanos, long duracionNanos) {
            this.powerUp = powerUp;
            this.iniciadoEnNanos = iniciadoEnNanos;
            this.duracionNanos = duracionNanos;
        }
    }
}
