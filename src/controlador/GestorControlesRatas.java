package controlador;

import modelos.enemigos.enemigo;

public class GestorControlesRatas {
    private final ControladorRata controladorRata;

    private boolean izquierdaPresionada;
    private boolean derechaPresionada;
    private boolean saltoPresionado;
    private boolean agacharsePresionado;

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

    public void presionarAgacharse() {
        if (!agacharsePresionado) {
            agacharsePresionado = true;
            controladorRata.agacharse();
        }
    }

    public void soltarAgacharse() {
        if (agacharsePresionado) {
            agacharsePresionado = false;
            controladorRata.levantarse();
        }
    }

    public void limpiarEntradas() {
        izquierdaPresionada = false;
        derechaPresionada = false;
        saltoPresionado = false;
        soltarAgacharse();
    }

    public void actualizarMovimiento(double xMinimo, double xMaximo) {
        if (!Double.isFinite(xMinimo) || !Double.isFinite(xMaximo) || xMinimo > xMaximo) {
            throw new IllegalArgumentException("Los límites horizontales deben ser finitos y válidos");
        }

        if (izquierdaPresionada != derechaPresionada) {
            if (izquierdaPresionada) {
                controladorRata.moverIzquierda();
            } else {
                controladorRata.moverDerecha();
            }
        }

        controladorRata.limitarPosicionHorizontal(xMinimo, xMaximo);
    }

    public boolean usarHabilidad(enemigo objetivo, double xDestino, double yDestino) {
        return controladorRata.usarHabilidad(objetivo, xDestino, yDestino);
    }
}