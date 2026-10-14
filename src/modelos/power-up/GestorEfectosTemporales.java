package controlador;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import modelos.items.CategoriaEfectoTemporal;
import modelos.items.EfectoTemporal;
import modelos.items.PowerUp;
import modelos.personajes.Personaje;

public final class GestorEfectosTemporales {
    private final Personaje personaje;
    private final Map<CategoriaEfectoTemporal, EfectoActivo> efectosActivos =
        new EnumMap<>(CategoriaEfectoTemporal.class);

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
        if (powerUp == null || powerUp.getEfecto() == null || powerUp.getDuracion() <= 0) {
            throw new IllegalArgumentException("El power-up debe tener efecto y duración válidos");
        }

        EfectoTemporal nuevoEfecto = powerUp.getEfecto();
        CategoriaEfectoTemporal categoria = nuevoEfecto.getCategoria();
        if (categoria == null) {
            throw new IllegalArgumentException("El efecto debe pertenecer a una categoría");
        }

        long ahora = System.nanoTime();
        long duracionNanos = TimeUnit.MILLISECONDS.toNanos(powerUp.getDuracion());
        EfectoActivo anterior = efectosActivos.get(categoria);

        if (anterior != null && anterior.efecto.getClass() != nuevoEfecto.getClass()) {
            anterior.efecto.revertir(personaje);
            efectosActivos.remove(categoria);
            anterior = null;
        }

        if (anterior == null) {
            nuevoEfecto.aplicar(personaje);
        }
        efectosActivos.put(categoria, new EfectoActivo(nuevoEfecto, ahora, duracionNanos));
    }

    public void actualizar() {
        long ahora = System.nanoTime();
        Iterator<Map.Entry<CategoriaEfectoTemporal, EfectoActivo>> iterador =
            efectosActivos.entrySet().iterator();
        while (iterador.hasNext()) {
            EfectoActivo activo = iterador.next().getValue();
            if (ahora - activo.iniciadoEnNanos >= activo.duracionNanos) {
                activo.efecto.revertir(personaje);
                iterador.remove();
            }
        }
    }

    public void limpiar() {
        for (EfectoActivo activo : efectosActivos.values()) {
            activo.efecto.revertir(personaje);
        }
        efectosActivos.clear();
    }

    public boolean estaActivo(CategoriaEfectoTemporal categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("Se requiere una categoría de efecto");
        }
        return efectosActivos.containsKey(categoria);
    }

    private static final class EfectoActivo {
        private final EfectoTemporal efecto;
        private final long iniciadoEnNanos;
        private final long duracionNanos;

        private EfectoActivo(EfectoTemporal efecto, long iniciadoEnNanos, long duracionNanos) {
            this.efecto = efecto;
            this.iniciadoEnNanos = iniciadoEnNanos;
            this.duracionNanos = duracionNanos;
        }
    }
}
