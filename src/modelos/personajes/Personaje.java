package modelos.personajes;

import modelos.enemigos.enemigo;
import java.util.List;

public abstract class Personaje {
   
    private final String nombre;
    private final TipoRata tipoRata;
    private int vida;
    private final int vidaMaxima;
    private double x;
    private double y;
    private double velocidad;
    private final double velocidadBase;
    private double multiplicadorVelocidadTemporal = 1.0;
    private final double fuerzaSalto;
    private boolean enElAire;
    private boolean mirandoIzquierda;
    private boolean agachada;
    private boolean invulnerable;
    private double estamina;
    private final double estaminaMaxima;
    private double velocidadY = 0;
    private static final double GRAVEDAD = 0.4;
    private static final double VELOCIDAD_MAXIMA_CAIDA = 15;
    
    public enum TipoRata {
        BLANCA, GRIS, NEGRA, VERDE
    }

    protected Personaje(String nombre, TipoRata tipoRata, int vidaMaxima, double velocidad,
                        double fuerzaSalto, double estaminaMaxima) {
        if (nombre == null || nombre.isBlank() || tipoRata == null
                || vidaMaxima <= 0 || !Double.isFinite(velocidad) || velocidad <= 0
                || !Double.isFinite(fuerzaSalto) || fuerzaSalto <= 0
                || !Double.isFinite(estaminaMaxima) || estaminaMaxima <= 0) {
            throw new IllegalArgumentException("El personaje debe tener identidad y estadísticas válidas");
        }
        this.nombre = nombre;
        this.tipoRata = tipoRata;
        this.vidaMaxima = vidaMaxima;
        this.vida = vidaMaxima;
        this.x = 0;
        this.y = 0;
        this.velocidad = velocidad;
        this.velocidadBase = velocidad;
        this.fuerzaSalto = fuerzaSalto;
        this.enElAire = false;
        this.agachada = false;
        this.estaminaMaxima = estaminaMaxima;
        this.estamina = estaminaMaxima;
    }

    public void moverIzquierda() { x -= getVelocidad(); mirandoIzquierda = true; }
    public void moverDerecha() { x += getVelocidad(); mirandoIzquierda = false; }

    public void limitarPosicionHorizontal(double xMinimo, double xMaximo) {
        if (!Double.isFinite(xMinimo) || !Double.isFinite(xMaximo) || xMinimo > xMaximo) {
            throw new IllegalArgumentException("Los límites horizontales deben ser finitos y válidos");
        }
        x = Math.max(xMinimo, Math.min(x, xMaximo));
    }

    public boolean saltar() {
        if (!enElAire) {
            enElAire = true;
            velocidadY = -fuerzaSalto;
            return true;
        }
        return false;
    }
    public void agacharse() { agachada = true; }
    public void levantarse() { agachada = false; }

    public void recargarEstamina() {
        if (estamina < estaminaMaxima) {

            setEstamina(estamina + 1);
        }
    }

    public void actualizar() {
        if (!enElAire) {
            return;
        }
        velocidadY = Math.min(velocidadY + GRAVEDAD, VELOCIDAD_MAXIMA_CAIDA);
        y += velocidadY;
    }

    public void recibirDanio(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("El daño no puede ser negativo");
        }
        if (invulnerable) {
            return;
        }
        vida = Math.max(0, vida - cantidad);
    }

    public void recuperarVida(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La recuperación de vida no puede ser negativa");
        }
        vida = (int) Math.min(vidaMaxima, (long) vida + cantidad);
    }

    public void aterrizar(double yPiso) {
        this.y = yPiso;
        this.velocidadY = 0.0;
        this.enElAire = false;
    }

    public boolean estaViva() { return vida > 0; }
    public int getVida() { return vida; }
    public int getVidaMaxima() { return vidaMaxima; }
    public double getX() { return x; }
    public double getY() { return y; }
    public String getNombre() { return nombre; }
    public TipoRata getTipoRata() { return tipoRata; }
    public boolean estaEnElAire() { return enElAire; }
    public boolean estaAgachada() { return agachada; }
    public boolean estaMirandoIzquierda() { return mirandoIzquierda; }
    public double getEstamina() { return estamina; }
    public double getEstaminaMaxima() { return estaminaMaxima; }
    public double getVelocidad() { return velocidad * multiplicadorVelocidadTemporal; }
    public double getFuerzaSalto() { return fuerzaSalto; }
    public double getVelocidadY(){return velocidadY;}
    public boolean esInvulnerable() { return invulnerable; }

    public void setMultiplicadorVelocidadTemporal(double multiplicador) {
        if (!Double.isFinite(multiplicador) || multiplicador <= 0) {
            throw new IllegalArgumentException("El multiplicador de velocidad debe ser finito y positivo");
        }
        this.multiplicadorVelocidadTemporal = multiplicador;
    }
    public void setInvulnerable(boolean invulnerable) {
        this.invulnerable = invulnerable;
    }
    public void setEstamina(double estamina) {
        if (!Double.isFinite(estamina)) {
            throw new IllegalArgumentException("La estamina debe ser un número finito");
        }
        this.estamina = Math.max(0, Math.min(estamina, estaminaMaxima));
    }

    public void detenerMovimiento() { velocidad = 0; }

    public abstract boolean usarHabilidad(enemigo objetivo, double xDestino, double yDestino);

    public abstract void actualizarHabilidad(List<enemigo> objetivos);

    public abstract void prepararCambioDeSeccion();

    protected final void validarObjetivosHabilidad(List<enemigo> objetivos) {
        if (objetivos == null || objetivos.stream().anyMatch(objetivo -> objetivo == null)) {
            throw new IllegalArgumentException("Los objetivos de la habilidad deben ser válidos");
        }
    }

    public List<ProyectilEspecial> getProyectilesEspeciales() {
        return List.of();
    }

    protected final boolean objetivoEnRango(enemigo objetivo, double rango) {
        return objetivo != null && objetivo.estavivo()
            && Math.hypot(objetivo.getX() - x, objetivo.getY() - y) <= rango;
    }

    public void setX(double x) { this.x = x; }
    public void setY(double y) {
        this.y = y;
        this.velocidadY = 0;
        this.enElAire = true;
    }

    protected final boolean consumirEstamina(double cantidad) {
        if (!Double.isFinite(cantidad) || cantidad < 0) {
            throw new IllegalArgumentException("El costo de estamina no puede ser negativo");
        }
        if (estamina < cantidad) {
            return false;
        }
        estamina -= cantidad;
        return true;
    }
    
public void setEnElAire(boolean enElAire) { this.enElAire = enElAire; }
public void setVelocidadY(double velocidadY) { this.velocidadY = velocidadY; }
public void corregirPosicion(double x, double y) { this.x = x; this.y = y; }
}