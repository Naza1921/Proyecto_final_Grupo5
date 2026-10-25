package servicios;

import java.sql.SQLException;
import persistencia.PuntajeDAO;
import persistencia.ResultadoPuntaje;

public final class ServicioPuntajes {
    private final PuntajeDAO puntajeDAO;

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
}
