package controlador;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import modelos.items.PowerUp;
import modelos.items.PowerUpInvulnerabilidad;
import modelos.items.PowerUpLento;
import modelos.items.PowerUpVelocidad;
import modelos.partida.Puntaje;
import modelos.personajes.Personaje;
import vista.MenuPrincipal;
import vista.MenuAjustesVista;
import vista.PanelJuego;
import vista.PanelSeleccionPersonaje;
import vista.PantallaIntro;

public class PrincipalControlador {

    private static final String VISTA_INTRO = "intro";
    private static final String VISTA_MENU = "menu";
    private static final String VISTA_AJUSTES = "ajustes";
    private static final String VISTA_SELECCION = "seleccion";
    private static final String VISTA_JUEGO = "juego";
    private static final long DURACION_VELOCIDAD_MS = 5_000;
    private static final long DURACION_LENTO_MS = 5_000;
    private static final long DURACION_INVULNERABILIDAD_MS = 3_000;

    private Personaje jugador;
    private JFrame ventana;
    private PanelJuego panelJuego;
    private ControladorPartida controladorPartida;
    private ControladorTecladoRata controladorTecladoRata;
    private GestorEfectosTemporales gestorEfectos;
    private MenuPrincipal menuPrincipal;
    private PanelSeleccionPersonaje panelSeleccion; //para la seleccion del pesonaje
    private PantallaIntro pantallaIntro;
    private MenuAjustesVista panelAjustes;
    private ControladorMenuAjustes controladorMenuAjustes;
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
        panelAjustes = new MenuAjustesVista();

        //El panel de seleccion llama a iniciarJuego pasandole el personaje elegido
        panelSeleccion = new PanelSeleccionPersonaje(this::iniciarJuego);

        contenedor.add(pantallaIntro, VISTA_INTRO);
        contenedor.add(menuPrincipal, VISTA_MENU);
        contenedor.add(panelAjustes, VISTA_AJUSTES);
        contenedor.add(panelSeleccion, VISTA_SELECCION);
        controladorMenuAjustes = new ControladorMenuAjustes(
            ventana,
            panelAjustes,
            new GestorMusica(this::mostrarErrorAjustes),
            new RepositorioAjustes(),
            this::mostrarErrorAjustes,
            this::mostrarConfirmacionAjustes,
            this::mostrarMenu
        );
        configurarBotonesMenu();

        ventana.add(contenedor);

        // pack() solo se llama UNA vez, para darle un tamaño inicial a la ventana
        // a partir del preferredSize de sus paneles. Después de esto, el usuario
        // maneja el tamaño arrastrando el borde de la ventana.
        cardLayout.show(contenedor, VISTA_INTRO);
        ventana.pack();
        ventana.setLocationRelativeTo(null);
        controladorMenuAjustes.iniciar();
        ventana.setVisible(true);
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
            cardLayout.show(contenedor, VISTA_AJUSTES));

        menuPrincipal.getBtnSalir().addActionListener(e -> System.exit(0));
    }

    private void mostrarErrorAjustes(String mensaje) {
        JOptionPane.showMessageDialog(
            ventana,
            mensaje,
            "Error en ajustes",
            JOptionPane.ERROR_MESSAGE
        );
    }

    private void mostrarConfirmacionAjustes(String mensaje) {
        JOptionPane.showMessageDialog(
            ventana,
            mensaje,
            "Ajustes",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void iniciarJuego(Personaje personajeElegido) {
        if (controladorPartida != null) {
            controladorPartida.detener();
        }
        if (gestorEfectos != null) {
            gestorEfectos.limpiar();
        }
        if (panelJuego != null) {
            contenedor.remove(panelJuego);
        }

        this.jugador = personajeElegido;
        // Posición inicial cerca del agujero verde del escenario (ajustar a ojo si hace falta).
        jugador.setX(50);
        jugador.setY(40);

        ControladorRata controladorRata = new ratacontroller(jugador);
        GestorControlesRatas gestorControles = new GestorControlesRatas(controladorRata);
        gestorEfectos = new GestorEfectosTemporales(jugador);
        Puntaje puntaje = new Puntaje();
        ControladorPuntaje controladorPuntaje = new ControladorPuntaje(puntaje);
        panelJuego = new PanelJuego(jugador, puntaje);
        controladorTecladoRata = new ControladorTecladoRata(panelJuego, gestorControles);
        Map<Integer, List<PowerUp>> powerUpsPorSeccion = Map.of(
            0, List.of(new PowerUpVelocidad(DURACION_VELOCIDAD_MS, 180, 640)),
            1, List.of(new PowerUpLento(DURACION_LENTO_MS, 600, 640)),
            2, List.of(new PowerUpInvulnerabilidad(DURACION_INVULNERABILIDAD_MS, 1020, 640))
        );
        controladorPartida = new ControladorPartida(
            controladorRata, gestorControles, gestorEfectos, controladorPuntaje,
            panelJuego, powerUpsPorSeccion
        );

        contenedor.add(panelJuego, VISTA_JUEGO);
        cardLayout.show(contenedor, VISTA_JUEGO);

        panelJuego.requestFocusInWindow();
        controladorPartida.iniciar();
    }
}