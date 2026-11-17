package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import recursos.CargadorRecursos;

public final class PanelCargaPartida extends JPanel {
    private final BufferedImage fondo =
        CargadorRecursos.cargarImagen("assets/escenarios/fondo de carga.png");
    private final JLabel archivoActual = new JLabel("", SwingConstants.CENTER);
    private final JLabel tiempoTranscurrido =
        new JLabel("Tiempo transcurrido: 0,0 s", SwingConstants.CENTER);
    private final Timer temporizador;
    private long inicioCargaNanos;

    public PanelCargaPartida() {
        setOpaque(false);
        setPreferredSize(new Dimension(800, 600));
        setBorder(BorderFactory.createEmptyBorder(0, 40, 40, 40));
        setLayout(new BorderLayout());
        archivoActual.setForeground(Color.WHITE);
        archivoActual.setFont(new Font("Monospaced", Font.BOLD, 15));
        tiempoTranscurrido.setForeground(Color.WHITE);
        tiempoTranscurrido.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JPanel informacionCarga = new JPanel(new java.awt.GridLayout(2, 1, 0, 4));
        informacionCarga.setOpaque(false);
        informacionCarga.add(archivoActual);
        informacionCarga.add(tiempoTranscurrido);
        add(informacionCarga, BorderLayout.SOUTH);

        temporizador = new Timer(100, evento -> actualizarTiempo());
        temporizador.setCoalesce(true);
    }

    public void iniciar() {
        inicioCargaNanos = System.nanoTime();
        actualizarTiempo();
        temporizador.start();
    }

    public void detener() {
        temporizador.stop();
    }

    public void actualizar(String archivo) {
        if (archivo == null || archivo.isBlank()) {
            throw new IllegalArgumentException("Se requiere el archivo que se está cargando");
        }
        archivoActual.setText("<html><div style='width: 560px; text-align: center;'>"
            + archivo + "</div></html>");
    }

    private void actualizarTiempo() {
        long transcurridoNanos = Math.max(0, System.nanoTime() - inicioCargaNanos);
        tiempoTranscurrido.setText(String.format(
            "Tiempo transcurrido: %.1f s", transcurridoNanos / 1_000_000_000.0
        ));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        double escala = Math.max(
            (double) getWidth() / fondo.getWidth(),
            (double) getHeight() / fondo.getHeight()
        );
        int ancho = (int) Math.round(fondo.getWidth() * escala);
        int alto = (int) Math.round(fondo.getHeight() * escala);
        int x = (getWidth() - ancho) / 2;
        int y = (getHeight() - alto) / 2;
        g2.drawImage(fondo, x, y, ancho, alto, this);
        g2.dispose();
    }
}
