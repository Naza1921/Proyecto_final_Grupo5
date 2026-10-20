package modelos.partida;

public final class Puntaje {
    private int total;

    public int getTotal() {
        return total;
    }

    public void agregar(int puntos) {
        if (puntos <= 0) {
            throw new IllegalArgumentException("Los puntos a agregar deben ser positivos");
        }
        total = Math.addExact(total, puntos);
    }

    public void reiniciar() {
        total = 0;
    }
}
