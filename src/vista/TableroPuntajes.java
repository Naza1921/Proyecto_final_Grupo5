package vista;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import persistencia.ResultadoPuntaje;

public final class TableroPuntajes extends JPanel {
    private static final int ANCHO_ARTE = 1152;
    private static final int ALTO_ARTE = 768;
    private static final int CANTIDAD_FILAS = 10;
    private static final int[] LINEAS_BASE = {
        403, 437, 471, 505, 539, 573, 607, 641, 675, 709
    };
    private static final Color COLOR_PRIMER_PUESTO = new Color(255, 190, 66);
    private static final Color COLOR_PUNTAJE = new Color(229, 218, 211);

    private final java.awt.image.BufferedImage fondo;
    private final java.awt.image.BufferedImage plantilla;
    private final Font fuentePuntajes;
    private final JButton botonVolver;
    private List<ResultadoPuntaje> resultados = List.of();

    public TableroPuntajes(Runnable alVolver) {
        if (alVolver == null) {
            throw new IllegalArgumentException("Se requiere una acción para volver al menú");
        }
        fondo = cargarImagen("/assets/tablero/fondo.png");
        plantilla = cargarImagen("/assets/tablero/tabla-puntajes.png");
        fuentePuntajes = cargarFuente("/assets/fonts/Press_Start_2P/PressStart2P-Regular.ttf");

        setPreferredSize(new Dimension(800, 600));
        setLayout(null);
        botonVolver = new BotonEstilizado("Volver al menú");
        botonVolver.setFont(fuentePuntajes.deriveFont(Font.PLAIN, 10f));
        botonVolver.setPreferredSize(new Dimension(210, 42));
        botonVolver.addActionListener(evento -> alVolver.run());
        add(botonVolver);

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "volver");
        getActionMap().put("volver", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evento) {
                alVolver.run();
            }
        });
    }

    public void mostrarResultados(List<ResultadoPuntaje> resultados) {
        if (resultados == null || resultados.size() > CANTIDAD_FILAS
                || resultados.stream().anyMatch(resultado -> resultado == null)) {
            throw new IllegalArgumentException("La lista de puntajes no es válida");
        }
        this.resultados = List.copyOf(resultados);
        repaint();
    }

    @Override
    public void doLayout() {
        int ancho = Math.min(210, Math.max(150, getWidth() / 4));
        int alto = Math.min(42, Math.max(32, getHeight() / 14));
        botonVolver.setBounds(
            Math.max(8, getWidth() - ancho - 18),
            12,
            ancho,
            alto
        );
    }

    @Override
    protected void paintComponent(Graphics grafico) {
        super.paintComponent(grafico);
        Graphics2D g = (Graphics2D) grafico.create();
        g.setRenderingHint(
            RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
        );
        g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
        g.drawImage(plantilla, 0, 0, getWidth(), getHeight(), this);
        dibujarResultados(g);
        g.dispose();
    }

    private void dibujarResultados(Graphics2D g) {
        double escalaX = (double) getWidth() / ANCHO_ARTE;
        double escalaY = (double) getHeight() / ALTO_ARTE;
        float tamanoFuente = (float) (16 * Math.min(escalaX, escalaY));
        Font fuente = fuentePuntajes.deriveFont(Math.max(8f, tamanoFuente));
        g.setFont(fuente);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        FontMetrics metricas = g.getFontMetrics();

        for (int i = 0; i < resultados.size(); i++) {
            ResultadoPuntaje resultado = resultados.get(i);
            g.setColor(i == 0 ? COLOR_PRIMER_PUESTO : COLOR_PUNTAJE);
            int linea = (int) Math.round(LINEAS_BASE[i] * escalaY);

            dibujarCentrado(g, Integer.toString(i + 1), (int) (257 * escalaX), linea, metricas);
            String nombre = ajustarNombre(resultado.getNombre(), metricas,
                (int) (315 * escalaX), (int) (601 * escalaX));
            g.drawString(nombre, (int) (315 * escalaX), linea);
            dibujarDerecha(g, Integer.toString(resultado.getPuntuacion()),
                (int) (780 * escalaX), linea, metricas);
            dibujarDerecha(g, formatearTiempo(resultado.getTiempoSegundos()),
                (int) (927 * escalaX), linea, metricas);
        }
    }

    private static void dibujarCentrado(Graphics2D g, String texto, int centroX,
                                         int linea, FontMetrics metricas) {
        g.drawString(texto, centroX - metricas.stringWidth(texto) / 2, linea);
    }

    private static void dibujarDerecha(Graphics2D g, String texto, int derechaX,
                                        int linea, FontMetrics metricas) {
        g.drawString(texto, derechaX - metricas.stringWidth(texto), linea);
    }

    private static String ajustarNombre(String nombre, FontMetrics metricas,
                                         int xInicial, int xFinal) {
        String resultado = nombre;
        int anchoDisponible = xFinal - xInicial;
        String sufijo = "...";
        int anchoConSufijo = anchoDisponible - metricas.stringWidth(sufijo);
        if (metricas.stringWidth(resultado) <= anchoDisponible) {
            return resultado;
        }
        while (!resultado.isEmpty() && metricas.stringWidth(resultado) > anchoConSufijo) {
            resultado = resultado.substring(0, resultado.length() - 1);
        }
        return resultado + sufijo;
    }

    private static String formatearTiempo(long segundos) {
        long minutos = segundos / 60;
        long segundosRestantes = segundos % 60;
        return String.format(Locale.ROOT, "%02d:%02d", minutos, segundosRestantes);
    }

    private static java.awt.image.BufferedImage cargarImagen(String recurso) {
        try (InputStream entrada = TableroPuntajes.class.getResourceAsStream(recurso)) {
            if (entrada == null) {
                throw new IllegalStateException("No se encontró el recurso " + recurso);
            }
            java.awt.image.BufferedImage imagen = ImageIO.read(entrada);
            if (imagen == null) {
                throw new IllegalStateException("No se pudo leer la imagen " + recurso);
            }
            return imagen;
        } catch (IOException excepcion) {
            throw new IllegalStateException("No se pudo cargar el recurso " + recurso, excepcion);
        }
    }

    private static Font cargarFuente(String recurso) {
        try (InputStream entrada = TableroPuntajes.class.getResourceAsStream(recurso)) {
            if (entrada == null) {
                throw new IllegalStateException("No se encontró la fuente " + recurso);
            }
            return Font.createFont(Font.TRUETYPE_FONT, entrada);
        } catch (FontFormatException | IOException excepcion) {
            throw new IllegalStateException("No se pudo cargar la fuente " + recurso, excepcion);
        }
    }
}
