package modelos.items;
import modelos.personajes.PersonajeDecorable;

public abstract class DecoradorBase implements modelos.personajes.PersonajeDecorable {
    protected final modelos.personajes.PersonajeDecorable personajeDecorado;

    protected DecoradorBase(modelos.personajes.PersonajeDecorable personajeDecorado) {
        if (personajeDecorado == null) {
            throw new IllegalArgumentException("El personaje decorado no puede ser nulo");
        }
        this.personajeDecorado = personajeDecorado;
    }

    @Override
    public double getVelocidad() {
        return personajeDecorado.getVelocidad();
    }

    @Override
    public void recibirDanio(int cantidad) {
        personajeDecorado.recibirDanio(cantidad);
    }

}
