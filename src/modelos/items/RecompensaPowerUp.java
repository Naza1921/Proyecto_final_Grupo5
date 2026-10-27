package modelos.items;

public record RecompensaPowerUp(int puntos, boolean penalizacion) {
    public RecompensaPowerUp {
        if (puntos <= 0) {
            throw new IllegalArgumentException("La recompensa debe tener puntos positivos");
        }
    }
}
