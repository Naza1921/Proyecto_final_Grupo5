package persistencia;

import java.sql.SQLException;
import java.util.List;

public interface PuntajeDAO {
    boolean guardarMejorPuntaje(ResultadoPuntaje resultado) throws SQLException;

    List<ResultadoPuntaje> listarMejoresPuntajes() throws SQLException;
}
