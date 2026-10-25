package persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class SQLitePuntajeDAO implements PuntajeDAO {
    private static final String INSERTAR_PUNTAJE = """
        INSERT INTO score (Nombre, Puntuacion, Tiempo) VALUES (?, ?, ?)
        ON CONFLICT(Nombre) DO UPDATE SET
            Puntuacion = excluded.Puntuacion,
            Tiempo = excluded.Tiempo
        WHERE excluded.Puntuacion > score.Puntuacion
            OR (excluded.Puntuacion = score.Puntuacion
                AND excluded.Tiempo < score.Tiempo)
        """;

    private static final String LISTAR_PUNTAJES = """
        SELECT Nombre, Puntuacion, Tiempo
        FROM score
        ORDER BY Puntuacion DESC, Tiempo ASC, Nombre COLLATE NOCASE ASC
        """;

    private final ConexionSQLite conexionSQLite;

    public SQLitePuntajeDAO(ConexionSQLite conexionSQLite) {
        if (conexionSQLite == null) {
            throw new IllegalArgumentException("Se requiere el gestor de conexión SQLite");
        }
        this.conexionSQLite = conexionSQLite;
    }

    @Override
    public synchronized boolean guardarMejorPuntaje(ResultadoPuntaje resultado)
            throws SQLException {
        if (resultado == null) {
            throw new IllegalArgumentException("Se requiere un resultado de partida");
        }
        Connection conexion = conexionSQLite.obtenerConexion();
        synchronized (conexion) {
            try (PreparedStatement sentencia = conexion.prepareStatement(INSERTAR_PUNTAJE)) {
                sentencia.setString(1, resultado.getNombre());
                sentencia.setInt(2, resultado.getPuntuacion());
                sentencia.setLong(3, resultado.getTiempoSegundos());
                return sentencia.executeUpdate() > 0;
            }
        }
    }

    @Override
    public synchronized List<ResultadoPuntaje> listarMejoresPuntajes() throws SQLException {
        Connection conexion = conexionSQLite.obtenerConexion();
        List<ResultadoPuntaje> resultados = new ArrayList<>();
        synchronized (conexion) {
            try (PreparedStatement sentencia = conexion.prepareStatement(LISTAR_PUNTAJES);
                 ResultSet filas = sentencia.executeQuery()) {
                while (filas.next()) {
                    resultados.add(new ResultadoPuntaje(
                        filas.getString("Nombre"),
                        filas.getInt("Puntuacion"),
                        filas.getLong("Tiempo")
                    ));
                }
            }
        }
        return List.copyOf(resultados);
    }
}
