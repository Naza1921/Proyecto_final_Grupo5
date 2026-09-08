package modelos;
// Clase que representa a la rata gris, un tipo específico de personaje que hereda de la clase Personaje.
// Esta clase tiene un método especial para recargar estamina automáticamente, sin necesidad de esconderse.
public class rata_gris extends Personaje {

    public rata_gris(String nombre) {
        super(nombre);
    }

    // Única que recarga estamina sola, sin esconderse.
    // Sobreescribimos el método del padre para que siempre sume.
    @Override
    public void recargarEstamina() {
        if (estamina < estaminaMaxima) {
            estamina += 2; // recarga automática, más generosa
        }
    }
}