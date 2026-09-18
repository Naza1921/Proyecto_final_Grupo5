package modelos;

public class rata_gris extends Personaje {

    public rata_gris(String nombre) {
        super(nombre);
    }

    @Override
    public void recargarEstamina() {
        if (getEstamina() < getEstaminaMaxima()) {
            setEstamina(Math.min(getEstamina() + 2, getEstaminaMaxima()));
        }
    }
}