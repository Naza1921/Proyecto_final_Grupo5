package controlador;

import modelos.enemigos.enemigo;
import modelos.personajes.Personaje;

public class ratacontroller implements ControladorRata {

    // Referencia al modelo Rata que este controlador se encarga de controlar
    private final Personaje rata;

    // Constructor encargado de recibir la Rata que será controlada
    public ratacontroller(Personaje rata) {
        if (rata == null) {
            throw new IllegalArgumentException("Se requiere un personaje");
        }
        this.rata = rata;
    }

    // Se mueve hacia la izquierda la rata
    @Override
    public void moverIzquierda() {
        rata.moverIzquierda();
    }

    // Se mueve hacia la derecha la rata
    @Override
    public void moverDerecha() {
        rata.moverDerecha();
    }

    // Delegamos el salto al modelo, que impide iniciarlo si la rata ya está en el aire.
    @Override
    public void saltar() {
        rata.saltar();
    }

    // El panel proporciona el ancho disponible y el modelo mantiene la rata dentro del escenario.
    @Override
    public void limitarPosicionHorizontal(double xMinimo, double xMaximo) {
        rata.limitarPosicionHorizontal(xMinimo, xMaximo);
    }

    // Se agacha la rata
    @Override
    public void agacharse() {
        rata.agacharse();
    }

    // La rata vuelve a levantarse
    @Override
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
    @Override
    public void actualizar(double yPiso) {
        rata.actualizar();
        if (rata.estaEnElAire() && rata.getVelocidadY() >= 0 && rata.getY() >= yPiso) {
            rata.aterrizar(yPiso);
        }
    }

    @Override
    public void recargarEstamina() {
        rata.recargarEstamina();
    }

    @Override
    public boolean usarHabilidad(enemigo objetivo, double xDestino, double yDestino) {
        return rata.usarHabilidad(objetivo, xDestino, yDestino);
    }
}