package modelos;

public record PreferenciasAjustes(
        boolean musicaActivada,
        int cancionSeleccionada,
        int volumen,
        boolean temaOscuro,
        boolean pantallaCompleta) {

    public static final PreferenciasAjustes ESTANDAR =
            new PreferenciasAjustes(true, 0, 50, false, false);

    public PreferenciasAjustes {
        if (cancionSeleccionada < 0 || cancionSeleccionada > 3) {
            throw new IllegalArgumentException("La canción debe ser 0, 1, 2 o 3.");
        }
        if (volumen < 0 || volumen > 100) {
            throw new IllegalArgumentException("El volumen debe estar entre 0 y 100.");
        }
    }
}
