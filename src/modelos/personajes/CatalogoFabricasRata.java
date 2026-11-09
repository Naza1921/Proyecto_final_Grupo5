package modelos.personajes;

import java.util.List;

public final class CatalogoFabricasRata {
    private static final List<FabricaRata> FABRICAS = List.of(
        new FabricaRataGris(),
        new FabricaRataBlanca(),
        new FabricaRataNegra(),
        new FabricaRataVerde()
    );

    private CatalogoFabricasRata() {
    }

    public static List<FabricaRata> obtenerFabricas() {
        return FABRICAS;
    }
}