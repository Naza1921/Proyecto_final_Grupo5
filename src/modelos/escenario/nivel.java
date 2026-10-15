import colisiones.Colision;
import java.util.ArrayList;

public class nivel {
    private int nivelNum;
    private String nombre;
    private ArrayList<Colision>  colisionesnivel;

    public nivel(int nivelNum, String nombre) {
        this.nivelNum = nivelNum;
        this.nombre = nombre;
        colisionesnivel = new ArrayList<>();
    }

    public int getNivelNum () {
        return nivelNum;
    }

    public void setNivelNum(int nivelNum) {
        this.nivelNum = nivelNum;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void agregarColision(Colision colision) {
    colisionesnivel.add(colision);
    }

    //musica//
    //fondo//
    
}