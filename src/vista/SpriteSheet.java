package vista;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class SpriteSheet {
    //aqui nosotros definimos el buffer de los frames 
    //tambien los contornos 
    private final BufferedImage[] frames;
    private final BufferedImage[] contornos;
    //aqui nosotros establecemos el tamano del grosor que es de 3
    //color del cortorno que seria amarillo
    private static final int GROSOR_CONTORNO = 3;
    private static final Color COLOR_CONTORNO = Color.YELLOW;

    public SpriteSheet(String path, int cantidadFrames) {
        //aqui no nosotros plateamoos la nueva cantidad de freames apartir 
        //de la imagen de png 
        frames = new BufferedImage[cantidadFrames];
        contornos = new BufferedImage[cantidadFrames];
        //apartir del path  
        //donde se  ubicaria el sprite
        try (InputStream is = getClass().getResourceAsStream(path)) {
            BufferedImage hoja = ImageIO.read(is);
            //se determina el tamano del frame 
            int anchoFrame = hoja.getWidth() / cantidadFrames; //ancho del frame
            int altoFrame = hoja.getHeight(); //alto del frame
           //la tolerancia del contorno 
            for (int i = 0; i < cantidadFrames; i++) {
                BufferedImage recorte = hoja.getSubimage(i * anchoFrame, 0, anchoFrame, altoFrame);
                BufferedImage sinFondo = quitarFondoNegro(recorte, 40);
                frames[i] = sinFondo;
                contornos[i] = generarContorno(sinFondo, GROSOR_CONTORNO, COLOR_CONTORNO);
            }
            //en caso que no se carge el frame , tire este error
        } catch (IOException | IllegalArgumentException e) {
            throw new RuntimeException("No se pudo cargar el sprite: " + path, e);
        }
    }
    //para eliminar el fondo negro que tenia 
    //el frame ya que este se arrastraria frame por frame
    private BufferedImage quitarFondoNegro(BufferedImage origen, int tolerancia) {
        BufferedImage resultado = new BufferedImage(
                origen.getWidth(), origen.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for (int py = 0; py < origen.getHeight(); py++) {
            for (int px = 0; px < origen.getWidth(); px++) {
                int rgb = origen.getRGB(px, py);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                if (r <= tolerancia && g <= tolerancia && b <= tolerancia) {
                    resultado.setRGB(px, py, 0x00000000);
                } else {
                    resultado.setRGB(px, py, rgb | 0xFF000000);
                }
            }
        }
        return resultado;
    }

    // Dilata la silueta no transparente de "origen" y pinta el borde con "color"
    private BufferedImage generarContorno(BufferedImage origen, int grosor, Color color) {
        int w = origen.getWidth();
        int h = origen.getHeight();
        //
        BufferedImage contorno = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        int colorRGB = color.getRGB() & 0x00FFFFFF;

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