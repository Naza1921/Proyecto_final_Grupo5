package modelos;

public class rata_blanca extends Personaje {

    private final double costoEstaminaStun = 15;

    public rata_blanca(String nombre) {
        super(nombre);
    }

    // Aturde enemigos y consume estamina
    public boolean tirarStun() {
        if (estamina >= costoEstaminaStun) {
            estamina -= costoEstaminaStun;
            // TODO: lógica del aturdimiento
            return true;
        }
        return false;
    }
}