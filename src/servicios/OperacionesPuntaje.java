package servicios;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import persistencia.ResultadoPuntaje;

public interface OperacionesPuntaje extends AutoCloseable {
    CompletableFuture<Boolean> guardarMejorPuntajeAsync(
        String nombre, int puntuacion, long tiempoSegundos
    );

    CompletableFuture<List<ResultadoPuntaje>> listarMejoresPuntajesAsync();

    @Override
    void close();
}
