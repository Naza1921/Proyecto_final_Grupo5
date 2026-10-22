package controlador;

import modelos.escenario.levels.level;
import modelos.personajes.Personaje;

public class GestorSecciones {
    private final levelmanager niveles;
    private final ColisionNivel colision;
    private final int tile, ancho, alto;
    private SeccionListener listener;
    private boolean enAgujero;
    private boolean terminado;

    public GestorSecciones(levelmanager niveles, ColisionNivel colision,
                           int tile, int ancho, int alto) {
        this.niveles = niveles; this.colision = colision;
        this.tile = tile; this.ancho = ancho; this.alto = alto;
    }

    public void setListener(SeccionListener listener) { this.listener = listener; }
    public boolean estaTerminado() { return terminado; }

    public void actualizar(Personaje p) {
        if (terminado) return;

        int tipo = niveles.getNivel().agujeroEn(p.getX(), p.getY(), ancho, alto, tile);
        if (tipo == -1) { enAgujero = false; return; }
        if (enAgujero) return;

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
        enAgujero = true;
        int[] c = nivel.buscar(agujeroLlegada);
        if (c != null) {
            p.corregirPosicion(c[0] * tile + (tile - ancho) / 2.0, (c[1] + 1) * tile - alto);
            p.setVelocidadY(0);
        }
        if (listener != null) listener.alCambiarSeccion(niveles.getSeccion());
    }
}