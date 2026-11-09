package modelos.personajes;

public final class Invulnerabilidad extends DecoradorEstadisticas {
    public Invulnerabilidad(EstadisticasRata estadisticas) {
        super(estadisticas);
    }

    @Override
    public boolean esInvulnerable() {
        return true;
    }
}
