
public abstract class Personaje
{
    protected String nombre;
    protected int vida;
    protected int tamano; // tamaño (ancho/alto)
    protected int monedas;
    protected int x;
    protected int y;

    // Constructor por defecto
    public Personaje()
    {
        this.nombre = null;
        this.vida = 100;
        this.tamano = 1;
        this.monedas = 0;
        this.x = 0;
        this.y = 0;
    }

    // Constructor con parámetros
    public Personaje(String nombre, int vida, int tamano, int monedas, int x, int y)
    {
        this.nombre = nombre;
        this.vida = vida;
        this.tamano = tamano;
        this.monedas = monedas;
        this.x = x;
        this.y = y;
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getVida() { return vida; }
    public void setVida(int vida) { this.vida = vida; }

    public int getTamano() { return tamano; }
    public void setTamano(int tamano) { this.tamano = tamano; }

    public int getMonedas() { return monedas; }
    public void setMonedas(int monedas) { this.monedas = monedas; }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
}

