package vista;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class SpriteRataInfestada {
    public enum Animacion {
        QUIETA,
        CAMINAR_DERECHA,
        CAMINAR_IZQUIERDA,
        CORRER_DERECHA,
        CORRER_IZQUIERDA,
        MORDER,
        HERIDA,
        SALTAR
    }

    private static final String RUTA_SPRITE = "/assets/arte_enemigos/rata_infestada.jpeg";
    private static final long DURACION_FRAME_QUIETA_MS = 180;
    private static final long DURACION_FRAME_CAMINAR_MS = 110;
    private static final long DURACION_FRAME_CORRER_MS = 80;
    private static final long DURACION_FRAME_ACCION_MS = 100;

    private final Map<Animacion, SpriteSheet> animaciones = new EnumMap<>(Animacion.class);
    private Animacion animacionActual = Animacion.QUIETA;
    private int frameActual;
    private long tiempoAcumuladoMs;

    public SpriteRataInfestada() {
        BufferedImage hoja = cargarHoja();
        agregarAnimacion(hoja, Animacion.QUIETA, 8, 20, 55, 1496, 105);
        agregarAnimacion(hoja, Animacion.CAMINAR_DERECHA, 10, 25, 210, 1490, 82);
        agregarAnimacion(hoja, Animacion.CAMINAR_IZQUIERDA, 10, 30, 350, 1490, 88);
        agregarAnimacion(hoja, Animacion.CORRER_DERECHA, 9, 20, 490, 1490, 86);
        agregarAnimacion(hoja, Animacion.CORRER_IZQUIERDA, 9, 25, 620, 1490, 88);
        agregarAnimacion(hoja, Animacion.MORDER, 9, 25, 755, 1490, 90);
        agregarAnimacion(hoja, Animacion.HERIDA, 2, 585, 900, 270, 110);
    }

    public void setAnimacion(Animacion nuevaAnimacion) {
        if (nuevaAnimacion == null) {
            throw new IllegalArgumentException("La animación no puede ser null.");
        }
        if (nuevaAnimacion != animacionActual) {
            animacionActual = nuevaAnimacion;
            frameActual = 0;
            tiempoAcumuladoMs = 0;
        }
    }

    public Animacion getAnimacion() {
        return animacionActual;
    }

    public void actualizar(long deltaMilisegundos) {
        if (deltaMilisegundos < 0) {
            throw new IllegalArgumentException("El tiempo transcurrido no puede ser negativo.");
        }

        SpriteSheet sprite = animaciones.get(animacionActual);
        if (sprite == null || sprite.getCantidadFrames() < 2) {
            return;
        }

        tiempoAcumuladoMs += deltaMilisegundos;
        long duracionFrame = duracionFrameActual();
        while (tiempoAcumuladoMs >= duracionFrame) {
            tiempoAcumuladoMs -= duracionFrame;
            if (esAnimacionDeUnaSolaEjecucion()
                    && frameActual == sprite.getCantidadFrames() - 1) {
                setAnimacion(Animacion.QUIETA);
                return;
            }
            frameActual = (frameActual + 1) % sprite.getCantidadFrames();
        }
    }

    public void dibujar(Graphics2D graficos, int x, int y, int ancho, int alto) {
        if (graficos == null || ancho <= 0 || alto <= 0) {
            throw new IllegalArgumentException("El gráfico y las dimensiones deben ser válidos.");
        }

        SpriteSheet sprite = animaciones.get(animacionActual);
        if (sprite == null) {
            return;
        }

        BufferedImage contorno = sprite.getContorno(frameActual);
        BufferedImage imagen = sprite.getFrame(frameActual);
        graficos.drawImage(contorno, x, y, ancho, alto, null);
        graficos.drawImage(imagen, x, y, ancho, alto, null);
    }

    private void agregarAnimacion(BufferedImage hoja, Animacion animacion, int cantidadFrames,
                                  int x, int y, int ancho, int alto) {
        animaciones.put(animacion, new SpriteSheet(hoja, cantidadFrames, x, y, ancho, alto));
    }

    private BufferedImage cargarHoja() {
        try (InputStream entrada = getClass().getResourceAsStream(RUTA_SPRITE)) {
            if (entrada == null) {
                throw new IllegalArgumentException("No se encontró el recurso: " + RUTA_SPRITE);
            }
            BufferedImage hoja = ImageIO.read(entrada);
            if (hoja == null) {
                throw new IllegalArgumentException("El recurso no es una imagen válida: " + RUTA_SPRITE);
            }
            return hoja;
        } catch (IOException | IllegalArgumentException e) {
            throw new RuntimeException("No se pudo cargar la animación de la rata infestada.", e);
        }
    }

    private long duracionFrameActual() {
        switch (animacionActual) {
            case QUIETA:
                return DURACION_FRAME_QUIETA_MS;
            case CAMINAR_DERECHA:
            case CAMINAR_IZQUIERDA:
                return DURACION_FRAME_CAMINAR_MS;
            case CORRER_DERECHA:
            case CORRER_IZQUIERDA:
                return DURACION_FRAME_CORRER_MS;
            default:
                return DURACION_FRAME_ACCION_MS;
        }
    }

    private boolean esAnimacionDeUnaSolaEjecucion() {
        return animacionActual == Animacion.MORDER || animacionActual == Animacion.HERIDA;
    }
}