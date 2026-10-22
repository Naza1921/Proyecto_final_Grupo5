package controlador;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

import javax.swing.SwingUtilities;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public final class GestorMusica implements ReproductorMusica {

    private final JFXPanel inicializadorJavaFx = new JFXPanel();
    private final Consumer<String> notificadorError;

    private volatile MediaPlayer reproductorActual;
    private volatile boolean musicaActivada = true;
    private volatile boolean cerrado;
    private double volumen = 0.5;

    public GestorMusica(Consumer<String> notificadorError) {
        this.notificadorError = notificadorError;
        Platform.runLater(() -> Platform.setImplicitExit(false));
    }

    public void seleccionarCancion(Path archivo) throws IOException {
        Path ruta = archivo.isAbsolute()
                ? archivo.toAbsolutePath().normalize()
                : Path.of(System.getProperty("user.dir")).resolve(archivo).toAbsolutePath().normalize();

        if (!Files.isRegularFile(ruta)) {
            throw new IOException("No se encontró el archivo de música: " + ruta);
        }

        Platform.runLater(() -> iniciarReproduccion(ruta));
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

    public void setMusicaActivada(boolean activada) {
        musicaActivada = activada;
        Platform.runLater(() -> {
            MediaPlayer reproductor = reproductorActual;
            if (reproductor == null) {
                return;
            }

            if (musicaActivada) {
                reproductor.play();
            } else {
                reproductor.pause();
            }
        });
    }

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

    private void iniciarReproduccion(Path ruta) {
        if (cerrado) {
            return;
        }

        try {
            Media media = new Media(ruta.toUri().toString());
            MediaPlayer nuevoReproductor = new MediaPlayer(media);
            nuevoReproductor.setCycleCount(MediaPlayer.INDEFINITE);
            nuevoReproductor.setVolume(volumen);
            media.setOnError(() -> notificarError(ruta, media.getError()));
            nuevoReproductor.setOnError(() -> notificarError(ruta, nuevoReproductor.getError()));
            nuevoReproductor.setOnReady(() -> {
                if (reproductorActual == nuevoReproductor && musicaActivada) {
                    nuevoReproductor.play();
                }
            });

            MediaPlayer reproductorAnterior = reproductorActual;
            reproductorActual = nuevoReproductor;
            if (reproductorAnterior != null) {
                reproductorAnterior.stop();
                reproductorAnterior.dispose();
            }

            if (musicaActivada) {
                nuevoReproductor.play();
            }
        } catch (RuntimeException error) {
            notificarError(ruta, error);
        }
    }

    private void notificarError(Path ruta, Throwable error) {
        String mensaje = "No se pudo reproducir la canción:\n" + ruta;
        if (error != null && error.getMessage() != null) {
            mensaje += "\n\n" + error.getMessage();
        }

        System.err.println(mensaje);
        if (error != null) {
            error.printStackTrace();
        }
        String mensajeFinal = mensaje;
        SwingUtilities.invokeLater(() -> notificadorError.accept(mensajeFinal));
    }

    @Override
    public void close() {
        cerrado = true;
        detener();
    }
}
