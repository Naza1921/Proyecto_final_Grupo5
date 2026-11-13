package controlador;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.*;
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
    private final ExecutorService precargadorImagenes = Executors.newSingleThreadExecutor(tarea -> {
        Thread hilo = new Thread(tarea, "precarga-imagenes");
        hilo.setDaemon(true);
        return hilo;
    });
    private CardLayout cardLayout;
    private JPanel contenedor;
    private boolean cargandoPuntajes;
    private boolean guardandoPuntaje;
    private boolean precargaIniciada;

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
                precargadorImagenes.shutdownNow();
            }
        });
        ventana.setVisible(true);
    }



    private void mostrarMenu() {
        cardLayout.show(contenedor, VISTA_MENU);
        iniciarPrecargaImagenes();
    }

    private void iniciarPrecargaImagenes() {
        if (precargaIniciada) {
            return;
        }
        precargaIniciada = true;
        precargadorImagenes.execute(() -> {
            try {
                CargadorRecursos.precargar(CreadorPartida.rutasImagenesPrecarga());
            } catch (RuntimeException error) {
                System.err.println("No se pudieron preparar las imágenes de la partida: "
                    + error.getMessage());
            }
        });
    }

    private void mostrarSeleccion() {
        cardLayout.show(contenedor, VISTA_SELECCION);
    }

    private void mostrarTablero() {
        if (cargandoPuntajes) {
            return;
        }
        cargandoPuntajes = true;
        menuPrincipal.getBtnRanking().setEnabled(false);
        new SwingWorker<java.util.List<persistencia.ResultadoPuntaje>, Void>() {
            @Override
            protected java.util.List<persistencia.ResultadoPuntaje> doInBackground()
                    throws Exception {
                return servicioPuntajes.listarMejoresPuntajes();
            }

            @Override
            protected void done() {
                try {
                    tableroPuntajes.mostrarResultados(get());
                    cardLayout.show(contenedor, VISTA_TABLERO);
                } catch (InterruptedException excepcion) {
                    Thread.currentThread().interrupt();
                    JOptionPane.showMessageDialog(
                        ventana,
                        "No se pudo cargar el tablero de puntajes: " + excepcion.getMessage(),
                        "Error de base de datos",
                        JOptionPane.ERROR_MESSAGE
                    );
                } catch (java.util.concurrent.ExecutionException excepcion) {
                    Throwable causa = excepcion.getCause();
                    JOptionPane.showMessageDialog(
                        ventana,
                        "No se pudo cargar el tablero de puntajes: "
                            + (causa == null ? excepcion.getMessage() : causa.getMessage()),
                        "Error de base de datos",
                        JOptionPane.ERROR_MESSAGE
                    );
                } finally {
                    cargandoPuntajes = false;
                    menuPrincipal.getBtnRanking().setEnabled(true);
                }
            }
        }.execute();
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
        menuGameOver.getBtnVolver().addActionListener(e -> volverAlMenuDesdeGameOver());
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

    private void volverAlMenuDesdeGameOver() {
        int respuesta = JOptionPane.showConfirmDialog(
            ventana,
            "Quisieras guardar tu progreso en la tabla de puntos?",
            "Guardar progreso",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        if (respuesta == JOptionPane.YES_OPTION) {
            guardarResultadoPartida(this::mostrarMenu);
            return;
        }
        mostrarMenu();
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
        guardarResultadoPartida(null);
    }

    private void guardarResultadoPartida(Runnable alTerminar) {
        if (guardandoPuntaje) {
            return;
        }
        guardandoPuntaje = true;
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
            finalizarGuardadoPuntaje(alTerminar);
            return;
        }
        if (nombre.isBlank()) {
            JOptionPane.showMessageDialog(
                ventana,
                "El nombre no puede estar vacío; el resultado no se guardó.",
                "Nombre inválido",
                JOptionPane.WARNING_MESSAGE
            );
            finalizarGuardadoPuntaje(alTerminar);
            return;
        }

        int puntuacion = controladorPartida.obtenerPuntaje();
        long tiempoSegundos = controladorPartida.obtenerTiempoPartidaSegundos();
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws SQLException {
                return servicioPuntajes.guardarMejorPuntaje(
                    nombre, puntuacion, tiempoSegundos
                );
            }

            @Override
            protected void done() {
                try {
                    boolean nuevoMejorPuntaje = get();
                    String mensaje = nuevoMejorPuntaje
                        ? "Puntaje guardado correctamente."
                        : "No superaste tu mejor puntaje anterior; se conservó el registro existente.";
                    JOptionPane.showMessageDialog(
                        ventana,
                        mensaje,
                        "Resultado de la partida",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                } catch (InterruptedException excepcion) {
                    Thread.currentThread().interrupt();
                    JOptionPane.showMessageDialog(
                        ventana,
                        "No se pudo guardar el puntaje en SQLite: " + excepcion.getMessage(),
                        "Error de base de datos",
                        JOptionPane.ERROR_MESSAGE
                    );
                } catch (java.util.concurrent.ExecutionException excepcion) {
                    Throwable causa = excepcion.getCause();
                    JOptionPane.showMessageDialog(
                        ventana,
                        "No se pudo guardar el puntaje en SQLite: "
                            + (causa == null ? excepcion.getMessage() : causa.getMessage()),
                        "Error de base de datos",
                        JOptionPane.ERROR_MESSAGE
                    );
                } finally {
                    finalizarGuardadoPuntaje(alTerminar);
                }
            }
        }.execute();
    }

    private void finalizarGuardadoPuntaje(Runnable alTerminar) {
        guardandoPuntaje = false;
        if (alTerminar != null) {
            alTerminar.run();
        }
    }
}