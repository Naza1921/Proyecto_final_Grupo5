package modelos.personajes;

public final class FabricaRataGris extends FabricaRata {
    @Override
    protected Personaje crearPersonaje() {
        return new rata_gris("Rata Gris");
    }
}