package modelos.personajes;

import modelos.enemigos.enemigo;

// La rata blanca hereda el estado y las acciones compartidas, y acá definimos
// sus estadísticas y la recarga extra que obtiene al agacharse.
public class rata_blanca extends Personaje {

    // Costo de estamina por intentar usar la habilidad especial.
    private static final double COSTO_ESTAMINA_STUN = 15;
    private static final double ALCANCE_STUN = 150;
    private static final long DURACION_STUN_MS = 2000;

    public rata_blanca(String nombre) {
        // Es más resistente, a cambio de moverse y saltar un poco menos.
        super(nombre, TipoRata.BLANCA, 100, 7,10.5, 100);
    }

    @Override
    public boolean usarHabilidad(enemigo objetivo, double xDestino, double yDestino) {
        return tirarStun(objetivo);
    }

    public boolean tirarStun(enemigo objetivo) {
        if (!objetivoEnRango(objetivo, ALCANCE_STUN)
                || !consumirEstamina(COSTO_ESTAMINA_STUN)) {
            return false;
        }
        objetivo.aturdir(DURACION_STUN_MS);
        return true;
    }

    @Override
    public void recargarEstamina() {
        // Agacharse permite recuperar estamina más rápido.
        double recarga = estaAgachada() ? 2 : 1;
        setEstamina(getEstamina() + recarga);
    }

}