package controlador;

import modelos.personajes;

public class ratacontroller {

    // Referencia al modelo Rata que este controlador se encarga de controlar
    private final personajes rata;

    // Constructor encargado de recibir la Rata que será controlada
    public ratacontroller(personajes rata) {
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