package modelos.enemigos;

import java.util.concurrent.TimeUnit;
import modelos.Personaje;

//la rata infestada tenga la habilidad de escupirte (ataque a distancia), salta para perseguirte, 
public class ratainfestada  extends enemigo{
    private static final int VIDA_RATA = 2;
    private static final int VIDA_RATA_MAXIMA = 2;
    private static final double RANGO_ESCUPITAJO = 200.0;
    private static final double FUERZA_SALTO = 10.0;
    private static final double DISTANCIA_MINIMA_SALTO_X = 10.0;
    private static final double RANGO_CONTACTO_FISICO = 10.0;
    private static final double GRAVEDAD = 0.8;
    private static final int DANIO_ESCUPITAJO = 5;
    private static final int DANIO_FISICO_SALTO = 5;
    private static final int ENFRIAMIENTO_ESCUPITAJO = 8;
    private boolean enelAire;
    private double velocidadVertical;
    private double alturaDespegue;
    private long ultimoEscupitajoNanos;
    private boolean yaEscupio;
    private boolean yaDanoFisicoEnEsteSalto;

    public ratainfestada(String nombre, double velocidad, int puntaje) {
        super(nombre, VIDA_RATA, VIDA_RATA_MAXIMA, velocidad, puntaje);
    }

    @Override 
    public void atacar(Personaje objetivo) {
        if (!objetivo.estaViva()) {
            return;
        }

        if (enelAire) {
            return;
        }

        double distancia = Math.hypot(objetivo.getX() - x, objetivo.getY() - y);
        double distanciaX = Math.abs(objetivo.getX() - x);

        if (distanciaX >= DISTANCIA_MINIMA_SALTO_X) {
            saltar();
        } else if (distancia <= RANGO_ESCUPITAJO) {
            escupir(objetivo);
        }
    }

    public void actualizar(Personaje objetivo) {
        if (!enelAire) {
            return;
        }

        // Avanza un paso de la trayectoria por cada actualización del ciclo de juego.
        y += velocidadVertical;
        velocidadVertical += GRAVEDAD;

        if (objetivo.estaViva()) {
            double diferenciaX = objetivo.getX() - x;
            double distanciaHorizontal = Math.abs(diferenciaX);
            double avanceHorizontal = Math.min(Math.max(0.0, velocidadActual), distanciaHorizontal);
            if (distanciaHorizontal > 0.0) {
                x += Math.copySign(avanceHorizontal, diferenciaX);
            }
        }

        if (!yaDanoFisicoEnEsteSalto && objetivo.estaViva()
                && Math.hypot(objetivo.getX() - x, objetivo.getY() - y) <= RANGO_CONTACTO_FISICO) {
            objetivo.recibirDanio(DANIO_FISICO_SALTO);
            yaDanoFisicoEnEsteSalto = true;
        }

        // La altura de despegue funciona como suelo temporal hasta integrar colisiones con el nivel.
        if (y >= alturaDespegue && velocidadVertical > 0.0) {
            y = alturaDespegue;
            velocidadVertical = 0.0;
            enelAire = false;
        }
    }

    private void escupir(Personaje objetivo) {
        long ahora = System.nanoTime();
        if (yaEscupio && ahora - ultimoEscupitajoNanos
                < TimeUnit.SECONDS.toNanos(ENFRIAMIENTO_ESCUPITAJO)) {
            return;
        }

        objetivo.recibirDanio(DANIO_ESCUPITAJO);
        ultimoEscupitajoNanos = ahora;
        yaEscupio = true;
    }

    public void saltar() {
        if (enelAire) {
            return;
        }

        alturaDespegue = y;
        velocidadVertical = -FUERZA_SALTO;
        enelAire = true;
        yaDanoFisicoEnEsteSalto = false;
    }

    public boolean estaEnElAire() {
        return enelAire;
    }
}
