package servicios;

import java.io.IOException;
import modelos.PreferenciasAjustes;

public interface RepositorioAjustes {
    PreferenciasAjustes cargar();

    void guardar(PreferenciasAjustes ajustes) throws IOException;
}
