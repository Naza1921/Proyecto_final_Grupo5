package modelos.personajes;

import modelos.enemigos.enemigo;
// La rata verde hereda el comportamiento común y conserva sus estadísticas
// y el costo de su habilidad de ácido en esta clase.
public class rata_verde extends Personaje {

    // Costo de estamina de la habilidad de ácido.
    private static final double COSTO_ESTAMINA_ACIDO = 15;
    private static final double ALCANCE_ACIDO = 220;
    private static final int DANIO_ACIDO = 1;

    public rata_verde(String nombre) {
        // Es algo más lenta, pero tiene una reserva de estamina amplia.
        super(nombre, TipoRata.VERDE, 100, 4, 9, 130);
    }

    @Override
    public boolean usarHabilidad(enemigo objetivo, double xDestino, double yDestino) {
        return tirarAcido(objetivo);
    }

    public boolean tirarAcido(enemigo objetivo) {
        if (!objetivoEnRango(objetivo, ALCANCE_ACIDO)
                || !consumirEstamina(COSTO_ESTAMINA_ACIDO)) {
            return false;
        }
        objetivo.recibirDanio(DANIO_ACIDO, enemigo.TipoDanio.ACIDO);
        return true;
    }
}