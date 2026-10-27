package modelos.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import modelos.escenario.levels.level;
import modelos.partida.ConfiguracionPartida;

public final class FabricaPowerUps {
    private static final long DURACION_VELOCIDAD_MS = 5_000;
    private static final long DURACION_LENTO_MS = 5_000;
    private static final long DURACION_INVULNERABILIDAD_MS = 3_000;

    private FabricaPowerUps() {
    }

    public static Map<Integer, List<PowerUp>> crearPorNivel(List<level> niveles) {
        if (niveles == null || niveles.isEmpty() || niveles.stream().anyMatch(nivel -> nivel == null)) {
            throw new IllegalArgumentException("Se requieren niveles válidos para crear power-ups");
        }

        Map<Integer, List<PowerUp>> resultado = new HashMap<>();
        for (int indiceNivel = 0; indiceNivel < niveles.size(); indiceNivel++) {
            level nivel = niveles.get(indiceNivel);
            List<PowerUp> powerUps = crearDelMapa(nivel);
            if (powerUps.stream().noneMatch(PowerUpPizza.class::isInstance)) {
                powerUps.add(crearPizzaEnUbicacionDisponible(nivel, powerUps));
            }
            if (indiceNivel == 1) {
                powerUps.add(new PowerUpVida(
                    20 * ConfiguracionPartida.TAM_TILE,
                    21 * ConfiguracionPartida.TAM_TILE
                ));
            }
            resultado.put(indiceNivel, List.copyOf(powerUps));
        }
        return Map.copyOf(resultado);
    }

    private static List<PowerUp> crearDelMapa(level nivel) {
        List<PowerUp> powerUps = new ArrayList<>();
        for (level.MarcadorPowerUp marcador : nivel.getMarcadoresPowerUps()) {
            double x = marcador.columna() * (double) ConfiguracionPartida.TAM_TILE;
            double y = marcador.fila() * (double) ConfiguracionPartida.TAM_TILE;
            powerUps.add(switch (marcador.tipo()) {
                case VELOCIDAD -> new PowerUpVelocidad(DURACION_VELOCIDAD_MS, x, y);
                case LENTO -> new PowerUpLento(DURACION_LENTO_MS, x, y);
                case INVULNERABILIDAD ->
                    new PowerUpPizza(DURACION_INVULNERABILIDAD_MS, x, y);
            });
        }
        return powerUps;
    }

    private static PowerUpPizza crearPizzaEnUbicacionDisponible(
            level nivel, List<PowerUp> powerUps) {
        int centro = nivel.getAncho() / 2;
        int tile = ConfiguracionPartida.TAM_TILE;
        for (int fila = nivel.getAlto() - 2; fila > 0; fila--) {
            int mejorColumna = -1;
            int menorDistancia = Integer.MAX_VALUE;
            for (int columna = 0; columna < nivel.getAncho(); columna++) {
                if (!esUbicacionTransitable(nivel, columna, fila)
                        || estaOcupada(powerUps, columna, fila)) {
                    continue;
                }
                int distancia = Math.abs(columna - centro);
                if (distancia < menorDistancia) {
                    mejorColumna = columna;
                    menorDistancia = distancia;
                }
            }
            if (mejorColumna >= 0) {
                return new PowerUpPizza(
                    DURACION_INVULNERABILIDAD_MS,
                    mejorColumna * (double) tile,
                    fila * (double) tile
                );
            }
        }
        throw new IllegalStateException(
            "No se encontró una ubicación transitable para la pizza del nivel"
        );
    }

    private static boolean esUbicacionTransitable(level nivel, int columna, int fila) {
        return !nivel.esSolido(columna, fila)
            && !nivel.esSolido(columna, fila - 1)
            && nivel.esSolido(columna, fila + 1);
    }

    private static boolean estaOcupada(List<PowerUp> powerUps, int columna, int fila) {
        double tamanoTile = ConfiguracionPartida.TAM_TILE;
        return powerUps.stream().anyMatch(powerUp ->
            Math.floor(powerUp.getX() / tamanoTile) == columna
                && Math.floor(powerUp.getY() / tamanoTile) == fila
        );
    }
}
