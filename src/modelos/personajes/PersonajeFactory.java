package modelos.personajes;
/**
 * Clase PersonajeFactory
 * Esta clase implementa el patrón Factory para centralizar la creación de personajes.
 * Permite crear diferentes tipos de ratas (subclases de Personaje) según el tipo especificado.
 */
public class PersonajeFactory {

    /**
     * Método estático para crear un personaje.
     * @param nombre El nombre del personaje.
     * @param tipoRata El tipo de rata (enum TipoRata) que determina la subclase a instanciar.
     * @return Una instancia de la subclase correspondiente de Personaje.
     * @throws IllegalArgumentException Si el tipo de rata no es reconocido.
     */
    public static Personaje crearPersonaje(String nombre, Personaje.TipoRata tipoRata) {

        switch (tipoRata) {
            case GRIS -> {
                return new RataGris(nombre);
            }
            case BLANCA -> {
                return new RataBlanca(nombre);
            }
            case NEGRA -> {
                return new RataNegra(nombre);
            }
            case VERDE -> {
                return new RataVerde(nombre);
            }
            default -> 
                throw new IllegalArgumentException("Tipo de rata no reconocido: " + tipoRata);
        }
    }
}