package modelos.items;

import java.util.function.Function;
import modelos.personajes.Personaje;
import modelos.personajes.PersonajeDecorable;

public abstract class PowerUpBase implements PowerUp {
    private final long duracion;
    private final double x;
    private final double y;
    private final RecompensaPowerUp recompensa;
    private final Function<PersonajeDecorable, PersonajeDecorable> crearDecorador;
    private final boolean instantaneo;
    private final int cantidadCuracion;

    protected PowerUpBase(
            long duracion,
            double x,
            double y,
            RecompensaPowerUp recompensa,
            Function<PersonajeDecorable, PersonajeDecorable> crearDecorador) {
        this(duracion, x, y, recompensa, crearDecorador, false, 0);
    }

    protected PowerUpBase(
            double x,
            double y,
            RecompensaPowerUp recompensa,
            int cantidadCuracion) {
        this(0, x, y, recompensa, null, true, cantidadCuracion);
    }

    private PowerUpBase(
            long duracion,
            double x,
            double y,
            RecompensaPowerUp recompensa,
            Function<PersonajeDecorable, PersonajeDecorable> crearDecorador,
            boolean instantaneo,
            int cantidadCuracion) {
        if (duracion < 0 || !Double.isFinite(x) || !Double.isFinite(y)
                || recompensa == null
                || (instantaneo && (duracion != 0 || crearDecorador != null
                    || cantidadCuracion <= 0))
                || (!instantaneo && (duracion == 0 || crearDecorador == null
                    || cantidadCuracion != 0))) {
            throw new IllegalArgumentException("El power-up requiere categoría, recompensa y datos válidos");
        }
        this.duracion = duracion;
        this.x = x;
        this.y = y;
        this.recompensa = recompensa;
        this.crearDecorador = crearDecorador;
        this.instantaneo = instantaneo;
        this.cantidadCuracion = cantidadCuracion;
    }

    @Override
    public RecompensaPowerUp getRecompensa() {
        return recompensa;
    }

    @Override
    public long getDuracion() {
        return duracion;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public boolean esInstantaneo() {
        return instantaneo;
    }

    @Override
    public void aplicarEfecto(Personaje personaje) {
        validarPersonaje(personaje);
        if (instantaneo) {
            personaje.recuperarVida(cantidadCuracion);
        } else {
            personaje.aplicarDecorador(crearDecorador);
        }
    }

    @Override
    public void revertirEfecto(Personaje personaje) {
        validarPersonaje(personaje);
        if (!instantaneo) {
            personaje.quitarDecorador();
        }
    }

    @Override
    public boolean colisionaCon(Personaje personaje) {
        validarPersonaje(personaje);
        double distancia = Math.hypot(personaje.getX() - x, personaje.getY() - y);
        return distancia < 20; // Radio de colisión
    }

    private static void validarPersonaje(Personaje personaje) {
        if (personaje == null) {
            throw new IllegalArgumentException("Se requiere un personaje para aplicar el power-up");
        }
    }
}