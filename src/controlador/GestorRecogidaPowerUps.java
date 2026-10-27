package controlador;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import modelos.items.PowerUp;
import modelos.items.RecompensaPowerUp;

final class GestorRecogidaPowerUps {
    private final Map<Integer, List<PowerUp>> powerUpsPorNivel;
    private final GestorEfectosTemporales gestorEfectos;
    private final IntConsumer agregarPuntos;
    private final IntConsumer penalizarPuntos;
    private final Consumer<GestorEfectosSonido.Efecto> reproducirSonido;
    private final Consumer<List<PowerUp>> actualizarVista;
    private final Runnable actualizarPuntaje;
    private int nivelPresentado = -1;

    GestorRecogidaPowerUps(
            Map<Integer, List<PowerUp>> powerUpsPorNivel,
            GestorEfectosTemporales gestorEfectos,
            IntConsumer agregarPuntos,
            IntConsumer penalizarPuntos,
            Consumer<GestorEfectosSonido.Efecto> reproducirSonido,
            Consumer<List<PowerUp>> actualizarVista,
            Runnable actualizarPuntaje) {
        if (powerUpsPorNivel == null || gestorEfectos == null
                || agregarPuntos == null || penalizarPuntos == null || reproducirSonido == null
                || actualizarVista == null || actualizarPuntaje == null) {
            throw new IllegalArgumentException("Se requieren dependencias válidas para los power-ups");
        }
        this.powerUpsPorNivel = copiarPowerUpsPorNivel(powerUpsPorNivel);
        this.gestorEfectos = gestorEfectos;
        this.agregarPuntos = agregarPuntos;
        this.penalizarPuntos = penalizarPuntos;
        this.reproducirSonido = reproducirSonido;
        this.actualizarVista = actualizarVista;
        this.actualizarPuntaje = actualizarPuntaje;
    }

    void actualizar(int indiceNivel) {
        validarIndiceNivel(indiceNivel);
        List<PowerUp> disponibles = powerUpsPorNivel.get(indiceNivel);
        if (indiceNivel != nivelPresentado) {
            actualizarVista.accept(disponibles == null ? List.of() : List.copyOf(disponibles));
            nivelPresentado = indiceNivel;
        }
        if (disponibles == null) {
            return;
        }

        boolean inventarioModificado = false;
        Iterator<PowerUp> iterador = disponibles.iterator();
        while (iterador.hasNext()) {
            PowerUp powerUp = iterador.next();
            if (recogerSiColisiona(powerUp)) {
                iterador.remove();
                inventarioModificado = true;
            }
        }
        if (inventarioModificado) {
            actualizarVista.accept(List.copyOf(disponibles));
        }
    }

    boolean recoger(PowerUp powerUp, int indiceNivel) {
        if (powerUp == null) {
            throw new IllegalArgumentException("Se requiere un power-up");
        }
        validarIndiceNivel(indiceNivel);
        List<PowerUp> disponibles = powerUpsPorNivel.get(indiceNivel);
        if (disponibles == null || !disponibles.contains(powerUp)
                || !recogerSiColisiona(powerUp)) {
            return false;
        }

        disponibles.remove(powerUp);
        actualizarVista.accept(List.copyOf(disponibles));
        nivelPresentado = indiceNivel;
        return true;
    }

    private boolean recogerSiColisiona(PowerUp powerUp) {
        RecompensaPowerUp recompensa = powerUp.getEfecto().getRecompensa();
        if (!gestorEfectos.recogerSiColisiona(powerUp)) {
            return false;
        }
        reproducirSonido.accept(
            recompensa.penalizacion()
                ? GestorEfectosSonido.Efecto.POWER_DOWN
                : GestorEfectosSonido.Efecto.POWER_UP
        );
        if (recompensa.penalizacion()) {
            penalizarPuntos.accept(recompensa.puntos());
        } else {
            agregarPuntos.accept(recompensa.puntos());
        }
        actualizarPuntaje.run();
        return true;
    }

    void reiniciarPresentacion() {
        nivelPresentado = -1;
    }

    private static Map<Integer, List<PowerUp>> copiarPowerUpsPorNivel(
            Map<Integer, List<PowerUp>> powerUpsPorNivel) {
        Map<Integer, List<PowerUp>> copia = new HashMap<>();
        for (Map.Entry<Integer, List<PowerUp>> entrada : powerUpsPorNivel.entrySet()) {
            Integer indiceNivel = entrada.getKey();
            List<PowerUp> powerUps = entrada.getValue();
            if (indiceNivel == null || indiceNivel < 0 || powerUps == null
                    || powerUps.stream().anyMatch(powerUp -> powerUp == null)) {
                throw new IllegalArgumentException(
                    "Cada nivel debe tener una lista válida de power-ups"
                );
            }
            copia.put(indiceNivel, new ArrayList<>(powerUps));
        }
        return copia;
    }

    private static void validarIndiceNivel(int indiceNivel) {
        if (indiceNivel < 0) {
            throw new IllegalArgumentException("El índice del nivel no puede ser negativo");
        }
    }
}
