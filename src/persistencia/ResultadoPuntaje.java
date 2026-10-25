package persistencia;

public final class ResultadoPuntaje {
    private final String nombre;
    private final int puntuacion;
    private final long tiempoSegundos;

    public ResultadoPuntaje(String nombre, int puntuacion, long tiempoSegundos) {
        if (nombre == null || nombre.isBlank() || puntuacion < 0 || tiempoSegundos < 0) {
            throw new IllegalArgumentException("El resultado de la partida no es válido");
        }
        this.nombre = nombre.trim();
        this.puntuacion = puntuacion;
        this.tiempoSegundos = tiempoSegundos;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public long getTiempoSegundos() {
        return tiempoSegundos;
    }
}
