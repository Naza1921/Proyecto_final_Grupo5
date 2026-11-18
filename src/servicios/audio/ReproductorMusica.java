package servicios.audio;

import java.io.IOException;

public interface ReproductorMusica extends AutoCloseable {
    void seleccionarCancion(int numeroCancion) throws IOException;

    void habilitarReproduccion();

    void detener();

    void setMusicaActivada(boolean activada);

    void setVolumen(int porcentaje);

    @Override
    void close();
}
