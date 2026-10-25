package controlador;

import modelos.partida.Puntaje;

public final class ControladorPuntaje {
    private final Puntaje puntaje;

    public ControladorPuntaje(Puntaje puntaje) {
        if (puntaje == null) {
            throw new IllegalArgumentException("Se requiere un modelo de puntaje");
        }
        this.puntaje = puntaje;
    }

    public int obtenerPuntaje() {
        return puntaje.getTotal();
    }

    public void agregarPuntos(int puntos) {
        puntaje.agregar(puntos);
    }

    public void penalizar(int puntos) {
        puntaje.penalizar(puntos);
    }

    public void reiniciar() {
        puntaje.reiniciar();
    }
}
