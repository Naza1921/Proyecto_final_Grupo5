package controlador.utilz;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import javax.imageio.ImageIO;
//import java.awt.Color;



public class loadsave {
    
    public static final String PLAYER_ATLAS = "Rata_Sprite.png";
    public static final String NIVEL_ATLAS = "assets/Nivel_Sprite_tablones.png";
    //public static final String NIVEL_UNO_DATO = "assets/Nivel_Uno_Sprite (3).png";
    public static final String AGUJERO_ATLAS = "assets/Agujero_Sprite.png";

    public static URL obtenerURLRecurso(String filename) {
        String rutaRecurso = filename.startsWith("/") ? filename : "/" + filename;
        URL recurso = loadsave.class.getResource(rutaRecurso);
        if (recurso != null) {
            return recurso;
        }

        Path rutaFuente = Path.of("src").resolve(filename.startsWith("/")
            ? filename.substring(1) : filename);
        if (Files.isRegularFile(rutaFuente)) {
            try {
                return rutaFuente.toUri().toURL();
            } catch (MalformedURLException e) {
                throw new IllegalStateException("Failed to locate resource: " + filename, e);
            }
        }

        throw new IllegalArgumentException("Resource not found: " + filename);
    }

    public static BufferedImage GetSpriteatlas(String filename) {
        InputStream recurso = loadsave.class.getResourceAsStream("/" + filename);
        if (recurso == null) {
            Path rutaFuente = Path.of("src").resolve(filename.startsWith("/")
                ? filename.substring(1) : filename);
            if (Files.isRegularFile(rutaFuente)) {
                try {
                    recurso = Files.newInputStream(rutaFuente);
                } catch (IOException e) {
                    throw new IllegalStateException("Failed to load image: " + filename, e);
                }
            }
        }

        try (InputStream is = recurso) {
            if (is == null) {
                throw new IllegalArgumentException("Resource not found: " + filename);
            }

            BufferedImage img = ImageIO.read(is);
            if (img == null) {
                throw new IllegalArgumentException("Image could not be loaded: " + filename);
            }
            return img;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load image: " + filename, e);
        }
    }
public static int[][] obtenerdatonivel(String ruta) {
    BufferedImage img = GetSpriteatlas(ruta);
    int[][] nivdato = new int[img.getWidth()][img.getHeight()];
    for (int j = 0; j < img.getHeight(); j++)
        for (int i = 0; i < img.getWidth(); i++)
            nivdato[i][j] = (img.getRGB(i, j) >> 16) & 0xFF;   // si exportás con ×8: / 8
    return nivdato;
}
}
