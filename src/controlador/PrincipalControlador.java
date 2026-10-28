package controlador;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.sql.SQLException;
import javax.swing.*;
import modelos.personajes.Personaje;
import persistencia.ConexionSQLite;
import persistencia.SQLitePuntajeDAO;
import servicios.ServicioPuntajes;
import vista.MenuPrincipal;
import vista.MenuAjustesVista;
import vista.PanelJuego;
import vista.PanelSeleccionPersonaje;
import vista.PantallaIntro;
import vista.MenuPausa;
import vista.MenuGameOver;
import vista.TableroPuntajes;

public class PrincipalControlador {

    private static final String VISTA_INTRO = "intro";
    private static final String VISTA_MENU = "menu";
    private static final String VISTA_AJUSTES = "ajustes";
    private static final String VISTA_SELECCION = "seleccion";
    private static final String VISTA_JUEGO = "juego";
    private static final String VISTA_PAUSA = "pausa";
    private static final String VISTA_GAME_OVER = "gameOver";
    private static final String VISTA_TABLERO = "tablero";

    private Personaje jugador;
    private JFrame ventana;
    private PanelJuego panelJuego;
    private ControladorPartida controladorPartida;
    private ControladorTecladoRata controladorTecladoRata;
    private GestorEfectosTemporales gestorEfectos;
    private MenuPrincipal menuPrincipal;
    private TableroPuntajes tableroPuntajes;
    private MenuPausa menuPausa;
    private MenuGameOver menuGameOver;
    private PanelSeleccionPersonaje panelSeleccion; //para la seleccion del pesonaje
    private PantallaIntro pantallaIntro;
    private MenuAjustesVista panelAjustes;
    private ControladorMenuAjustes controladorMenuAjustes;
    private ReproductorMusica reproductorMusica;
    private final ServicioPuntajes servicioPuntajes;
    private final GestorEfectosSonido efectosSonido;
    private final CreadorPartida creadorPartida;
    private CardLayout cardLayout;
    private JPanel contenedor;

    public PrincipalControlador() {
        this(new ServicioPuntajes(
            new SQLitePuntajeDAO(ConexionSQLite.obtenerInstancia())
        ));
    }

    public PrincipalControlador(ServicioPuntajes servicioPuntajes) {
        if (servicioPuntajes == null) {
            throw new IllegalArgumentException("Se requiere el servicio de puntajes");
        }
        this.servicioPuntajes = servicioPuntajes;
        this.efectosSonido = new GestorEfectosSonido(this::mostrarErrorAjustes);
        this.creadorPartida = new CreadorPartida(efectosSonido);
        iniciarVentana();
    }

    private void iniciarVentana() {
        ventana = new JFrame("Ghosts and Rats");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setResizable(true);
        ventana.setMinimumSize(new Dimension(640, 480));

        cardLayout = new CardLayout();
        contenedor = new JPanel(cardLayout);

        pantallaIntro = new PantallaIntro(this::mostrarMenu);
        menuPrincipal = new MenuPrincipal();
        tableroPuntajes = new TableroPuntajes(this::mostrarMenu);
        menuPausa = new MenuPausa();
        menuGameOver = new MenuGameOver();
        panelAjustes = new MenuAjustesVista();
        panelSeleccion = new PanelSeleccionPersonaje(this::iniciarJuego);

        contenedor.add(pantallaIntro, VISTA_INTRO);
        contenedor.add(menuPrincipal, VISTA_MENU);
        contenedor.add(tableroPuntajes, VISTA_TABLERO);
        contenedor.add(panelAjustes, VISTA_AJUSTES);
        contenedor.add(panelSeleccion, VISTA_SELECCION);
        contenedor.add(menuPausa, VISTA_PAUSA);
        contenedor.add(menuGameOver, VISTA_GAME_OVER);
        reproductorMusica = new GestorMusica(this::mostrarErrorAjustes);
        controladorMenuAjustes = new ControladorMenuAjustes(
            ventana,
            panelAjustes,
            reproductorMusica,
            new RepositorioAjustes(),
            this::mostrarErrorAjustes,
            this::mostrarConfirmacionAjustes,
            this::mostrarMenu
        );
        configurarBotonesMenu();

        ventana.add(contenedor);

        cardLayout.show(contenedor, VISTA_INTRO);
        ventana.pack();
        ventana.setLocationRelativeTo(null);
        controladorMenuAjustes.iniciar();
        ventana.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evento) {
                efectosSonido.close();
            }
        });
        ventana.setVisible(true);
    }



    private void mostrarMenu() {
        cardLayout.show(contenedor, VISTA_MENU);
    }

    private void mostrarSeleccion() {
        cardLayout.show(contenedor, VISTA_SELECCION);
    }

    private void mostrarTablero() {
        try {
            tableroPuntajes.mostrarResultados(servicioPuntajes.listarMejoresPuntajes());
            cardLayout.show(contenedor, VISTA_TABLERO);
        } catch (SQLException excepcion) {
            JOptionPane.showMessageDialog(
                ventana,
                "No se pudo cargar el tablero de puntajes: " + excepcion.getMessage(),
                "Error de base de datos",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void configurarBotonesMenu() {
        menuPrincipal.getBtnJugar().addActionListener(e -> mostrarSeleccion());

        menuPrincipal.getBtnOpciones().addActionListener(e ->
            cardLayout.show(contenedor, VISTA_AJUSTES));

        menuPrincipal.getBtnRanking().addActionListener(e -> mostrarTablero());
        menuPrincipal.getBtnSalir().addActionListener(e -> System.exit(0));
        menuPausa.getBtnReanudar().addActionListener(e -> reanudarJuego());
        menuPausa.getBtnVolverAlMenu().addActionListener(e -> volverAlMenuDesdePausa());
        menuPausa.getBtnSalir().addActionListener(e -> System.exit(0));
        menuGameOver.getBtnVolver().addActionListener(e -> mostrarMenu());
        menuGameOver.getBtnSalir().addActionListener(e -> System.exit(0));
    }

    private void pausarJuego() {
        if (controladorPartida == null) {
            return;
        }
        if (!controladorPartida.pausar()) {
            return;
        }
        controladorTecladoRata.limpiarEntradas();
        gestorEfectos.pausar();
        cardLayout.show(contenedor, VISTA_PAUSA);
        menuPausa.getBtnReanudar().requestFocusInWindow();
    }

    private void reanudarJuego() {
        if (controladorPartida == null) {
            return;
        }
        gestorEfectos.reanudar();
        controladorPartida.reanudar();
        cardLayout.show(contenedor, VISTA_JUEGO);
        panelJuego.requestFocusInWindow();
    }

    private void volverAlMenuDesdePausa() {
        controladorPartida.detener();
        gestorEfectos.limpiar();
        cardLayout.show(contenedor, VISTA_MENU);
    }

    private void mostrarMenuGameOver() {
        if (controladorTecladoRata != null) {
            controladorTecladoRata.limpiarEntradas();
        }
        if (gestorEfectos != null) {
            gestorEfectos.limpiar();
        }
        cardLayout.show(contenedor, VISTA_GAME_OVER);
        menuGameOver.getBtnVolver().requestFocusInWindow();
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
        CreadorPartida.PartidaCreada partida = creadorPartida.crear(
            jugador,
            this::mostrarMenuGameOver,
            this::pausarJuego,
            this::guardarResultadoPartida
        );
        panelJuego = partida.panelJuego();
        gestorEfectos = partida.gestorEfectos();
        controladorPartida = partida.controladorPartida();
        controladorTecladoRata = partida.controladorTecladoRata();

        contenedor.add(panelJuego, VISTA_JUEGO);
        cardLayout.show(contenedor, VISTA_JUEGO);

        panelJuego.requestFocusInWindow();
        controladorPartida.iniciar();
        reproductorMusica.habilitarReproduccion();
    }

    private void guardarResultadoPartida() {
        String nombre = JOptionPane.showInputDialog(
            ventana,
            "Ingresá tu nombre para guardar el puntaje:",
            jugador.getNombre()
        );
        if (nombre == null) {
            JOptionPane.showMessageDialog(
                ventana,
                "El resultado de esta partida no se guardó.",
                "Puntaje no guardado",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        if (nombre.isBlank()) {
            JOptionPane.showMessageDialog(
                ventana,
                "El nombre no puede estar vacío; el resultado no se guardó.",
                "Nombre inválido",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            boolean nuevoMejorPuntaje = servicioPuntajes.guardarMejorPuntaje(
                nombre,
                controladorPartida.obtenerPuntaje(),
                controladorPartida.obtenerTiempoPartidaSegundos()
            );
            String mensaje = nuevoMejorPuntaje
                ? "Puntaje guardado correctamente."
                : "No superaste tu mejor puntaje anterior; se conservó el registro existente.";
            JOptionPane.showMessageDialog(
                ventana,
                mensaje,
                "Resultado de la partida",
                JOptionPane.INFORMATION_MESSAGE
            );
        } catch (SQLException excepcion) {
            JOptionPane.showMessageDialog(
                ventana,
                "No se pudo guardar el puntaje en SQLite: " + excepcion.getMessage(),
                "Error de base de datos",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}