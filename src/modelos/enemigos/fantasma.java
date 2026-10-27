package modelos.enemigos;

import modelos.personajes.Personaje;

public class fantasma extends enemigo {
    private static final int VIDA_FANTASMA = 1; 
    private static final int VIDA_FANTASMA_MAXIMA = 1;
    private static final int DANIO_CONTACTO = 10;

    private double xMinimo;
    private double xMaximo;
    private final double ancho;
    private final double alto;
    private boolean moviendoseDerecha = true;
    private boolean enContacto;

    public fantasma(String nombre, double velocidad, int puntaje, double xMinimo,
                    double xMaximo, double ancho, double alto) {
        super(nombre, VIDA_FANTASMA, VIDA_FANTASMA_MAXIMA, velocidad, puntaje);
        if (nombre == null || nombre.isBlank() || !Double.isFinite(velocidad)
                || velocidad <= 0 || !Double.isFinite(xMinimo) || xMinimo < 0
                || !Double.isFinite(xMaximo) || xMaximo <= xMinimo
                || !Double.isFinite(ancho) || ancho <= 0
                || !Double.isFinite(alto) || alto <= 0) {
            throw new IllegalArgumentException("El fantasma requiere dimensiones y patrulla válidas");
        }
        this.ancho = ancho;
        this.alto = alto;
        reiniciarPatrulla(xMinimo, xMaximo, 0);
    }

    public void reiniciarPatrulla(double xMinimo, double xMaximo, double posicionY) {
        if (!Double.isFinite(xMinimo) || xMinimo < 0
                || !Double.isFinite(xMaximo) || xMaximo <= xMinimo
                || !Double.isFinite(posicionY) || posicionY < 0) {
            throw new IllegalArgumentException("La patrulla del fantasma debe ser válida");
        }
        this.xMinimo = xMinimo;
        this.xMaximo = xMaximo;
        reiniciar(xMinimo, posicionY);
        moviendoseDerecha = true;
        enContacto = false;
    }

    public void actualizarPatrulla(double posicionY) {
        if (!estavivo()) {
            return;
        }
        if (!Double.isFinite(posicionY) || posicionY < 0) {
            throw new IllegalArgumentException("La posición vertical del fantasma debe ser válida");
        }

        y = posicionY;
        double velocidad = getVelocidadActual();
        double siguienteX = x + (moviendoseDerecha ? velocidad : -velocidad);
        if (siguienteX >= xMaximo) {
            x = xMaximo;
            moviendoseDerecha = false;
        } else if (siguienteX <= xMinimo) {
            x = xMinimo;
            moviendoseDerecha = true;
        } else {
            x = siguienteX;
        }
    }

    @Override
    public void atacar(Personaje objetivo) {
        atacar(objetivo, ancho, alto);
    }

    public void atacar(Personaje objetivo, double anchoObjetivo, double altoObjetivo) {
        if (objetivo == null || !Double.isFinite(anchoObjetivo) || anchoObjetivo <= 0
                || !Double.isFinite(altoObjetivo) || altoObjetivo <= 0) {
            throw new IllegalArgumentException("El objetivo y su hitbox deben ser válidos");
        }

        boolean contactoActual = estavivo() && objetivo.estaViva()
            && objetivo.getX() < x + ancho
            && objetivo.getX() + anchoObjetivo > x
            && objetivo.getY() < y + alto
            && objetivo.getY() + altoObjetivo > y;

        if (contactoActual && !enContacto) {
            objetivo.recibirDanio(DANIO_CONTACTO);
        }
        enContacto = contactoActual;
    }

    public double getAncho() {
        return ancho;
    }

    public double getAlto() {
        return alto;
    }

    @Override
    public double getAnchoColision() {
        return ancho;
    }

    @Override
    public double getAltoColision() {
        return alto;
    }

    public boolean estaMoviendoseDerecha() {
        return moviendoseDerecha;
    }
}