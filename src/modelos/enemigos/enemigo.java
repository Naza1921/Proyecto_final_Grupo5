package modelos.enemigos;

public abstract class enemigo {
    protected  String nombre;
    protected int vida;
    protected int vidaMaxima; 
    protected  double velocidadBase;
    protected  double velocidadActual;
    protected  double x; 
    protected  double y;
    protected  int puntaje;
    protected  boolean ralentizado; 
    protected  double porcentajerelantizado; 
    protected double factorRalentizacion;

    public enemigo(String nombre, int vida, int vidaMaxima,double velocidad, int puntaje) {
        this.nombre = nombre;
        this.vida = vida; 
        this.vidaMaxima = vidaMaxima;
        this.puntaje = puntaje;
        this.velocidadBase = velocidad; //despues revisar por que velocidad 
        this.velocidadActual = velocidad; //despues revisar por que velocidad
        this.ralentizado = false;
        this.porcentajerelantizado = 1.0;

    }
    
    public enum TipoDanio {
    ACIDO,
    STUN,
    NORMAL,
    SAL
    }


    public void recibirDanio(int danio, TipoDanio tipo) {
        if (!estavivo()) return;
        this.vida -= danio;
        
        if (this.vida < 0 ) {
            this.vida = 0;
        }
        if (estavivo()) { 
        switch (tipo) {
        //en este public vamos a hace que cuando personaje le de con un stun o acido, este se relantize
        case ACIDO:
            aplicarRelantizacion(0.5); //reduce la velocidad al 50%
            break; //Detiene el código aquí y sale del switch.
        case STUN: 
            aplicarRelantizacion(0.0); // Lo reduce la velocidad al 100% 
            break; 
        case NORMAL: 
            aplicarRelantizacion(0.7);
            break; //reduce la velocidad al 70%
        case SAL:

            break; 
        }
        }
    }

    public void aplicarRelantizacion (double factor) {
        this.ralentizado = true; //aplica la ralentizacion 
        this.factorRalentizacion = factor;
        this.velocidadActual = this.velocidadBase * factor;

    }
    public void restaurarVelocidad() {
        this.ralentizado = false;
        this.factorRalentizacion = 1.0;
        this.velocidadActual = this.velocidadBase;
    }

    public boolean estavivo() {
        return vida > 0 ;
    }
    // Abstraccion del tipo de danio que genera el enemigo 
     public abstract void atacar(double objetivoX, double objetivoY);

    
    //getter y Setters 
    public String getNombre() {return nombre;}
    public int getVida() {
        return vida;
    }
    public int getVidaMaxima(){return vidaMaxima;}
    public double getVelocidadActual () {
        return velocidadActual;
    }
    public double getX () { return x;}
    public double getY() {return y;}
    public int getPuntaje() {return puntaje;}
    public boolean isRalentizado () {return ralentizado;}

    //setter

    public void setPosicion(double x, double y) {
        this.x =x;
        this.y= y; 
    }
    

}