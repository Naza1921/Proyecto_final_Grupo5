package modelos.escenario.levels;

public class level {
    private int[][] nivelDato;

    public level(int[][] nivelDato) {
        this.nivelDato = nivelDato;
    }

    public int getspriteindex(int x, int y) {
        return nivelDato[x][y];
    }
}
