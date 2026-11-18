package servicios.audio;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import recursos.CargadorRecursos;

public final class ReproductorMusicaJavaFx implements ReproductorMusica {
    private static final Map<Integer, String> CANCIONES = Map.of(
        1, "assets/audio/alex-morgan-video-game-pixel-chiptune-music-583271.mp3",
        2, "assets/audio/maksymmalko-game-game-music-581017.mp3",
        3, "assets/audio/MCZ STH2HD.mpeg",
        4, "assets/audio/MM MHX AA.mpeg",
        5, "assets/audio/MM3 - Snake.mpeg"
    );

    private final JFXPanel inicializadorJavaFx = new JFXPanel();
    private final Consumer<String> notificadorError;

    private volatile MediaPlayer reproductorActual;
    private volatile boolean musicaActivada = true;
    private volatile boolean reproduccionHabilitada;
    private volatile boolean cerrado;
    private double volumen = 0.5;

    public ReproductorMusicaJavaFx(Consumer<String> notificadorError) {
        if (notificadorError == null) {
            throw new IllegalArgumentException("Se requiere un notificador de errores");
        }
        this.notificadorError = notificadorError;
        Platform.runLater(() -> Platform.setImplicitExit(false));
    }

    @Override
    public void seleccionarCancion(int numeroCancion) throws IOException {
        String recurso = CANCIONES.get(numeroCancion);
        if (recurso == null) {
            throw new IOException("La canción seleccionada no existe: " + numeroCancion);
        }
        try {
            URL url = CargadorRecursos.obtenerUrlRecurso(recurso);
            String urlRecurso = url.toExternalForm();
            Platform.runLater(() -> iniciarReproduccion(urlRecurso));
        } catch (IllegalArgumentException error) {
            throw new IOException("No se pudo localizar la canción: " + recurso, error);
        }
    }

    @Override
    public void habilitarReproduccion() {
        reproduccionHabilitada = true;
        Platform.runLater(() -> {
            MediaPlayer reproductor = reproductorActual;
            if (reproductor != null && musicaActivada) {
                reproductor.play();
            }
        });
    }

    @Override
    public void detener() {
        Platform.runLater(() -> {
            MediaPlayer reproductor = reproductorActual;
            reproductorActual = null;
            if (reproductor != null) {
                reproductor.stop();
                reproductor.dispose();
            }
        });
    }

    @Override
    public void setMusicaActivada(boolean activada) {
        musicaActivada = activada;
        Platform.runLater(() -> {
            MediaPlayer reproductor = reproductorActual;
            if (reproductor == null) {
                return;
            }

            if (musicaActivada && reproduccionHabilitada) {
                reproductor.play();
            } else {
                reproductor.pause();
            }
        });
    }

    @Override
    public void setVolumen(int porcentaje) {
        if (porcentaje < 0 || porcentaje > 100) {
            throw new IllegalArgumentException("El volumen debe estar entre 0 y 100.");
        }

        volumen = porcentaje / 100.0;
        Platform.runLater(() -> {
            MediaPlayer reproductor = reproductorActual;
            if (reproductor != null) {
                reproductor.setVolume(volumen);
            }
        });
    }

    private void iniciarReproduccion(String urlRecurso) {
        if (cerrado) {
            return;
        }

        try {
            Media media = new Media(urlRecurso);
            MediaPlayer nuevoReproductor = new MediaPlayer(media);
            nuevoReproductor.setCycleCount(MediaPlayer.INDEFINITE);
            nuevoReproductor.setVolume(volumen);
            media.setOnError(() -> notificarError(urlRecurso, media.getError()));
            nuevoReproductor.setOnError(
                () -> notificarError(urlRecurso, nuevoReproductor.getError())
            );
            nuevoReproductor.setOnReady(() -> {
                if (reproductorActual == nuevoReproductor && musicaActivada
                        && reproduccionHabilitada) {
                    nuevoReproductor.play();
                }
            });

            MediaPlayer reproductorAnterior = reproductorActual;
            reproductorActual = nuevoReproductor;
            if (reproductorAnterior != null) {
                reproductorAnterior.stop();
                reproductorAnterior.dispose();
            }

            if (musicaActivada && reproduccionHabilitada) {
                nuevoReproductor.play();
            }
        } catch (RuntimeException error) {
            notificarError(urlRecurso, error);
        }
    }

    private void notificarError(String urlRecurso, Throwable error) {
        String mensaje = "No se pudo reproducir la canción:\n" + urlRecurso;
        if (error != null && error.getMessage() != null) {
            mensaje += "\n\n" + error.getMessage();
        }

        System.err.println(mensaje);
        if (error != null) {
            error.printStackTrace();
        }
        notificadorError.accept(mensaje);
    }

    @Override
    public void close() {
        cerrado = true;
        detener();
    }
}
