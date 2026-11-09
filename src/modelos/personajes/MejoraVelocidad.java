package modelos.personajes;

public final class MejoraVelocidad extends DecoradorEstadisticas {
    private static final double MULTIPLICADOR = 1.5;

    public MejoraVelocidad(EstadisticasRata envuelta) {
        super(envuelta);
    }

    @Override
    public double getVelocidad() {
        return super.getVelocidad() * MULTIPLICADOR;
    }
}
