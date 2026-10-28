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
import recursos.CargadorRecursos;

// Mostramos el video de entrada con JavaFX dentro de la ventana Swing

public class PantallaIntro extends JPanel {

   
    private MediaPlayer player;

   
    private final Runnable alTerminar;
  
    private final AtomicBoolean introTerminada = new AtomicBoolean(false);
    private final AtomicBoolean alternativaMostrada = new AtomicBoolean(false);

    
    public PantallaIntro() {
        this(null);
    }

    
    public PantallaIntro(Runnable alTerminar) {

        this.alTerminar = alTerminar;
    
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke("ENTER"), "saltarIntro");

        getActionMap().put("saltarIntro", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                terminar();
            }
        });
       
        setPreferredSize(new Dimension(800, 600));
        setLayout(new BorderLayout());
        setBackground(java.awt.Color.BLACK);

        Platform.setImplicitExit(false);

        JFXPanel panelVideo = new JFXPanel();

        // Agregar el panel de video al centro de la pantalla.
        add(panelVideo, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> Platform.runLater(() -> {

            java.net.URL urlVideo;
            try {
                urlVideo = CargadorRecursos.obtenerUrlRecurso(
                    "assets/intro_concepto_final/Intro_Final.mp4"
                );
            } catch (IllegalArgumentException error) {
                mostrarAlternativa(panelVideo, error.getMessage());
                return;
            }

            try {
                Media media = new Media(urlVideo.toExternalForm());
                media.setOnError(() -> mostrarAlternativa(panelVideo,
                    "No se pudo leer el video: " + media.getError()));

                MediaPlayer nuevoPlayer = new MediaPlayer(media);
                player = nuevoPlayer;
                nuevoPlayer.setOnError(() -> mostrarAlternativa(panelVideo,
                    "No se pudo reproducir el video: " + nuevoPlayer.getError()));

                MediaView vistaVideo = new MediaView(nuevoPlayer);

                StackPane raiz = new StackPane(vistaVideo);
                raiz.setStyle("-fx-background-color: black;");


                vistaVideo.fitWidthProperty().bind(raiz.widthProperty());
                vistaVideo.fitHeightProperty().bind(raiz.heightProperty());
                vistaVideo.setPreserveRatio(true);

                panelVideo.setScene(new Scene(raiz, Color.BLACK));

                nuevoPlayer.setOnEndOfMedia(this::terminar);

                nuevoPlayer.setOnReady(nuevoPlayer::play);
            } catch (RuntimeException error) {
                mostrarAlternativa(panelVideo,
                    "No se pudo iniciar el reproductor: " + error.getMessage());
            }
        }));
    }

    private void mostrarAlternativa(JFXPanel panelVideo, String motivo) {
        if (introTerminada.get() || !alternativaMostrada.compareAndSet(false, true)) {
            return;
        }

        System.err.println("Error del reproductor: " + motivo);

        if (player != null) {
            player.dispose();
            player = null;
        }

        java.net.URL urlGif;
        try {
            urlGif = CargadorRecursos.obtenerUrlRecurso(
                "assets/intro_concepto_final/concepto_inicio_juego_10s.gif"
            );
        } catch (IllegalArgumentException error) {
            System.err.println("No se encontró el GIF alternativo: " + error.getMessage());
            terminar();
            return;
        }
        SwingUtilities.invokeLater(() -> {
            remove(panelVideo);
            JLabel imagen = new JLabel(new ImageIcon(urlGif));
            imagen.setHorizontalAlignment(SwingConstants.CENTER);
            imagen.setVerticalAlignment(SwingConstants.CENTER);
            add(imagen, BorderLayout.CENTER);
            revalidate();
            repaint();

            Timer temporizador = new Timer(10_000, evento -> terminar());
            temporizador.setRepeats(false);
            temporizador.start();
        });
    }

    public void terminar() {
        if (!introTerminada.compareAndSet(false, true)) {
            return;
        }

        Platform.runLater(() -> {
            if (player != null) {
                player.stop();
                player.dispose();
                player = null;
            }
        });

        if (alTerminar != null) {
            SwingUtilities.invokeLater(alTerminar);
        }
    }
}
