package modelos.personajes;

public final class FabricaRataNegra extends FabricaRata {
    @Override
    protected Personaje crearPersonaje() {
        return new rata_negra("Rata Negra");
    }
}