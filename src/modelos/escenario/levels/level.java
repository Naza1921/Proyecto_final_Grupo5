package modelos.escenario.levels;

public class level {
    public static final int COLUMNAS_ATLAS = 7;
    public static final int COLUMNA_AIRE = 6;
    private final int[][] nivelDato;

    public level(int[][] nivelDato) { this.nivelDato = nivelDato; }

    public int getAncho() { return nivelDato.length; }
    public int getAlto()  { return nivelDato[0].length; }

    private boolean dentro(int x, int y) {
        return x >= 0 && y >= 0 && x < getAncho() && y < getAlto();
    }

    public int getspriteindex(int x, int y) {
        return dentro(x, y) ? nivelDato[x][y] : COLUMNA_AIRE;
    }

    public boolean esSolido(int x, int y) {
        if (!dentro(x, y)) return true; // fuera del mapa = pared
        return nivelDato[x][y] % COLUMNAS_ATLAS != COLUMNA_AIRE;
    }

    // Rectángulo en píxeles contra la grilla
    public boolean haySolidoEn(double x, double y, double ancho, double alto, int tile) {
        int izq = (int) Math.floor(x / tile);
        int der = (int) Math.floor((x + ancho - 1) / tile);
        int arr = (int) Math.floor(y / tile);
        int aba = (int) Math.floor((y + alto - 1) / tile);
        for (int j = arr; j <= aba; j++)
            for (int i = izq; i <= der; i++)
                if (esSolido(i, j)) return true;
        return false;
    }
}