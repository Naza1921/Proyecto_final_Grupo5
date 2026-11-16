package aplicacion;

public final class NivelVista {
    private final int ancho;
    private final int alto;
    private final int[][] tiles;
    private final boolean ultimaSeccion;

    public NivelVista(int ancho, int alto, int[][] tiles, boolean ultimaSeccion) {
        if (ancho <= 0 || alto <= 0 || tiles == null || tiles.length != ancho) {
            throw new IllegalArgumentException("Los datos del nivel deben ser válidos");
        }
        this.tiles = new int[ancho][alto];
        for (int x = 0; x < ancho; x++) {
            if (tiles[x] == null || tiles[x].length != alto) {
                throw new IllegalArgumentException("Los tiles deben formar un nivel rectangular");
            }
            System.arraycopy(tiles[x], 0, this.tiles[x], 0, alto);
        }
        this.ancho = ancho;
        this.alto = alto;
        this.ultimaSeccion = ultimaSeccion;
    }

    public int getAncho() {
        return ancho;
    }

    public int getAlto() {
        return alto;
    }

    public int getSpriteIndex(int columna, int fila) {
        if (columna < 0 || columna >= ancho || fila < 0 || fila >= alto) {
            throw new IndexOutOfBoundsException("La posición está fuera del nivel");
        }
        return tiles[columna][fila];
    }

    public boolean esUltimaSeccion() {
        return ultimaSeccion;
    }
}
