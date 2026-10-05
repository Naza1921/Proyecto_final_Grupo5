package controlador;

import modelos.personajes.Personaje;

public class ratacontroller {

    // Referencia al modelo Rata que este controlador se encarga de controlar
    private final Personaje rata;

    // Constructor encargado de recibir la Rata que será controlada
    public ratacontroller(Personaje rata) {
        this.rata = rata;
    }

    // Se mueve hacia la izquierda la rata
    public void moverIzquierda() {
        rata.moverIzquierda();
    }

    // Se mueve hacia la derecha la rata
    public void moverDerecha() {
        rata.moverDerecha();
    }

    // Delegamos el salto al modelo, que impide iniciarlo si la rata ya está en el aire.
    public void saltar() {
        rata.saltar();
    }

    // El panel proporciona el ancho disponible y el modelo mantiene la rata dentro del escenario.
    public void limitarPosicionHorizontal(double xMinimo, double xMaximo) {
        rata.limitarPosicionHorizontal(xMinimo, xMaximo);
    }

    // Se agacha la rata
    public void agacharse() {
        rata.agacharse();
    }

    // La rata vuelve a levantarse
    public void levantarse() {
        rata.levantarse();
    }

    // Interactúa con un objeto, comida, power-up, etc.
    public void interactuar() {
        rata.interactuar();
    }

    // Detiene el movimiento de la rata
    public void detenerMovimiento() {
        rata.detenerMovimiento();
    }

    // Actualiza el estado de la rata
    public void actualizar() {
        rata.actualizar();
    }
}