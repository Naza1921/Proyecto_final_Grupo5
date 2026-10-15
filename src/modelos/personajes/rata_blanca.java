package modelos.personajes;
// La rata blanca hereda el estado y las acciones compartidas, y acá definimos
// sus estadísticas y la recarga extra que obtiene al agacharse.
public class rata_blanca extends Personaje {

    // Costo de estamina por intentar usar la habilidad especial.
    private static final double COSTO_ESTAMINA_STUN = 15;

    public rata_blanca(String nombre) {
        // Es más resistente, a cambio de moverse y saltar un poco menos.
        super(nombre, TipoRata.BLANCA, 100, 4.5, 9, 100);
    }

    // Reservamos el costo de la habilidad y avisamos si el personaje pudo pagarlo.
    // El efecto de aturdir se conectará cuando esté implementado el sistema de enemigos.
    public boolean tirarStun() {
        // Devuelve false si no alcanza la estamina para pagar el costo.
        return consumirEstamina(COSTO_ESTAMINA_STUN);
    }

    @Override
    public void recargarEstamina() {
        // Agacharse permite recuperar estamina más rápido.
        double recarga = estaAgachada() ? 2 : 1;
        setEstamina(getEstamina() + recarga);
    }

}