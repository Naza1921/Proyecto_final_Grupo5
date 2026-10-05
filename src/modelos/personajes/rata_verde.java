package modelos.personajes;
// La rata verde hereda el comportamiento común y conserva sus estadísticas
// y el costo de su habilidad de ácido en esta clase.
public class rata_verde extends Personaje {

    // Costo de estamina de la habilidad de ácido.
    private static final double COSTO_ESTAMINA_ACIDO = 15;

    public rata_verde(String nombre) {
        // Es algo más lenta, pero tiene una reserva de estamina amplia.
        super(nombre, TipoRata.VERDE, 100, 4, 9, 130);
    }

    // Verificamos si hay estamina suficiente para intentar la habilidad.
    // El proyectil y la ralentización todavía no están implementados.
    public boolean tirarAcido() {
        // Informa si puede pagar el costo; la lógica del proyectil aún no está implementada.
        return consumirEstamina(COSTO_ESTAMINA_ACIDO);
    }
}