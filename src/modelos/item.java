package modelos;

public class item {
    private final String nombre;
    private final int precio;
    private final TipoEfecto efecto;
    private final double valor; // cuánto suma/mejora

    public item(String nombre, int precio, TipoEfecto efecto, double valor) {
        this.nombre = nombre;
        this.precio = precio;
        this.efecto = efecto;
        this.valor = valor;
    }

    public String getNombre() { return nombre; }
    public int getPrecio() { return precio; }
    public TipoEfecto getEfecto() { return efecto; }
    public double getValor() { return valor; }

    public enum TipoEfecto {
        VIDA, VELOCIDAD, ESTAMINA_MAXIMA, FUERZA_SALTO
    }
}