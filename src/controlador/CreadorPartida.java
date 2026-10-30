package controlador;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    PartidaCreada crear(
            Personaje jugador,
            Runnable alTerminarAnimacionMuerte,
            Runnable alPausar,
            Runnable alCompletarNivel) {
        if (jugador == null || alTerminarAnimacionMuerte == null
                || alPausar == null || alCompletarNivel == null) {
            throw new IllegalArgumentException("Se requieren el personaje y las acciones de la partida");
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

        levelmanager niveles = new levelmanager();
        BufferedImage[] fondosJuego = cargarFondosJuego();
        BufferedImage tutorialControles = CargadorRecursos.cargarImagen(
            "assets/tutorial_controles.png"
        );
        PanelJuego panelJuego = new PanelJuego(
            jugador, puntaje, fantasmas, ratasInfestadas, niveles,
            alTerminarAnimacionMuerte, fondosJuego, tutorialControles
        );
        Map<Integer, List<PowerUp>> powerUpsPorNivel =
            FabricaPowerUps.crearPorNivel(niveles.getNiveles());
        Map<Integer, List<Moneda>> monedasPorSeccion = crearMonedasPorSeccion();
        ControladorPartida controladorPartida = new ControladorPartida(
            controladorRata, gestorControles, gestorEfectos, efectosSonido,
            controladorPuntaje, panelJuego, niveles, powerUpsPorNivel,
            monedasPorSeccion, jugador, fantasmas, ratasInfestadas, alCompletarNivel
        );
        ControladorTecladoRata controladorTecladoRata = new ControladorTecladoRata(
            panelJuego,
            gestorControles,
            alPausar,
            () -> efectosSonido.reproducir(GestorEfectosSonido.Efecto.SALTO),
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
            "assets/escenarios/Nivel_1_Top_Floor X.png",
            "assets/escenarios/Nivel_2_Kitchen_x.png",
            "assets/escenarios/Pasillo_oscuro.png",
            "assets/escenarios/Nivel_4_Hallway_x.png",
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
}
