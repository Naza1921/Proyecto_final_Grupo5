package controlador;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import modelos.enemigos.fantasma;
import modelos.items.PowerUp;
import modelos.items.PowerUpInvulnerabilidad;
import modelos.items.PowerUpLento;
import modelos.items.PowerUpVelocidad;
import modelos.partida.Puntaje;
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

public class PrincipalControlador {

    private static final String VISTA_INTRO = "intro";
    private static final String VISTA_MENU = "menu";
    private static final String VISTA_AJUSTES = "ajustes";
    private static final String VISTA_SELECCION = "seleccion";
    private static final String VISTA_JUEGO = "juego";
    private static final String VISTA_PAUSA = "pausa";
    private static final long DURACION_VELOCIDAD_MS = 5_000;
    private static final long DURACION_LENTO_MS = 5_000;
    private static final long DURACION_INVULNERABILIDAD_MS = 3_000;
    private static final double ANCHO_FANTASMA = 72;
    private static final double ALTO_FANTASMA = 82;

    private Personaje jugador;
    private JFrame ventana;
    private PanelJuego panelJuego;
    private ControladorPartida controladorPartida;
    private ControladorTecladoRata controladorTecladoRata;
    private GestorEfectosTemporales gestorEfectos;
    private MenuPrincipal menuPrincipal;
    private MenuPausa menuPausa;
    private PanelSeleccionPersonaje panelSeleccion; //para la seleccion del pesonaje
    private PantallaIntro pantallaIntro;
    private MenuAjustesVista panelAjustes;
    private ControladorMenuAjustes controladorMenuAjustes;
    private final ServicioPuntajes servicioPuntajes;
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
        menuPausa = new MenuPausa();
        panelAjustes = new MenuAjustesVista();

        //El panel de seleccion llama a iniciarJuego pasandole el personaje elegido
        panelSeleccion = new PanelSeleccionPersonaje(this::iniciarJuego);

        contenedor.add(pantallaIntro, VISTA_INTRO);
        contenedor.add(menuPrincipal, VISTA_MENU);
        contenedor.add(panelAjustes, VISTA_AJUSTES);
        contenedor.add(panelSeleccion, VISTA_SELECCION);
        contenedor.add(menuPausa, VISTA_PAUSA);
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
        menuPausa.getBtnReanudar().addActionListener(e -> reanudarJuego());
        menuPausa.getBtnVolverAlMenu().addActionListener(e -> volverAlMenuDesdePausa());
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
        fantasma enemigo = new fantasma(
            "Fantasma", 2.0, 0, 190, 420, ANCHO_FANTASMA, ALTO_FANTASMA
        );
        panelJuego = new PanelJuego(jugador, puntaje, enemigo);
        controladorTecladoRata = new ControladorTecladoRata(
            panelJuego, gestorControles, this::pausarJuego
        );
        Map<Integer, List<PowerUp>> powerUpsPorSeccion = Map.of(
            0, List.of(new PowerUpVelocidad(DURACION_VELOCIDAD_MS, 180, 640)),
            1, List.of(new PowerUpLento(DURACION_LENTO_MS, 600, 640)),
            2, List.of(new PowerUpInvulnerabilidad(DURACION_INVULNERABILIDAD_MS, 1020, 640))
        );
        controladorPartida = new ControladorPartida(
            controladorRata, gestorControles, gestorEfectos, controladorPuntaje,
            panelJuego, powerUpsPorSeccion, jugador, enemigo, this::guardarResultadoPartida
        );

        contenedor.add(panelJuego, VISTA_JUEGO);
        cardLayout.show(contenedor, VISTA_JUEGO);

        panelJuego.requestFocusInWindow();
        controladorPartida.iniciar();
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