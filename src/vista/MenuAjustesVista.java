package vista;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.border.Border;

import modelos.PreferenciasAjustes;

public final class MenuAjustesVista extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Color COLOR_FONDO_CLARO = new Color(0xF3F5FA);
    private static final Color COLOR_TARJETA_CLARO = Color.WHITE;
    private static final Color COLOR_TEXTO_CLARO = new Color(0x20263A);
    private static final Color COLOR_SECUNDARIO_CLARO = new Color(0x697386);
    private static final Color COLOR_ACENTO_CLARO = new Color(0x536DFE);
    private static final Color COLOR_BORDE_CLARO = new Color(0xE7EAF1);
    private static final Color COLOR_FONDO_OSCURO = new Color(0x171A24);
    private static final Color COLOR_TARJETA_OSCURO = new Color(0x232837);
    private static final Color COLOR_TEXTO_OSCURO = new Color(0xF1F3FA);
    private static final Color COLOR_SECUNDARIO_OSCURO = new Color(0xAAB2C5);
    private static final Color COLOR_ACENTO_OSCURO = new Color(0x7187FF);
    private static final Color COLOR_BORDE_OSCURO = new Color(0x394153);
    private static final Color COLOR_TEMA_CLARO = new Color(0xEEF0FF);
    private static final Color COLOR_TEMA_OSCURO = new Color(0x353C52);
    private static final Color COLOR_BORDE_BOTON_CLARO = new Color(0xDCE1EC);

    private final JLabel titulo = new JLabel("Ajustes");
    private final JLabel subtitulo = new JLabel("Personaliza tu experiencia");
    private final JPanel encabezado = new JPanel();

    private final JLabel lblEncabezadoMusica = new JLabel("SONIDO");
    private final JCheckBox chkMusica = new JCheckBox("Música activada");
    private final JLabel lblElegir = new JLabel("Elegir música");
    private final JRadioButton rdbCancion1 = new JRadioButton("Canción 1");
    private final JRadioButton rdbCancion2 = new JRadioButton("Canción 2");
    private final JRadioButton rdbCancion3 = new JRadioButton("Canción 3");
    private final JRadioButton rdbCancion4 = new JRadioButton("Canción 4");
    private final JRadioButton rdbCancion5 = new JRadioButton("Canción 5");
    private final ButtonGroup grupoCanciones = new ButtonGroup();
    private final JPanel canciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 0));
    private final JPanel tarjetaMusica = new JPanel();

    private final JLabel lblEncabezadoVolumen = new JLabel("NIVEL DE AUDIO");
    private final JLabel lblVolumen = new JLabel("Volumen");
    private final JSlider volumen = new JSlider(0, 100, 50);
    private final JPanel tarjetaVolumen = new JPanel();

    private final JLabel lblEncabezadoApariencia = new JLabel("APARIENCIA");
    private final JLabel lblTema = new JLabel("Tema");
    private final JToggleButton btnTema = new JToggleButton("☀  Claro     |     🌙  Oscuro");
    private final JCheckBox chkPantallaCompleta = new JCheckBox("Pantalla completa");
    private final JPanel tarjetaApariencia = new JPanel();

    private final JButton btnRestaurar = new JButton("Restaurar estándar");
    private final JButton btnGuardar = new JButton("Guardar");
    private final JButton btnVolver = new JButton("Volver al menú principal");
    private final JPanel botones = new JPanel(new GridLayout(1, 2, 10, 0));
    private boolean temaOscuro;

    public MenuAjustesVista() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(COLOR_FONDO_CLARO);
        setBorder(BorderFactory.createEmptyBorder(20, 26, 20, 26));

        configurarEncabezado();
        configurarEstiloEncabezadosTarjetas();
        configurarTarjetaMusica();
        configurarTarjetaVolumen();
        configurarTarjetaApariencia();
        configurarBotones();
        configurarCambioTema();
        aplicarTema();

        add(encabezado);
        add(Box.createVerticalStrut(12));
        add(tarjetaMusica);
        add(Box.createVerticalStrut(12));
        add(tarjetaVolumen);
        add(Box.createVerticalStrut(12));
        add(tarjetaApariencia);
        add(Box.createVerticalStrut(12));
        add(botones);
        add(Box.createVerticalStrut(8));
        add(btnVolver);
    }

    public void addPantallaCompletaListener(Consumer<Boolean> listener) {
        chkPantallaCompleta.addItemListener(event ->
                listener.accept(event.getStateChange() == java.awt.event.ItemEvent.SELECTED));
    }

    public void addMusicaListener(Consumer<Boolean> listener) {
        chkMusica.addItemListener(event ->
                listener.accept(event.getStateChange() == java.awt.event.ItemEvent.SELECTED));
    }

    public void addCancionListener(IntConsumer listener) {
        rdbCancion1.addActionListener(event -> listener.accept(1));
        rdbCancion2.addActionListener(event -> listener.accept(2));
        rdbCancion3.addActionListener(event -> listener.accept(3));
        rdbCancion4.addActionListener(event -> listener.accept(4));
        rdbCancion5.addActionListener(event -> listener.accept(5));
    }

    public void addVolumenListener(IntConsumer listener) {
        volumen.addChangeListener(event -> listener.accept(volumen.getValue()));
    }

    public void addGuardarListener(Runnable listener) {
        btnGuardar.addActionListener(event -> listener.run());
    }

    public void addRestaurarListener(Runnable listener) {
        btnRestaurar.addActionListener(event -> listener.run());
    }

    public void addVolverListener(Runnable listener) {
        btnVolver.addActionListener(event -> listener.run());
    }

    public PreferenciasAjustes obtenerPreferencias() {
        int cancionSeleccionada = rdbCancion1.isSelected() ? 1 : rdbCancion2.isSelected() ? 2 : rdbCancion3.isSelected() ? 3 : rdbCancion4.isSelected() ? 4 : rdbCancion5.isSelected() ? 5 : 0;
        return new PreferenciasAjustes(
                chkMusica.isSelected(),
                cancionSeleccionada,
                volumen.getValue(),
                temaOscuro,
                chkPantallaCompleta.isSelected());
    }

    public void aplicarPreferencias(PreferenciasAjustes preferencias) {
        chkMusica.setSelected(preferencias.musicaActivada());
        volumen.setValue(preferencias.volumen());
        btnTema.setSelected(preferencias.temaOscuro());
        temaOscuro = preferencias.temaOscuro();
        aplicarTema();
        grupoCanciones.clearSelection();
        if (preferencias.cancionSeleccionada() == 1) {
            rdbCancion1.setSelected(true);
        } else if (preferencias.cancionSeleccionada() == 2) {
            rdbCancion2.setSelected(true);
        } else if (preferencias.cancionSeleccionada() == 3) {
            rdbCancion3.setSelected(true);
        } else if (preferencias.cancionSeleccionada() == 4) {
            // Selección de la canción 4 (si es necesario)
        } else if (preferencias.cancionSeleccionada() == 5) {
            // Selección de la canción 5 (si es necesario)
        }
        chkPantallaCompleta.setSelected(preferencias.pantallaCompleta());
    }

    private void configurarEncabezado() {
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
        encabezado.setOpaque(false);

        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 26));
        titulo.setForeground(COLOR_TEXTO_CLARO);
        subtitulo.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        subtitulo.setForeground(COLOR_SECUNDARIO_CLARO);

        encabezado.add(titulo);
        encabezado.add(Box.createVerticalStrut(4));
        encabezado.add(subtitulo);
        encabezado.setAlignmentX(Component.LEFT_ALIGNMENT);
        encabezado.setMaximumSize(new Dimension(Integer.MAX_VALUE, encabezado.getPreferredSize().height));
    }

    private void configurarTarjetaMusica() {
        chkMusica.setSelected(true);
        chkMusica.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        chkMusica.setOpaque(false);
        lblElegir.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        lblElegir.setForeground(COLOR_SECUNDARIO_CLARO);
        rdbCancion1.setOpaque(false);
        rdbCancion2.setOpaque(false);
        rdbCancion3.setOpaque(false);
        rdbCancion4.setOpaque(false);
        rdbCancion5.setOpaque(false);
        canciones.setOpaque(false);
        grupoCanciones.add(rdbCancion1);
        grupoCanciones.add(rdbCancion2);
        grupoCanciones.add(rdbCancion3);
        grupoCanciones.add(rdbCancion4);
        grupoCanciones.add(rdbCancion5);
        canciones.add(rdbCancion1);
        canciones.add(rdbCancion2);
        canciones.add(rdbCancion3);
        canciones.add(rdbCancion4);
        canciones.add(rdbCancion5); 

        tarjetaMusica.setLayout(new BoxLayout(tarjetaMusica, BoxLayout.Y_AXIS));
        tarjetaMusica.setBackground(COLOR_TARJETA_CLARO);
        tarjetaMusica.setBorder(crearBordeTarjeta(COLOR_BORDE_CLARO));
        tarjetaMusica.add(lblEncabezadoMusica);
        tarjetaMusica.add(Box.createVerticalStrut(8));
        tarjetaMusica.add(chkMusica);
        tarjetaMusica.add(Box.createVerticalStrut(8));
        tarjetaMusica.add(lblElegir);
        tarjetaMusica.add(canciones);
        tarjetaMusica.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjetaMusica.setMaximumSize(new Dimension(Integer.MAX_VALUE, tarjetaMusica.getPreferredSize().height));
    }

    private void configurarTarjetaVolumen() {
        lblVolumen.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        lblVolumen.setForeground(COLOR_TEXTO_CLARO);
        volumen.setMajorTickSpacing(50);
        volumen.setMinorTickSpacing(10);
        volumen.setPaintTicks(true);
        volumen.setPaintLabels(true);
        volumen.setOpaque(false);

        tarjetaVolumen.setLayout(new BoxLayout(tarjetaVolumen, BoxLayout.Y_AXIS));
        tarjetaVolumen.setBackground(COLOR_TARJETA_CLARO);
        tarjetaVolumen.setBorder(crearBordeTarjeta(COLOR_BORDE_CLARO));
        tarjetaVolumen.add(lblEncabezadoVolumen);
        tarjetaVolumen.add(Box.createVerticalStrut(8));
        tarjetaVolumen.add(lblVolumen);
        tarjetaVolumen.add(volumen);
        tarjetaVolumen.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjetaVolumen.setMaximumSize(new Dimension(Integer.MAX_VALUE, tarjetaVolumen.getPreferredSize().height));
    }

    private void configurarTarjetaApariencia() {
        lblTema.setForeground(COLOR_TEXTO_CLARO);
        btnTema.setBackground(COLOR_TEMA_CLARO);
        btnTema.setForeground(COLOR_TEXTO_CLARO);
        btnTema.setFocusPainted(false);
        btnTema.setBorder(BorderFactory.createEmptyBorder(9, 8, 9, 8));
        btnTema.setMaximumSize(new Dimension(Integer.MAX_VALUE, btnTema.getPreferredSize().height));
        chkPantallaCompleta.setOpaque(false);

        tarjetaApariencia.setLayout(new BoxLayout(tarjetaApariencia, BoxLayout.Y_AXIS));
        tarjetaApariencia.setBackground(COLOR_TARJETA_CLARO);
        tarjetaApariencia.setBorder(crearBordeTarjeta(COLOR_BORDE_CLARO));
        tarjetaApariencia.add(lblEncabezadoApariencia);
        tarjetaApariencia.add(Box.createVerticalStrut(8));
        tarjetaApariencia.add(lblTema);
        tarjetaApariencia.add(Box.createVerticalStrut(4));
        tarjetaApariencia.add(btnTema);
        tarjetaApariencia.add(Box.createVerticalStrut(4));
        tarjetaApariencia.add(chkPantallaCompleta);
        tarjetaApariencia.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjetaApariencia.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, tarjetaApariencia.getPreferredSize().height));
    }

    private void configurarBotones() {
        btnRestaurar.setFocusPainted(false);
        btnRestaurar.setOpaque(true);
        btnRestaurar.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));

        btnGuardar.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        btnGuardar.setFocusPainted(false);
        btnGuardar.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));

        btnVolver.setFocusPainted(false);
        btnVolver.setOpaque(true);
        btnVolver.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnVolver.setMaximumSize(new Dimension(Integer.MAX_VALUE, btnVolver.getPreferredSize().height));
        btnVolver.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));

        botones.setOpaque(false);
        botones.add(btnRestaurar);
        botones.add(btnGuardar);
        botones.setAlignmentX(Component.LEFT_ALIGNMENT);
        botones.setMaximumSize(new Dimension(Integer.MAX_VALUE, botones.getPreferredSize().height));
    }

    private void configurarCambioTema() {
        btnTema.addActionListener(event -> {
            temaOscuro = btnTema.isSelected();
            aplicarTema();
        });
    }

    private void aplicarTema() {
        Color colorFondo = temaOscuro ? COLOR_FONDO_OSCURO : COLOR_FONDO_CLARO;
        Color colorTarjeta = temaOscuro ? COLOR_TARJETA_OSCURO : COLOR_TARJETA_CLARO;
        Color colorTexto = temaOscuro ? COLOR_TEXTO_OSCURO : COLOR_TEXTO_CLARO;
        Color colorSecundario = temaOscuro ? COLOR_SECUNDARIO_OSCURO : COLOR_SECUNDARIO_CLARO;
        Color colorAcento = temaOscuro ? COLOR_ACENTO_OSCURO : COLOR_ACENTO_CLARO;
        Color colorBorde = temaOscuro ? COLOR_BORDE_OSCURO : COLOR_BORDE_CLARO;

        setBackground(colorFondo);
        titulo.setForeground(colorTexto);
        subtitulo.setForeground(colorSecundario);
        lblEncabezadoMusica.setForeground(colorSecundario);
        chkMusica.setForeground(colorTexto);
        lblElegir.setForeground(colorSecundario);
        rdbCancion1.setForeground(colorTexto);
        rdbCancion2.setForeground(colorTexto);
        rdbCancion3.setForeground(colorTexto);
        rdbCancion4.setForeground(colorTexto);
        rdbCancion5.setForeground(colorTexto);
        lblEncabezadoVolumen.setForeground(colorSecundario);
        lblVolumen.setForeground(colorTexto);
        volumen.setForeground(colorTexto);
        lblEncabezadoApariencia.setForeground(colorSecundario);
        lblTema.setForeground(colorTexto);
        chkPantallaCompleta.setForeground(colorTexto);

        tarjetaMusica.setBackground(colorTarjeta);
        tarjetaVolumen.setBackground(colorTarjeta);
        tarjetaApariencia.setBackground(colorTarjeta);
        Border bordeTarjeta = crearBordeTarjeta(colorBorde);
        tarjetaMusica.setBorder(bordeTarjeta);
        tarjetaVolumen.setBorder(bordeTarjeta);
        tarjetaApariencia.setBorder(bordeTarjeta);

        btnTema.setText(temaOscuro ? "🌙  Tema oscuro" : "☀  Tema claro");
        btnTema.setBackground(temaOscuro ? COLOR_TEMA_OSCURO : COLOR_TEMA_CLARO);
        btnTema.setForeground(colorTexto);
        btnRestaurar.setBackground(colorTarjeta);
        btnRestaurar.setForeground(colorTexto);
        btnRestaurar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(temaOscuro ? colorBorde : COLOR_BORDE_BOTON_CLARO),
                BorderFactory.createEmptyBorder(9, 7, 9, 7)));
        btnGuardar.setBackground(colorAcento);
        btnGuardar.setForeground(Color.WHITE);
        btnVolver.setBackground(colorTarjeta);
        btnVolver.setForeground(colorTexto);
        btnVolver.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(temaOscuro ? colorBorde : COLOR_BORDE_BOTON_CLARO),
                BorderFactory.createEmptyBorder(9, 7, 9, 7)));

        revalidate();
        repaint();
    }

    private Border crearBordeTarjeta(Color colorBorde) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(colorBorde),
                BorderFactory.createEmptyBorder(12, 16, 12, 16));
    }

    private void configurarEstiloEncabezadosTarjetas() {
        Font fuente = new Font(Font.SANS_SERIF, Font.BOLD, 10);
        lblEncabezadoMusica.setFont(fuente);
        lblEncabezadoMusica.setForeground(COLOR_SECUNDARIO_CLARO);
        lblEncabezadoVolumen.setFont(fuente);
        lblEncabezadoVolumen.setForeground(COLOR_SECUNDARIO_CLARO);
        lblEncabezadoApariencia.setFont(fuente);
        lblEncabezadoApariencia.setForeground(COLOR_SECUNDARIO_CLARO);
    }
}
