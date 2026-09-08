package modelos;
// Clase que representa a la rata blanca, un tipo específico de personaje. que hereda de la clase Personaje.
//  Esta clase tiene un método especial para tirar un "stun" que aturde a los enemigos y consume estamina.
public class rata_blanca extends Personaje {

    private final double costoEstaminaStun = 15;

    public rata_blanca(String nombre) {
        super(nombre);
    }

    // Aturde enemigos y consume estamina
    public boolean tirarStun() {
        if (estamina >= costoEstaminaStun) {
            estamina -= costoEstaminaStun;
            // es cuando se implementaría la lógica para aturdir a los enemigos cercanos
            //devolver true si el stun fue exitoso, false si no se pudo realizar por falta de estamina
            return true;
        }
        return false;
    }

    @Override
    public void recargarEstamina() {
      if (agachada && estamina < estaminaMaxima) {
          estamina += 2; // Recarga más rápido cuando está agachada
      } else if (estamina < estaminaMaxima) {
          estamina += 1; // Recarga normal cuando no está agachada
      }
    }

}