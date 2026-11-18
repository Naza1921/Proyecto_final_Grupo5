package controlador;

import java.util.concurrent.CompletionException;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import persistencia.ResultadoPuntaje;
import servicios.OperacionesPuntaje;
import vista.TableroPuntajes;

public final class ControladorPuntajes {
    private final JFrame ventana;
    private final OperacionesPuntaje operacionesPuntaje;
    private final TableroPuntajes tablero;

    public ControladorPuntajes(
            JFrame ventana,
            OperacionesPuntaje operacionesPuntaje,
            TableroPuntajes tablero) {
        if (ventana == null || operacionesPuntaje == null || tablero == null) {
            throw new IllegalArgumentException("Se requieren dependencias válidas para los puntajes");
        }
        this.ventana = ventana;
        this.operacionesPuntaje = operacionesPuntaje;
        this.tablero = tablero;
    }

    public void cargarTablero() {
        tablero.mostrarCargando();
        operacionesPuntaje.listarMejoresPuntajesAsync().whenComplete((resultados, error) ->
            SwingUtilities.invokeLater(() -> {
                if (error == null) {
                    tablero.mostrarResultados(resultados);
                    return;
                }
                tablero.mostrarErrorCarga();
                mostrarAvisoNoModal(
                    "No se pudo cargar el tablero de puntajes: "
                        + obtenerCausa(error).getMessage(),
                    "Error de base de datos",
                    JOptionPane.ERROR_MESSAGE
                );
            })
        );
    }

    public void solicitarGuardado(ResultadoPuntaje resultado) {
        if (resultado == null) {
            throw new IllegalArgumentException("Se requiere el resultado de una partida");
        }
        String nombre = JOptionPane.showInputDialog(
            ventana,
            "Ingresá tu nombre para guardar el puntaje:",
            resultado.getNombre()
        );
        if (nombre == null) {
            JOptionPane.showMessageDialog(
                ventana,
                "El resultado de esta partida no se guardó.",
                "Puntaje no guardado",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        if (nombre.isBlank()) {
            JOptionPane.showMessageDialog(
                ventana,
                "El nombre no puede estar vacío; el resultado no se guardó.",
                "Nombre inválido",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        operacionesPuntaje.guardarMejorPuntajeAsync(
            nombre,
            resultado.getPuntuacion(),
            resultado.getTiempoSegundos()
        ).whenComplete((nuevoMejorPuntaje, error) -> SwingUtilities.invokeLater(() -> {
            if (error != null) {
                mostrarAvisoNoModal(
                    "No se pudo guardar el puntaje en SQLite: "
                        + obtenerCausa(error).getMessage(),
                    "Error de base de datos",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            String mensaje = nuevoMejorPuntaje
                ? "Puntaje guardado correctamente."
                : "No superaste tu mejor puntaje anterior; se conservó el registro existente.";
            mostrarAvisoNoModal(
                mensaje,
                "Resultado de la partida",
                JOptionPane.INFORMATION_MESSAGE
            );
        }));
    }

    public void confirmarGuardado(ResultadoPuntaje resultado) {
        if (resultado == null) {
            throw new IllegalArgumentException("Se requiere el resultado de una partida");
        }
        int respuesta = JOptionPane.showConfirmDialog(
            ventana,
            "Quisieras guardar tu progreso en la tabla de puntos?",
            "Guardar progreso",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        if (respuesta == JOptionPane.YES_OPTION) {
            solicitarGuardado(resultado);
        }
    }

    private void mostrarAvisoNoModal(String mensaje, String titulo, int tipo) {
        JOptionPane panelAviso = new JOptionPane(mensaje, tipo);
        JDialog dialogo = panelAviso.createDialog(ventana, titulo);
        dialogo.setModal(false);
        dialogo.setVisible(true);
    }

    private Throwable obtenerCausa(Throwable error) {
        Throwable causa = error;
        while (causa instanceof CompletionException && causa.getCause() != null) {
            causa = causa.getCause();
        }
        return causa;
    }
}
