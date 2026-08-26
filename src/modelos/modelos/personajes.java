package modelos;

public class personajes{
    //nombre que tendra la rata
    private String nombre;
    //cantidad de vida actual de la rata
    private int vida;
    //vida maxima que puede tener la rata
    private final int vidaMaxima;
    //Posicion horizontal de la rata
    private double x;
    //Posicion vertical de la rata 
    private double y;
    //Velocidad de movimiento de la rata
    private double velocidad;
    //Fueza con la que la rata realiza el salto
    private final double fuerzaSalto;
    //Indica si la rata se encuentra en el aire
    private boolean enElaire;
    //Indica si la rata esta agachada
    private boolean agachada;

    //constructor encargado de crear una nueva rata

    public personajes(String nombre) { 
     //Guardamos el nombre recibido
     this.nombre = nombre;
     //Establecemos lavida maxima de la rata
     this.vidaMaxima = 100;
     //Al comenzar, la rata tien toda su vida
     this.vida =vidaMaxima;
     //Posicion inicial de la rata
     this.x = 0;
     this.y = 0;
     //velocidad inicial de movimiento
     this.velocidad = 5;
     //Fuerza incial del salto
     this.fuerzaSalto = 10;
     //la rata comienza en el suelo
     this.enElaire = false;
     //La rata comienza sin estar agachada
     this.agachada = false;
    }

    //metodo encargado de mover la rata hacia la izquierda
    public void moverIzquierda(){
     //Disminuimos la posicion x segun la velocidad 
      x  -= velocidad;
    }
    
    //metodo encargado de mover la rata hacia la derecha
     public void moverDerecha(){
        //aumentamos la posicion de x segun la velocidad
        x += velocidad;
     
    }

    //metodo encargado de hacer saltar a la rata
    public void saltar (){
        if (!enElaire) {
            //indicamos que ahora esta en el aire
            enElaire = true;
            //modificamos temporalmente su posicion vertical
            y -= fuerzaSalto;
        }
    }

    //metodo encargado de agachar a la rata 
    public void agacharse(){
        //indicamos que la rata esta agachada
        agachada = true;
    }

    //metodo encargado de hacer que la rata se levante
    public void levantarse(){
        agachada = false;
    }

    //metodo encargado de actualizar el estado de la rata 
    public void actualizar(){
        //aca posteriormente agregamos :
        //gravedad
        //fisicas
        //animaciones 
        //efectos temporales
        //otros metodos mas 
    }

    //metodo encargado de recibir danio
    public void recibirDanio(int cantidad) {
        //reducimos la vida segun la cantidad de danio recibido
        vida -= cantidad;

        //evitamos que la vida tenga valores negativos
        if (vida < 0) {
            vida = 0;
        }
    }

    //metodo que permite comprobar si la rata continua viva
    public boolean estaViva(){
        //sila vida es mayor a cero, la rata sigue viva
        return vida > 0;
    }

    //devuelve la vida actual de la rata
    public int getVida(){
        return vida;
    }

    //devuelve la vida maxima de la rata
    public int getVidaMaxima(){
        return vidaMaxima;
    }

    //devuelve la pisicion horizontal
    public double getX(){
        return x;
    }

    //devuelve la posicion vertical
    public double getY(){
        return y;
    }

    //devuelve el nombre de la rata
    public String getNombre(){
        return nombre;
    }

    //permite comprobar si la rata esta en el aire
    public boolean estaEnElaire(){
        return enElaire;
    }

    //permite comprobar si la esta agachada
    public boolean estaAgachada(){
        return agachada;
    }

    //se mantiene en la misma posicion la rata
    public void detenerMovimiento(){
        velocidad = 0;
    }
    
    //interactua con un objeto, comida, power-up, etc.
    public void interactuar(){
        //aca posteriormente agregamos la logica de interaccion con objetos
    }
}