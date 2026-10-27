package modelos.enemigos;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import modelos.partida.ConfiguracionPartida;
import modelos.escenario.levels.level;
import modelos.personajes.Personaje;

public class ratainfestada  extends enemigo{
    private static final int VIDA_RATA = 2;
    private static final int VIDA_RATA_MAXIMA = 2;
    private static final double RANGO_ESCUPITAJO = 200.0;
    private static final double FUERZA_SALTO = 9.0;
    private static final double DISTANCIA_MINIMA_SALTO_X = 10.0;
    private static final double RANGO_CONTACTO_FISICO = 10.0;
    private static final double GRAVEDAD = 0.4;
    private static final int DANIO_FISICO_SALTO = 5;
    private static final int ENFRIAMIENTO_ESCUPITAJO = 8;
    private boolean enelAire;
    private double velocidadVertical;
    private final List<ProyectilAcido> proyectilesAcido = new ArrayList<>();
    private long ultimoEscupitajoNanos;
    private boolean yaEscupio;
    private boolean yaDanoFisicoEnEsteSalto;

    public ratainfestada(String nombre, double velocidad, int puntaje) {
        super(nombre, VIDA_RATA, VIDA_RATA_MAXIMA, velocidad, puntaje);
    }

    @Override 
    public void atacar(Personaje objetivo) {
        if (!estavivo() || objetivo == null || !objetivo.estaViva() || estaAturdido()) {
            return;
        }

        if (enelAire) {
            return;
        }

        if (estaDistraido()) {
            double distanciaDistraccion = Math.hypot(
                getDistraccionX() - x, getDistraccionY() - y
            );
            if (distanciaDistraccion >= DISTANCIA_MINIMA_SALTO_X) {
                saltar();
            }
            return;
        }

        double distancia = Math.hypot(objetivo.getX() - x, objetivo.getY() - y);
        double distanciaX = Math.abs(objetivo.getX() - x);

        if (distancia <= RANGO_ESCUPITAJO && distancia > 0) {
            escupir(objetivo);
        } else if (distanciaX >= DISTANCIA_MINIMA_SALTO_X || distancia > RANGO_ESCUPITAJO) {
            saltar();
        }
    }

    public void actualizarProyectiles(Personaje objetivo) {
        if (objetivo == null) {
            throw new IllegalArgumentException("Se requiere un personaje objetivo");
        }
        if (!estavivo()) {
            proyectilesAcido.clear();
            return;
        }

        Iterator<ProyectilAcido> iterador = proyectilesAcido.iterator();
        while (iterador.hasNext()) {
            ProyectilAcido proyectil = iterador.next();
            proyectil.actualizar();
            proyectil.impactarSiColisiona(objetivo);
            if (!proyectil.estaActivo()) {
                iterador.remove();
            }
        }
    }

    public List<ProyectilAcido> getProyectilesAcido() {
        return List.copyOf(proyectilesAcido);
    }

    public void actualizar(Personaje objetivo, level nivel, int tile) {
        if (objetivo == null || nivel == null || tile <= 0) {
            throw new IllegalArgumentException("Se requieren el objetivo y un nivel válido");
        }
        if (!estavivo() || estaAturdido()) {
            return;
        }

        boolean tieneSuelo = nivel.haySolidoEn(
            x, y + 1, ConfiguracionPartida.ANCHO_JUGADOR,
            ConfiguracionPartida.ALTO_JUGADOR, tile
        );
        if (!enelAire && !tieneSuelo) {
            enelAire = true;
            velocidadVertical = 0;
        }
        if (!enelAire) {
            return;
        }

        boolean distraido = estaDistraido();
        double destinoX = distraido ? getDistraccionX() : objetivo.getX();

        double diferenciaX = destinoX - x;
        double distanciaHorizontal = Math.abs(diferenciaX);
        double avanceHorizontal = Math.min(Math.max(0.0, getVelocidadActual()), distanciaHorizontal);
        if (distanciaHorizontal > 0.0) {
            double siguienteX = x + Math.copySign(avanceHorizontal, diferenciaX);
            if (!nivel.haySolidoEn(
                    siguienteX, y, ConfiguracionPartida.ANCHO_JUGADOR,
                    ConfiguracionPartida.ALTO_JUGADOR, tile)) {
                x = siguienteX;
            }
        }

        // La rata avanza en vertical y aterriza o frena al chocar con un tile sólido.
        double siguienteY = y + velocidadVertical;
        if (nivel.haySolidoEn(
                x, siguienteY, ConfiguracionPartida.ANCHO_JUGADOR,
                ConfiguracionPartida.ALTO_JUGADOR, tile)) {
            if (velocidadVertical >= 0) {
                int filaSuelo = (int) Math.floor(
                    (siguienteY + ConfiguracionPartida.ALTO_JUGADOR - 1) / tile
                );
                y = filaSuelo * tile - ConfiguracionPartida.ALTO_JUGADOR;
                enelAire = false;
            } else {
                int filaCabeza = (int) Math.floor(siguienteY / tile);
                y = (filaCabeza + 1) * tile;
            }
            velocidadVertical = 0;
        } else {
            y = siguienteY;
            velocidadVertical += GRAVEDAD;
        }

        if (!distraido && !yaDanoFisicoEnEsteSalto && objetivo.estaViva()
                && Math.hypot(objetivo.getX() - x, objetivo.getY() - y) <= RANGO_CONTACTO_FISICO) {
            objetivo.recibirDanio(DANIO_FISICO_SALTO);
            yaDanoFisicoEnEsteSalto = true;
        }
    }

    private void escupir(Personaje objetivo) {
        long ahora = System.nanoTime();
        if (yaEscupio && ahora - ultimoEscupitajoNanos
                < TimeUnit.SECONDS.toNanos(ENFRIAMIENTO_ESCUPITAJO)) {
            return;
        }

        proyectilesAcido.add(new ProyectilAcido(x, y, objetivo.getX(), objetivo.getY()));
        ultimoEscupitajoNanos = ahora;
        yaEscupio = true;
    }

    public void saltar() {
        if (enelAire) {
            return;
        }

        velocidadVertical = -FUERZA_SALTO;
        enelAire = true;
        yaDanoFisicoEnEsteSalto = false;
    }

    public boolean estaEnElAire() {
        return enelAire;
    }

    @Override
    public void reiniciar(double x, double y) {
        super.reiniciar(x, y);
        enelAire = false;
        velocidadVertical = 0;
        proyectilesAcido.clear();
        ultimoEscupitajoNanos = 0;
        yaEscupio = false;
        yaDanoFisicoEnEsteSalto = false;
    }
}
