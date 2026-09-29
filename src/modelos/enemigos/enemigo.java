package modelos.enemigos;

public abstract class enemigo {
    protected  String nombre;
    protected  double velocidadBase;
    protected  double velocidadActual;
    protected  double x; 
    protected  double y;
    protected  int puntaje;
    protected  boolean ralentizado; 
    protected  double porcentajerelantizado; 
    protected double factorRalentizacion;

    public enemigo(String nombre, double velocidad, int puntaje) {
        this.nombre = nombre;
        this.puntaje = puntaje;
        this.velocidadBase = velocidad; //despues revisar por que velocidad 
        this.velocidadActual = velocidad; //despues revisar por que velocidad
        this.ralentizado = false;
        this.porcentajerelantizado = 1.0;

    }
    
    public enum TipoDanio {
    ACIDO,
    STUN,
    NORMAL
    }


    public void recibirDanio(int danio, TipoDanio tipo) {
        switch (tipo) {
        //en este public vamos a hace que cuando personaje le de con un stun o acido, este se relantize
        case ACIDO:
            aplicarRalentizacion(0.5); //reduce la velocidad al 50%
            break; //Detiene el código aquí y sale del switch.
        case STUN: 
            aplicarRalentizacion(0.0); // Lo reduce la velocidad al 100% 
            break; 
        case NORMAL: 
            aplicarRalentizacion(0.7);
            break; //reduce la velocidad al 70%
        }
    }

    public void aplicarRalentizacion (double factor) {
        this.ralentizado = true; //aplica la ralentizacion 
        this.factorRalentizacion = factor;
        this.velocidadActual = this.velocidadBase * factor;

    }
    
    public void restaurarVelocidad() {
        this.ralentizado = false;
        this.factorRalentizacion = 1.0;
        this.velocidadActual = this.velocidadBase;
    }


    //getter y Setters 
    public double getVelocidadActual () {
        return velocidadActual;
    }


}