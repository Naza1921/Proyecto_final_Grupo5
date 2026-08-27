package modelos;

public class rata_negra extends Personaje {

    private final double costoEstaminaDistraccion = 15;

    public rata_negra(String nombre) {
        super(nombre);
    }

    // Tira un objeto que distrae y dirige a los enemigos a un lugar
    public boolean tirarDistraccion(double xDestino, double yDestino) {
        if (estamina >= costoEstaminaDistraccion) {
            estamina -= costoEstaminaDistraccion;
            // TODO: lógica de distracción hacia (xDestino, yDestino)
            return true;
        }
        return false;
    }
}