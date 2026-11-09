package modelos.personajes;

public abstract class DecoradorEstadisticas implements EstadisticasRata {
    private final EstadisticasRata envuelta;

    protected DecoradorEstadisticas(EstadisticasRata envuelta) {
        if (envuelta == null) {
            throw new IllegalArgumentException("Se requieren estadísticas para decorar");
        }
        this.envuelta = envuelta;
    }

    @Override
    public double getVelocidad() {
        return envuelta.getVelocidad();
    }

    @Override
    public boolean esInvulnerable() {
        return envuelta.esInvulnerable();
    }
}
