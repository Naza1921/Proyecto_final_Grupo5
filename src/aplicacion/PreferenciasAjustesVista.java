package aplicacion;

public record PreferenciasAjustesVista(
        boolean musicaActivada,
        int cancionSeleccionada,
        int volumen,
        boolean temaOscuro,
        boolean pantallaCompleta) {

    public PreferenciasAjustesVista {
        if (cancionSeleccionada < 0 || cancionSeleccionada > 5) {
            throw new IllegalArgumentException("La canción debe ser 0, 1, 2, 3, 4 o 5.");
        }
        if (volumen < 0 || volumen > 100) {
            throw new IllegalArgumentException("El volumen debe estar entre 0 y 100.");
        }
    }
}
