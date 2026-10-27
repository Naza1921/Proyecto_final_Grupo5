package modelos.personajes;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import modelos.enemigos.enemigo;

public final class CharcoAcido {
    public static final double ANCHO = 70;
    public static final double ALTO = 12;

    private static final long DEMORA_APARICION_NANOS = 1_500_000_000L;
    private static final int DANIO = 1;
    private static final long INTERVALO_DANIO_NANOS = 4_000_000_000L;
    private static final long DURACION_NANOS = 20_000_000_000L;

    private final double x;
    private final double y;
    private final long apareceEnNanos = System.nanoTime() + DEMORA_APARICION_NANOS;
    private long expiraEnNanos;
    private final Map<enemigo, Long> proximoDanioPorEnemigo = new IdentityHashMap<>();

    public CharcoAcido(double x, double y) {
        if (!Double.isFinite(x) || !Double.isFinite(y) || x < 0 || y < 0) {
            throw new IllegalArgumentException("La posición del charco debe ser válida");
        }
        this.x = x;
        this.y = y;
    }

    public boolean actualizar(List<enemigo> enemigos) {
        if (enemigos == null || enemigos.stream().anyMatch(enemigo -> enemigo == null)) {
            throw new IllegalArgumentException("La lista de enemigos debe ser válida");
        }
        if (System.nanoTime() < apareceEnNanos) {
            return true;
        }
        if (expiraEnNanos == 0) {
            expiraEnNanos = apareceEnNanos + DURACION_NANOS;
        }
        if (!estaActivo()) {
            return false;
        }

        long ahora = System.nanoTime();
        for (enemigo objetivo : enemigos) {
            if (!objetivo.estavivo() || !colisionaCon(objetivo)) {
                continue;
            }
            long proximoDanio = proximoDanioPorEnemigo.getOrDefault(objetivo, 0L);
            if (ahora >= proximoDanio) {
                objetivo.recibirDanio(DANIO, enemigo.TipoDanio.ACIDO);
                proximoDanioPorEnemigo.put(objetivo, ahora + INTERVALO_DANIO_NANOS);
            }
        }
        proximoDanioPorEnemigo.keySet().removeIf(objetivo -> !objetivo.estavivo());
        return true;
    }

    public boolean estaActivo() {
        return expiraEnNanos == 0 || System.nanoTime() < expiraEnNanos;
    }

    public boolean estaVisible() {
        return System.nanoTime() >= apareceEnNanos && estaActivo();
    }

    private boolean colisionaCon(enemigo objetivo) {
        return objetivo.getX() < x + ANCHO
            && objetivo.getX() + objetivo.getAnchoColision() > x
            && objetivo.getY() < y + ALTO
            && objetivo.getY() + objetivo.getAltoColision() > y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
