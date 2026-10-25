package persistencia;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConexionSQLite {
    private static final String PROPIEDAD_RUTA = "ghostsrats.db.path";
    private static final String VARIABLE_RUTA = "GHOSTSRATS_DB_PATH";
    private static final String CREAR_TABLA_PUNTAJES = """
        CREATE TABLE IF NOT EXISTS score (
            Nombre TEXT PRIMARY KEY,
            Puntuacion INTEGER,
            Tiempo INTEGER
        )
        """;

    private Connection conexion;

    private ConexionSQLite() { }

    private static class Instancia {
        private static final ConexionSQLite UNICA = new ConexionSQLite();
    }

    public static ConexionSQLite obtenerInstancia() {
        return Instancia.UNICA;
    }

    public synchronized Connection obtenerConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            Path ruta = obtenerRutaBaseDatos();
            if (!Files.isRegularFile(ruta)) {
                throw new SQLException(
                    "No se encontró la base SQLite en " + ruta
                        + ". Configure la propiedad " + PROPIEDAD_RUTA
                        + " o la variable " + VARIABLE_RUTA + "."
                );
            }
            conexion = DriverManager.getConnection("jdbc:sqlite:" + ruta);
            try (var sentencia = conexion.createStatement()) {
                sentencia.execute(CREAR_TABLA_PUNTAJES);
            } catch (SQLException excepcion) {
                conexion.close();
                conexion = null;
                throw excepcion;
            }
        }
        return conexion;
    }

    private Path obtenerRutaBaseDatos() throws SQLException {
        String rutaConfigurada = System.getProperty(PROPIEDAD_RUTA);
        if (rutaConfigurada == null || rutaConfigurada.isBlank()) {
            rutaConfigurada = System.getenv(VARIABLE_RUTA);
        }
        if (rutaConfigurada == null || rutaConfigurada.isBlank()) {
            rutaConfigurada = "Score.db";
        }
        try {
            return Path.of(rutaConfigurada).toAbsolutePath().normalize();
        } catch (RuntimeException excepcion) {
            throw new SQLException("La ruta configurada para SQLite no es válida", excepcion);
        }
    }
}
