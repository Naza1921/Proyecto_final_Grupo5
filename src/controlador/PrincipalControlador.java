package controlador;

import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.*;
import aplicacion.NavegacionPartida;
import modelos.personajes.Personaje;
import servicios.OperacionesPuntaje;
import servicios.RepositorioAjustes;
import servicios.audio.ReproductorEfectosSonido;
import servicios.audio.ReproductorMusica;
import vista.MenuAjustesVista;
import vista.MenuGameOver;
import vista.MenuPausa;
import vista.MenuPrincipal;
import vista.PanelJuego;
import vista.PanelSeleccionPersonaje;
import vista.PantallaIntro;
import vista.TableroPuntajes;

public final class PrincipalControlador implements NavegacionPartida {
    private static final String VISTA_INTRO = "intro";
    private static final String VISTA_MENU = "menu";
    private static final String VISTA_AJUSTES = "ajustes";
    private static final String VISTA_SELECCION = "seleccion";
    private static final String VISTA_JUEGO = "juego";
    private static final String VISTA_PAUSA = "pausa";
    private static final String VISTA_GAME_OVER = "gameOver";
    private static final String VISTA_TABLERO = "tablero";
    private static final String VISTA_CARGA = "carga";

    private final OperacionesPuntaje operacionesPuntaje;
    private final ReproductorMusica reproductorMusica;
    private final ReproductorEfectosSonido reproductorEfectosSonido;
    private final FabricaPartida fabricaPartida;
    private final RepositorioAjustes repositorioAjustes;

    private JFrame ventana;
    private CardLayout cardLayout;
    private JPanel contenedor;
    private TableroPuntajes tableroPuntajes;
    private MenuPausa menuPausa;
    private MenuGameOver menuGameOver;
    private ControladorMenuAjustes controladorMenuAjustes;
    private ControladorPuntajes controladorPuntajes;
    private ControladorFlujoPartida controladorFlujoPartida;

    public PrincipalControlador(
            OperacionesPuntaje operacionesPuntaje,
            ReproductorMusica reproductorMusica,
            ReproductorEfectosSonido reproductorEfectosSonido,
            FabricaPartida fabricaPartida,
            RepositorioAjustes repositorioAjustes) {
        if (operacionesPuntaje == null || reproductorMusica == null
                || reproductorEfectosSonido == null || fabricaPartida == null
                || repositorioAjustes == null) {
            throw new IllegalArgumentException("Se requieren dependencias válidas para la aplicación");
        }
        this.operacionesPuntaje = operacionesPuntaje;
        this.reproductorMusica = reproductorMusica;
        this.reproductorEfectosSonido = reproductorEfectosSonido;
        this.fabricaPartida = fabricaPartida;
        this.repositorioAjustes = repositorioAjustes;
        iniciarVentana();
    }

    private void iniciarVentana() {
        ventana = new JFrame("Ghosts and Rats");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setResizable(true);
        ventana.setMinimumSize(new Dimension(640, 480));

        cardLayout = new CardLayout();
        contenedor = new JPanel(cardLayout);
        MenuPrincipal menuPrincipal = new MenuPrincipal();
        tableroPuntajes = new TableroPuntajes(this::mostrarMenu);
        menuPausa = new MenuPausa();
        menuGameOver = new MenuGameOver();
        MenuAjustesVista panelAjustes = new MenuAjustesVista();
        PanelSeleccionPersonaje panelSeleccion =
            new PanelSeleccionPersonaje(this::iniciarJuego);

        contenedor.add(crearPanelCargaPartida(), VISTA_CARGA);
        contenedor.add(new PantallaIntro(this::mostrarMenu), VISTA_INTRO);
        contenedor.add(menuPrincipal, VISTA_MENU);
        contenedor.add(tableroPuntajes, VISTA_TABLERO);
        contenedor.add(panelAjustes, VISTA_AJUSTES);
        contenedor.add(panelSeleccion, VISTA_SELECCION);
        contenedor.add(menuPausa, VISTA_PAUSA);
        contenedor.add(menuGameOver, VISTA_GAME_OVER);

        controladorPuntajes = new ControladorPuntajes(
            ventana, operacionesPuntaje, tableroPuntajes
        );
        controladorFlujoPartida = new ControladorFlujoPartida(
            fabricaPartida,
            reproductorMusica,
            this,
            controladorPuntajes::solicitarGuardado
        );
        controladorMenuAjustes = new ControladorMenuAjustes(
            ventana,
            panelAjustes,
            reproductorMusica,
            repositorioAjustes,
            this::mostrarErrorAjustes,
            this::mostrarConfirmacionAjustes,
            this::mostrarMenu
        );
        configurarBotonesMenu(menuPrincipal);

        ventana.add(contenedor);
        cardLayout.show(contenedor, VISTA_INTRO);
        ventana.pack();
        ventana.setLocationRelativeTo(null);
        controladorMenuAjustes.iniciar();
        ventana.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evento) {
                controladorFlujoPartida.cerrar();
                reproductorEfectosSonido.close();
                reproductorMusica.close();
                operacionesPuntaje.close();
            }
        });
        ventana.setVisible(true);
    }

    private void configurarBotonesMenu(MenuPrincipal menuPrincipal) {
        menuPrincipal.getBtnJugar().addActionListener(evento -> mostrarSeleccion());
        menuPrincipal.getBtnOpciones().addActionListener(
            evento -> cardLayout.show(contenedor, VISTA_AJUSTES)
        );
        menuPrincipal.getBtnRanking().addActionListener(evento -> mostrarTablero());
        menuPrincipal.getBtnSalir().addActionListener(evento -> System.exit(0));
        menuPausa.getBtnReanudar().addActionListener(
            evento -> controladorFlujoPartida.reanudar()
        );
        menuPausa.getBtnVolverAlMenu().addActionListener(
            evento -> controladorFlujoPartida.volverAlMenuDesdePausa()
        );
        menuPausa.getBtnSalir().addActionListener(evento -> System.exit(0));
        menuGameOver.getBtnVolver().addActionListener(evento -> volverAlMenuDesdeGameOver());
        menuGameOver.getBtnSalir().addActionListener(evento -> System.exit(0));
    }

    @Override
    public void mostrarMenu() {
        cardLayout.show(contenedor, VISTA_MENU);
    }

    private void mostrarSeleccion() {
        cardLayout.show(contenedor, VISTA_SELECCION);
    }

    private void mostrarTablero() {
        cardLayout.show(contenedor, VISTA_TABLERO);
        controladorPuntajes.cargarTablero();
    }

    @Override
    public void mostrarCargaPartida() {
        cardLayout.show(contenedor, VISTA_CARGA);
    }

    private JPanel crearPanelCargaPartida() {
        JPanel panel = new JPanel(new java.awt.GridBagLayout());
        panel.setBackground(new java.awt.Color(15, 13, 25));
        JLabel mensaje = new JLabel("Preparando la partida...");
        mensaje.setForeground(new java.awt.Color(255, 214, 133));
        mensaje.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 22));
        panel.add(mensaje);
        return panel;
    }

    @Override
    public void instalarVistaPartida(PanelJuego panelJuego) {
        if (panelJuego.getParent() != contenedor) {
            contenedor.add(panelJuego, VISTA_JUEGO);
        }
        cardLayout.show(contenedor, VISTA_JUEGO);
        panelJuego.requestFocusInWindow();
    }

    @Override
    public void quitarVistaPartida(PanelJuego panelJuego) {
        contenedor.remove(panelJuego);
    }

    @Override
    public void mostrarPausa() {
        cardLayout.show(contenedor, VISTA_PAUSA);
        menuPausa.getBtnReanudar().requestFocusInWindow();
    }

    @Override
    public void mostrarMenuGameOver() {
        cardLayout.show(contenedor, VISTA_GAME_OVER);
        menuGameOver.getBtnVolver().requestFocusInWindow();
    }

    private void volverAlMenuDesdeGameOver() {
        controladorPuntajes.confirmarGuardado(
            controladorFlujoPartida.obtenerResultadoActual()
        );
        controladorFlujoPartida.volverAlMenuDesdeGameOver();
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

    @Override
    public void mostrarErrorCreacionPartida(String mensaje) {
        JOptionPane.showMessageDialog(
            ventana,
            mensaje,
            "Error al iniciar el juego",
            JOptionPane.ERROR_MESSAGE
        );
        cardLayout.show(contenedor, VISTA_SELECCION);
    }

    private void iniciarJuego(Personaje personajeElegido) {
        controladorFlujoPartida.iniciar(personajeElegido);
    }
}
