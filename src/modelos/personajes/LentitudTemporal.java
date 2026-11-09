package modelos.personajes;

public final class LentitudTemporal extends DecoradorEstadisticas {
    private static final double MULTIPLICADOR = 0.5;

    public LentitudTemporal(EstadisticasRata envuelta) {
        super(envuelta);
    }

    @Override
    public double getVelocidad() {
        return super.getVelocidad() * MULTIPLICADOR;
    }
}
