package modelos.items;

public class DecoradorVelocidad extends DecoradorBase {
    
    public DecoradorVelocidad(modelos.personajes.PersonajeDecorable personajeDecorado) {
        super(personajeDecorado);
    }

private final double  MultiVelocidad=1.5;

@Override 
public double getVelocidad() {
    return personajeDecorado.getVelocidad()*this.MultiVelocidad;
}

}
