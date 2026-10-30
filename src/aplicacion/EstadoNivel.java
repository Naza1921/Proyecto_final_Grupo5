package aplicacion;

import modelos.escenario.levels.level;

public interface EstadoNivel {
    level getNivel();

    int getPiso();

    boolean esPrimeraSeccion();

    boolean esUltimaSeccion();
}
