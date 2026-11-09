package vista;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.function.Supplier;

// Relacionamos cada tipo y acción con las hojas de sprites de la rata.
public final class SpritesRatas {
    private static final int FILA_IDLE = 0;

    public enum Accion {
        QUIETA, CAMINAR, SALTAR, AGACHADA, HAB_ESPECIAL, RECIBIR_DANIO, MUERTE
    }

    private SpritesRatas() {
    }

    public static SpriteSheet cargar(String clave) {
        if (clave == null) {
            throw new IllegalArgumentException("Se requiere la clave de la rata para cargar sus sprites");
        }
        return cargarHoja(clave, Accion.QUIETA);
    }

    public static BufferedImage[] cargarFrames(String clave, Accion accion) {
        if (clave == null || accion == null) {
            throw new IllegalArgumentException("Se requieren la rata y la acción de animación");
        }
        Supplier<BufferedImage[]> cargador =
            accion == Accion.HAB_ESPECIAL ? CatalogoSpritesRatas.framesHabilidad(clave) : null;
        if (cargador != null) {
            return cargador.get();
        }
        return crearFramesAlineados(cargarHoja(clave, accion), FILA_IDLE);
    }
    private static SpriteSheet cargarHoja(String clave, Accion accion) {
        return CatalogoSpritesRatas.cargar(clave, accion);
    }

    // Devuelve el primer cuadro de la fila de reposo, ajustado sin deformar el sprite.
    public static BufferedImage crearVistaIdle(String clave, int ancho, int alto) {
        BufferedImage frame = crearFramesAlineados(cargar(clave), FILA_IDLE)[0];
        BufferedImage vista = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = vista.createGraphics();
        dibujarAjustado(g, frame, 0, 0, ancho, alto);
        g.dispose();
        return vista;
    }

    public static void dibujarAjustado(Graphics2D g, BufferedImage frame,
                                       int x, int y, int ancho, int alto) {
        
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        double escala = Math.min((double) ancho / frame.getWidth(), (double) alto / frame.getHeight());
        int anchoDibujo = (int) Math.round(frame.getWidth() * escala);
        int altoDibujo = (int) Math.round(frame.getHeight() * escala);
        g.drawImage(frame, x + (ancho - anchoDibujo) / 2, y + (alto - altoDibujo) / 2,
            anchoDibujo, altoDibujo, null);
    }

   
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
