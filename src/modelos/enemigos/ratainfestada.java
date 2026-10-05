package modelos.enemigos;
//la rata infestada tenga la habilidad de escupirte (ataque a distancia), salta para perseguirte, 
public class ratainfestada  extends enemigo{
    private static final int VIDA_RATA = 2;
    private static final int VIDA_RATA_MAXIMA = 2;
    private static final double RANGO_ESCUPITAJO = 200.0;
    private static final double fuerzaSalto = 5;
    private final boolean enElAire = false;

    super(nombre, VIDA_RATA, VIDA_RATA_MAXIMA, velocidad, puntaje);

    @Override 
    public void atacar(double objetivoX, double objetivoY) {
        double distancia = Math.hypot(objetivoX - x, objetivoY - y);

        if (distancia <= fuerzaSalto) {
            //si la distancia es suficiente para saltar hacia otra plataforma, la rata infestada lo hara
            saltar(objetivoX, objetivoY);

        } else if (distancia <= rango_escupitajo) {
            //si la rata infestada esta a rango de la rata, esta le escupira
            escupir(objetivoX, objetivoY);
        }
    }
    }

    public void escupir (double objetivoX, double objetivoY){
        //aca va el ataque de distancia
    }
    public void saltar(double objetivoX, double objetivoY) {
        //aca va el salto de la rata infestada
    }
}
