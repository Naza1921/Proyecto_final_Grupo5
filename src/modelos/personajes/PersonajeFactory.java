package modelos.personajes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Supplier;

/**
 * Simple Factory que centraliza la creación de las distintas ratas.
 */
public final class PersonajeFactory {
    private static final LinkedHashMap<String, Supplier<Personaje>> FABRICAS = crearFabricas();
    private static final List<String> CLAVES_DISPONIBLES = List.copyOf(FABRICAS.keySet());

    private PersonajeFactory() {
    }

    public static Personaje crearPersonaje(String clave) {
        Supplier<Personaje> fabrica = FABRICAS.get(clave);
        if (fabrica == null) {
            throw new IllegalArgumentException("No existe una rata registrada con la clave: " + clave);
        }
        return fabrica.get();
    }

    public static List<String> clavesDisponibles() {
        return CLAVES_DISPONIBLES;
    }

    private static LinkedHashMap<String, Supplier<Personaje>> crearFabricas() {
        LinkedHashMap<String, Supplier<Personaje>> fabricas = new LinkedHashMap<>();
        registrar(fabricas, () -> new rata_gris("Rata Gris"));
        registrar(fabricas, () -> new rata_blanca("Rata Blanca"));
        registrar(fabricas, () -> new rata_negra("Rata Negra"));
        registrar(fabricas, () -> new rata_verde("Rata Verde"));
        return fabricas;
    }

    private static void registrar(LinkedHashMap<String, Supplier<Personaje>> fabricas,
                                  Supplier<Personaje> fabrica) {
        Personaje muestra = fabrica.get();
        fabricas.put(muestra.getClaveSprite(), fabrica);
    }
}
