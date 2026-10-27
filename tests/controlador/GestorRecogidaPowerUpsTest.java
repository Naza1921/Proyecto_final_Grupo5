package controlador;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import modelos.items.PowerUp;
import modelos.items.PowerUpLento;
import modelos.items.PowerUpPizza;
import modelos.personajes.Personaje;
import modelos.personajes.rata_blanca;

public final class GestorRecogidaPowerUpsTest {
    public static void main(String[] args) {
        verificarRecogidaDePowerUp();
        verificarPowerDown();
        verificarQueNoSeRecojaSinColision();
        System.out.println("GestorRecogidaPowerUpsTest: OK");
    }

    private static void verificarRecogidaDePowerUp() {
        Personaje jugador = crearJugadorEn(0, 0);
        PowerUp pizza = new PowerUpPizza(3_000, 0, 0);
        ListaEstado estado = new ListaEstado(List.of(pizza));
        GestorEfectosTemporales efectos = new GestorEfectosTemporales(jugador);
        GestorRecogidaPowerUps gestor = crearGestor(estado, efectos);

        exigir(gestor.recoger(pizza, 0), "La pizza colisionante debe recogerse");
        exigir(jugador.esInvulnerable(), "La pizza debe aplicar invulnerabilidad");
        exigir(estado.puntos == 1_000, "La pizza debe otorgar 1000 puntos");
        exigir(estado.ultimoSonido == GestorEfectosSonido.Efecto.POWER_UP,
            "La pizza debe reproducir el sonido de power-up");
        exigir(estado.inventarioVisible.isEmpty(),
            "La pizza recogida debe desaparecer de la vista");
        exigir(estado.actualizacionesPuntaje == 1,
            "La recogida debe actualizar el puntaje presentado");
        efectos.limpiar();
    }

    private static void verificarPowerDown() {
        Personaje jugador = crearJugadorEn(0, 0);
        PowerUp lento = new PowerUpLento(5_000, 0, 0);
        ListaEstado estado = new ListaEstado(List.of(lento));
        GestorRecogidaPowerUps gestor = crearGestor(
            estado, new GestorEfectosTemporales(jugador)
        );

        gestor.actualizar(0);

        exigir(estado.penalizacion == 300, "El power-down debe penalizar 300 puntos");
        exigir(estado.ultimoSonido == GestorEfectosSonido.Efecto.POWER_DOWN,
            "El power-down debe reproducir su sonido");
        exigir(estado.inventarioVisible.isEmpty(),
            "El power-down recogido debe desaparecer de la vista");
        exigir(estado.actualizacionesPuntaje == 1,
            "La penalización debe actualizar el puntaje presentado");
    }

    private static void verificarQueNoSeRecojaSinColision() {
        Personaje jugador = crearJugadorEn(500, 500);
        PowerUp pizza = new PowerUpPizza(3_000, 0, 0);
        ListaEstado estado = new ListaEstado(List.of(pizza));
        GestorRecogidaPowerUps gestor = crearGestor(
            estado, new GestorEfectosTemporales(jugador)
        );

        exigir(!gestor.recoger(pizza, 0), "No debe recogerse un power-up distante");
        exigir(estado.puntos == 0 && estado.penalizacion == 0,
            "Una no-colisión no debe cambiar el puntaje");
        exigir(estado.sonidos.isEmpty(), "Una no-colisión no debe reproducir sonidos");
        exigir(estado.inventarioVisible.equals(List.of(pizza)),
            "Una no-colisión debe conservar el power-up visible");
    }

    private static Personaje crearJugadorEn(double x, double y) {
        Personaje jugador = new rata_blanca("Prueba");
        jugador.setX(x);
        jugador.setY(y);
        return jugador;
    }

    private static GestorRecogidaPowerUps crearGestor(
            ListaEstado estado, GestorEfectosTemporales efectos) {
        return new GestorRecogidaPowerUps(
            Map.of(0, estado.inventarioVisible),
            efectos,
            puntos -> estado.puntos += puntos,
            puntos -> estado.penalizacion += puntos,
            sonido -> {
                estado.ultimoSonido = sonido;
                estado.sonidos.add(sonido);
            },
            visibles -> estado.inventarioVisible = List.copyOf(visibles),
            () -> estado.actualizacionesPuntaje++
        );
    }

    private static void exigir(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
    }

    private static final class ListaEstado {
        private List<PowerUp> inventarioVisible;
        private final List<GestorEfectosSonido.Efecto> sonidos = new ArrayList<>();
        private GestorEfectosSonido.Efecto ultimoSonido;
        private int puntos;
        private int penalizacion;
        private int actualizacionesPuntaje;

        private ListaEstado(List<PowerUp> inventario) {
            inventarioVisible = new ArrayList<>(inventario);
        }
    }
}
