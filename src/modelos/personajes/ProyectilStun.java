package modelos.personajes;

import modelos.enemigos.enemigo;

public final class ProyectilStun implements ProyectilEspecial {
    private static final double VELOCIDAD = 10.0;
    private static final double ALCANCE_MAXIMO = 600.0;
    private static final double ANCHO = 16.0;
    private static final double ALTO = 16.0;
    private static final int DANIO = 1;
    private static final long DURACION_STUN_MS = 3_000;

    private final double direccion;
    private double x;
    private final double y;
    private double distanciaRestante = ALCANCE_MAXIMO;
    private boolean activo = true;

    public ProyectilStun(double x, double y, boolean mirandoIzquierda) {
        if (!Double.isFinite(x) || !Double.isFinite(y) || x < 0 || y < 0) {
            throw new IllegalArgumentException("La posición inicial del proyectil debe ser válida");
        }
        this.x = x - ANCHO / 2.0;
        this.y = y;
        direccion = mirandoIzquierda ? -1.0 : 1.0;
    }

    public void actualizar() {
        if (!activo) {
            return;
        }
        double avance = Math.min(VELOCIDAD, distanciaRestante);
        x += direccion * avance;
        distanciaRestante -= avance;
        if (distanciaRestante <= 0) {
            activo = false;
        }
    }

    public boolean impactarSiColisiona(enemigo objetivo) {
        if (objetivo == null) {
            throw new IllegalArgumentException("Se requiere un enemigo objetivo");
        }
        if (!activo || !objetivo.estavivo()) {
            return false;
        }
        boolean colisiona = x < objetivo.getX() + objetivo.getAnchoColision()
            && x + ANCHO > objetivo.getX()
            && y < objetivo.getY() + objetivo.getAltoColision()
            && y + ALTO > objetivo.getY();
        if (colisiona) {
            objetivo.recibirDanioYAturdir(DANIO, DURACION_STUN_MS);
            activo = false;
        }
        return colisiona;
    }

    public boolean estaActivo() {
        return activo;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getAncho() {
        return ANCHO;
    }

    @Override
    public double getAlto() {
        return ALTO;
    }
}
