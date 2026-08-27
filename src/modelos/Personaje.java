package modelos;

public class Personaje {
    
    protected String nombre;
    protected int vida;
    protected final int vidaMaxima;
    protected double x;
    protected double y;
    protected double velocidad;
    protected final double fuerzaSalto;
    protected boolean enElAire;
    protected boolean agachada;
    protected double estamina;
    protected final double estaminaMaxima;

    public Personaje(String nombre) {
        this.nombre = nombre;
        this.vidaMaxima = 100;
        this.vida = vidaMaxima;
        this.x = 0;
        this.y = 0;
        this.velocidad = 5;
        this.fuerzaSalto = 10;
        this.enElAire = false;
        this.agachada = false;
        this.estaminaMaxima = 100;
        this.estamina = estaminaMaxima;
    }

    public void moverIzquierda() { x -= velocidad; }
    public void moverDerecha() { x += velocidad; }

    public void saltar() {
        if (!enElAire) {
            enElAire = true;
            y -= fuerzaSalto;
        }
    }

    public void agacharse() { agachada = true; }
    public void levantarse() { agachada = false; }

    // La rata base recarga estamina automáticamente con el tiempo.
    // Las demás la recargan solo al esconderse (agachada) — por eso
    // este método vive en el padre pero cada hija puede sobreescribirlo.
    public void recargarEstamina() {
        if (estamina < estaminaMaxima) {
            estamina += 1;
        }
    }

    public void actualizar() {
        // gravedad, físicas, animaciones, efectos temporales, etc.
    }

    public void recibirDanio(int cantidad) {
        vida -= cantidad;
        if (vida < 0) vida = 0;
    }

    public boolean estaViva() { return vida > 0; }
    public int getVida() { return vida; }
    public int getVidaMaxima() { return vidaMaxima; }
    public double getX() { return x; }
    public double getY() { return y; }
    public String getNombre() { return nombre; }
    public boolean estaEnElAire() { return enElAire; }
    public boolean estaAgachada() { return agachada; }
    public double getEstamina() { return estamina; }

    public void detenerMovimiento() { velocidad = 0; }
    public void interactuar() {
        // lógica de interacción con objetos
    }
}