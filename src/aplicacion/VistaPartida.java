package aplicacion;

import java.util.List;
import modelos.enemigos.fantasma;
import modelos.enemigos.ratainfestada;
import modelos.items.Moneda;
import modelos.items.PowerUp;

public interface VistaPartida {
    void actualizarMonedas(List<Moneda> monedas);

    void mostrarAnimacionHabilidad();

    void mostrarAnimacionMuerte();

    void actualizarPuntajePresentado(int puntaje);

    void mostrarAnimacionDanio();

    void actualizarPresentacion();

    void actualizarTiempoPartida(long tiempoPartidaMillis);

    void mostrarMuerteFantasma(fantasma enemigo);

    void mostrarMuerteRataInfestada(ratainfestada rata);

    void actualizarEnemigosVisibles(
        List<fantasma> fantasmas, List<ratainfestada> ratasInfestadas
    );

    void mostrarNivelCompletado();

    void mostrarNivelCompletado(Runnable alCerrar);

    void actualizarPowerUps(List<PowerUp> powerUps);
}
