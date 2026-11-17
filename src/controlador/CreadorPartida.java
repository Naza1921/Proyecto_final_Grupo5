package controlador;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.swing.SwingUtilities;
import modelos.enemigos.fantasma;
import modelos.enemigos.ratainfestada;
import modelos.items.FabricaPowerUps;
import modelos.items.Moneda;
import modelos.items.PowerUp;
import modelos.partida.ConfiguracionPartida;
import modelos.partida.Puntaje;
import modelos.escenario.levels.level;
import modelos.personajes.Personaje;
import recursos.CargadorRecursos;
import vista.PanelJuego;

final class CreadorPartida {
    private static final double ANCHO_FANTASMA = 36;
    private static final double ALTO_FANTASMA = 41;

    private final GestorEfectosSonido efectosSonido;

    CreadorPartida(GestorEfectosSonido efectosSonido) {
        if (efectosSonido == null) {
            throw new IllegalArgumentException("Se requiere el gestor de efectos de sonido");
        }
        this.efectosSonido = efectosSonido;
    }

    PreparacionPartida preparar(
            Supplier<Personaje> crearJugador,
            Runnable alTerminarAnimacionMuerte,
            Consumer<ProgresoCarga> reportarProgreso) {
        if (SwingUtilities.isEventDispatchThread()) {
            throw new IllegalStateException("La preparación debe ejecutarse fuera del EDT");
        }
        if (crearJugador == null || alTerminarAnimacionMuerte == null
                || reportarProgreso == null) {
            throw new IllegalArgumentException("Se requieren las acciones de preparación");
        }

        return CargadorRecursos.observarCargas(
            archivo -> reportarProgreso.accept(new ProgresoCarga(archivo)),
            () -> prepararPartida(
                crearJugador, alTerminarAnimacionMuerte, reportarProgreso
            )
        );
    }

    private PreparacionPartida prepararPartida(
            Supplier<Personaje> crearJugador,
            Runnable alTerminarAnimacionMuerte,
            Consumer<ProgresoCarga> reportarProgreso) {
        Personaje jugador = crearJugador.get();
        if (jugador == null) {
            throw new IllegalArgumentException("La selección no produjo un personaje");
        }
        jugador.setX(ConfiguracionPartida.POSICION_INICIAL_X);
        jugador.setY(ConfiguracionPartida.POSICION_INICIAL_Y);

        ControladorRata controladorRata = new ratacontroller(jugador);
        GestorControlesRatas gestorControles = new GestorControlesRatas(controladorRata);
        GestorEfectosTemporales gestorEfectos = new GestorEfectosTemporales(jugador);
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

        String[] rutasFondos = {
            "assets/escenarios/Nivel_1_Piso_mayor.png",
            "assets/escenarios/Nivel_2_Cocina.png",
            "assets/escenarios/Nivel_3_Gran_Salon.png",
            "assets/escenarios/Nivel_4_Pasillo_oscuro.png",
            "assets/escenarios/Nivel_5_Server_Room.png"
        };
        ExecutorService grupoCargaFondos = Executors.newFixedThreadPool(2);
        try {
            List<CompletableFuture<BufferedImage>> tareasFondos =
                cargarFondosEnParalelo(rutasFondos, grupoCargaFondos, reportarProgreso);

            levelmanager niveles = new levelmanager();
            Map<Integer, List<PowerUp>> powerUpsPorNivel =
                FabricaPowerUps.crearPorNivel(niveles.getNiveles());
            Map<Integer, List<Moneda>> monedasPorSeccion = crearMonedasPorSeccion();
            BufferedImage tutorialControles = CargadorRecursos.cargarImagen(
                "assets/tutorial_controles.png"
            );
            PanelJuego.RecursosPreparados recursosVista = PanelJuego.prepararRecursos(
                jugador, fantasmas, ratasInfestadas, alTerminarAnimacionMuerte
            );
            BufferedImage[] fondosJuego = obtenerFondosEnOrden(tareasFondos);
            return new PreparacionPartida(
                jugador, controladorRata, gestorControles, gestorEfectos,
                puntaje, controladorPuntaje, fantasmas, ratasInfestadas, niveles,
                powerUpsPorNivel, monedasPorSeccion, fondosJuego, tutorialControles,
                recursosVista, alTerminarAnimacionMuerte
            );
        } finally {
            grupoCargaFondos.shutdownNow();
        }
    }

    PartidaCreada crear(
            PreparacionPartida preparacion,
            Runnable alPausar,
            Runnable alCompletarNivel) {
        if (!SwingUtilities.isEventDispatchThread()) {
            throw new IllegalStateException("La vista debe crearse en el EDT");
        }
        if (preparacion == null || alPausar == null || alCompletarNivel == null) {
            throw new IllegalArgumentException("Se requieren la preparación y las acciones de la partida");
        }
        PanelJuego panelJuego = new PanelJuego(
            preparacion.jugador, preparacion.puntaje,
            preparacion.fantasmas, preparacion.ratasInfestadas, preparacion.niveles,
            preparacion.alTerminarAnimacionMuerte, preparacion.fondosJuego,
            preparacion.tutorialControles, preparacion.recursosVista
        );
        ControladorPartida controladorPartida = new ControladorPartida(
            preparacion.controladorRata, preparacion.gestorControles,
            preparacion.gestorEfectos, efectosSonido,
            preparacion.controladorPuntaje, panelJuego, preparacion.niveles,
            preparacion.powerUpsPorNivel, preparacion.monedasPorSeccion,
            preparacion.jugador, preparacion.fantasmas,
            preparacion.ratasInfestadas, alCompletarNivel
        );
        ControladorTecladoRata controladorTecladoRata = new ControladorTecladoRata(
            panelJuego, preparacion.gestorControles,
            alPausar,
            () -> efectosSonido.reproducir(GestorEfectosSonido.Efecto.SALTO),
            controladorPartida::activarHabilidad
        );
        return new PartidaCreada(
            panelJuego, preparacion.gestorEfectos,
            controladorPartida, controladorTecladoRata
        );
    }

    private static Map<Integer, List<Moneda>> crearMonedasPorSeccion() {
        return Map.of(
            0, List.of(
                new Moneda(330, 640), new Moneda(480, 640), new Moneda(720, 640)
            ),
            1, List.of(new Moneda(450, 640), new Moneda(780, 640)),
            2, List.of(new Moneda(300, 640), new Moneda(660, 640)),
            3, List.of(new Moneda(510, 640), new Moneda(900, 640)),
            4, List.of(new Moneda(360, 640), new Moneda(750, 640))
        );
    }

    private static List<CompletableFuture<BufferedImage>> cargarFondosEnParalelo(
            String[] rutas, ExecutorService grupoCarga,
            Consumer<ProgresoCarga> reportarProgreso) {
        reportarProgreso.accept(new ProgresoCarga(
            "Cargando fondos (0 de " + rutas.length + ")"
        ));
        AtomicInteger fondosCargados = new AtomicInteger();
        Object bloqueoProgreso = new Object();
        List<CompletableFuture<BufferedImage>> tareas = new ArrayList<>(rutas.length);
        for (int indice = 0; indice < rutas.length; indice++) {
            String ruta = rutas[indice];
            tareas.add(CompletableFuture.supplyAsync(() -> {
                BufferedImage fondo = CargadorRecursos.cargarImagen(ruta);
                synchronized (bloqueoProgreso) {
                    int cantidadCargada = fondosCargados.incrementAndGet();
                    reportarProgreso.accept(new ProgresoCarga(
                        "Cargando fondos (" + cantidadCargada
                            + " de " + rutas.length + ")"
                    ));
                }
                return fondo;
            }, grupoCarga));
        }
        return tareas;
    }

    private static BufferedImage[] obtenerFondosEnOrden(
            List<CompletableFuture<BufferedImage>> tareas) {
        CompletableFuture<?>[] tareasCompletadas =
            tareas.toArray(CompletableFuture<?>[]::new);
        CompletableFuture.allOf(tareasCompletadas).join();
        BufferedImage[] fondos = new BufferedImage[tareas.size()];
        for (int indice = 0; indice < tareas.size(); indice++) {
            fondos[indice] = tareas.get(indice).join();
        }
        return fondos;
    }

    record ProgresoCarga(String archivo) { }

    record PreparacionPartida(
        Personaje jugador,
        ControladorRata controladorRata,
        GestorControlesRatas gestorControles,
        GestorEfectosTemporales gestorEfectos,
        Puntaje puntaje,
        ControladorPuntaje controladorPuntaje,
        List<fantasma> fantasmas,
        List<ratainfestada> ratasInfestadas,
        levelmanager niveles,
        Map<Integer, List<PowerUp>> powerUpsPorNivel,
        Map<Integer, List<Moneda>> monedasPorSeccion,
        BufferedImage[] fondosJuego,
        BufferedImage tutorialControles,
        PanelJuego.RecursosPreparados recursosVista,
        Runnable alTerminarAnimacionMuerte
    ) { }

    record PartidaCreada(
        PanelJuego panelJuego,
        GestorEfectosTemporales gestorEfectos,
        ControladorPartida controladorPartida,
        ControladorTecladoRata controladorTecladoRata
    ) { }
}
