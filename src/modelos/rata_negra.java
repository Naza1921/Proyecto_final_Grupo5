package modelos;
// Clase que representa a la rata negra, un tipo específico de personaje que hereda de la clase Personaje.
// Esta clase tiene un método especial para tirar un objeto que distrae a los enemigos y consume estamina.

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