package controlador;

import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.*;
import modelos.personajes.Personaje;
import vista.MenuPrincipal;
import vista.PanelJuego;
import vista.PanelSeleccionPersonaje;
import vista.PantallaIntro;

public class PrincipalControlador {

    private static final String VISTA_INTRO = "intro";
    private static final String VISTA_MENU = "menu";
    private static final String VISTA_SELECCION = "seleccion";
    private static final String VISTA_JUEGO = "juego";

    private Personaje jugador;
    private JFrame ventana;
    private PanelJuego panelJuego;
    private MenuPrincipal menuPrincipal;
    private PanelSeleccionPersonaje panelSeleccion; //para la seleccion del pesonaje
    private PantallaIntro pantallaIntro;
    private CardLayout cardLayout;
    private JPanel contenedor;

    public PrincipalControlador() {
        iniciarVentana();
    }

    private void iniciarVentana() {
        ventana = new JFrame("Ghosts and Rats");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Antes estaba en false: eso es lo que impedía agrandar la ventana.
        ventana.setResizable(true);
        // Evita que el jugador la achique tanto que el HUD o los botones no entren.
        ventana.setMinimumSize(new Dimension(640, 480));

        cardLayout = new CardLayout();
        contenedor = new JPanel(cardLayout);

        pantallaIntro = new PantallaIntro(this::mostrarMenu);
        menuPrincipal = new MenuPrincipal();

        //El panel de seleccion llama a iniciarJuego pasandole el personaje elegido
    panelSeleccion = new PanelSeleccionPersonaje (this::iniciarJuego);

        contenedor.add(pantallaIntro, VISTA_INTRO);
        contenedor.add(menuPrincipal, VISTA_MENU);
        contenedor.add(panelSeleccion, VISTA_SELECCION);
        configurarBotonesMenu();

        ventana.add(contenedor);

        // pack() solo se llama UNA vez, para darle un tamaño inicial a la ventana
        // a partir del preferredSize de sus paneles. Después de esto, el usuario
        // maneja el tamaño arrastrando el borde de la ventana.
        ventana.pack();
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);

        cardLayout.show(contenedor, VISTA_INTRO);
    }

    

    private void mostrarMenu() {
        cardLayout.show(contenedor, VISTA_MENU);
    }

    private void mostrarSeleccion() {
        cardLayout.show(contenedor, VISTA_SELECCION);
    }
    private void configurarBotonesMenu() {
        menuPrincipal.getBtnJugar().addActionListener(e -> mostrarSeleccion());

        menuPrincipal.getBtnOpciones().addActionListener(e ->
            JOptionPane.showMessageDialog(ventana,
                "falta implementar opciones.",
                "Opciones", JOptionPane.INFORMATION_MESSAGE)
        );

        menuPrincipal.getBtnSalir().addActionListener(e -> System.exit(0));
    }

    private void iniciarJuego(Personaje personajeElegido) {
        this.jugador = personajeElegido;
        // Posición inicial cerca del agujero verde del escenario (ajustar a ojo si hace falta).
        jugador.setX(50);
        jugador.setY(480);

        panelJuego = new PanelJuego(jugador);

        contenedor.add(panelJuego, VISTA_JUEGO);
        cardLayout.show(contenedor, VISTA_JUEGO);

        panelJuego.requestFocusInWindow();
    }
}