package modelos.enemigos;

import modelos.Personaje;

//la rata infestada tenga la habilidad de escupirte (ataque a distancia), salta para perseguirte, 
public class ratainfestada  extends enemigo{
    private static final int VIDA_RATA = 2;
    private static final int VIDA_RATA_MAXIMA = 2;
    private static final double RANGO_ESCUPITAJO = 200.0;
    private static final double FUERZA_SALTO = 5.0;
    private static final int DANIO_ESCUPITAJO = 5;
    private static final long ENFRIAMIENTO_ESCUPITAJO_NANOS = 8_000_000_000L;
    private final boolean enElAire = false ;
    private long ultimoEscupitajoNanos;
    private boolean yaEscupio;

    public ratainfestada(String nombre, double velocidad, int puntaje) {
        super(nombre, VIDA_RATA, VIDA_RATA_MAXIMA, velocidad, puntaje);
    }

    @Override 
    public void atacar(Personaje objetivo) {
        if (!objetivo.estaViva()) {
            return;
        }

        double distancia = Math.hypot(objetivo.getX() - x, objetivo.getY() - y);

        if (distancia <= FUERZA_SALTO) {
            saltar(objetivo.getX(), objetivo.getY());
        } else if (distancia <= RANGO_ESCUPITAJO) {
            escupir(objetivo);
        }
    }

    private void escupir(Personaje objetivo) {
        long ahora = System.nanoTime();
        if (yaEscupio && ahora - ultimoEscupitajoNanos < ENFRIAMIENTO_ESCUPITAJO_NANOS) {
            return;
        }

        objetivo.recibirDanio(DANIO_ESCUPITAJO);
        ultimoEscupitajoNanos = ahora;
        yaEscupio = true;
    }

    public void saltar(double objetivoX, double objetivoY) {
        // Pendiente: implementar el salto hacia el objetivo.
    }
}
