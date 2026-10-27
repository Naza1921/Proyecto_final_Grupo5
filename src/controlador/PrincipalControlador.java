package controlador;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import modelos.enemigos.fantasma;
import modelos.enemigos.ratainfestada;
import modelos.items.PowerUp;
import modelos.items.FabricaPowerUps;
import modelos.items.Moneda;
import modelos.partida.ConfiguracionPartida;
import modelos.partida.Puntaje;
import modelos.escenario.levels.level;
import modelos.personajes.Personaje;
import persistencia.ConexionSQLite;
import persistencia.SQLitePuntajeDAO;
import recursos.CargadorRecursos;
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
    private static final double ANCHO_FANTASMA = 36;
    private static final double ALTO_FANTASMA = 41;

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
        tableroPuntajes = new TableroPuntajes(this::mostrarMenu);
        menuPausa = new MenuPausa();
        menuGameOver = new MenuGameOver();
        panelAjustes = new MenuAjustesVista();

        //El panel de seleccion llama a iniciarJuego pasandole el personaje elegido
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

        // pack() solo se llama UNA vez, para darle un tamaño inicial a la ventana
        // a partir del preferredSize de sus paneles. Después de esto, el usuario
        // maneja el tamaño arrastrando el borde de la ventana.
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
        jugador.setX(ConfiguracionPartida.POSICION_INICIAL_X);
        jugador.setY(ConfiguracionPartida.POSICION_INICIAL_Y);

        ControladorRata controladorRata = new ratacontroller(jugador);
        GestorControlesRatas gestorControles = new GestorControlesRatas(controladorRata);
        gestorEfectos = new GestorEfectosTemporales(jugador);
        Puntaje puntaje = new Puntaje();
        ControladorPuntaje controladorPuntaje = new ControladorPuntaje(puntaje);
        List<fantasma> fantasmas = new ArrayList<>();
        List<ratainfestada> ratasInfestadas = new ArrayList<>();
        for (int indice = 0; indice < level.MAX_ENEMIGOS_POR_TIPO; indice++) {
            fantasmas.add(new fantasma(
                "Fantasma " + (indice + 1), 2.0, 0, 0,
                ConfiguracionPartida.TAM_TILE, ANCHO_FANTASMA, ALTO_FANTASMA
            ));
            ratasInfestadas.add(
                new ratainfestada("Rata Infestada " + (indice + 1), 2.0, 0)
            );
        }
        levelmanager niveles = new levelmanager();
        BufferedImage fondoJuego = cargarFondoJuego();
        BufferedImage tutorialControles = CargadorRecursos.cargarImagen(
            "assets/tutorial_controles.png"
        );
        panelJuego = new PanelJuego(
            jugador, puntaje, fantasmas, ratasInfestadas, niveles,
            this::mostrarMenuGameOver, fondoJuego, tutorialControles
        );
        Map<Integer, List<PowerUp>> powerUpsPorNivel =
            FabricaPowerUps.crearPorNivel(niveles.getNiveles());
        Map<Integer, List<Moneda>> monedasPorSeccion = Map.of(
            0, List.of(
                new Moneda(330, 640), new Moneda(480, 640), new Moneda(720, 640)
            ),
            1, List.of(new Moneda(450, 640), new Moneda(780, 640)),
            2, List.of(new Moneda(300, 640), new Moneda(660, 640)),
            3, List.of(new Moneda(510, 640), new Moneda(900, 640)),
            4, List.of(new Moneda(360, 640), new Moneda(750, 640))
        );
        controladorPartida = new ControladorPartida(
            controladorRata, gestorControles, gestorEfectos, efectosSonido,
            controladorPuntaje, panelJuego, niveles, powerUpsPorNivel,
            monedasPorSeccion, jugador, fantasmas, ratasInfestadas,
            this::guardarResultadoPartida
        );
        controladorTecladoRata = new ControladorTecladoRata(
            panelJuego, gestorControles, this::pausarJuego,
            () -> efectosSonido.reproducir(GestorEfectosSonido.Efecto.SALTO),
            controladorPartida::activarHabilidad
        );

        contenedor.add(panelJuego, VISTA_JUEGO);
        cardLayout.show(contenedor, VISTA_JUEGO);

        panelJuego.requestFocusInWindow();
        controladorPartida.iniciar();
        reproductorMusica.habilitarReproduccion();
    }

    private BufferedImage cargarFondoJuego() {
        String ruta = "/assets/escenarios/Nivel_1_Top_Floor X.png";
        try {
            return CargadorRecursos.cargarImagen(ruta.substring(1));
        } catch (IllegalArgumentException error) {
            System.err.println("No se pudo cargar el fondo " + ruta + ": " + error.getMessage());
            return null;
        }
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