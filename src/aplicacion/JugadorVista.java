package aplicacion;

import java.util.List;

public record JugadorVista(
        String nombre,
        TipoRataVista tipo,
        double x,
        double y,
        int vida,
        int vidaMaxima,
        double estamina,
        double estaminaMaxima,
        boolean enElAire,
        boolean agachado,
        List<HabilidadVista> habilidades,
        List<ProyectilVista> proyectiles) {
    public JugadorVista {
        if (nombre == null || nombre.isBlank() || tipo == null
                || !Double.isFinite(x) || !Double.isFinite(y)
                || vida < 0 || vidaMaxima <= 0 || vida > vidaMaxima
                || !Double.isFinite(estamina) || estamina < 0
                || !Double.isFinite(estaminaMaxima) || estaminaMaxima <= 0
                || estamina > estaminaMaxima || habilidades == null || proyectiles == null
                || habilidades.stream().anyMatch(habilidad -> habilidad == null)
                || proyectiles.stream().anyMatch(proyectil -> proyectil == null)) {
            throw new IllegalArgumentException("Los datos del jugador deben ser válidos");
        }
        habilidades = List.copyOf(habilidades);
        proyectiles = List.copyOf(proyectiles);
    }
}
