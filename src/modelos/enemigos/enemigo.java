package modelos.enemigos;

import modelos.personajes.Personaje;
import java.util.concurrent.TimeUnit;

public abstract class enemigo {
    private static final long DURACION_RALENTIZACION_MS = 5000;
    private static final long DURACION_ATURDIMIENTO_MS = 2000;
    protected  String nombre;
    protected int vida;
    protected int vidaMaxima; 
    protected  double velocidadBase;
    protected  double velocidadActual;
    protected  double x; 
    protected  double y;
    protected  int puntaje;
    protected  boolean ralentizado; 
    protected  double porcentajerelantizado; 
    protected double factorRalentizacion;
    private long ralentizacionHastaNanos;
    private long aturdimientoHastaNanos;
    private long distraccionHastaNanos;
    private double distraccionX;
    private double distraccionY;

    public enemigo(String nombre, int vida, int vidaMaxima,double velocidad, int puntaje) {
        this.nombre = nombre;
        this.vida = vida; 
        this.vidaMaxima = vidaMaxima;
        this.puntaje = puntaje;
        this.velocidadBase = velocidad; //despues revisar por que velocidad 
        this.velocidadActual = velocidad; //despues revisar por que velocidad
        this.ralentizado = false;
        this.porcentajerelantizado = 1.0;
        this.factorRalentizacion = 1.0;

    }
    
    public enum TipoDanio {
    ACIDO,
    STUN,
    NORMAL,
    SAL,
    FUEGO
    }


    public void recibirDanio(int danio, TipoDanio tipo) {
        if (danio < 0 || tipo == null) {
            throw new IllegalArgumentException("El daño y su tipo deben ser válidos");
        }
        if (!estavivo()) return;
        vida = Math.max(0, vida - danio);
        if (!estavivo()) return;

        switch (tipo) {
            case ACIDO -> aplicarRelantizacion(0.5, DURACION_RALENTIZACION_MS);
            case STUN -> aturdir(DURACION_ATURDIMIENTO_MS);
            case NORMAL -> aplicarRelantizacion(0.7, 1500);
            case SAL, FUEGO -> { }
        }
    }

    public void recibirDanioYAturdir(int danio, long duracionMs) {
        if (danio < 0 || duracionMs <= 0) {
            throw new IllegalArgumentException("El daño y la duración del aturdimiento deben ser válidos");
        }
        if (!estavivo()) {
            return;
        }
        vida = Math.max(0, vida - danio);
        if (estavivo()) {
            aturdir(duracionMs);
        }
    }

    public void aplicarRelantizacion(double factor) {
        aplicarRelantizacion(factor, DURACION_RALENTIZACION_MS);
    }

    public void aplicarRelantizacion(double factor, long duracionMs) {
        if (!Double.isFinite(factor) || factor < 0 || factor > 1 || duracionMs <= 0) {
            throw new IllegalArgumentException("La ralentización requiere un factor y duración válidos");
        }
        actualizarEstados();
        ralentizado = true;
        factorRalentizacion = Math.min(factorRalentizacion, factor);
        ralentizacionHastaNanos = Math.max(
            ralentizacionHastaNanos, System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(duracionMs)
        );
        actualizarVelocidad();
    }

    public void aturdir(long duracionMs) {
        if (duracionMs <= 0) {
            throw new IllegalArgumentException("La duración del aturdimiento debe ser positiva");
        }
        aturdimientoHastaNanos = Math.max(
            aturdimientoHastaNanos, System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(duracionMs)
        );
        actualizarVelocidad();
    }

    private void actualizarEstados() {
        long ahora = System.nanoTime();
        if (ralentizacionHastaNanos != 0 && ahora >= ralentizacionHastaNanos) {
            ralentizado = false;
            factorRalentizacion = 1.0;
            ralentizacionHastaNanos = 0;
        }
        if (aturdimientoHastaNanos != 0 && ahora >= aturdimientoHastaNanos) {
            aturdimientoHastaNanos = 0;
        }
        if (distraccionHastaNanos != 0 && ahora >= distraccionHastaNanos) {
            distraccionHastaNanos = 0;
        }
        actualizarVelocidad();
    }

    private void actualizarVelocidad() {
        velocidadActual = aturdimientoHastaNanos > System.nanoTime()
            ? 0 : velocidadBase * factorRalentizacion;
    }

    public boolean estavivo() {
        return vida > 0 ;
    }

    public boolean estaAturdido() {
        actualizarEstados();
        return aturdimientoHastaNanos != 0;
    }

    public boolean estaDistraido() {
        actualizarEstados();
        return distraccionHastaNanos != 0;
    }

    public double getDistraccionX() {
        actualizarEstados();
        return distraccionX;
    }

    public double getDistraccionY() {
        actualizarEstados();
        return distraccionY;
    }
    // Abstraccion del tipo de danio que genera el enemigo 
     public abstract void atacar(Personaje objetivo);

    
    //getter y Setters 
    public String getNombre() {return nombre;}
    public int getVida() {
        return vida;
    }
    public int getVidaMaxima(){return vidaMaxima;}
    public double getVelocidadActual () {
        actualizarEstados();
        return velocidadActual;
    }
    public double getX () { return x;}
    public double getY() {return y;}
    public double getAnchoColision() { return 25.0; }
    public double getAltoColision() { return 20.0; }
    //setter

    public void setPosicion(double x, double y) {
        this.x =x;
        this.y= y; 
    }

    public void reiniciar(double x, double y) {
        if (!Double.isFinite(x) || x < 0 || !Double.isFinite(y) || y < 0) {
            throw new IllegalArgumentException("La posición del enemigo debe ser válida");
        }
        vida = vidaMaxima;
        setPosicion(x, y);
        ralentizado = false;
        porcentajerelantizado = 1.0;
        factorRalentizacion = 1.0;
        ralentizacionHastaNanos = 0;
        aturdimientoHastaNanos = 0;
        distraccionHastaNanos = 0;
        actualizarVelocidad();
    }
    

}