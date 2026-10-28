package controlador;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import modelos.items.Moneda;
import modelos.personajes.Personaje;
import aplicacion.VistaPartida;

final class GestorMonedasPartida {
    private static final int PUNTOS_MONEDA = 100;

    private final Map<Integer, List<Moneda>> monedasPorSeccion;
    private final Personaje jugador;
    private final ControladorPuntaje controladorPuntaje;
    private final GestorEfectosSonido efectosSonido;
    private final VistaPartida vista;
    private final Runnable actualizarPuntajePresentado;
    private int seccionPresentada = -1;

    GestorMonedasPartida(
            Map<Integer, List<Moneda>> monedasPorSeccion,
            Personaje jugador,
            ControladorPuntaje controladorPuntaje,
            GestorEfectosSonido efectosSonido,
            VistaPartida vista,
            Runnable actualizarPuntajePresentado) {
        if (monedasPorSeccion == null || jugador == null || controladorPuntaje == null
                || efectosSonido == null || vista == null
                || actualizarPuntajePresentado == null) {
            throw new IllegalArgumentException("Se requieren datos válidos para gestionar las monedas");
        }
        this.monedasPorSeccion = copiarMonedasPorSeccion(monedasPorSeccion);
        this.jugador = jugador;
        this.controladorPuntaje = controladorPuntaje;
        this.efectosSonido = efectosSonido;
        this.vista = vista;
        this.actualizarPuntajePresentado = actualizarPuntajePresentado;
    }

    void actualizar(int seccionActual) {
        if (seccionActual < 0) {
            throw new IllegalArgumentException("La sección actual no puede ser negativa");
        }

        List<Moneda> monedas = monedasPorSeccion.get(seccionActual);
        if (seccionActual != seccionPresentada) {
            vista.actualizarMonedas(monedas == null ? List.of() : monedas);
            seccionPresentada = seccionActual;
        }
        if (monedas == null) {
            return;
        }

        boolean inventarioModificado = false;
        Iterator<Moneda> iterador = monedas.iterator();
        while (iterador.hasNext()) {
            Moneda moneda = iterador.next();
            if (moneda.colisionaCon(jugador)) {
                iterador.remove();
                controladorPuntaje.agregarPuntos(PUNTOS_MONEDA);
                efectosSonido.reproducir(GestorEfectosSonido.Efecto.MONEDA);
                inventarioModificado = true;
            }
        }
        if (inventarioModificado) {
            vista.actualizarMonedas(monedas);
            actualizarPuntajePresentado.run();
        }
    }

    void reiniciarPresentacion() {
        seccionPresentada = -1;
    }

    private static Map<Integer, List<Moneda>> copiarMonedasPorSeccion(
            Map<Integer, List<Moneda>> monedasPorSeccion) {
        Map<Integer, List<Moneda>> copia = new HashMap<>();
        for (Map.Entry<Integer, List<Moneda>> entrada : monedasPorSeccion.entrySet()) {
            Integer seccion = entrada.getKey();
            List<Moneda> monedas = entrada.getValue();
            if (seccion == null || seccion < 0 || monedas == null
                    || monedas.stream().anyMatch(moneda -> moneda == null)) {
                throw new IllegalArgumentException(
                    "Cada sección debe tener una lista válida de monedas"
                );
            }
            copia.put(seccion, new ArrayList<>(monedas));
        }
        return copia;
    }
}
