package modelos.personajes;

// Acá concentramos lo que todas las ratas tienen en común: estadísticas, posición
// y acciones básicas. Cada tipo concreto hereda de esta clase y aporta sus diferencias.
public abstract class Personaje {
   
   // cambiar el tipo de rata a un enum para que sea mas facil de manejar
   // cada subclase concreta define su tipo de rata y sus estadísticas iniciales.
   

    // Guardamos el estado como privado para que los cambios pasen por las operaciones del modelo.
    private final String nombre;
    private final TipoRata tipoRata;
    private int vida;
    private final int vidaMaxima;
    private double x;
    private double y;
    private double velocidad;
    private final double velocidadBase;
    private final double fuerzaSalto;
    private boolean enElAire;
    private boolean agachada;
    private double estamina;
    private final double estaminaMaxima;
    private double velocidadY = 0;
    private static final double GRAVEDAD = 0.8;
    private static final double VELOCIDAD_MAXIMA_CAIDA = 15;
    
    //revisar si es necesario tipo enum para el tipo de rata, para que sea mas facil de manejar
    public enum TipoRata {
        BLANCA, GRIS, NEGRA, VERDE
    }
    // Cada subclase pasa sus números iniciales por super(); así no repetimos estas variables
    // en cada rata y mantenemos en un solo lugar la validación del estado compartido.
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

    // El movimiento horizontal usa la velocidad actual del personaje.
    public void moverIzquierda() { x -= velocidad; }
    public void moverDerecha() { x += velocidad; }

    // Mantiene la posición horizontal dentro del área que el nivel permite recorrer.
    public void limitarPosicionHorizontal(double xMinimo, double xMaximo) {
        if (!Double.isFinite(xMinimo) || !Double.isFinite(xMaximo) || xMinimo > xMaximo) {
            throw new IllegalArgumentException("Los límites horizontales deben ser finitos y válidos");
        }
        x = Math.max(xMinimo, Math.min(x, xMaximo));
    }

    public void saltar() {
        // Solo se puede iniciar un salto si el personaje está en el suelo.
        if (!enElAire) {
            enElAire = true;
            velocidadY = -fuerzaSalto;
        }
    }
    // Mantenemos la postura como parte del modelo porque también puede cambiar la recarga.
    public void agacharse() { agachada = true; }
    public void levantarse() { agachada = false; }

    public void recargarEstamina() {
        if (estamina < estaminaMaxima) {
            // El setter limita la recarga al máximo permitido.
            setEstamina(estamina + 1);
        }
    }

    public void actualizar() {
        // La gravedad solo se aplica mientras el personaje está en el aire.
        if (!enElAire) {
            return;
        }
        // Aceleramos la caída hasta el límite para que no siga aumentando indefinidamente.
        velocidadY = Math.min(velocidadY + GRAVEDAD, VELOCIDAD_MAXIMA_CAIDA);
        y += velocidadY;
    }

    public void recibirDanio(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("El daño no puede ser negativo");
        }
        // La vida nunca puede quedar por debajo de cero.
        vida = Math.max(0, vida - cantidad);
    }

    // Recupera vida sin superar el máximo definido por la subclase concreta.
    public void recuperarVida(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La recuperación de vida no puede ser negativa");
        }
        vida = (int) Math.min(vidaMaxima, (long) vida + cantidad);
    }

    public void aterrizar(double yPiso) {
        // Detiene la caída y deja al personaje apoyado en la plataforma.
        this.y = yPiso;
        this.velocidadY = 0.0;
        this.enElAire = false;
    }

    // Restaura los valores de partida antes de volver a cargar la posición inicial del nivel.
    public void restablecerEstado() {
        vida = vidaMaxima;
        setEstamina(estaminaMaxima);
        velocidad = velocidadBase;
        x = 0;
        y = 0;
        velocidadY = 0;
        enElAire = false;
        agachada = false;
    }

    public void recibirDanioYReducirEstamina(int danioVida, double danioEstamina) {
        if (danioVida < 0 || !Double.isFinite(danioEstamina) || danioEstamina < 0) {
            throw new IllegalArgumentException("El daño y la reducción de estamina no pueden ser negativos");
        }
        vida = Math.max(0, vida - danioVida);
        // Reutiliza la validación y los límites definidos para la estamina.
        setEstamina(estamina - danioEstamina);
    }

    // Exponemos consultas del estado sin entregar acceso directo a los atributos.
    public boolean estaViva() { return vida > 0; }
    public int getVida() { return vida; }
    public int getVidaMaxima() { return vidaMaxima; }
    public double getX() { return x; }
    public double getY() { return y; }
    public String getNombre() { return nombre; }
    public TipoRata getTipoRata() { return tipoRata; }
    public boolean estaEnElAire() { return enElAire; }
    public boolean estaAgachada() { return agachada; }
    public double getEstamina() { return estamina; }
    public double getEstaminaMaxima() { return estaminaMaxima; }
    public double getVelocidad() { return velocidad; }
    public double getFuerzaSalto() { return fuerzaSalto; }
    public double getVelocidadY(){return velocidadY;}

    public void setVelocidad(double velocidad) { this.velocidad = velocidad; }
    public void setEstamina(double estamina) {
        if (!Double.isFinite(estamina)) {
            throw new IllegalArgumentException("La estamina debe ser un número finito");
        }
        // Centraliza los límites para evitar valores fuera del rango válido.
        this.estamina = Math.max(0, Math.min(estamina, estaminaMaxima));
    }
    // Al detener el movimiento ponemos a cero la velocidad horizontal actual.
    public void detenerMovimiento() { velocidad = 0; }

    public void interactuar() {
        // La interacción concreta depende del objeto cercano.
    }

    // Estas posiciones se actualizan al iniciar un nivel o cuando se mueve al personaje.
    public void setX(double x) { this.x = x; }
    public void setY(double y) {
        this.y = y;
        this.velocidadY = 0;
        // Al cargar un nivel, el personaje debe poder caer desde su posición inicial.
        this.enElAire = true;
    }

    // Las habilidades de las subclases comparten esta comprobación y descuento:
    // si no alcanza la estamina, no la modificamos y devolvemos false.
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
}