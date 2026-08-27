package modelos;

public class rata_verde extends Personaje {

    private final double costoEstaminaAcido = 15;

    public rata_verde(String nombre) {
        super(nombre);
    }

    // Tira ácido que ralentiza enemigos y consume estamina
    public boolean tirarAcido() {
        if (estamina >= costoEstaminaAcido) {
            estamina -= costoEstaminaAcido;
            // TODO: lógica del proyectil de ácido
            return true;
        }
        return false; // no tiene estamina suficiente
    }
}
