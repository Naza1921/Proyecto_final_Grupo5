package servicios.audio;

public interface ReproductorEfectosSonido extends AutoCloseable {
    enum Efecto {
        MUERTE,
        HABILIDAD,
        POWER_UP,
        POWER_DOWN,
        SALTO,
        MONEDA,
        NIVEL_COMPLETADO
    }

    void reproducir(Efecto efecto);

    @Override
    void close();
}
