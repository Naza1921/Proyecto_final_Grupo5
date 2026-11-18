package controlador;

import java.util.concurrent.TimeUnit;
import modelos.items.CategoriaEfectoTemporal;
import modelos.items.EfectoTemporal;
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
        if (powerUp == null || powerUp.getEfecto() == null
                || powerUp.getDuracion() < 0
                || (powerUp.getDuracion() == 0 && !powerUp.getEfecto().esInstantaneo())
                || (powerUp.getDuracion() > 0 && powerUp.getEfecto().esInstantaneo())) {
            throw new IllegalArgumentException("El power-up debe tener efecto y duración válidos");
        }

        EfectoTemporal nuevoEfecto = powerUp.getEfecto();
        CategoriaEfectoTemporal categoria = nuevoEfecto.getCategoria();
        if (categoria == null) {
            throw new IllegalArgumentException("El efecto debe pertenecer a una categoría");
        }

        if (nuevoEfecto.esInstantaneo()) {
            nuevoEfecto.aplicar(personaje);
            return;
        }

        long ahora = System.nanoTime();
        long duracionNanos = TimeUnit.MILLISECONDS.toNanos(powerUp.getDuracion());

        if (efectoActivo == null
                || efectoActivo.efecto.getClass() != nuevoEfecto.getClass()) {
            if (efectoActivo != null) {
                efectoActivo.efecto.revertir(personaje);
            }
            nuevoEfecto.aplicar(personaje);
        }

        efectoActivo = new EfectoActivo(nuevoEfecto, ahora, duracionNanos);
    }

    public void actualizar() {
        if (efectoActivo == null) {
            return;
        }

        long ahora = System.nanoTime();
        if (ahora - efectoActivo.iniciadoEnNanos >= efectoActivo.duracionNanos) {
            efectoActivo.efecto.revertir(personaje);
            efectoActivo = null;
        }
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
            efectoActivo.efecto.revertir(personaje);
            efectoActivo = null;
        }
        pausaIniciadaEnNanos = 0;
        pausado = false;
    }

    public boolean estaActivo(CategoriaEfectoTemporal categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("Se requiere una categoría de efecto");
        }
        return efectoActivo != null && efectoActivo.efecto.getCategoria() == categoria;
    }

    private static final class EfectoActivo {
        private final EfectoTemporal efecto;
        private long iniciadoEnNanos;
        private final long duracionNanos;

        private EfectoActivo(EfectoTemporal efecto, long iniciadoEnNanos, long duracionNanos) {
            this.efecto = efecto;
            this.iniciadoEnNanos = iniciadoEnNanos;
            this.duracionNanos = duracionNanos;
        }
    }
}
