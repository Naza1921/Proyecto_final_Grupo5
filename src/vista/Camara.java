package vista;

import java.awt.geom.AffineTransform;

public final class Camara {
    private static final long DELTA_MAXIMO_NANOS = 50_000_000L;

    private final double zoom;
    private final double duracionSuavizadoNanos;
    private double x;
    private double y;
    private long ultimaActualizacionNanos;
    private boolean inicializada;

    public Camara(double zoom, double duracionSuavizadoSegundos) {
        if (!Double.isFinite(zoom) || zoom <= 0
                || !Double.isFinite(duracionSuavizadoSegundos)
                || duracionSuavizadoSegundos <= 0) {
            throw new IllegalArgumentException("El zoom y el suavizado deben ser positivos y finitos");
        }
        this.zoom = zoom;
        duracionSuavizadoNanos = duracionSuavizadoSegundos * 1_000_000_000L;
    }

    public AffineTransform actualizar(double focoX, double focoY,
                                      double anchoVista, double altoVista,
                                      double anchoMundo, double altoMundo) {
        if (!Double.isFinite(focoX) || !Double.isFinite(focoY)
                || !Double.isFinite(anchoVista) || anchoVista < 0
                || !Double.isFinite(altoVista) || altoVista < 0
                || !Double.isFinite(anchoMundo) || anchoMundo <= 0
                || !Double.isFinite(altoMundo) || altoMundo <= 0) {
            throw new IllegalArgumentException("Las dimensiones y el foco de la cámara deben ser válidos");
        }

        double anchoVisibleMundo = anchoVista / zoom;
        double altoVisibleMundo = altoVista / zoom;
        double objetivoX = limitar(
            focoX - anchoVisibleMundo / 2,
            Math.max(0, anchoMundo - anchoVisibleMundo)
        );
        double objetivoY = limitar(
            focoY - altoVisibleMundo / 2,
            Math.max(0, altoMundo - altoVisibleMundo)
        );

        long ahora = System.nanoTime();
        if (!inicializada) {
            x = objetivoX;
            y = objetivoY;
            inicializada = true;
        } else {
            long deltaNanos = Math.max(
                0,
                Math.min(ahora - ultimaActualizacionNanos, DELTA_MAXIMO_NANOS)
            );
            double avance = 1 - Math.exp(-deltaNanos / duracionSuavizadoNanos);
            x += (objetivoX - x) * avance;
            y += (objetivoY - y) * avance;
            x = limitar(x, Math.max(0, anchoMundo - anchoVisibleMundo));
            y = limitar(y, Math.max(0, altoMundo - altoVisibleMundo));
        }
        ultimaActualizacionNanos = ahora;

        AffineTransform transformacion = new AffineTransform();
        transformacion.scale(zoom, zoom);
        transformacion.translate(-x, -y);
        return transformacion;
    }

    private double limitar(double posicion, double maximo) {
        return Math.max(0, Math.min(posicion, maximo));
    }
}
