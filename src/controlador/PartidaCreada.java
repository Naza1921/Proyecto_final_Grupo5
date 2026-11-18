package controlador;

import vista.PanelJuego;

public record PartidaCreada(
    PanelJuego panelJuego,
    GestorEfectosTemporales gestorEfectos,
    ControladorPartida controladorPartida,
    ControladorTecladoRata controladorTecladoRata
) {
    public PartidaCreada {
        if (panelJuego == null || gestorEfectos == null || controladorPartida == null
                || controladorTecladoRata == null) {
            throw new IllegalArgumentException("La partida creada debe estar completa");
        }
    }
}
