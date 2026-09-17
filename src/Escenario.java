import java.util.ArrayList;
import java.util.List;

public class Escenario
{
    private Heroe heroe;
    private List<Enemigo> enemigos = new ArrayList<>();

    // Gravedad (valor típico en m/s^2)
    public static final double GRAVEDAD = 9.81;

    // POLIMORFISMO:

    public Escenario()
    {
    }    

    public Escenario(Heroe heroe)
    {
        this.heroe = heroe;
    }

    public Escenario(Heroe heroe, List<Enemigo> enemigos)
    {
        this.heroe = heroe;
        this.enemigos = enemigos;   // reemplaza la lista vacía por la proporcionada
    }

    public Heroe getHeroe()
    {
        return heroe;
    }

    public List<Enemigo> getEnemigos()
    {
        return enemigos;
    }

    /**
     * Simula la reproducci?n autom?tica de m?sica cuando se crea el primer escenario.
     * En una aplicaci?n real se usaría una API de audio (javax.sound, JavaFX, etc.).
     */
    public void reproducirMusica()
    {
        System.out.println("Reproduciendo musica de fondo...");
    }

    /**
     * Detecta colisiones entre el h?roe y cualquier enemigo usando colisi?n AABB.
     * Asume que el personaje es un cuadrado cuyo lado es tamano y posici?n (x,y) es la esquina superior-izquierda.
     * @return true si hay al menos una colisi?n, false en caso contrario.
     */
    public boolean hayColision()
    {
        if (heroe == null) {
            return false;
        }
        int hx = heroe.getX();
        int hy = heroe.getY();
        int htam = heroe.getTamano();

        for (Enemigo e : enemigos) {
            int ex = e.getX();
            int ey = e.getY();
            int etam = e.getTamano();

            // Comprobaci?n de solapamiento en eje X y Y
            if (hx < ex + etam && hx + htam > ex &&
                hy < ey + etam && hy + htam > ey) {
                return true; // colisi?n detectada
            }
        }
        return false; // no hay colisiones
    }
}
