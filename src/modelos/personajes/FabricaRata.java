package modelos.personajes;

public abstract class FabricaRata {
    protected abstract Personaje crearPersonaje();

    public final Personaje crearRata() {
        Personaje personaje = crearPersonaje();
        if (personaje == null) {
            throw new IllegalStateException("La fábrica no puede crear una rata nula");
        }
        String claveSprite = personaje.getClaveSprite();
        if (claveSprite == null || claveSprite.isBlank()) {
            throw new IllegalStateException("La rata creada debe tener una clave de sprite");
        }
        return personaje;
    }
}