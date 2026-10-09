package vista;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

// Leemos hojas de sprites y guardamos cada cuadro por fila para que la vista pida
// una animación concreta sin mezclarla con los cuadros de otra acción.
public class SpriteSheet {
    private final BufferedImage[][] frames;
    private final BufferedImage[][] contornos;
    private static final int GROSOR_CONTORNO = 3;
    private static final Color COLOR_CONTORNO = Color.YELLOW;

    // Mantiene compatibilidad con hojas de una sola fila, como la barra de vida.
    public SpriteSheet(String path, int cantidadFrames) {
        this(path, new int[] {0, -1}, new int[] {cantidadFrames},
            new int[] {cantidadFrames}, new int[] {0});
    }

    // Estas opciones sencillas sirven cuando todas las filas parten de la primera columna.
    public SpriteSheet(String path, int[] limitesFilas, int[] cantidadesPorFila,
                    int[] columnasPorFila) {
        this(path, limitesFilas, cantidadesPorFila, columnasPorFila,
            new int[cantidadesPorFila == null ? 0 : cantidadesPorFila.length]);
    }

    // Acá recibimos la configuración completa: límites verticales, cuadros,
    // columnas fuente y desplazamiento horizontal por fila.
    public SpriteSheet(String path, int[] limitesFilas, int[] cantidadesPorFila,
                       int[] columnasPorFila, int[] inicioColumna) {
        if (limitesFilas == null || cantidadesPorFila == null
                || limitesFilas.length != cantidadesPorFila.length + 1
                || columnasPorFila == null || columnasPorFila.length != cantidadesPorFila.length
                || inicioColumna == null || inicioColumna.length != cantidadesPorFila.length) {
            throw new IllegalArgumentException("La configuración de filas y columnas no es válida");
        }

        int[] cantidadCuadros = cantidadesPorFila.clone();
        int[] cantidadColumnas = columnasPorFila.clone();
        int[] columnaInicial = inicioColumna.clone();
        frames = new BufferedImage[cantidadCuadros.length][];
        contornos = new BufferedImage[cantidadCuadros.length][];

        try (InputStream entrada = getClass().getResourceAsStream(path)) {
            if (entrada == null) {
                throw new IOException("No se encontró el recurso");
            }
            BufferedImage hoja = ImageIO.read(entrada);
            if (hoja == null) {
                throw new IOException("El recurso no contiene una imagen válida");
            }

            int[] limites = limitesFilas.clone();
            if (limites[limites.length - 1] == -1) {
                limites[limites.length - 1] = hoja.getHeight();
            }
            validarConfiguracion(hoja, limites, cantidadCuadros, cantidadColumnas, columnaInicial);

            for (int fila = 0; fila < cantidadCuadros.length; fila++) {
                frames[fila] = new BufferedImage[cantidadCuadros[fila]];
                contornos[fila] = new BufferedImage[cantidadCuadros[fila]];
                for (int columna = 0; columna < cantidadCuadros[fila]; columna++) {
                    int indiceColumna = columnaInicial[fila] + columna;
                    int xInicio = indiceColumna * hoja.getWidth() / cantidadColumnas[fila];
                    int xFin = (indiceColumna + 1) * hoja.getWidth() / cantidadColumnas[fila];
                    BufferedImage recorte = hoja.getSubimage(
                        xInicio, limites[fila], xFin - xInicio, limites[fila + 1] - limites[fila]
                    );
                    int[] fondos = {
                        recorte.getRGB(0, 0),
                        recorte.getRGB(recorte.getWidth() - 1, 0),
                        recorte.getRGB(0, recorte.getHeight() - 1),
                        recorte.getRGB(recorte.getWidth() - 1, recorte.getHeight() - 1)
                    };
                    frames[fila][columna] = quitarFondo(recorte, fondos, 24);
                    contornos[fila][columna] =
                        generarContorno(frames[fila][columna], GROSOR_CONTORNO, COLOR_CONTORNO);
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            throw new IllegalArgumentException("No se pudo cargar la hoja de sprites: " + path, e);
        }
    }

    private void validarConfiguracion(BufferedImage hoja, int[] limites,
                                      int[] cantidadesPorFila, int[] columnasPorFila,
                                      int[] inicioColumna) {
        // Permitimos cargar una sola franja de una hoja completa sin procesar las animaciones
        // que el panel todavía no usa; cada límite debe quedar dentro de la imagen.
        for (int fila = 0; fila < cantidadesPorFila.length; fila++) {
            if (limites[fila] < 0 || limites[fila + 1] > hoja.getHeight()
                    || limites[fila] >= limites[fila + 1]
                    || cantidadesPorFila[fila] <= 0
                    || columnasPorFila[fila] <= 0
                    || inicioColumna[fila] < 0
                    || inicioColumna[fila] + cantidadesPorFila[fila] > columnasPorFila[fila]) {
                throw new IllegalArgumentException("La fila " + fila + " tiene límites o cuadros inválidos");
            }
        }
    }

    public BufferedImage getFrame(int indice) {
        return getFrame(0, Math.max(0, Math.min(indice, frames[0].length - 1)));
    }

    // El acceso por fila es estricto: un índice inválido no se convierte en otro frame.
    public BufferedImage getFrame(int fila, int indice) {
        if (fila < 0 || fila >= frames.length || indice < 0 || indice >= frames[fila].length) {
            throw new IndexOutOfBoundsException("El frame solicitado no existe en esa fila");
        }
        return frames[fila][indice];
    }

    public BufferedImage getContorno(int indice) {
        return contornos[0][Math.max(0, Math.min(indice, contornos[0].length - 1))];
    }

    public BufferedImage[] getFrames() {
        return frames[0].clone();
    }

    public int getCantidadFrames() {
        return frames[0].length;
    }

    public int getCantidadFrames(int fila) {
        validarFila(fila);
        return frames[fila].length;
    }

    public int getCantidadFilas() {
        return frames.length;
    }

    private void validarFila(int fila) {
        if (fila < 0 || fila >= frames.length) {
            throw new IndexOutOfBoundsException("La fila solicitada no existe en la hoja");
        }
    }

    // Quitamos solo el fondo que toca los bordes; así no borramos los colores parecidos
    // que forman parte del dibujo cuando están encerrados dentro del sprite.
    private BufferedImage quitarFondo(BufferedImage origen, int[] coloresFondo, int tolerancia) {
        int ancho = origen.getWidth();
        int alto = origen.getHeight();
        BufferedImage resultado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        boolean[] esFondo = new boolean[ancho * alto];
        java.util.Deque<Integer> pendientes = new java.util.ArrayDeque<>();
        for (int x = 0; x < ancho; x++) {
            agregarSiEsFondo(origen, x, 0, coloresFondo, tolerancia, esFondo, pendientes);
            agregarSiEsFondo(origen, x, alto - 1, coloresFondo, tolerancia, esFondo, pendientes);
        }
        for (int y = 0; y < alto; y++) {
            agregarSiEsFondo(origen, 0, y, coloresFondo, tolerancia, esFondo, pendientes);
            agregarSiEsFondo(origen, ancho - 1, y, coloresFondo, tolerancia, esFondo, pendientes);
        }

        while (!pendientes.isEmpty()) {
            int indice = pendientes.remove();
            int x = indice % ancho;
            int y = indice / ancho;
            agregarSiEsFondo(origen, x - 1, y, coloresFondo, tolerancia, esFondo, pendientes);
            agregarSiEsFondo(origen, x + 1, y, coloresFondo, tolerancia, esFondo, pendientes);
            agregarSiEsFondo(origen, x, y - 1, coloresFondo, tolerancia, esFondo, pendientes);
            agregarSiEsFondo(origen, x, y + 1, coloresFondo, tolerancia, esFondo, pendientes);
        }

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int color = origen.getRGB(x, y);
                resultado.setRGB(x, y, esFondo[y * ancho + x] ? 0x00000000 : color);
            }
        }
        return resultado;
    }

    private void agregarSiEsFondo(BufferedImage origen, int x, int y, int[] coloresFondo,
                                  int tolerancia,
                                  boolean[] esFondo, java.util.Deque<Integer> pendientes) {
        int ancho = origen.getWidth();
        int alto = origen.getHeight();
        if (x < 0 || y < 0 || x >= ancho || y >= alto) {
            return;
        }
        int indice = y * ancho + x;
        if (esFondo[indice]) {
            return;
        }

        // Guardamos el píxel para revisarlo una sola vez y expandir el fondo desde sus bordes.
        int color = origen.getRGB(x, y);
        if (esColorDeFondo(color, coloresFondo, tolerancia)) {
            esFondo[indice] = true;
            pendientes.add(indice);
        }
    }

    private boolean esColorDeFondo(int color, int[] coloresFondo, int tolerancia) {
        // Las esquinas pueden tener tonos distintos; aceptamos una pequeña variación por canal.
        for (int fondo : coloresFondo) {
            int diferenciaRojo = Math.abs(((color >> 16) & 0xFF) - ((fondo >> 16) & 0xFF));
            int diferenciaVerde = Math.abs(((color >> 8) & 0xFF) - ((fondo >> 8) & 0xFF));
            int diferenciaAzul = Math.abs((color & 0xFF) - (fondo & 0xFF));
            if (diferenciaRojo <= tolerancia && diferenciaVerde <= tolerancia
                    && diferenciaAzul <= tolerancia) {
                return true;
            }
        }
        return false;
    }

    private BufferedImage generarContorno(BufferedImage origen, int grosor, Color color) {
        // Construimos el contorno aparte para poder dibujarlo sin modificar el frame original.
        int ancho = origen.getWidth();
        int alto = origen.getHeight();
        BufferedImage contorno = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        int colorRGB = color.getRGB() & 0x00FFFFFF;

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                if (((origen.getRGB(x, y) >> 24) & 0xFF) != 0) {
                    continue;
                }
                boolean esBorde = false;
                for (int dy = -grosor; dy <= grosor && !esBorde; dy++) {
                    for (int dx = -grosor; dx <= grosor && !esBorde; dx++) {
                        int vecinoX = x + dx;
                        int vecinoY = y + dy;
                        if (vecinoX >= 0 && vecinoY >= 0 && vecinoX < ancho && vecinoY < alto
                                && ((origen.getRGB(vecinoX, vecinoY) >> 24) & 0xFF) > 0) {
                            esBorde = true;
                        }
                    }
                }
                if (esBorde) {
                    contorno.setRGB(x, y, (0xFF << 24) | colorRGB);
                }
            }
        }
        return contorno;
    }
}
