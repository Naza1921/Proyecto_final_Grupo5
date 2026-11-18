package controlador;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import modelos.enemigos.Fantasma;
import modelos.enemigos.RataInfestada;
import modelos.items.FabricaPowerUps;
import modelos.items.Moneda;
import modelos.items.PowerUp;
import modelos.partida.ConfiguracionPartida;
import modelos.partida.Puntaje;
import modelos.escenario.levels.Level;
import modelos.personajes.Personaje;
import recursos.CargadorRecursos;
import servicios.ServicioEfectosSonido;
import vista.PanelJuego;
import vista.RecursosJuego;

final class CreadorPartida {
    private static final double ANCHO_FANTASMA = 36;
    private static final double ALTO_FANTASMA = 41;

    private final ServicioEfectosSonido efectosSonido;

    CreadorPartida(ServicioEfectosSonido efectosSonido) {
        if (efectosSonido == null) {
            throw new IllegalArgumentException("Se requiere el gestor de efectos de sonido");
        }
        this.efectosSonido = efectosSonido;
    }

    PartidaPreparada preparar(
            Personaje jugador,
            Runnable alTerminarAnimacionMuerte) {
        if (jugador == null || alTerminarAnimacionMuerte == null) {
            throw new IllegalArgumentException("Se requieren el personaje y los datos de carga");
        }

        LevelManager niveles = new LevelManager();
        BufferedImage[] fondosJuego = cargarFondosJuego();
        BufferedImage tutorialControles = CargadorRecursos.cargarImagen(
            "assets/tutorial_controles.png"
        );
        RecursosJuego recursosJuego = RecursosJuego.preparar(
            new PresentadorPartida().presentar(jugador),
            alTerminarAnimacionMuerte
        );
        return new PartidaPreparada(niveles, fondosJuego, tutorialControles, recursosJuego);
    }

    PartidaCreada crear(
            Personaje jugador,
            PartidaPreparada preparacion,
            Runnable alTerminarAnimacionMuerte,
            Runnable alPausar,
            Runnable alCompletarNivel) {
        if (jugador == null || preparacion == null || alTerminarAnimacionMuerte == null
                || alPausar == null || alCompletarNivel == null) {
            throw new IllegalArgumentException("Se requieren el personaje y las acciones de la partida");
        }

        jugador.setX(ConfiguracionPartida.POSICION_INICIAL_X);
        jugador.setY(ConfiguracionPartida.POSICION_INICIAL_Y);

        ControladorRata controladorRata = new RataController(jugador);
        GestorControlesRatas gestorControles = new GestorControlesRatas(controladorRata);
        GestorEfectosTemporales gestorEfectos = new GestorEfectosTemporales(jugador);
        Puntaje puntaje = new Puntaje();
        ControladorPuntaje controladorPuntaje = new ControladorPuntaje(puntaje);
        List<Fantasma> fantasmas = new ArrayList<>();
        List<RataInfestada> ratasInfestadas = new ArrayList<>();
        for (int indice = 0; indice < Level.MAX_ENEMIGOS_POR_TIPO; indice++) {
            fantasmas.add(new Fantasma(
                "Fantasma " + (indice + 1), 2.0, 0, 0,
                ConfiguracionPartida.TAM_TILE, ANCHO_FANTASMA, ALTO_FANTASMA
            ));
            ratasInfestadas.add(
                new RataInfestada("Rata Infestada " + (indice + 1), 2.0, 0)
            );
        }

        LevelManager niveles = preparacion.niveles();
        PresentadorPartida presentador = new PresentadorPartida();
        PanelJuego panelJuego = new PanelJuego(
            presentador.presentar(jugador), puntaje.getTotal(), List.of(), niveles,
            alTerminarAnimacionMuerte, preparacion.fondosJuego(),
            preparacion.tutorialControles(), presentador.presentarTutorial(),
            preparacion.recursosJuego()
        );
        Map<Integer, List<PowerUp>> powerUpsPorNivel =
            FabricaPowerUps.crearPorNivel(niveles.getNiveles());
        Map<Integer, List<Moneda>> monedasPorSeccion = crearMonedasPorSeccion();
        ControladorPartida controladorPartida = new ControladorPartida(
            controladorRata, gestorControles, gestorEfectos, efectosSonido,
            controladorPuntaje, panelJuego, presentador, niveles, powerUpsPorNivel,
            monedasPorSeccion, jugador, fantasmas, ratasInfestadas, alCompletarNivel
        );
        ControladorTecladoRata controladorTecladoRata = new ControladorTecladoRata(
            panelJuego,
            gestorControles,
            alPausar,
            () -> efectosSonido.reproducir(ServicioEfectosSonido.Efecto.SALTO),
            controladorPartida::activarHabilidad
        );
        return new PartidaCreada(
            panelJuego, gestorEfectos, controladorPartida, controladorTecladoRata
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

    private static BufferedImage[] cargarFondosJuego() {
        String[] rutas = {
            "assets/escenarios/Nivel_1_Piso_mayor.png",
            "assets/escenarios/Nivel_2_Cocina.png",
            "assets/escenarios/Nivel_3_Gran_Salon.png",
            "assets/escenarios/Nivel_4_Pasillo_oscuro.png",
            "assets/escenarios/Nivel_5_Server_Room.png"
        };
        BufferedImage[] fondos = new BufferedImage[rutas.length];
        for (int indice = 0; indice < rutas.length; indice++) {
            fondos[indice] = CargadorRecursos.cargarImagen(rutas[indice]);
        }
        return fondos;
    }

    record PartidaCreada(
        PanelJuego panelJuego,
        GestorEfectosTemporales gestorEfectos,
        ControladorPartida controladorPartida,
        ControladorTecladoRata controladorTecladoRata
    ) { }

    record PartidaPreparada(
        LevelManager niveles,
        BufferedImage[] fondosJuego,
        BufferedImage tutorialControles,
        RecursosJuego recursosJuego
    ) { }
}
