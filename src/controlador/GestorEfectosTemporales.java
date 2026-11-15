package controlador;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import modelos.items.CategoriaEfectoTemporal;
import modelos.items.PowerUp;
import modelos.personajes.Personaje;

public final class GestorEfectosTemporales {
    private final Personaje personaje;
    private final Map<CategoriaEfectoTemporal, EfectoActivo> efectosActivos =
        new EnumMap<>(CategoriaEfectoTemporal.class);
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
        if (powerUp == null || powerUp.getCategoria() == null || powerUp.getRecompensa() == null
                || powerUp.getDuracion() < 0
                || (powerUp.getDuracion() == 0 && !powerUp.esInstantaneo())
                || (powerUp.getDuracion() > 0 && powerUp.esInstantaneo())) {
            throw new IllegalArgumentException("El power-up debe tener categoría y duración válidas");
        }

        if (powerUp.esInstantaneo()) {
            powerUp.aplicarEfecto(personaje);
            return;
        }

        long ahora = System.nanoTime();
        long duracionNanos = TimeUnit.MILLISECONDS.toNanos(powerUp.getDuracion());
        CategoriaEfectoTemporal categoria = powerUp.getCategoria();
        EfectoActivo anterior = efectosActivos.get(categoria);

        if (anterior != null && anterior.powerUp.getClass() == powerUp.getClass()) {
            long transcurrido = Math.max(0, ahora - anterior.iniciadoEnNanos);
            long restante = Math.max(0, anterior.duracionNanos - transcurrido);
            anterior.iniciadoEnNanos = ahora;
            anterior.duracionNanos = sumarSaturando(restante, duracionNanos);
            return;
        }

        if (anterior != null) {
            anterior.powerUp.revertirEfecto(personaje);
            efectosActivos.remove(categoria);
        }
        powerUp.aplicarEfecto(personaje);
        efectosActivos.put(categoria, new EfectoActivo(powerUp, ahora, duracionNanos));
    }

    public void actualizar() {
        if (pausado) {
            return;
        }
        long ahora = System.nanoTime();
        Iterator<Map.Entry<CategoriaEfectoTemporal, EfectoActivo>> iterador =
            efectosActivos.entrySet().iterator();
        while (iterador.hasNext()) {
            EfectoActivo activo = iterador.next().getValue();
            if (ahora - activo.iniciadoEnNanos >= activo.duracionNanos) {
                activo.powerUp.revertirEfecto(personaje);
                iterador.remove();
            }
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
        for (EfectoActivo activo : efectosActivos.values()) {
            activo.iniciadoEnNanos += duracionPausaNanos;
        }
        pausaIniciadaEnNanos = 0;
        pausado = false;
    }

    public void limpiar() {
        for (EfectoActivo activo : efectosActivos.values()) {
            activo.powerUp.revertirEfecto(personaje);
        }
        efectosActivos.clear();
        pausaIniciadaEnNanos = 0;
        pausado = false;
    }

    public boolean estaActivo(CategoriaEfectoTemporal categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("Se requiere una categoría de efecto");
        }
        return efectosActivos.containsKey(categoria);
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
