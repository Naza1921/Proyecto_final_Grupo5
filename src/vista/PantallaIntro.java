package vista;

import javax.swing.*;
import java.awt.*;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;

// Clase que representa la pantalla de introducción del juego,
// mostrando un video MP4 animado con música.
public class PantallaIntro extends JPanel {

    // Reproductor del video para evitar que se cierre mientras está activo.
    private MediaPlayer player;

    // Acción a ejecutar cuando termina el video (por ejemplo, pasar al menú). Puede ser null.
    private final Runnable alTerminar;

    // Constructor sin acción final: se mantiene por compatibilidad con el código existente.
    public PantallaIntro() {
        this(null);
    }

    // Constructor de la pantalla de introducción.
    public PantallaIntro(Runnable alTerminar) {

        this.alTerminar = alTerminar;

        // Configuración del panel de introducción.
        setPreferredSize(new Dimension(680, 480));
        setLayout(new BorderLayout());
        setBackground(java.awt.Color.BLACK);

        // Evitar que JavaFX se cierre solo al cambiar de pantalla.
        Platform.setImplicitExit(false);

        // Panel que permite insertar un reproductor JavaFX dentro de Swing.
        JFXPanel panelVideo = new JFXPanel();

        // Agregar el panel de video al centro de la pantalla.
        add(panelVideo, BorderLayout.CENTER);

        // JavaFX debe crear y reproducir el video en su propio hilo.
        Platform.runLater(() -> {

            // Cargar el video MP4 desde los recursos del proyecto.
            java.net.URL urlVideo = getClass().getResource(
                "/assets/concepto_inicio_juego_10s_con_musica.mp4"
            );

            // Verificar si se encontró el video y manejar el error.
            if (urlVideo == null) {
                System.err.println(
                    "No se encontró el MP4 en /assets/concepto_inicio_juego_10s_con_musica.mp4"
                );
                // Si no hay video, continuar con el juego para no dejar la pantalla negra.
                terminar();
                return;
            }

            // Crear el recurso multimedia a partir de la URL del video.
            Media media = new Media(urlVideo.toExternalForm());

            // Crear el reproductor (se guarda en el campo para poder liberarlo después).
            player = new MediaPlayer(media);

            // Crear la vista que mostrará el video.
            MediaView vistaVideo = new MediaView(player);

            // Contenedor con fondo negro que centra el video.
            StackPane raiz = new StackPane(vistaVideo);
            raiz.setStyle("-fx-background-color: black;");

            // Ajustar el video al tamaño de la pantalla de introducción,
            // acompañando los cambios de tamaño de la ventana.
            vistaVideo.fitWidthProperty().bind(raiz.widthProperty());
            vistaVideo.fitHeightProperty().bind(raiz.heightProperty());
            vistaVideo.setPreserveRatio(true);

            // Crear la escena de JavaFX y asignarla al panel Swing.
            panelVideo.setScene(new Scene(raiz, Color.BLACK));

            // Si el reproductor falla (códec no soportado, etc.), mostrar el error y continuar.
            player.setOnError(() -> {
                System.err.println("Error del reproductor: " + player.getError());
                terminar();
            });

            // Al terminar el video, liberar recursos y continuar con el juego.
            player.setOnEndOfMedia(this::terminar);

            // Iniciar el video y la música.
            player.play();
        });
    }

    // Detiene el video, libera los recursos y ejecuta la acción final (en el hilo de Swing).
    // También se puede llamar desde afuera para saltar la introducción.
    public void terminar() {

        // Liberar el reproductor en el hilo de JavaFX.
        Platform.runLater(() -> {
            if (player != null) {
                player.stop();
                player.dispose();
                player = null;
            }
        });

        // Ejecutar la acción final en el hilo de Swing.
        if (alTerminar != null) {
            SwingUtilities.invokeLater(alTerminar);
        }
    }
}
