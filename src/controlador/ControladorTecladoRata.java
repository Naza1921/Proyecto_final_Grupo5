package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

// Instala los enlaces Swing y avisa al gestor cuando cambian las entradas.
public class ControladorTecladoRata {
    private final GestorControlesRatas gestorControles;

    public ControladorTecladoRata(JComponent componente, GestorControlesRatas gestorControles) {
        if (componente == null || gestorControles == null) {
            throw new IllegalArgumentException("Se requieren el componente y el gestor de controles");
        }
        this.gestorControles = gestorControles;

        vincularTecla(componente, "pressed LEFT", "izquierdaPresionada",
            gestorControles::presionarIzquierda);
        vincularTecla(componente, "released LEFT", "izquierdaLiberada",
            gestorControles::soltarIzquierda);
        vincularTecla(componente, "pressed RIGHT", "derechaPresionada",
            gestorControles::presionarDerecha);
        vincularTecla(componente, "released RIGHT", "derechaLiberada",
            gestorControles::soltarDerecha);
        vincularTecla(componente, "pressed SPACE", "saltar",
            gestorControles::presionarSalto);
        vincularTecla(componente, "released SPACE", "saltarLiberado",
            gestorControles::soltarSalto);

        componente.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent evento) {
                ControladorTecladoRata.this.gestorControles.limpiarEntradas();
            }
        });
    }

    private void vincularTecla(JComponent componente, String secuencia,
                               String nombreAccion, Runnable accion) {
        componente.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(secuencia), nombreAccion);
        componente.getActionMap().put(nombreAccion, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evento) {
                accion.run();
            }
        });
    }
}
