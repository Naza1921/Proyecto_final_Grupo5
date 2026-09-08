package controlador;

import modelos.Personaje;
// Clase que representa el controlador de una rata, encargado de manejar la lógica de movimiento y acciones del personaje.

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