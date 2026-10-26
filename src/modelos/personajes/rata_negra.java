package modelos.personajes;

import modelos.enemigos.enemigo;

// La rata negra puede atraer a un enemigo hacia un punto cercano.
public class rata_negra extends Personaje {

    // Costo de estamina de la habilidad de distracción.
    private static final double COSTO_ESTAMINA_DISTRACCION = 15;
    private static final double ALCANCE_LANZAMIENTO = 250;
    private static final double RADIO_DISTRACCION = 100;
    private static final long DURACION_DISTRACCION_MS = 3000;

    public rata_negra(String nombre) {
        // Es más rápida y salta más alto, pero tiene menos vida y estamina.
        super(nombre, TipoRata.NEGRA, 50, 3.5, 9, 90);
    }

    @Override
    public boolean usarHabilidad(enemigo objetivo, double xDestino, double yDestino) {
        return tirarDistraccion(objetivo, xDestino, yDestino);
    }

    public boolean tirarDistraccion(enemigo objetivo, double xDestino, double yDestino) {
        if (!Double.isFinite(xDestino) || !Double.isFinite(yDestino)
                || Math.hypot(xDestino - getX(), yDestino - getY()) > ALCANCE_LANZAMIENTO
                || objetivo == null || !objetivo.estavivo()
                || Math.hypot(objetivo.getX() - xDestino, objetivo.getY() - yDestino)
                    > RADIO_DISTRACCION
                || !consumirEstamina(COSTO_ESTAMINA_DISTRACCION)) {
            return false;
        }
        objetivo.distraer(xDestino, yDestino, DURACION_DISTRACCION_MS);
        return true;
    }
}