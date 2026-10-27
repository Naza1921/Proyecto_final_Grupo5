package modelos.items;

import controlador.levelmanager;
import java.util.List;
import java.util.Map;

public final class FabricaPowerUpsTest {
    public static void main(String[] args) {
        List<modelos.escenario.levels.level> niveles = new levelmanager().getNiveles();
        Map<Integer, List<PowerUp>> powerUpsPorNivel = FabricaPowerUps.crearPorNivel(niveles);

        exigir(niveles.size() == 22, "El juego debe cargar las 22 secciones actuales");
        exigir(powerUpsPorNivel.size() == niveles.size(),
            "Debe generarse inventario para cada sección");
        for (int indice = 0; indice < niveles.size(); indice++) {
            List<PowerUp> powerUps = powerUpsPorNivel.get(indice);
            exigir(powerUps != null && powerUps.stream().anyMatch(PowerUpPizza.class::isInstance),
                "La sección " + indice + " debe tener al menos una pizza");
            exigir(powerUps.stream().noneMatch(powerUp ->
                    powerUp.getEfecto() instanceof EfectoInvulnerabilidad
                        && !(powerUp instanceof PowerUpPizza)),
                "Solo la pizza debe conceder invulnerabilidad en sección " + indice);
        }
        exigir(powerUpsPorNivel.get(1).stream().anyMatch(PowerUpVida.class::isInstance),
            "El queso de vida de la segunda sección debe conservarse");
        System.out.println("FabricaPowerUpsTest: OK (" + niveles.size() + " secciones)");
    }

    private static void exigir(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
    }
}
