package controlador;

import java.io.IOException;
import java.nio.file.Path;

public interface ReproductorMusica extends AutoCloseable {

    void seleccionarCancion(Path archivo) throws IOException;

    void habilitarReproduccion();

    void detener();

    void setMusicaActivada(boolean activada);

    void setVolumen(int porcentaje);

    @Override
    void close();
}
