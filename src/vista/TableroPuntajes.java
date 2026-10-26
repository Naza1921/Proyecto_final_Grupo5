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
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import controlador.utilz.loadsave;
import persistencia.ResultadoPuntaje;

public final class TableroPuntajes extends JPanel {
    private static final int ANCHO_DISENO = 900;
    private static final int ALTO_DISENO = 600;
    private static final int CANTIDAD_FILAS = 10;
    private static final Color COLOR_DORADO = new Color(255, 204, 91);
    private static final Color COLOR_TEXTO = new Color(255, 235, 190);

    private final java.awt.image.BufferedImage fondo;
    private final Font fuentePuntajes;
    private final JButton botonVolver;
    private List<ResultadoPuntaje> resultados = List.of();

    public TableroPuntajes(Runnable alVolver) {
        if (alVolver == null) {
            throw new IllegalArgumentException("Se requiere una acción para volver al menú");
        }
        fondo = cargarImagen("/assets/tablero/fondoV2.png");
        fuentePuntajes = cargarFuente("/assets/fonts/Cinzel/Cinzel-Bold.ttf");

        setPreferredSize(new Dimension(900, 600));
        setLayout(null);
        botonVolver = new JButton("Volver al menú");
        botonVolver.setFont(fuentePuntajes.deriveFont(14f));
        botonVolver.setForeground(COLOR_TEXTO);
        botonVolver.setBackground(new Color(5, 43, 34));
        botonVolver.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_DORADO, 2),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        botonVolver.setFocusPainted(false);
        botonVolver.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
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
        double escala = Math.min(
            (double) getWidth() / ANCHO_DISENO,
            (double) getHeight() / ALTO_DISENO
        );
        int ancho = (int) Math.round(184 * escala);
        int alto = (int) Math.round(40 * escala);
        int desplazamientoX = (getWidth() - (int) (ANCHO_DISENO * escala)) / 2;
        int desplazamientoY = (getHeight() - (int) (ALTO_DISENO * escala)) / 2;
        botonVolver.setFont(fuentePuntajes.deriveFont((float) (14 * escala)));
        botonVolver.setBounds(
            desplazamientoX + (int) (18 * escala),
            desplazamientoY + (int) (18 * escala),
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
            RenderingHints.VALUE_INTERPOLATION_BICUBIC
        );
        double escala = Math.min(
            (double) getWidth() / ANCHO_DISENO,
            (double) getHeight() / ALTO_DISENO
        );
        int desplazamientoX = (getWidth() - (int) (ANCHO_DISENO * escala)) / 2;
        int desplazamientoY = (getHeight() - (int) (ALTO_DISENO * escala)) / 2;
        g.setColor(new Color(10, 17, 31));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.drawImage(fondo, desplazamientoX, desplazamientoY,
            (int) (ANCHO_DISENO * escala), (int) (ALTO_DISENO * escala), this);
        dibujarResultados(g, desplazamientoX, desplazamientoY, escala);
        g.dispose();
    }

    private void dibujarResultados(Graphics2D g, int desplazamientoX,
                                    int desplazamientoY, double escala) {
        int x = desplazamientoX + (int) (215 * escala);
        int ancho = (int) (470 * escala);

        Font fuente = fuentePuntajes.deriveFont((float) (16 * escala));
        g.setFont(fuente);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        FontMetrics metricas = g.getFontMetrics();

        g.setColor(COLOR_DORADO);
        g.setFont(fuentePuntajes.deriveFont((float) (18 * escala)));
        dibujarCentrado(g, "TABLA DE PUNTAJES",
            desplazamientoX + (int) (450 * escala),
            desplazamientoY + (int) (158 * escala), g.getFontMetrics());

        Font fuenteEncabezado = fuentePuntajes.deriveFont((float) (12 * escala));
        g.setFont(fuenteEncabezado);
        FontMetrics metricasEncabezado = g.getFontMetrics();
        int encabezado = desplazamientoY + (int) (205 * escala);
        g.setColor(COLOR_DORADO);
        dibujarCentrado(g, "PUESTO", desplazamientoX + (int) (250 * escala),
            encabezado, metricasEncabezado);
        dibujarCentrado(g, "JUGADOR", desplazamientoX + (int) (380 * escala),
            encabezado, metricasEncabezado);
        dibujarCentrado(g, "PUNTAJE", desplazamientoX + (int) (545 * escala),
            encabezado, metricasEncabezado);
        dibujarCentrado(g, "TIEMPO", desplazamientoX + (int) (652 * escala),
            encabezado, metricasEncabezado);

        int inicioFilas = desplazamientoY + (int) (230 * escala);
        int separacionFila = (int) (23 * escala);
        for (int i = 0; i < resultados.size(); i++) {
            ResultadoPuntaje resultado = resultados.get(i);
            int linea = inicioFilas + i * separacionFila;
            int altoFila = (int) (20 * escala);
            g.setColor(i == 0
                ? new Color(164, 98, 10, 150)
                : new Color(7, 53, 42, i % 2 == 0 ? 105 : 65));
            g.fillRoundRect(x + (int) (5 * escala), linea - (int) (16 * escala),
                ancho - (int) (10 * escala), altoFila, altoFila, altoFila);

            g.setColor(new Color(222, 164, 55, 105));
            g.drawLine(x + (int) (10 * escala), linea + (int) (5 * escala),
                x + ancho - (int) (10 * escala), linea + (int) (5 * escala));

            g.setColor(i == 0 ? COLOR_DORADO : COLOR_TEXTO);
            String puesto = Integer.toString(i + 1);
            dibujarCentrado(g, puesto, desplazamientoX + (int) (250 * escala), linea,
                metricas);
            String nombre = ajustarNombre(resultado.getNombre(), metricas,
                desplazamientoX + (int) (290 * escala),
                desplazamientoX + (int) (475 * escala));
            g.drawString(nombre, desplazamientoX + (int) (290 * escala), linea);
            dibujarDerecha(g, Integer.toString(resultado.getPuntuacion()),
                desplazamientoX + (int) (590 * escala), linea, metricas);
            dibujarDerecha(g, formatearTiempo(resultado.getTiempoSegundos()),
                desplazamientoX + (int) (675 * escala), linea, metricas);
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
        String rutaRecurso = recurso.startsWith("/") ? recurso.substring(1) : recurso;
        return loadsave.GetSpriteatlas(rutaRecurso);
    }

    private static Font cargarFuente(String recurso) {
        try (InputStream entrada = loadsave.obtenerURLRecurso(recurso).openStream()) {
            return Font.createFont(Font.TRUETYPE_FONT, entrada);
        } catch (FontFormatException | IOException excepcion) {
            throw new IllegalStateException("No se pudo cargar la fuente " + recurso, excepcion);
        }
    }
}
