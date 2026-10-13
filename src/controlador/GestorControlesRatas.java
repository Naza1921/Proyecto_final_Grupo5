package controlador;

// Interpreta las entradas mantenidas y delega las acciones al controlador del personaje.
public class GestorControlesRatas {
    private final ControladorRata controladorRata;
    private boolean izquierdaPresionada;
    private boolean derechaPresionada;
    private boolean saltoPresionado;

    public GestorControlesRatas(ControladorRata controladorRata) {
        if (controladorRata == null) {
            throw new IllegalArgumentException("Se requiere un controlador de rata");
        }
        this.controladorRata = controladorRata;
    }

    public void presionarIzquierda() {
        izquierdaPresionada = true;
    }

    public void soltarIzquierda() {
        izquierdaPresionada = false;
    }

    public void presionarDerecha() {
        derechaPresionada = true;
    }

    public void soltarDerecha() {
        derechaPresionada = false;
    }

    public void presionarSalto() {
        if (!saltoPresionado) {
            saltoPresionado = true;
            controladorRata.saltar();
        }
    }

    public void soltarSalto() {
        saltoPresionado = false;
    }

    public void limpiarEntradas() {
        izquierdaPresionada = false;
        derechaPresionada = false;
        saltoPresionado = false;
    }

    public void actualizarMovimiento(double xMaximo) {
        if (!Double.isFinite(xMaximo) || xMaximo < 0) {
            throw new IllegalArgumentException("El límite horizontal debe ser finito y no negativo");
        }

        if (izquierdaPresionada != derechaPresionada) {
            if (izquierdaPresionada) {
                controladorRata.moverIzquierda();
            } else {
                controladorRata.moverDerecha();
            }
        }
        controladorRata.limitarPosicionHorizontal(0, xMaximo);
    }
}
