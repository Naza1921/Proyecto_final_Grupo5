package aplicacion;

import vista.PanelJuego;

public interface NavegacionPartida {
    void mostrarCargaPartida();

    void instalarVistaPartida(PanelJuego panelJuego);

    void quitarVistaPartida(PanelJuego panelJuego);

    void mostrarPausa();

    void mostrarMenuGameOver();

    void mostrarMenu();

    void mostrarErrorCreacionPartida(String mensaje);
}
