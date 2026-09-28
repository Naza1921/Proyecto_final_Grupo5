package controlador;

import java.awt.*;
import javax.swing.*;
import modelos.Personaje;
import vista.MenuPrincipal;
import vista.PanelJuego;
import vista.PantallaIntro;

public class PrincipalControlador {

    private static final String VISTA_INTRO = "intro";
    private static final String VISTA_MENU = "menu";
    private static final String VISTA_JUEGO = "juego";

    private Personaje jugador;
    private JFrame ventana;
    private PanelJuego panelJuego;
    private MenuPrincipal menuPrincipal;
    private PantallaIntro pantallaIntro;
    private CardLayout cardLayout;
    private JPanel contenedor;

    public PrincipalControlador() {
        iniciarVentana();
    }
    // Inicia la ventana principal del juego, configurando el JFrame, el CardLayout y las vistas
    // Se agregan la pantalla de introducción y el menú principal al contenedor
    private void iniciarVentana() {
        ventana = new JFrame("Ghosts and Rats");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setResizable(false);

        cardLayout = new CardLayout();
        contenedor = new JPanel(cardLayout);

        // La intro avisa cuando el video termina para mostrar el menú principal.
        pantallaIntro = new PantallaIntro(this::mostrarMenu);
        menuPrincipal = new MenuPrincipal();

        contenedor.add(pantallaIntro, VISTA_INTRO);
        contenedor.add(menuPrincipal, VISTA_MENU);

        configurarBotonesMenu();

        ventana.add(contenedor);
        ventana.pack();
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);

        cardLayout.show(contenedor, VISTA_INTRO);
    }
    // Cambia de la pantalla de introducción al menú principal.
    // Se ejecuta cuando termina el video de la intro (ya no depende de un temporizador fijo).
    private void mostrarMenu() {
        cardLayout.show(contenedor, VISTA_MENU);
    } 
    // Configura los botones del menú principal para iniciar el juego, mostrar instrucciones 
    // o salir
    // Se agregan ActionListeners a los botones del menú principal
    private void configurarBotonesMenu() {
        menuPrincipal.getBtnJugar().addActionListener(e -> iniciarJuego());

        menuPrincipal.getBtnOpciones().addActionListener(e ->
            JOptionPane.showMessageDialog(ventana,
                "falta implementar opciones.",
                "Opciones", JOptionPane.INFORMATION_MESSAGE)
        );

        menuPrincipal.getBtnSalir().addActionListener(e -> System.exit(0));
    }
    // Inicia el juego creando un nuevo personaje y un panel de juego,
    // y cambia la vista a la del juego
    private void iniciarJuego() {
        jugador = new Personaje("Rata Gris");
        panelJuego = new PanelJuego(jugador);

        contenedor.add(panelJuego, VISTA_JUEGO);
        cardLayout.show(contenedor, VISTA_JUEGO);

        ventana.pack();
        ventana.setLocationRelativeTo(null);
        panelJuego.requestFocusInWindow();
    }
}