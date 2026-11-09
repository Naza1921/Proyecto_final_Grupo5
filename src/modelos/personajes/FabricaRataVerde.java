package modelos.personajes;

public final class FabricaRataVerde extends FabricaRata {
    @Override
    protected Personaje crearPersonaje() {
        return new rata_verde("Rata Verde");
    }
}