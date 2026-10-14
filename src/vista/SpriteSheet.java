package vista;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class SpriteSheet {
    // Arreglo de imágenes que representan los frames del sprite
    // Arreglo de imágenes que representan los contornos de los frames del sprite
    private final BufferedImage[] frames;
    private final BufferedImage[] contornos;
    // Constantes para el grosor y color del contorno
    private static final int GROSOR_CONTORNO = 2;
    private static final Color COLOR_CONTORNO = Color.BLACK;

    public SpriteSheet(String path, int cantidadFrames) {
        this(cargarHoja(path), cantidadFrames);
    }

    public SpriteSheet(BufferedImage hoja, int cantidadFrames) {
        this(hoja, cantidadFrames, 0, 0,
                hoja == null ? 0 : hoja.getWidth(),
                hoja == null ? 0 : hoja.getHeight());
    }

    public SpriteSheet(BufferedImage hoja, int cantidadFrames, int x, int y,
                       int anchoRegion, int altoRegion) {
        if (hoja == null || cantidadFrames <= 0 || x < 0 || y < 0
                || anchoRegion < cantidadFrames || altoRegion <= 0
                || x > hoja.getWidth() - anchoRegion
                || y > hoja.getHeight() - altoRegion) {
            throw new IllegalArgumentException("Región inválida para la hoja de sprites.");
        }

        frames = new BufferedImage[cantidadFrames];
        contornos = new BufferedImage[cantidadFrames];
        for (int i = 0; i < cantidadFrames; i++) {
            int inicioX = x + (int) ((long) i * anchoRegion / cantidadFrames);
            int finX = x + (int) ((long) (i + 1) * anchoRegion / cantidadFrames);
            BufferedImage recorte = hoja.getSubimage(inicioX, y, finX - inicioX, altoRegion);
            BufferedImage sinFondo = quitarFondoNegro(recorte, 40);
            frames[i] = sinFondo;
            contornos[i] = generarContorno(sinFondo, GROSOR_CONTORNO, COLOR_CONTORNO);
        }
    }

    private static BufferedImage cargarHoja(String path) {
        try (InputStream is = SpriteSheet.class.getResourceAsStream(path)) {
            if (is == null) {
                throw new IllegalArgumentException("No se encontró el recurso: " + path);
            }
            BufferedImage hoja = ImageIO.read(is);
            if (hoja == null) {
                throw new IllegalArgumentException("El recurso no es una imagen válida: " + path);
            }
            return hoja;
        } catch (IOException | IllegalArgumentException e) {
            throw new RuntimeException("No se pudo cargar el sprite: " + path, e);
        }
    }

    // Método privado que elimina el fondo negro de una imagen
    // Se recorre cada pixel de la imagen y se verifica si es negro
    // Si es negro, se establece como transparente en la imagen de resultado
    // Se utiliza un valor de tolerancia para determinar qué tan negro 
    // debe ser un pixel para considerarlo fondo
    //
    // A diferencia de un recorrido pixel por pixel, acá solo se marca como fondo
    // el negro que está conectado a los bordes de la imagen (flood fill). Así,
    // el pelaje oscuro o las sombras del propio personaje no se vuelven transparentes,
    // aunque tengan un color tan oscuro como el fondo.
    private BufferedImage quitarFondoNegro(BufferedImage origen, int tolerancia) {
        int w = origen.getWidth();
        int h = origen.getHeight();

        BufferedImage resultado = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        boolean[] esFondo = new boolean[w * h];

        java.util.Deque<Integer> pendientes = new java.util.ArrayDeque<>();

        // Sembrar el flood fill con los píxeles del borde que sean negros.
        for (int px = 0; px < w; px++) {
            agregarSiEsNegro(origen, px, 0, tolerancia, esFondo, pendientes);
            agregarSiEsNegro(origen, px, h - 1, tolerancia, esFondo, pendientes);
        }
        for (int py = 0; py < h; py++) {
            agregarSiEsNegro(origen, 0, py, tolerancia, esFondo, pendientes);
            agregarSiEsNegro(origen, w - 1, py, tolerancia, esFondo, pendientes);
        }

        // Propagar el fondo a los píxeles negros vecinos (arriba, abajo, izquierda, derecha).
        while (!pendientes.isEmpty()) {
            int idx = pendientes.poll();
            int px = idx % w;
            int py = idx / w;

            agregarSiEsNegro(origen, px - 1, py, tolerancia, esFondo, pendientes);
            agregarSiEsNegro(origen, px + 1, py, tolerancia, esFondo, pendientes);
            agregarSiEsNegro(origen, px, py - 1, tolerancia, esFondo, pendientes);
            agregarSiEsNegro(origen, px, py + 1, tolerancia, esFondo, pendientes);
        }

        // Construir la imagen final: transparente donde se detectó fondo, opaco en el resto.
        for (int py = 0; py < h; py++) {
            for (int px = 0; px < w; px++) {
                if (esFondo[py * w + px]) {
                    resultado.setRGB(px, py, 0x00000000);
                } else {
                    int rgb = origen.getRGB(px, py);
                    resultado.setRGB(px, py, rgb | 0xFF000000);
                }
            }
        }

        return resultado;
    }

    // Si el píxel (px, py) es negro (según la tolerancia) y todavía no fue marcado,
    // lo agrega a la cola del flood fill y lo marca como fondo.
    private void agregarSiEsNegro(BufferedImage origen, int px, int py, int tolerancia,
                                    boolean[] esFondo, java.util.Deque<Integer> pendientes) {
        int w = origen.getWidth();
        int h = origen.getHeight();
        if (px < 0 || py < 0 || px >= w || py >= h) return;

        int idx = py * w + px;
        if (esFondo[idx]) return;

        int rgb = origen.getRGB(px, py);
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        if (r <= tolerancia && g <= tolerancia && b <= tolerancia) {
            esFondo[idx] = true;
            pendientes.add(idx);
        }
    }

    // Dilata la silueta no transparente de "origen" y pinta el borde con "color"
    private BufferedImage generarContorno(BufferedImage origen, int grosor, Color color) {
        int w = origen.getWidth();
        int h = origen.getHeight();
        //
        BufferedImage contorno = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        int colorRGB = color.getRGB() & 0x00FFFFFF;
        // Recorremos cada pixel de la imagen original
       // Si el pixel es transparente, verificamos si hay un pixel no transparente
       // en su vecindad (dentro del grosor especificado). Si es así
       // pintamos ese pixel con el color del contorno.
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int alpha = (origen.getRGB(x, y) >> 24) & 0xFF;
                if (alpha > 0) continue; // ya es parte de la cola, no es contorno

                boolean esBorde = false;
                for (int dy = -grosor; dy <= grosor && !esBorde; dy++) {
                    for (int dx = -grosor; dx <= grosor && !esBorde; dx++) {
                        int nx = x + dx, ny = y + dy;
                        if (nx < 0 || ny < 0 || nx >= w || ny >= h) continue;
                        int nAlpha = (origen.getRGB(nx, ny) >> 24) & 0xFF;
                        if (nAlpha > 0) esBorde = true;
                    }
                }

                if (esBorde) {
                    contorno.setRGB(x, y, (0xFF << 24) | colorRGB);
                }
            }
        }
        return contorno;
    }
    // Getters para acceder a los frames y contornos
    public BufferedImage[] getFrames() {
        return frames;
    }

    public BufferedImage getFrame(int index) {
        return frames[clamp(index)];
    }

    public BufferedImage getContorno(int index) {
        return contornos[clamp(index)];
    }

    private int clamp(int index) {
        return Math.max(0, Math.min(frames.length - 1, index));
    }

    public int getCantidadFrames() {
        return frames.length;
    }
}