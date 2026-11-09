package modelos.personajes;

public final class FabricaRataBlanca extends FabricaRata {
    @Override
    protected Personaje crearPersonaje() {
        return new rata_blanca("Rata Blanca");
    }
}