package aplicacion;

import java.util.List;

public interface VistaPartida {
    void actualizarJugador(JugadorVista jugador);

    void actualizarMonedas(List<MonedaVista> monedas);

    void mostrarAnimacionHabilidad();

    void mostrarAnimacionMuerte();

    void actualizarPuntajePresentado(int puntaje);

    void mostrarAnimacionDanio();

    void actualizarPresentacion();

    void actualizarTiempoPartida(long tiempoPartidaMillis);

    void mostrarMuerteFantasma(EnemigoVista enemigo);

    void mostrarMuerteRataInfestada(EnemigoVista rata);

    void actualizarEnemigosVisibles(List<EnemigoVista> enemigos);

    void mostrarNivelCompletado();

    void mostrarNivelCompletado(Runnable alCerrar);

    void actualizarPowerUps(List<PowerUpVista> powerUps);
}
