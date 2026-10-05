package modelos.enemigos;
public class fantasma extends enemigo {
    private static final int VIDA_FANTASMA = 1; 
    private static final int VIDA_FANTASMA_MAXIMA = 1;
    public fantasma(String nombre, double velocidad, int puntaje) {
        super(nombre, VIDA_FANTASMA, VIDA_FANTASMA_MAXIMA , velocidad, puntaje);
    }
    @Override

    public void atacar(double objetivoX, double objetivoY) {
        //Aca ira el danio que hara el fantasma al personaje (danio cuerpo a cuerpo)
    }
    
}