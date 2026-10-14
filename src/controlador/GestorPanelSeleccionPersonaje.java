package controlador;

import java.util.function.Consumer;
import modelos.personajes.Personaje;
import modelos.personajes.Personaje.TipoRata;
import modelos.personajes.PersonajeFactory;

// Convierte la elección de la vista en un modelo y lo entrega al flujo de partida.
public class GestorPanelSeleccionPersonaje {
    private final Consumer<Personaje> alCrearPersonaje;

    public GestorPanelSeleccionPersonaje(Consumer<Personaje> alCrearPersonaje) {
        if (alCrearPersonaje == null) {
            throw new IllegalArgumentException("Se requiere una acción para entregar el personaje creado");
        }
        this.alCrearPersonaje = alCrearPersonaje;
    }

    public void seleccionarPersonaje(TipoRata tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("Se requiere un tipo de rata");
        }

        String nombre = switch (tipo) {
            case GRIS -> "Rata Gris";
            case BLANCA -> "Rata Blanca";
            case NEGRA -> "Rata Negra";
            case VERDE -> "Rata Verde";
        };
        alCrearPersonaje.accept(PersonajeFactory.crearPersonaje(nombre, tipo));
    }
}
