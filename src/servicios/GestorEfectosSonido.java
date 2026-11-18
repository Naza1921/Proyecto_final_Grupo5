package servicios;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;
import recursos.CargadorRecursos;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public final class GestorEfectosSonido implements ServicioEfectosSonido {

    private final JFXPanel inicializadorJavaFx = new JFXPanel();
    private final Consumer<String> notificadorError;
    private final Set<MediaPlayer> reproductoresActivos = new HashSet<>();
    private boolean cerrado;

    public GestorEfectosSonido(Consumer<String> notificadorError) {
        if (notificadorError == null) {
            throw new IllegalArgumentException("Se requiere un notificador de errores");
        }
        this.notificadorError = notificadorError;
        Platform.runLater(() -> Platform.setImplicitExit(false));
    }

    public void reproducir(ServicioEfectosSonido.Efecto efecto) {
        if (efecto == null) {
            throw new IllegalArgumentException("Se requiere un efecto de sonido");
        }
        Platform.runLater(() -> reproducirEnJavaFx(efecto));
    }

    private void reproducirEnJavaFx(ServicioEfectosSonido.Efecto efecto) {
        if (cerrado) {
            return;
        }
        try {
            ConfiguracionEfecto configuracion = configuracionDe(efecto);
            URL recurso = CargadorRecursos.obtenerUrlRecurso(configuracion.ruta());
            Media media = new Media(recurso.toExternalForm());
            MediaPlayer reproductor = new MediaPlayer(media);
            reproductor.setVolume(configuracion.volumen());
            reproductoresActivos.add(reproductor);
            media.setOnError(() -> notificarError(efecto, media.getError()));
            reproductor.setOnError(() -> notificarError(efecto, reproductor.getError()));
            reproductor.setOnEndOfMedia(() -> liberar(reproductor));
            reproductor.play();
        } catch (RuntimeException error) {
            notificarError(efecto, error);
        }
    }

    private void liberar(MediaPlayer reproductor) {
        reproductoresActivos.remove(reproductor);
        reproductor.dispose();
    }

    private void notificarError(ServicioEfectosSonido.Efecto efecto, Throwable error) {
        String mensaje = "No se pudo reproducir el efecto " + efecto + ": "
            + (error == null || error.getMessage() == null
                ? "error multimedia desconocido" : error.getMessage());
        System.err.println(mensaje);
        SwingUtilities.invokeLater(() -> notificadorError.accept(mensaje));
    }

    private static ConfiguracionEfecto configuracionDe(ServicioEfectosSonido.Efecto efecto) {
        return switch (efecto) {
            case MUERTE -> new ConfiguracionEfecto("assets/audio/death_retro.mp3", 1.0);
            case HABILIDAD -> new ConfiguracionEfecto("assets/audio/hadoken.mp3", 1.0);
            case POWER_UP -> new ConfiguracionEfecto("assets/audio/POWER_UP.mp3", 1.0);
            case POWER_DOWN -> new ConfiguracionEfecto("assets/audio/power_down.mp3", 1.0);
            case SALTO -> new ConfiguracionEfecto("assets/audio/salto.mp3", 0.5);
            case MONEDA -> new ConfiguracionEfecto("assets/audio/sonido_moneda.wav", 1.0);
            case NIVEL_COMPLETADO -> new ConfiguracionEfecto("assets/audio/win_level.mp3", 1.0);
        };
    }

    private record ConfiguracionEfecto(String ruta, double volumen) { }

    @Override
    public void close() {
        Platform.runLater(() -> {
            cerrado = true;
            for (MediaPlayer reproductor : reproductoresActivos) {
                reproductor.stop();
                reproductor.dispose();
            }
            reproductoresActivos.clear();
        });
    }
}
