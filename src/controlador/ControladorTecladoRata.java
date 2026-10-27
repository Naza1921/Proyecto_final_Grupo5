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
    private final Runnable accionSalto;
    private final Runnable accionHabilidad;
    private boolean habilidadPresionada;

    public ControladorTecladoRata(JComponent componente, GestorControlesRatas gestorControles,
                                  Runnable accionPausa, Runnable accionSalto,
                                  Runnable accionHabilidad) {
        if (componente == null || gestorControles == null
                || accionPausa == null || accionSalto == null || accionHabilidad == null) {
            throw new IllegalArgumentException(
                "Se requieren el componente, el gestor y las acciones de pausa, salto y habilidad"
            );
        }
        this.gestorControles = gestorControles;
        this.accionSalto = accionSalto;
        this.accionHabilidad = accionHabilidad;

        vincularTecla(componente, "pressed LEFT", "izquierdaPresionada",
            gestorControles::presionarIzquierda);
        vincularTecla(componente, "released LEFT", "izquierdaLiberada",
            gestorControles::soltarIzquierda);
        vincularTecla(componente, "pressed RIGHT", "derechaPresionada",
            gestorControles::presionarDerecha);
        vincularTecla(componente, "released RIGHT", "derechaLiberada",
            gestorControles::soltarDerecha);
        vincularTecla(componente, "pressed SPACE", "saltar", this::presionarSalto);
        vincularTecla(componente, "released SPACE", "saltarLiberado",
            gestorControles::soltarSalto);
        vincularTecla(componente, "pressed DOWN", "agacharse",
            gestorControles::presionarAgacharse);
        vincularTecla(componente, "released DOWN", "levantarse",
            gestorControles::soltarAgacharse);
        vincularTecla(componente, "pressed F", "habilidadPresionada",
            this::presionarHabilidad);
        vincularTecla(componente, "released F", "habilidadLiberada",
            this::soltarHabilidad);
        vincularTecla(componente, "pressed ESCAPE", "pausar",
            accionPausa);

        componente.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent evento) {
                ControladorTecladoRata.this.limpiarEntradas();
            }
        });
    }

    private void presionarSalto() {
        if (gestorControles.presionarSalto()) {
            accionSalto.run();
        }
    }

    private void presionarHabilidad() {
        if (!habilidadPresionada) {
            habilidadPresionada = true;
            accionHabilidad.run();
        }
    }

    private void soltarHabilidad() {
        habilidadPresionada = false;
    }

    public void limpiarEntradas() {
        gestorControles.limpiarEntradas();
        habilidadPresionada = false;
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
