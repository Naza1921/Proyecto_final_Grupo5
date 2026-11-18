package modelos.personajes;

import java.util.List;
import modelos.enemigos.Enemigo;
import modelos.partida.ConfiguracionPartida;

public class RataNegra extends Personaje {

    private static final double COSTO_ESTAMINA_LAMPARA = 15;
    private LamparaFuego lamparaFuego;

    public RataNegra(String nombre) {
        super(nombre, TipoRata.NEGRA, 50, 7, 9.5, 90);
    }

    @Override
    public boolean usarHabilidad(Enemigo objetivo, double xDestino, double yDestino) {
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
    public void actualizarHabilidad(List<Enemigo> objetivos) {
        validarObjetivosHabilidad(objetivos);
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

    @Override
    public void prepararCambioDeSeccion() {
        limpiarLamparaFuego();
    }
}