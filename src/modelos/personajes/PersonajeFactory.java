package modelos.personajes;

public class PersonajeFactory {    public static Personaje crearPersonaje(String nombre, Personaje.TipoRata tipoRata) {
    switch (tipoRata) {
        case GRIS:
            return new rata_gris(nombre);
        case BLANCA:
            return new rata_blanca(nombre);
        case NEGRA:
            return new rata_negra(nombre);
        case VERDE:
            return new rata_verde(nombre);
        default:
            throw new IllegalArgumentException("Tipo de rata no reconocido: " + tipoRata);
    }
}
}
