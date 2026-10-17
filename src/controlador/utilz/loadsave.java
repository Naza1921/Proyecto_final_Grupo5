package controlador.utilz;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import java.awt.Color;



public class loadsave {
    
    public static final String PLAYER_ATLAS = "Rata_Sprite.png";
    public static final String NIVEL_ATLAS = "assets/Nivel_Sprite_para_usar_fondos.png";
    public static final String NIVEL_UNO_DATO = "assets/Nivel_Uno_Sprite (3).png";


    public static BufferedImage GetSpriteatlas(String filename) {
        try (InputStream is = loadsave.class.getResourceAsStream("/" + filename)) {
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
        public static int[][] obtenerdatonivel() {
        int[][] nivdato = new int [192][128];
        BufferedImage img = GetSpriteatlas(NIVEL_UNO_DATO);
        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color colorval = new Color(img.getRGB(i, j));
                int valorposi = img.getRGB(i, j);
                if (valorposi >= 48) {
                    valorposi = 0;
                }
                nivdato[i][j] = colorval.getRed(); // Asignar el valor del canal rojo a la matriz

            }
        }
        return nivdato;
    } 
}

