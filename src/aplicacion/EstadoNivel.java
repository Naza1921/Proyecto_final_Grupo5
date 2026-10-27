package aplicacion;

import modelos.escenario.levels.level;

public interface EstadoNivel {
    level getNivel();

    boolean esPrimeraSeccion();

    boolean esUltimaSeccion();
}
