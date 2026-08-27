package modelos;

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