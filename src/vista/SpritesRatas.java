package vista;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import modelos.personajes.Personaje.TipoRata;

// Relacionamos cada tipo de rata con su hoja organizada y preparamos su secuencia idle.
public final class SpritesRatas {
    private static final int FILA_IDLE = 0;

    private SpritesRatas() {
    }

    public static SpriteSheet cargar(TipoRata tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("Se requiere el tipo de rata para cargar sus sprites");
        }
        // Cada fuente tiene su propia grilla; cargamos la secuencia idle que usa actualmente el juego.
        switch (tipo) {
            case BLANCA:
                return cargarSecuenciaIdle("/assets/rata_blanca/rata_blanca.png", 122, 4, 8);
            case GRIS:
                return cargarSecuenciaIdle(
                    "/assets/rata_sprites_gris/rata_idle_quieto.png", 108, 4, 4
                );
            case NEGRA:
                return cargarSecuenciaIdle("/assets/rata_negra/rata_negra.png", 169, 3, 3);
            case VERDE:
                return cargarSecuenciaIdle("/assets/rata_verde/rata_verde.png", 105, 6, 8);
            default:
                throw new IllegalArgumentException("Tipo de rata sin hoja de sprites: " + tipo);
        }
    }

    // Los recursos organizados conservan las hojas fuente; el juego solo anima la secuencia idle.
    private static SpriteSheet cargarSecuenciaIdle(String ruta, int altoFila,
                                                   int cantidadFrames, int columnas) {
        // Cargamos la franja idle del atlas sin interpretar como frame el resto de la hoja.
        return new SpriteSheet(ruta, new int[] {0, altoFila},
            new int[] {cantidadFrames}, new int[] {columnas});
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
