package modelos.personajes;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import modelos.enemigos.enemigo;

public final class LamparaFuego {
    public enum Estado {
        PREPARANDO, VOLANDO, FUEGO, TERMINADA
    }

    public static final double ANCHO_FUEGO = 60;
    public static final double ALTO_FUEGO = 36;

    private static final double DISTANCIA_VUELO = 150;
    private static final double ALTURA_ARCO = 65;
    private static final long DEMORA_LANZAMIENTO_NANOS = 1_500_000_000L;
    private static final long DURACION_VUELO_NANOS = 2_000_000_000L;
    private static final long DURACION_FUEGO_NANOS = 10_000_000_000L;
    private static final int DANIO_FUEGO = 2;

    private final double xInicial;
    private final double ySuelo;
    private final double direccion;
    private final long creadoEnNanos = System.nanoTime();
    private final Set<enemigo> enemigosEnFuego =
        Collections.newSetFromMap(new IdentityHashMap<>());
    private Estado estado = Estado.PREPARANDO;
    private double progresoVuelo;
    private double xFuego;
    private long inicioFuegoNanos;

    public LamparaFuego(double xInicial, double ySuelo, boolean mirandoIzquierda) {
        if (!Double.isFinite(xInicial) || xInicial < 0
                || !Double.isFinite(ySuelo) || ySuelo < 0) {
            throw new IllegalArgumentException("La posición de la lámpara debe ser válida");
        }
        this.xInicial = xInicial;
        this.ySuelo = ySuelo;
        this.direccion = mirandoIzquierda ? -1 : 1;
    }

    public boolean actualizar(List<enemigo> enemigos) {
        if (enemigos == null || enemigos.stream().anyMatch(enemigo -> enemigo == null)) {
            throw new IllegalArgumentException("La lista de enemigos debe ser válida");
        }

        long ahora = System.nanoTime();
        long transcurrido = ahora - creadoEnNanos;
        if (transcurrido < DEMORA_LANZAMIENTO_NANOS) {
            estado = Estado.PREPARANDO;
            return true;
        }
        if (transcurrido < DEMORA_LANZAMIENTO_NANOS + DURACION_VUELO_NANOS) {
            estado = Estado.VOLANDO;
            progresoVuelo = (double) (transcurrido - DEMORA_LANZAMIENTO_NANOS)
                / DURACION_VUELO_NANOS;
            return true;
        }

        if (estado != Estado.FUEGO) {
            estado = Estado.FUEGO;
            xFuego = Math.max(0, xInicial + direccion * DISTANCIA_VUELO - ANCHO_FUEGO / 2);
            inicioFuegoNanos = creadoEnNanos
                + DEMORA_LANZAMIENTO_NANOS + DURACION_VUELO_NANOS;
        }
        if (ahora - inicioFuegoNanos >= DURACION_FUEGO_NANOS) {
            estado = Estado.TERMINADA;
            enemigosEnFuego.clear();
            return false;
        }

        Set<enemigo> enemigosActuales =
            Collections.newSetFromMap(new IdentityHashMap<>());
        for (enemigo objetivo : enemigos) {
            if (objetivo.estavivo() && colisionaConFuego(objetivo)) {
                enemigosActuales.add(objetivo);
                if (!enemigosEnFuego.contains(objetivo)) {
                    objetivo.recibirDanio(DANIO_FUEGO, enemigo.TipoDanio.FUEGO);
                }
            }
        }
        enemigosEnFuego.clear();
        enemigosEnFuego.addAll(enemigosActuales);
        return true;
    }

    private boolean colisionaConFuego(enemigo objetivo) {
        return objetivo.getX() < xFuego + ANCHO_FUEGO
            && objetivo.getX() + objetivo.getAnchoColision() > xFuego
            && objetivo.getY() < ySuelo
            && objetivo.getY() + objetivo.getAltoColision() > ySuelo - ALTO_FUEGO;
    }

    public Estado getEstado() {
        return estado;
    }

    public double getXLampara() {
        return xInicial + direccion * DISTANCIA_VUELO * progresoVuelo;
    }

    public double getYLampara() {
        return ySuelo - 14 - ALTURA_ARCO * Math.sin(Math.PI * progresoVuelo);
    }

    public double getXFuego() {
        return xFuego;
    }

    public double getYFuego() {
        return ySuelo - ALTO_FUEGO;
    }
}
