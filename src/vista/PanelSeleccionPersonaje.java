package vista;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import modelos.personajes.Personaje;
import modelos.personajes.rata_blanca;
import modelos.personajes.rata_gris;
import modelos.personajes.rata_negra;
import modelos.personajes.rata_verde;

// Presenta cada rata en una tarjeta y comunica la elección al controlador.
public class PanelSeleccionPersonaje extends JPanel {
    private static final Color COLOR_TEXTO = new Color(247, 239, 255);
    private static final Color COLOR_ACENTO = new Color(255, 214, 133);
    private static final Color COLOR_FONDO_TARJETA = new Color(39, 31, 53, 235);

    private final Consumer<Personaje> onPersonajeSelected;

    public PanelSeleccionPersonaje(Consumer<Personaje> onPersonajeSelected) {
        if (onPersonajeSelected == null) {
            throw new IllegalArgumentException("Se requiere una acción al seleccionar un personaje");
        }
        this.onPersonajeSelected = onPersonajeSelected;

        setOpaque(false);
        setPreferredSize(new Dimension(800, 600));
        setBorder(BorderFactory.createEmptyBorder(28, 48, 26, 48));
        setLayout(new BorderLayout(0, 18));

        JLabel titulo = new JLabel("ELEGÍ TU PERSONAJE", SwingConstants.CENTER);
        titulo.setFont(new Font("Serif", Font.BOLD, 28));
        titulo.setForeground(COLOR_ACENTO);

        JLabel subtitulo = new JLabel(
            "Seleccioná una rata para comenzar la aventura",
            SwingConstants.CENTER
        );
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitulo.setForeground(new Color(216, 203, 229));

        JPanel encabezado = new JPanel(new GridLayout(2, 1, 0, 5));
        encabezado.setOpaque(false);
        encabezado.add(titulo);
        encabezado.add(subtitulo);
        add(encabezado, BorderLayout.NORTH);
        // Crear un panel de tarjetas para mostrar las opciones de personajes pertenece 
        // en realidad esta seccion de codigo tendria que estar dentro de un gestor
        //de paneldeseleccion, pero por ahora lo dejamos aca, ya que no tenemos un gestor de paneleseleccion
        // El panel de tarjetas tiene un GridLayout de 2 filas y 2 columnas, con espacio entre ellas.
        JPanel tarjetas = new JPanel(new GridLayout(2, 2, 18, 16));
        tarjetas.setOpaque(false);
        /* 
        tarjetas.add(crearBoton("Rata Gris", modelos.personajes.Personaje.TipoRata.GRIS,
            () -> new rata_gris("Rata Gris")));
        tarjetas.add(crearBoton("Rata Blanca", modelos.personajes.Personaje.TipoRata.BLANCA,
            () -> new rata_blanca("Rata Blanca")));
        tarjetas.add(crearBoton("Rata Negra", modelos.personajes.Personaje.TipoRata.NEGRA,
            () -> new rata_negra("Rata Negra")));
        tarjetas.add(crearBoton("Rata Verde", modelos.personajes.Personaje.TipoRata.VERDE,
            () -> new rata_verde("Rata Verde")));
        */
            tarjetas.add(crearBoton("Rata Gris", modelos.personajes.Personaje.TipoRata.GRIS,
            () -> PersonajeFactory.crearPersonaje("Rata Gris", modelos.personajes.Personaje.TipoRata.GRIS)));
            tarjetas.add(crearBoton("Rata Blanca", modelos.personajes.Personaje.TipoRata.BLANCA,
            () -> PersonajeFactory.crearPersonaje("Rata Blanca", modelos.personajes.Personaje.TipoRata.BLANCA)));
            tarjetas.add(crearBoton("Rata Negra", modelos.personajes.Personaje.TipoRata.NEGRA,
            () -> PersonajeFactory.crearPersonaje("Rata Negra", modelos.personajes.Personaje.TipoRata.NEGRA)));
            tarjetas.add(crearBoton("Rata Verde", modelos.personajes.Personaje.TipoRata.VERDE,
            () -> PersonajeFactory.crearPersonaje("Rata Verde", modelos.personajes.Personaje.TipoRata.VERDE)));

            add(tarjetas, BorderLayout.CENTER);
    }

    // Dibuja el fondo del selector sin cubrir las tarjetas ni su contenido.
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(new GradientPaint(0, 0, new Color(35, 25, 52),
            0, getHeight(), new Color(15, 13, 25)));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(new Color(255, 214, 133, 32));
        g2.fillOval(getWidth() - 260, -170, 430, 430);
        g2.dispose();
    }

    // Muestra el nombre y el sprite; al hacer clic crea la instancia elegida.
    private JButton crearBoton(String nombre, Personaje.TipoRata tipo,
                               Supplier<Personaje> fabrica) {
        JButton boton = new JButton(
            "<html><center>" + nombre + "</center></html>",
            new ImageIcon(SpritesRatas.crearVistaIdle(tipo, 170, 112))
        ) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                boolean encima = getModel().isRollover();
                g2.setColor(encima
                    ? new Color(75, 54, 96, 245)
                    : COLOR_FONDO_TARJETA);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                g2.setColor(encima
                    ? COLOR_ACENTO
                    : new Color(174, 148, 198, 175));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        boton.setHorizontalTextPosition(SwingConstants.CENTER);
        boton.setVerticalTextPosition(SwingConstants.BOTTOM);
        boton.setIconTextGap(2);
        boton.setFocusPainted(false);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setOpaque(false);
        boton.setBorder(BorderFactory.createEmptyBorder(7, 10, 8, 10));
        boton.setForeground(COLOR_TEXTO);
        boton.setFont(new Font("SansSerif", Font.BOLD, 16));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setToolTipText("Elegir " + nombre);
        boton.getAccessibleContext().setAccessibleName(nombre);
        boton.addActionListener(evento -> onPersonajeSelected.accept(fabrica.get()));
        return boton;
    }
}
