package controlador;

import modelos.enemigos.Enemigo;

public interface ControladorRata {
    void moverIzquierda();
    void moverDerecha();
    boolean saltar();
    void agacharse();
    void levantarse();
    void limitarPosicionHorizontal(double xMinimo, double xMaximo);
    void actualizar(double yPiso);
    void recargarEstamina();
    boolean usarHabilidad(Enemigo objetivo, double xDestino, double yDestino);
}
