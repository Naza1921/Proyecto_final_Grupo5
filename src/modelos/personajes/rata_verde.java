package modelos.personajes;
import modelos.enemigos.enemigo;
import java.util.List;
import modelos.partida.ConfiguracionPartida;
// La rata verde hereda el comportamiento común y conserva sus estadísticas
// y el costo de su habilidad de ácido en esta clase.
public class rata_verde extends Personaje {

    // Costo de estamina de la habilidad de ácido.
    private static final double COSTO_ESTAMINA_ACIDO = 15;
    private CharcoAcido charcoAcido;

    public rata_verde(String nombre) {
        // Es algo más lenta, pero tiene una reserva de estamina amplia.
        super(nombre, TipoRata.VERDE, 80, 5, 11, 130);
    }

    @Override
    public boolean usarHabilidad(enemigo objetivo, double xDestino, double yDestino) {
        return tirarAcido();
    }

    public boolean tirarAcido() {
        if (!consumirEstamina(COSTO_ESTAMINA_ACIDO)) {
            return false;
        }
        double xCharco = estaMirandoIzquierda()
            ? getX() - CharcoAcido.ANCHO
            : getX() + ConfiguracionPartida.ANCHO_JUGADOR;
        xCharco = Math.max(0, xCharco);
        double yCharco = getY() + ConfiguracionPartida.ALTO_JUGADOR - CharcoAcido.ALTO;
        charcoAcido = new CharcoAcido(xCharco, yCharco);
        return true;
    }

    @Override
    public void actualizarHabilidad(List<enemigo> objetivos) {
        super.actualizarHabilidad(objetivos);
        if (charcoAcido != null && !charcoAcido.actualizar(objetivos)) {
            charcoAcido = null;
        }
    }

    public CharcoAcido getCharcoAcido() {
        return charcoAcido != null && charcoAcido.estaVisible() ? charcoAcido : null;
    }

    public void limpiarCharcoAcido() {
        charcoAcido = null;
    }
}