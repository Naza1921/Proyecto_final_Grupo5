package controlador;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import modelos.escenario.levels.level;
import modelos.enemigos.enemigo;
import modelos.enemigos.fantasma;
import modelos.enemigos.ratainfestada;
import modelos.personajes.Personaje;

final class GestorEnemigosPartida {
    private static final int PUNTOS_DERROTAR_FANTASMA = 300;
    private static final int PUNTOS_DERROTAR_RATA_INFESTADA = 500;

    private final ConfiguradorEnemigosSeccion configuradorEnemigos;
    private final BiConsumer<List<fantasma>, List<ratainfestada>> actualizarVistaEnemigos;
    private final Personaje jugador;
    private final levelmanager niveles;
    private List<fantasma> fantasmasVisibles = List.of();
    private List<ratainfestada> ratasInfestadasVisibles = List.of();
    private final Set<fantasma> fantasmasPremiados =
        Collections.newSetFromMap(new IdentityHashMap<>());
    private final Set<ratainfestada> ratasInfestadasPremiadas =
        Collections.newSetFromMap(new IdentityHashMap<>());

    GestorEnemigosPartida(
            ConfiguradorEnemigosSeccion configuradorEnemigos,
            BiConsumer<List<fantasma>, List<ratainfestada>> actualizarVistaEnemigos,
            Personaje jugador,
            levelmanager niveles) {
        if (configuradorEnemigos == null || actualizarVistaEnemigos == null
                || jugador == null || niveles == null) {
            throw new IllegalArgumentException("Se requieren las referencias del juego y enemigos");
        }
        this.configuradorEnemigos = configuradorEnemigos;
        this.actualizarVistaEnemigos = actualizarVistaEnemigos;
        this.jugador = jugador;
        this.niveles = niveles;
    }

    void configurarVisibles(int seccion) {
        if (seccion < 0 || seccion != niveles.getSeccion()) {
            throw new IllegalArgumentException("La sección de enemigos debe coincidir con el nivel actual");
        }
        jugador.prepararCambioDeSeccion();

        ConfiguradorEnemigosSeccion.EnemigosVisibles enemigos =
            configuradorEnemigos.configurar(niveles.getNivel(), seccion);
        fantasmasVisibles = enemigos.fantasmas();
        ratasInfestadasVisibles = enemigos.ratasInfestadas();
        fantasmasPremiados.clear();
        ratasInfestadasPremiadas.clear();
        actualizarVistaEnemigos.accept(fantasmasVisibles, ratasInfestadasVisibles);
    }

    List<enemigo> actualizar(
            level nivel,
            int tile,
            int anchoJugador,
            int altoJugador,
            IntConsumer penalizarPuntaje,
            int penalizacionGolpeFantasma) {
        if (nivel == null || penalizarPuntaje == null || tile <= 0
                || anchoJugador <= 0 || altoJugador <= 0 || penalizacionGolpeFantasma < 0) {
            throw new IllegalArgumentException("Se requieren parámetros válidos para actualizar enemigos");
        }
        List<enemigo> enemigosActivos = new ArrayList<>(
            fantasmasVisibles.size() + ratasInfestadasVisibles.size()
        );

        for (fantasma enemigo : fantasmasVisibles) {
            enemigosActivos.add(enemigo);
            enemigo.actualizarPatrulla(enemigo.getY());
            int vidaAntesDelGolpe = jugador.getVida();
            enemigo.atacar(jugador, anchoJugador, altoJugador);
            if (jugador.getVida() < vidaAntesDelGolpe) {
                penalizarPuntaje.accept(penalizacionGolpeFantasma);
            }
        }

        for (ratainfestada rataInfestada : ratasInfestadasVisibles) {
            enemigosActivos.add(rataInfestada);
            rataInfestada.atacar(jugador);
            rataInfestada.actualizar(jugador, nivel, tile);
            rataInfestada.actualizarProyectiles(jugador);
        }

        return enemigosActivos;
    }

    void registrarEnemigosDerrotados(
            IntConsumer sumarPuntos,
            Consumer<fantasma> mostrarMuerteFantasma,
            Consumer<ratainfestada> mostrarMuerteRataInfestada) {
        if (sumarPuntos == null || mostrarMuerteFantasma == null
                || mostrarMuerteRataInfestada == null) {
            throw new IllegalArgumentException("Se requieren acciones para registrar las derrotas");
        }
        for (fantasma enemigo : fantasmasVisibles) {
            if (!enemigo.estavivo() && fantasmasPremiados.add(enemigo)) {
                sumarPuntos.accept(PUNTOS_DERROTAR_FANTASMA);
                mostrarMuerteFantasma.accept(enemigo);
            }
        }
        for (ratainfestada rataInfestada : ratasInfestadasVisibles) {
            if (!rataInfestada.estavivo() && ratasInfestadasPremiadas.add(rataInfestada)) {
                sumarPuntos.accept(PUNTOS_DERROTAR_RATA_INFESTADA);
                mostrarMuerteRataInfestada.accept(rataInfestada);
            }
        }
    }
}
