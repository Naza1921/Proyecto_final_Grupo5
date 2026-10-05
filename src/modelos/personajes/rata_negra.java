package modelos.personajes;
// La rata negra comparte el comportamiento general de Personaje, pero tiene
// estadísticas propias y prepara el costo de su habilidad de distracción.
public class rata_negra extends Personaje {

    // Costo de estamina de la habilidad de distracción.
    private static final double COSTO_ESTAMINA_DISTRACCION = 15;

    public rata_negra(String nombre) {
        // Es más rápida y salta más alto, pero tiene menos vida y estamina.
        super(nombre, TipoRata.NEGRA, 85, 7, 12, 90);
    }

    // Recibimos el destino pensado para la distracción y verificamos el costo.
    // Todavía no se crea el proyectil ni se aplica el efecto a los enemigos.
    public boolean tirarDistraccion(double xDestino, double yDestino) {
        // Informa si puede pagar el costo; la lógica del proyectil aún no está implementada.
        return consumirEstamina(COSTO_ESTAMINA_DISTRACCION);
    }
}