package controlador;

import modelos.personajes.Personaje;

public interface FabricaPartida {
    PartidaCreada crear(
        Personaje jugador,
        Runnable alTerminarAnimacionMuerte,
        Runnable alPausar,
        Runnable alCompletarNivel
    );
}
