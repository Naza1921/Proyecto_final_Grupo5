package vista;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import recursos.CargadorRecursos;
import modelos.personajes.Personaje.TipoRata;

// Relacionamos cada tipo y acción con las hojas de sprites de la rata.
public final class SpritesRatas {
    private static final int FILA_IDLE = 0;

    public enum Accion {
        QUIETA, CAMINAR, SALTAR, AGACHADA, HAB_ESPECIAL, RECIBIR_DANIO, MUERTE
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
        if (accion == Accion.HAB_ESPECIAL && tipo == TipoRata.BLANCA) {
            return cargarFramesHabilidadBlanca();
        }
        if (accion == Accion.HAB_ESPECIAL && tipo == TipoRata.VERDE) {
            return cargarFramesHabilidadVerde();
        }
        if (accion == Accion.HAB_ESPECIAL && tipo == TipoRata.NEGRA) {
            return cargarFramesHabilidadNegra();
        }
        return crearFramesAlineados(cargarHoja(tipo, accion), FILA_IDLE);
    }

    private static BufferedImage[] cargarFramesHabilidadBlanca() {
        BufferedImage hoja = CargadorRecursos.cargarImagen(
            "assets/rata_blanca/Animacion_de_ataque_energetico_del_raton.png"
        );
        int[][] limitesX = {
            {0, 351, 678, 1019, 1364, 1740, 2170},
            {0, 394, 648, 1011, 1373, 1783, 2170}
        };
        int[] iniciosY = {145, 435};
        int[] alturas = {160, 165};
        List<BufferedImage> frames = new ArrayList<>(12);
        for (int fila = 0; fila < limitesX.length; fila++) {
            SpriteSheet filaSprites = new SpriteSheet(
                hoja, limitesX[fila], iniciosY[fila], alturas[fila]
            );
            for (int indice = 0; indice < filaSprites.getCantidadFrames(0); indice++) {
                frames.add(filaSprites.getFrame(0, indice));
            }
        }
        return centrarFrames(frames);
    }

    private static BufferedImage[] cargarFramesHabilidadVerde() {
        BufferedImage hoja = CargadorRecursos.cargarImagen(
            "assets/rata_verde/rata_lanzando_veneno_6x2_transparente.png"
        );
        SpriteSheet spriteSheet = new SpriteSheet(
            hoja,
            new int[]{0, 230, 460},
            new int[]{6, 6},
            new int[]{6, 6}
        );
        List<BufferedImage> frames = new ArrayList<>(12);
        for (int fila = 0; fila < 2; fila++) {
            for (int indice = 0; indice < spriteSheet.getCantidadFrames(fila); indice++) {
                frames.add(spriteSheet.getFrame(fila, indice));
            }
        }
        return centrarFrames(frames);
    }

    private static BufferedImage[] cargarFramesHabilidadNegra() {
        BufferedImage hoja = CargadorRecursos.cargarImagen(
            "assets/rata_negra/rata_peste_lanzando_lampara_8x2_transparente.png"
        );
        SpriteSheet spriteSheet = new SpriteSheet(
            hoja,
            new int[]{0, 240, 480},
            new int[]{8, 8},
            new int[]{8, 8}
        );
        List<BufferedImage> frames = new ArrayList<>(16);
        for (int fila = 0; fila < 2; fila++) {
            for (int indice = 0; indice < spriteSheet.getCantidadFrames(fila); indice++) {
                frames.add(spriteSheet.getFrame(fila, indice));
            }
        }
        return centrarFrames(frames);
    }

    private static BufferedImage[] centrarFrames(List<BufferedImage> frames) {
        int anchoLienzo = 0;
        int altoLienzo = 0;
        for (BufferedImage frame : frames) {
            anchoLienzo = Math.max(anchoLienzo, frame.getWidth());
            altoLienzo = Math.max(altoLienzo, frame.getHeight());
        }

        BufferedImage[] alineados = new BufferedImage[frames.size()];
        for (int indice = 0; indice < frames.size(); indice++) {
            BufferedImage frame = frames.get(indice);
            BufferedImage alineado = new BufferedImage(
                anchoLienzo, altoLienzo, BufferedImage.TYPE_INT_ARGB
            );
            Graphics2D graficos = alineado.createGraphics();
            graficos.drawImage(
                frame,
                (anchoLienzo - frame.getWidth()) / 2,
                (altoLienzo - frame.getHeight()) / 2,
                null
            );
            graficos.dispose();
            alineados[indice] = alineado;
        }
        return alineados;
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
