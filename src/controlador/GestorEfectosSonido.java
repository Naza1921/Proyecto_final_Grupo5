package controlador;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;
import controlador.utilz.loadsave;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public final class GestorEfectosSonido implements AutoCloseable {
    public enum Efecto {
        MUERTE("assets/audio/death_retro.mp3"),
        HABILIDAD("assets/audio/hadoken.mp3"),
        POWER_UP("assets/audio/POWER_UP.mp3"),
        POWER_DOWN("assets/audio/power_down.mp3"),
        SALTO("assets/audio/salto.mp3"),
        MONEDA("assets/audio/sonido_moneda.wav"),
        NIVEL_COMPLETADO("assets/audio/win_level.mp3");

        private final String ruta;

        Efecto(String ruta) {
            this.ruta = ruta;
        }
    }

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

    public void reproducir(Efecto efecto) {
        if (efecto == null) {
            throw new IllegalArgumentException("Se requiere un efecto de sonido");
        }
        Platform.runLater(() -> reproducirEnJavaFx(efecto));
    }

    private void reproducirEnJavaFx(Efecto efecto) {
        if (cerrado) {
            return;
        }
        try {
            URL recurso = loadsave.obtenerURLRecurso(efecto.ruta);
            Media media = new Media(recurso.toExternalForm());
            MediaPlayer reproductor = new MediaPlayer(media);
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

    private void notificarError(Efecto efecto, Throwable error) {
        String mensaje = "No se pudo reproducir el efecto " + efecto + ": "
            + (error == null || error.getMessage() == null
                ? "error multimedia desconocido" : error.getMessage());
        System.err.println(mensaje);
        SwingUtilities.invokeLater(() -> notificadorError.accept(mensaje));
    }

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
