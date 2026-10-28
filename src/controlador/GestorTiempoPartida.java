package controlador;

final class GestorTiempoPartida {
    private static final long INTERVALO_PENALIZACION_SEGUNDOS = 30;
    private static final int PENALIZACION_PUNTOS = 10;

    private final ControladorPuntaje controladorPuntaje;
    private long inicioIntervaloNanos;
    private long tiempoAcumuladoNanos;
    private boolean corriendo;

    GestorTiempoPartida(ControladorPuntaje controladorPuntaje) {
        if (controladorPuntaje == null) {
            throw new IllegalArgumentException("Se requiere un controlador de puntaje");
        }
        this.controladorPuntaje = controladorPuntaje;
    }

    void iniciar() {
        if (!corriendo) {
            inicioIntervaloNanos = System.nanoTime();
            corriendo = true;
        }
    }

    void pausar() {
        if (corriendo) {
            tiempoAcumuladoNanos += System.nanoTime() - inicioIntervaloNanos;
            corriendo = false;
        }
    }

    void detener() {
        pausar();
    }

    long obtenerTiempoNanos() {
        if (!corriendo) {
            return tiempoAcumuladoNanos;
        }
        return tiempoAcumuladoNanos + System.nanoTime() - inicioIntervaloNanos;
    }

    long obtenerTiempoMilisegundos() {
        return obtenerTiempoNanos() / 1_000_000L;
    }

    long obtenerTiempoSegundos() {
        return obtenerTiempoNanos() / 1_000_000_000L;
    }

    int obtenerPuntajeActual() {
        long intervalosCompletos =
            obtenerTiempoSegundos() / INTERVALO_PENALIZACION_SEGUNDOS;
        long penalizacion = intervalosCompletos * PENALIZACION_PUNTOS;
        return (int) Math.max(0, controladorPuntaje.obtenerPuntaje() - penalizacion);
    }
}
