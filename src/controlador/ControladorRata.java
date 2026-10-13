package controlador;

public interface ControladorRata {
    void moverIzquierda();
    void moverDerecha();
    void saltar();
    void limitarPosicionHorizontal(double xMinimo, double xMaximo);
    void actualizar(double yPiso);
}
