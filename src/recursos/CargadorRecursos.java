package recursos;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;

public final class CargadorRecursos {
    public static final String NIVEL_ATLAS = "assets/Nivel_Sprite_tablones.png";
    public static final String AGUJERO_ATLAS = "assets/Agujero_Sprite.png";

    private CargadorRecursos() { }

    public static URL obtenerUrlRecurso(String nombreArchivo) {
        String rutaRecurso = nombreArchivo.startsWith("/")
            ? nombreArchivo : "/" + nombreArchivo;
        URL recurso = CargadorRecursos.class.getResource(rutaRecurso);
        if (recurso != null) {
            return recurso;
        }

        Path rutaFuente = rutaFuente(nombreArchivo);
        if (Files.isRegularFile(rutaFuente)) {
            try {
                return rutaFuente.toUri().toURL();
            } catch (MalformedURLException error) {
                throw new IllegalStateException(
                    "Failed to locate resource: " + nombreArchivo, error
                );
            }
        }

        throw new IllegalArgumentException("Resource not found: " + nombreArchivo);
    }

    public static BufferedImage cargarImagen(String nombreArchivo) {
        InputStream recurso = CargadorRecursos.class.getResourceAsStream(
            "/" + nombreArchivo
        );
        if (recurso == null) {
            Path rutaFuente = rutaFuente(nombreArchivo);
            if (Files.isRegularFile(rutaFuente)) {
                try {
                    recurso = Files.newInputStream(rutaFuente);
                } catch (IOException error) {
                    throw new IllegalStateException(
                        "Failed to load image: " + nombreArchivo, error
                    );
                }
            }
        }

        try (InputStream entrada = recurso) {
            if (entrada == null) {
                throw new IllegalArgumentException(
                    "Resource not found: " + nombreArchivo
                );
            }

            BufferedImage imagen = ImageIO.read(entrada);
            if (imagen == null) {
                throw new IllegalArgumentException(
                    "Image could not be loaded: " + nombreArchivo
                );
            }
            return imagen;
        } catch (IOException error) {
            throw new IllegalStateException(
                "Failed to load image: " + nombreArchivo, error
            );
        }
    }

    public static int[][][] obtenerCanalesNivel(String ruta) {
        BufferedImage imagen = cargarImagen(ruta);
        int[][][] canales = new int[3][imagen.getWidth()][imagen.getHeight()];
        for (int y = 0; y < imagen.getHeight(); y++) {
            for (int x = 0; x < imagen.getWidth(); x++) {
                int pixel = imagen.getRGB(x, y);
                canales[0][x][y] = (pixel >>> 16) & 0xFF;
                canales[1][x][y] = (pixel >>> 8) & 0xFF;
                canales[2][x][y] = pixel & 0xFF;
            }
        }
        return canales;
    }

    private static Path rutaFuente(String nombreArchivo) {
        return Path.of("src").resolve(nombreArchivo.startsWith("/")
            ? nombreArchivo.substring(1) : nombreArchivo);
    }
}
