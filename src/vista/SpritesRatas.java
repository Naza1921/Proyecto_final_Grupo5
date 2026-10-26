package vista;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import modelos.personajes.Personaje.TipoRata;

// Relacionamos cada tipo y acción con las hojas de sprites de la rata.
public final class SpritesRatas {
    private static final int FILA_IDLE = 0;

    public enum Accion {
        QUIETA, CAMINAR, SALTAR, AGACHADA, RECIBIR_DANIO, MUERTE
    }

    private SpritesRatas() {
    }

    public static SpriteSheet cargar(TipoRata tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("Se requiere el tipo de rata para cargar sus sprites");
        }
        return cargarHoja(tipo, Accion.QUIETA);
    }

    public static BufferedImage[] cargarFrames(TipoRata tipo, Accion accion) {
        return crearFramesAlineados(cargarHoja(tipo, accion), FILA_IDLE);
    }

    private static SpriteSheet cargarHoja(TipoRata tipo, Accion accion) {
        return CatalogoSpritesRatas.cargar(tipo, accion);
    }

    // Devuelve el primer cuadro de la fila de reposo, ajustado sin deformar el sprite.
    public static BufferedImage crearVistaIdle(TipoRata tipo, int ancho, int alto) {
        BufferedImage frame = crearFramesAlineados(cargar(tipo), FILA_IDLE)[0];
        BufferedImage vista = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = vista.createGraphics();
        dibujarAjustado(g, frame, 0, 0, ancho, alto);
        g.dispose();
        return vista;
    }

    public static void dibujarAjustado(Graphics2D g, BufferedImage frame,
                                       int x, int y, int ancho, int alto) {
        // Ajustamos el dibujo al espacio disponible sin estirar la imagen ni suavizar sus píxeles.
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        double escala = Math.min((double) ancho / frame.getWidth(), (double) alto / frame.getHeight());
        int anchoDibujo = (int) Math.round(frame.getWidth() * escala);
        int altoDibujo = (int) Math.round(frame.getHeight() * escala);
        g.drawImage(frame, x + (ancho - anchoDibujo) / 2, y + (alto - altoDibujo) / 2,
            anchoDibujo, altoDibujo, null);
    }

    // Conserva el anclaje original de cada celda y solo completa a la derecha si una división
    // de la hoja dejó anchos de uno o dos píxeles distintos entre cuadros.
    public static BufferedImage[] crearFramesAlineados(SpriteSheet hoja, int fila) {
        int cantidad = hoja.getCantidadFrames(fila);
        BufferedImage[] alineados = new BufferedImage[cantidad];
        int anchoLienzo = 0;
        int altoLienzo = 0;

        for (int indice = 0; indice < cantidad; indice++) {
            BufferedImage frame = hoja.getFrame(fila, indice);
            anchoLienzo = Math.max(anchoLienzo, frame.getWidth());
            altoLienzo = Math.max(altoLienzo, frame.getHeight());
        }

        for (int indice = 0; indice < cantidad; indice++) {
            BufferedImage frame = hoja.getFrame(fila, indice);
            BufferedImage alineado = new BufferedImage(anchoLienzo, altoLienzo,
                BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = alineado.createGraphics();
            g.drawImage(frame, 0, 0, null);
            g.dispose();
            alineados[indice] = alineado;
        }
        return alineados;
    }
}
