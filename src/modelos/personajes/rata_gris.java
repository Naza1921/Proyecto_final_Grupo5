package modelos.personajes;

// La rata gris reutiliza el comportamiento común y solo cambia sus estadísticas
// y la cantidad de estamina que recupera en cada actualización.
public class rata_gris extends Personaje {

    // Pasamos sus valores iniciales al padre para no duplicar estado en esta subclase.
    public rata_gris(String nombre) {
        // Tiene estadísticas equilibradas y recupera estamina más rápido.
        super(nombre, TipoRata.GRIS, 120, 5, 10, 100);
    }

    @Override
    public void recargarEstamina() {
        // Esta rata recupera dos puntos por actualización, hasta el máximo.
        setEstamina(getEstamina() + 2);
    }
}