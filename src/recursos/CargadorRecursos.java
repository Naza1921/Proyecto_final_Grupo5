package recursos;

import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import javax.imageio.ImageIO;

public final class CargadorRecursos {
    public static final String NIVEL_ATLAS = "assets/Nivel_Sprite_tablones.png";
    public static final String AGUJERO_ATLAS = "assets/Agujero_Sprite.png";
    private static final ConcurrentMap<String, SoftReference<BufferedImage>> IMAGENES =
        new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, CargaImagen> CARGAS_EN_CURSO =
        new ConcurrentHashMap<>();

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
        String clave = nombreArchivo.startsWith("/")
            ? nombreArchivo.substring(1) : nombreArchivo;
        SoftReference<BufferedImage> referencia = IMAGENES.get(clave);
        BufferedImage imagen = referencia == null ? null : referencia.get();
        if (imagen != null) {
            return imagen;
        }
        if (referencia != null) {
            IMAGENES.remove(clave, referencia);
        }

        CargaImagen nuevaCarga = new CargaImagen(clave);
        CargaImagen carga = CARGAS_EN_CURSO.putIfAbsent(clave, nuevaCarga);
        if (carga == null) {
            carga = nuevaCarga;
            carga.run();
        }

        try {
            imagen = carga.get();
            IMAGENES.put(clave, new SoftReference<>(imagen));
            CARGAS_EN_CURSO.remove(clave, carga);
            return imagen;
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
            return cargarImagenSinCache(clave);
        } catch (ExecutionException error) {
            CARGAS_EN_CURSO.remove(clave, carga);
            RuntimeException fallo = carga.getFallo();
            if (fallo != null) {
                throw fallo;
            }
            throw new IllegalStateException(
                "Failed to load image: " + clave, error.getCause()
            );
        }
    }

    public static void precargar(List<String> rutas) {
        if (rutas == null) {
            throw new IllegalArgumentException("Se requieren rutas de imágenes");
        }
        Set<String> rutasUnicas = new LinkedHashSet<>();
        for (String ruta : rutas) {
            if (ruta != null) {
                rutasUnicas.add(ruta.startsWith("/") ? ruta.substring(1) : ruta);
            }
        }
        for (String ruta : rutasUnicas) {
            try {
                cargarImagen(ruta);
            } catch (RuntimeException error) {
                System.err.println("No se pudo precargar la imagen " + ruta + ": "
                    + error.getMessage());
            }
        }
    }

    private static BufferedImage cargarImagenSinCache(String nombreArchivo) {
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

    private static final class CargaImagen extends FutureTask<BufferedImage> {
        private final FalloCarga fallo;

        private CargaImagen(String clave) {
            this(new FalloCarga(), clave);
        }

        private CargaImagen(FalloCarga fallo, String clave) {
            super(() -> {
                try {
                    return cargarImagenSinCache(clave);
                } catch (RuntimeException error) {
                    fallo.error = error;
                    throw error;
                }
            });
            this.fallo = fallo;
        }

        private RuntimeException getFallo() {
            return fallo.error;
        }
    }

    private static final class FalloCarga {
        private volatile RuntimeException error;
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
