package modelos.escenario.levels;

public class level {
    public static final int COLUMNAS_ATLAS = 7;
    public static final int COLUMNA_AIRE = 6;
    public static final int AGUJERO_SIGUIENTE = 28;
    public static final int AGUJERO_ANTERIOR = 29;

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
        if (!dentro(x, y)) return true;
        int v = nivelDato[x][y];
        if (v >= AGUJERO_SIGUIENTE) return false;
        return v % COLUMNAS_ATLAS != COLUMNA_AIRE;
    }

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

    public int agujeroEn(double x, double y, double ancho, double alto, int tile) {
        int izq = (int) Math.floor(x / tile), der = (int) Math.floor((x + ancho - 1) / tile);
        int arr = (int) Math.floor(y / tile), aba = (int) Math.floor((y + alto - 1) / tile);
        for (int j = arr; j <= aba; j++)
            for (int i = izq; i <= der; i++)
                if (dentro(i, j) && nivelDato[i][j] >= AGUJERO_SIGUIENTE) return nivelDato[i][j];
        return -1;
    }

    public int[] buscar(int tipo) {
        for (int j = 0; j < getAlto(); j++)
            for (int i = 0; i < getAncho(); i++)
                if (nivelDato[i][j] == tipo) return new int[]{i, j};
        return null;
    }
}