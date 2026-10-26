package modelos.enemigos;

import modelos.partida.ConfiguracionPartida;
import modelos.personajes.Personaje;

public final class ProyectilAcido {
    private static final double VELOCIDAD = 6.0;
    private static final double ALCANCE_MAXIMO = 200.0;
    private static final double ANCHO = 12.0;
    private static final double ALTO = 12.0;
    private static final int DANIO = 20;

    private final double direccionX;
    private final double direccionY;
    private double x;
    private double y;
    private double distanciaRestante = ALCANCE_MAXIMO;
    private boolean activo = true;

    public ProyectilAcido(double xInicial, double yInicial,
                          double objetivoX, double objetivoY) {
        if (!Double.isFinite(xInicial) || !Double.isFinite(yInicial)
                || !Double.isFinite(objetivoX) || !Double.isFinite(objetivoY)) {
            throw new IllegalArgumentException("Las posiciones del proyectil deben ser finitas");
        }

        double diferenciaX = objetivoX - xInicial;
        double diferenciaY = objetivoY - yInicial;
        double distancia = Math.hypot(diferenciaX, diferenciaY);
        if (distancia == 0) {
            throw new IllegalArgumentException("El proyectil requiere una dirección de movimiento");
        }

        x = xInicial;
        y = yInicial;
        direccionX = diferenciaX / distancia;
        direccionY = diferenciaY / distancia;
    }

    public void actualizar() {
        if (!activo) {
            return;
        }
        if (distanciaRestante <= 0) {
            activo = false;
            return;
        }

        double avance = Math.min(VELOCIDAD, distanciaRestante);
        x += direccionX * avance;
        y += direccionY * avance;
        distanciaRestante -= avance;
    }

    public boolean impactarSiColisiona(Personaje objetivo) {
        if (objetivo == null) {
            throw new IllegalArgumentException("Se requiere un personaje objetivo");
        }
        if (!activo || !objetivo.estaViva()) {
            return false;
        }

        boolean colisiona = x < objetivo.getX() + ConfiguracionPartida.ANCHO_JUGADOR
            && x + ANCHO > objetivo.getX()
            && y < objetivo.getY() + ConfiguracionPartida.ALTO_JUGADOR
            && y + ALTO > objetivo.getY();
        if (colisiona) {
            objetivo.recibirDanio(DANIO);
            activo = false;
        }
        return colisiona;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getAncho() {
        return ANCHO;
    }

    public double getAlto() {
        return ALTO;
    }

    public boolean estaActivo() {
        return activo;
    }
}
