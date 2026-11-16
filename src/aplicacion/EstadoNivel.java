package aplicacion;

public interface EstadoNivel {
    NivelVista getNivelVista();

    int getPiso();

    boolean esPrimeraSeccion();

    boolean esUltimaSeccion();
}
