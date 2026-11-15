package modelos.items;

import modelos.personajes.PersonajeDecorable;

public final class DecoradorLentitud extends DecoradorBase {
    private static final double MULTIPLICADOR_VELOCIDAD = 0.5;

    public DecoradorLentitud(PersonajeDecorable personajeDecorado) {
        super(personajeDecorado);
    }

    @Override
    public double getVelocidad() {
        return personajeDecorado.getVelocidad() * MULTIPLICADOR_VELOCIDAD;
    }
}
