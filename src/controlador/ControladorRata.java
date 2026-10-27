package controlador;

import modelos.enemigos.enemigo;

public interface ControladorRata {
    void moverIzquierda();
    void moverDerecha();
    boolean saltar();
    void agacharse();
    void levantarse();
    void limitarPosicionHorizontal(double xMinimo, double xMaximo);
    void actualizar(double yPiso);
    void recargarEstamina();
    boolean usarHabilidad(enemigo objetivo, double xDestino, double yDestino);
}
