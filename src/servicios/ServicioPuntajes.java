package servicios;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import persistencia.PuntajeDAO;
import persistencia.ResultadoPuntaje;

public final class ServicioPuntajes implements AutoCloseable {
    private static final int CANTIDAD_PUNTAJES_TABLERO = 10;

    private final PuntajeDAO puntajeDAO;
    private final ExecutorService ejecutor = Executors.newSingleThreadExecutor(tarea -> {
        Thread hilo = new Thread(tarea, "persistencia-puntajes");
        hilo.setDaemon(true);
        return hilo;
    });

    public ServicioPuntajes(PuntajeDAO puntajeDAO) {
        if (puntajeDAO == null) {
            throw new IllegalArgumentException("Se requiere un DAO de puntajes");
        }
        this.puntajeDAO = puntajeDAO;
    }

    public boolean guardarMejorPuntaje(String nombre, int puntuacion, long tiempoSegundos)
            throws SQLException {
        ResultadoPuntaje resultado = new ResultadoPuntaje(nombre, puntuacion, tiempoSegundos);
        return puntajeDAO.guardarMejorPuntaje(resultado);
    }

    public List<ResultadoPuntaje> listarMejoresPuntajes() throws SQLException {
        return puntajeDAO.listarMejoresPuntajes().stream()
            .limit(CANTIDAD_PUNTAJES_TABLERO)
            .toList();
    }

    public CompletableFuture<Boolean> guardarMejorPuntajeAsync(
            String nombre, int puntuacion, long tiempoSegundos) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return guardarMejorPuntaje(nombre, puntuacion, tiempoSegundos);
            } catch (SQLException excepcion) {
                throw new java.util.concurrent.CompletionException(excepcion);
            }
        }, ejecutor);
    }

    public CompletableFuture<List<ResultadoPuntaje>> listarMejoresPuntajesAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return listarMejoresPuntajes();
            } catch (SQLException excepcion) {
                throw new java.util.concurrent.CompletionException(excepcion);
            }
        }, ejecutor);
    }

    @Override
    public void close() {
        ejecutor.shutdown();
    }
}
