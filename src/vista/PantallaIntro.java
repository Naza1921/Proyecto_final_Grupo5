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
import java.util.concurrent.atomic.AtomicBoolean;

// Mostramos el video de entrada con JavaFX dentro de la ventana Swing
// y avisamos al controlador cuando termina o no se puede reproducir.
public class PantallaIntro extends JPanel {

    // Reproductor del video para evitar que se cierre mientras está activo.
    private MediaPlayer player;

    // Acción a ejecutar cuando termina el video (por ejemplo, pasar al menú). Puede ser null.
    private final Runnable alTerminar;
    // Las señales pueden llegar desde Swing y JavaFX; estas banderas evitan repetir cambios de pantalla.
    private final AtomicBoolean introTerminada = new AtomicBoolean(false);
    private final AtomicBoolean alternativaMostrada = new AtomicBoolean(false);

    // Constructor sin acción final: se mantiene por compatibilidad con el código existente.
    public PantallaIntro() {
        this(null);
    }

    // Constructor de la pantalla de introducción.
    public PantallaIntro(Runnable alTerminar) {

        this.alTerminar = alTerminar;

        // Configuración del panel de introducción.
        setPreferredSize(new Dimension(800, 600));
        setLayout(new BorderLayout());
        setBackground(java.awt.Color.BLACK);

        // Evitar que JavaFX se cierre solo al cambiar de pantalla.
        Platform.setImplicitExit(false);

        // Panel que permite insertar un reproductor JavaFX dentro de Swing.
        JFXPanel panelVideo = new JFXPanel();

        // Agregar el panel de video al centro de la pantalla.
        add(panelVideo, BorderLayout.CENTER);

        // Esperamos a que Swing muestre la ventana para que JavaFX no arranque el video antes de tiempo.
        SwingUtilities.invokeLater(() -> Platform.runLater(() -> {

            // Cargar el video MP4 desde los recursos del proyecto.
            java.net.URL urlVideo = getClass().getResource(
                "/assets/intro_concepto_final/concepto_inicio_juego_10s_con_musica.mp4"
            );

            // Verificar si se encontró el video y manejar el error.
            if (urlVideo == null) {
                System.err.println(
                    "No se encontró el MP4 en "
                        + "/assets/intro_concepto_final/concepto_inicio_juego_10s_con_musica.mp4"
                );
                // Si no hay video, continuar con el juego para no dejar la pantalla negra.
                terminar();
                return;
            }

            try {
                // Crear el recurso multimedia a partir de la URL del video.
                Media media = new Media(urlVideo.toExternalForm());
                media.setOnError(() -> mostrarAlternativa(panelVideo,
                    "No se pudo leer el video: " + media.getError()));

                // Crear el reproductor (se guarda en el campo para poder liberarlo después).
                MediaPlayer nuevoPlayer = new MediaPlayer(media);
                player = nuevoPlayer;
                nuevoPlayer.setOnError(() -> mostrarAlternativa(panelVideo,
                    "No se pudo reproducir el video: " + nuevoPlayer.getError()));

                // Crear la vista que mostrará el video.
                MediaView vistaVideo = new MediaView(nuevoPlayer);

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

                // Al terminar el video, liberar recursos y continuar con el juego.
                nuevoPlayer.setOnEndOfMedia(this::terminar);

                // Empezar cuando JavaFX ya terminó de preparar el video.
                nuevoPlayer.setOnReady(nuevoPlayer::play);
            } catch (RuntimeException error) {
                mostrarAlternativa(panelVideo,
                    "No se pudo iniciar el reproductor: " + error.getMessage());
            }
        }));
    }

    // Si JavaFX no puede leer o reproducir el MP4, mostramos el GIF incluido y luego seguimos al menú.
    private void mostrarAlternativa(JFXPanel panelVideo, String motivo) {
        if (introTerminada.get() || !alternativaMostrada.compareAndSet(false, true)) {
            return;
        }

        System.err.println("Error del reproductor: " + motivo);

        if (player != null) {
            player.dispose();
            player = null;
        }

        java.net.URL urlGif = getClass().getResource(
            "/assets/intro_concepto_final/concepto_inicio_juego_10s.gif"
        );
        SwingUtilities.invokeLater(() -> {
            if (urlGif == null) {
                System.err.println("No se encontró el GIF alternativo de la introducción.");
                terminar();
                return;
            }

            remove(panelVideo);
            JLabel imagen = new JLabel(new ImageIcon(urlGif));
            imagen.setHorizontalAlignment(SwingConstants.CENTER);
            imagen.setVerticalAlignment(SwingConstants.CENTER);
            add(imagen, BorderLayout.CENTER);
            revalidate();
            repaint();

            // La alternativa GIF es muda; tras diez segundos continúa al menú.
            Timer temporizador = new Timer(10_000, evento -> terminar());
            temporizador.setRepeats(false);
            temporizador.start();
        });
    }

    // Detiene el video, libera los recursos y ejecuta la acción final en Swing.
    // La bandera evita repetir la navegación si coinciden dos señales de finalización.
    public void terminar() {
        if (!introTerminada.compareAndSet(false, true)) {
            return;
        }

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
