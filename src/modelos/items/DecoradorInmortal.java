package modelos.items;

import modelos.personajes.PersonajeDecorable;

public class DecoradorInmortal extends DecoradorBase {
    

    public DecoradorInmortal(PersonajeDecorable personajeDecorado) {
        super(personajeDecorado);
    }
    @Override 
    public void recibirDanio(int cantidad) {
        return;
    }

}
