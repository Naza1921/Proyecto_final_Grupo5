package controlador;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.function.Consumer;
import javax.swing.JFrame;
import modelos.PreferenciasAjustes;
import servicios.RepositorioAjustes;
import servicios.audio.ReproductorMusica;
import vista.MenuAjustesVista;

public final class ControladorMenuAjustes {
    private final JFrame ventana;
    private final MenuAjustesVista vista;
    private final ReproductorMusica reproductorMusica;
    private final RepositorioAjustes repositorioAjustes;
    private final Consumer<String> notificadorError;
    private final Consumer<String> notificadorExito;
    private final Runnable alVolver;

    private Rectangle limitesVentana;
    private Dimension tamanoMinimoVentana;
    private GraphicsConfiguration configuracionVentana;

    public ControladorMenuAjustes(
            JFrame ventana,
            MenuAjustesVista vista,
            ReproductorMusica reproductorMusica,
            RepositorioAjustes repositorioAjustes,
            Consumer<String> notificadorError,
            Consumer<String> notificadorExito,
            Runnable alVolver) {
        this.ventana = ventana;
        this.vista = vista;
        this.reproductorMusica = reproductorMusica;
        this.repositorioAjustes = repositorioAjustes;
        this.notificadorError = notificadorError;
        this.notificadorExito = notificadorExito;
        this.alVolver = alVolver;
    }

    public void iniciar() {
    vista.addPantallaCompletaListener(
            activar -> cambiarPantallaCompleta(activar)
        );
        vista.addMusicaListener(
            activada -> reproductorMusica.setMusicaActivada(activada)
        );
        vista.addCancionListener(
            numeroCancion -> seleccionarCancion(numeroCancion)
        );
        vista.addVolumenListener(
            volumen -> reproductorMusica.setVolumen(volumen)
        );
        vista.addGuardarListener(
            () -> guardarPreferencias()
        );
        vista.addRestaurarListener(
            () -> restaurarPreferenciasEstandar()
        );
        vista.addVolverListener(
            () -> alVolver.run()
        );
        cargarPreferencias();
    }

    private void cargarPreferencias() {
        PreferenciasAjustes preferencias;
        try {
            preferencias = repositorioAjustes.cargar();
        } catch (IllegalArgumentException error) {
            notificadorError.accept("Las preferencias guardadas no son válidas. Se usarán los valores estándar.");
            preferencias = PreferenciasAjustes.ESTANDAR;
        }
        aplicarPreferencias(preferencias);
    }

    private void guardarPreferencias() {
        try {
            repositorioAjustes.guardar(vista.obtenerPreferencias());
            notificadorExito.accept("Los cambios se guardaron correctamente.");
        } catch (IOException error) {
            notificadorError.accept(error.getMessage());
        }
    }

    private void restaurarPreferenciasEstandar() {
        PreferenciasAjustes estandar = PreferenciasAjustes.ESTANDAR;
        aplicarPreferencias(estandar);
        try {
            repositorioAjustes.guardar(estandar);
            notificadorExito.accept("Se restauró y guardó la configuración estándar.");
        } catch (IOException error) {
            notificadorError.accept(error.getMessage());
        }
    }

    private void aplicarPreferencias(PreferenciasAjustes preferencias) {
        vista.aplicarPreferencias(preferencias);
        if (preferencias.cancionSeleccionada() == 0) {
            reproductorMusica.detener();
        } else {
            seleccionarCancion(preferencias.cancionSeleccionada());
        }
    }

    private void seleccionarCancion(int numeroCancion) {
        try {
            reproductorMusica.seleccionarCancion(numeroCancion);
        } catch (IOException error) {
            notificadorError.accept(error.getMessage());
        }
    }

    private void cambiarPantallaCompleta(boolean activar) {
        if (activar == (limitesVentana != null)) {
            return;
        }

        if (activar) {
            limitesVentana = ventana.getBounds();
            tamanoMinimoVentana = ventana.getMinimumSize();
            configuracionVentana = ventana.getGraphicsConfiguration();

            ventana.dispose();
            ventana.setUndecorated(true);
            ventana.setMinimumSize(new Dimension(0, 0));
            ventana.setBounds(configuracionVentana.getBounds());
        } else {
            ventana.dispose();
            ventana.setUndecorated(false);
            ventana.setMinimumSize(tamanoMinimoVentana);
            ventana.setBounds(limitesVentana);

            limitesVentana = null;
            tamanoMinimoVentana = null;
            configuracionVentana = null;
        }

        ventana.setVisible(true);
    }
}
