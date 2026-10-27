package modelos.personajes;

import java.util.List;
import modelos.enemigos.enemigo;
import modelos.partida.ConfiguracionPartida;

// La rata negra prepara una lámpara y la convierte en una zona de fuego.
public class rata_negra extends Personaje {

    // Conserva el costo de estamina que ya tenía la habilidad de esta rata.
    private static final double COSTO_ESTAMINA_LAMPARA = 15;
    private LamparaFuego lamparaFuego;

    public rata_negra(String nombre) {
        // Es más rápida y salta más alto, pero tiene menos vida y estamina.
        super(nombre, TipoRata.NEGRA, 50, 3.5, 9, 90);
    }

    @Override
    public boolean usarHabilidad(enemigo objetivo, double xDestino, double yDestino) {
        return tirarLampara();
    }

    public boolean tirarLampara() {
        if (!consumirEstamina(COSTO_ESTAMINA_LAMPARA)) {
            return false;
        }
        double xInicial = getX() + ConfiguracionPartida.ANCHO_JUGADOR / 2.0;
        double ySuelo = getY() + ConfiguracionPartida.ALTO_JUGADOR;
        lamparaFuego = new LamparaFuego(xInicial, ySuelo, estaMirandoIzquierda());
        return true;
    }

    @Override
    public void actualizarHabilidad(List<enemigo> objetivos) {
        super.actualizarHabilidad(objetivos);
        if (lamparaFuego != null && !lamparaFuego.actualizar(objetivos)) {
            lamparaFuego = null;
        }
    }

    public LamparaFuego getLamparaFuego() {
        return lamparaFuego;
    }

    public void limpiarLamparaFuego() {
        lamparaFuego = null;
    }
}