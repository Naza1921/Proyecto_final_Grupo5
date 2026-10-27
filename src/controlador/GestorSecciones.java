package controlador;

import modelos.escenario.levels.level;
import modelos.personajes.Personaje;

public class GestorSecciones {
    private final levelmanager niveles;
    private final ColisionNivel colision;
    private final int tile, ancho, alto;
    private SeccionListener listener;
    private boolean puertaBloqueada;
    private double zonaBloqueadaIzquierda;
    private double zonaBloqueadaSuperior;
    private double zonaBloqueadaDerecha;
    private double zonaBloqueadaInferior;
    private boolean terminado;

    public GestorSecciones(levelmanager niveles, ColisionNivel colision,
                           int tile, int ancho, int alto) {
        this.niveles = niveles; this.colision = colision;
        this.tile = tile; this.ancho = ancho; this.alto = alto;
    }

    public void setListener(SeccionListener listener) { this.listener = listener; }
    public int getSeccionActual() { return niveles.getSeccion(); }

    public void actualizar(Personaje p) {
        if (terminado) return;

        if (puertaBloqueada) {
            if (seSolapaConZonaBloqueada(p)) return;
            puertaBloqueada = false;
            return;
        }

        int tipo = niveles.getNivel().agujeroEn(p.getX(), p.getY(), ancho, alto, tile);
        if (tipo == -1) return;

        if (tipo == level.AGUJERO_SIGUIENTE) {
            if (niveles.cambiarSeccion(1)) {
                entrar(p, level.AGUJERO_ANTERIOR);
            } else {
                terminado = true;
                if (listener != null) listener.alTerminarNivel();
            }
        } else if (tipo == level.AGUJERO_ANTERIOR && niveles.cambiarSeccion(-1)) {
            entrar(p, level.AGUJERO_SIGUIENTE);
        }
    }

    private void entrar(Personaje p, int agujeroLlegada) {
        level nivel = niveles.getNivel();
        colision.setNivel(nivel);
        int[] c = nivel.buscar(agujeroLlegada);
        if (c != null) {
            double puertaX = c[0] * (double) tile;
            double puertaY = c[1] * (double) tile;
            zonaBloqueadaIzquierda = puertaX - tile;
            zonaBloqueadaSuperior = puertaY - tile;
            zonaBloqueadaDerecha = puertaX + 2.0 * tile;
            zonaBloqueadaInferior = puertaY + 2.0 * tile;
            puertaBloqueada = true;
            p.corregirPosicion(c[0] * tile + (tile - ancho) / 2.0, (c[1] + 1) * tile - alto);
            p.setVelocidadY(0);
        } else {
            puertaBloqueada = false;
        }
        if (listener != null) listener.alCambiarSeccion(niveles.getSeccion());
    }

    private boolean seSolapaConZonaBloqueada(Personaje p) {
        return p.getX() < zonaBloqueadaDerecha
            && p.getX() + ancho > zonaBloqueadaIzquierda
            && p.getY() < zonaBloqueadaInferior
            && p.getY() + alto > zonaBloqueadaSuperior;
    }

    public void reiniciar(Personaje p) {
        if (p == null) {
            throw new IllegalArgumentException("Se requiere el personaje para reiniciar la sección");
        }

        terminado = false;
        puertaBloqueada = false;
        level nivel = niveles.getNivel();
        colision.setNivel(nivel);
        int[] coordenadas = nivel.buscar(level.AGUJERO_ANTERIOR);
        if (coordenadas != null) {
            p.corregirPosicion(
                coordenadas[0] * tile + (tile - ancho) / 2.0,
                (coordenadas[1] + 1) * tile - alto
            );
            p.setVelocidadY(0);
        }
        if (listener != null) {
            listener.alCambiarSeccion(niveles.getSeccion());
        }
    }
}