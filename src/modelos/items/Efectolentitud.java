package modelos.items;

import modelos.personajes.PersonajeDecorable;

public class Efectolentitud extends DecoradorBase {
    
    
    private final double  MultiVelocidad=0.5;
    public Efectolentitud (PersonajeDecorable personajeDecorable){ 

        super(personajeDecorable);
    }


    @Override 
    public double getVelocidad() {
        return personajeDecorado.getVelocidad()*this.MultiVelocidad;
    }

}
