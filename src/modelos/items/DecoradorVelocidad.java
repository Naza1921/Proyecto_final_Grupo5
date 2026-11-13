package modelos.items;
import modelos.personajes.PersonajeDecorable;
public class DecoradorVelocidad extends DecoradorBase {
   
    private final double  MultiVelocidad=1.5;

    public DecoradorVelocidad(PersonajeDecorable personajeDecorado) {
        super(personajeDecorado);
    }


@Override 
public double getVelocidad() {
    return personajeDecorado.getVelocidad()*this.MultiVelocidad;
}

}
