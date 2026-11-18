package controlador;

import java.util.ArrayList;
import java.util.List;
import modelos.enemigos.Fantasma;
import modelos.enemigos.RataInfestada;
import modelos.escenario.levels.Level;
import modelos.partida.ConfiguracionPartida;

final class ConfiguradorEnemigosSeccion {
    private static final List<ConfiguracionPredeterminada> POSICIONES_PREDETERMINADAS =
        List.of(
            new ConfiguracionPredeterminada(
                new PosicionFantasma(190, 420, 390),
                new PosicionRataInfestada(600, 640)
            ),
            new ConfiguracionPredeterminada(
                new PosicionFantasma(500, 760, 310),
                null
            ),
            new ConfiguracionPredeterminada(
                null,
                new PosicionRataInfestada(900, 640)
            ),
            new ConfiguracionPredeterminada(
                new PosicionFantasma(700, 1000, 290),
                new PosicionRataInfestada(300, 640)
            ),
            new ConfiguracionPredeterminada(
                new PosicionFantasma(820, 1120, 350),
                null
            )
        );

    record EnemigosVisibles(List<Fantasma> fantasmas, List<RataInfestada> ratasInfestadas) {
        EnemigosVisibles {
            fantasmas = List.copyOf(fantasmas);
            ratasInfestadas = List.copyOf(ratasInfestadas);
        }
    }

    private record PosicionFantasma(double xMinimo, double xMaximo, double y) { }
    private record PosicionRataInfestada(double x, double y) { }
    private record ConfiguracionPredeterminada(
        PosicionFantasma fantasma,
        PosicionRataInfestada rataInfestada
    ) { }

    private final List<Fantasma> fantasmasDisponibles;
    private final List<RataInfestada> ratasDisponibles;

    ConfiguradorEnemigosSeccion(
            List<Fantasma> fantasmas, List<RataInfestada> ratasInfestadas) {
        if (fantasmas == null || ratasInfestadas == null
                || fantasmas.size() > Level.MAX_ENEMIGOS_POR_TIPO
                || ratasInfestadas.size() > Level.MAX_ENEMIGOS_POR_TIPO
                || fantasmas.stream().anyMatch(enemigo -> enemigo == null)
                || ratasInfestadas.stream().anyMatch(rata -> rata == null)) {
            throw new IllegalArgumentException("Se requieren listas válidas de enemigos");
        }
        fantasmasDisponibles = List.copyOf(fantasmas);
        ratasDisponibles = List.copyOf(ratasInfestadas);
    }

    EnemigosVisibles configurar(Level nivel, int indiceSeccion) {
        if (nivel == null || indiceSeccion < 0) {
            throw new IllegalArgumentException("Se requiere un nivel y sección válidos");
        }
        List<Level.PatrullaFantasma> patrullas = nivel.getPatrullasFantasma();
        List<Level.MarcadorEnemigo> marcadoresRatas = nivel.getMarcadoresEnemigos().stream()
            .filter(marcador -> marcador.tipo() == Level.TipoEnemigoMapa.RATA_INFESTADA)
            .toList();
        boolean usarPosicionesPredeterminadas = patrullas.isEmpty() && marcadoresRatas.isEmpty();
        ConfiguracionPredeterminada configuracionPredeterminada =
            usarPosicionesPredeterminadas
                ? POSICIONES_PREDETERMINADAS.get(
                    indiceSeccion % POSICIONES_PREDETERMINADAS.size()
                )
                : null;

        List<Fantasma> fantasmasActivos = configurarFantasmas(
            patrullas, usarPosicionesPredeterminadas, configuracionPredeterminada
        );
        List<RataInfestada> ratasActivas = configurarRatasInfestadas(
            marcadoresRatas, usarPosicionesPredeterminadas, configuracionPredeterminada
        );
        return new EnemigosVisibles(fantasmasActivos, ratasActivas);
    }

    private List<Fantasma> configurarFantasmas(
            List<Level.PatrullaFantasma> patrullas,
            boolean usarPosicionesPredeterminadas,
            ConfiguracionPredeterminada configuracionPredeterminada) {
        if (patrullas.size() > fantasmasDisponibles.size()) {
            throw new IllegalStateException(
                "No hay suficientes instancias para las patrullas del nivel"
            );
        }
        List<Fantasma> activos = new ArrayList<>();
        for (int indice = 0; indice < fantasmasDisponibles.size(); indice++) {
            Fantasma enemigo = fantasmasDisponibles.get(indice);
            if (usarPosicionesPredeterminadas && indice == 0
                    && configuracionPredeterminada.fantasma() != null) {
                PosicionFantasma posicion = configuracionPredeterminada.fantasma();
                enemigo.reiniciarPatrulla(
                    posicion.xMinimo(), posicion.xMaximo(), posicion.y()
                );
                activos.add(enemigo);
            } else if (!usarPosicionesPredeterminadas && indice < patrullas.size()) {
                Level.PatrullaFantasma patrulla = patrullas.get(indice);
                enemigo.reiniciarPatrulla(
                    patrulla.inicio().columna() * (double) ConfiguracionPartida.TAM_TILE,
                    patrulla.fin().columna() * (double) ConfiguracionPartida.TAM_TILE,
                    patrulla.inicio().fila() * (double) ConfiguracionPartida.TAM_TILE
                );
                activos.add(enemigo);
            } else {
                enemigo.reiniciarPatrulla(0, ConfiguracionPartida.TAM_TILE, 0);
            }
        }
        return activos;
    }

    private List<RataInfestada> configurarRatasInfestadas(
            List<Level.MarcadorEnemigo> marcadoresRatas,
            boolean usarPosicionesPredeterminadas,
            ConfiguracionPredeterminada configuracionPredeterminada) {
        if (marcadoresRatas.size() > ratasDisponibles.size()) {
            throw new IllegalStateException(
                "No hay suficientes instancias para las ratas del nivel"
            );
        }
        List<RataInfestada> activas = new ArrayList<>();
        for (int indice = 0; indice < ratasDisponibles.size(); indice++) {
            RataInfestada rata = ratasDisponibles.get(indice);
            if (usarPosicionesPredeterminadas && indice == 0
                    && configuracionPredeterminada.rataInfestada() != null) {
                PosicionRataInfestada posicion = configuracionPredeterminada.rataInfestada();
                rata.reiniciar(posicion.x(), posicion.y());
                activas.add(rata);
            } else if (!usarPosicionesPredeterminadas && indice < marcadoresRatas.size()) {
                Level.MarcadorEnemigo marcador = marcadoresRatas.get(indice);
                rata.reiniciar(
                    marcador.columna() * (double) ConfiguracionPartida.TAM_TILE,
                    marcador.fila() * (double) ConfiguracionPartida.TAM_TILE
                );
                activas.add(rata);
            } else {
                rata.reiniciar(0, 0);
            }
        }
        return activas;
    }
}
