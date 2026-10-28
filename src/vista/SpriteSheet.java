package vista;

import java.awt.Color;
import java.awt.image.BufferedImage;
import recursos.CargadorRecursos;


public class SpriteSheet {
    private final BufferedImage[][] frames;
    private final BufferedImage[][] contornos;
    private static final int GROSOR_CONTORNO = 3;
    private static final Color COLOR_CONTORNO = Color.YELLOW;

    public SpriteSheet(String path, int cantidadFrames) {
        this(cargarHoja(path), cantidadFrames);
    }

    public SpriteSheet(String path, int[] limitesFilas, int[] cantidadesPorFila,
                       int[] columnasPorFila) {
        this(path, limitesFilas, cantidadesPorFila, columnasPorFila,
            new int[cantidadesPorFila == null ? 0 : cantidadesPorFila.length]);
    }

    public SpriteSheet(String path, int[] limitesFilas, int[] cantidadesPorFila,
                       int[] columnasPorFila, int[] inicioColumna) {
        this(cargarHoja(path), limitesFilas, cantidadesPorFila, columnasPorFila, inicioColumna);
    }

    public SpriteSheet(BufferedImage hoja, int cantidadFrames) {
        this(hoja, cantidadFrames, 0, 0,
                hoja == null ? 0 : hoja.getWidth(),
                hoja == null ? 0 : hoja.getHeight());
    }

    public SpriteSheet(BufferedImage hoja, int cantidadFrames, int x, int y,
                       int anchoRegion, int altoRegion) {
        this(hoja, crearLimitesFrames(
            hoja, cantidadFrames, x, y, anchoRegion, altoRegion
        ), y, altoRegion);
    }

    public SpriteSheet(BufferedImage hoja, int[] limitesX, int y, int altoRegion) {
        if (hoja == null || limitesX == null || limitesX.length < 2
                || y < 0 || altoRegion <= 0 || y > hoja.getHeight() - altoRegion) {
            throw new IllegalArgumentException("Región inválida para la hoja de sprites.");
        }

        int[] limites = limitesX.clone();
        for (int i = 0; i < limites.length - 1; i++) {
            if (limites[i] < 0 || limites[i] >= limites[i + 1]
                    || limites[i + 1] > hoja.getWidth()) {
                throw new IllegalArgumentException("Los límites de los cuadros no son válidos.");
            }
        }
        frames = new BufferedImage[1][];
        contornos = new BufferedImage[1][];
        frames[0] = new BufferedImage[limites.length - 1];
        contornos[0] = new BufferedImage[limites.length - 1];
        for (int i = 0; i < frames[0].length; i++) {
            BufferedImage recorte = hoja.getSubimage(
                limites[i], y, limites[i + 1] - limites[i], altoRegion
            );
            BufferedImage sinFondo = quitarFondo(recorte, obtenerColoresFondo(recorte), 24);
            frames[0][i] = sinFondo;
            contornos[0][i] = generarContorno(sinFondo, GROSOR_CONTORNO, COLOR_CONTORNO);
        }
    }

    private static int[] crearLimitesFrames(BufferedImage hoja, int cantidadFrames,
                                             int x, int y, int anchoRegion, int altoRegion) {
        if (hoja == null || cantidadFrames <= 0 || x < 0 || y < 0
                || anchoRegion < cantidadFrames || altoRegion <= 0
                || x > hoja.getWidth() - anchoRegion
                || y > hoja.getHeight() - altoRegion) {
            throw new IllegalArgumentException("Región inválida para la hoja de sprites.");
        }

        int[] limites = new int[cantidadFrames + 1];
        for (int i = 0; i <= cantidadFrames; i++) {
            limites[i] = x + (int) ((long) i * anchoRegion / cantidadFrames);
        }
        return limites;
    }

    public SpriteSheet(BufferedImage hoja, int[] limitesFilas, int[] cantidadesPorFila,
                       int[] columnasPorFila) {
        this(hoja, limitesFilas, cantidadesPorFila, columnasPorFila,
            new int[cantidadesPorFila == null ? 0 : cantidadesPorFila.length]);
    }

    public SpriteSheet(BufferedImage hoja, int[] limitesFilas, int[] cantidadesPorFila,
                       int[] columnasPorFila, int[] inicioColumna) {
        if (hoja == null || limitesFilas == null || cantidadesPorFila == null
                || cantidadesPorFila.length == 0
                || limitesFilas.length != cantidadesPorFila.length + 1
                || columnasPorFila == null || columnasPorFila.length != cantidadesPorFila.length
                || inicioColumna == null || inicioColumna.length != cantidadesPorFila.length) {
            throw new IllegalArgumentException("La configuración de filas y columnas no es válida");
        }

        int[] limites = limitesFilas.clone();
        int[] cantidadCuadros = cantidadesPorFila.clone();
        int[] cantidadColumnas = columnasPorFila.clone();
        int[] columnaInicial = inicioColumna.clone();
        if (limites[limites.length - 1] == -1) {
            limites[limites.length - 1] = hoja.getHeight();
        }
        validarConfiguracion(hoja, limites, cantidadCuadros, cantidadColumnas, columnaInicial);

        frames = new BufferedImage[cantidadCuadros.length][];
        contornos = new BufferedImage[cantidadCuadros.length][];
        for (int fila = 0; fila < cantidadCuadros.length; fila++) {
            frames[fila] = new BufferedImage[cantidadCuadros[fila]];
            contornos[fila] = new BufferedImage[cantidadCuadros[fila]];
            for (int columna = 0; columna < cantidadCuadros[fila]; columna++) {
                int indiceColumna = columnaInicial[fila] + columna;
                int inicioX = indiceColumna * hoja.getWidth() / cantidadColumnas[fila];
                int finX = (indiceColumna + 1) * hoja.getWidth() / cantidadColumnas[fila];
                BufferedImage recorte = hoja.getSubimage(
                    inicioX, limites[fila], finX - inicioX, limites[fila + 1] - limites[fila]
                );
                BufferedImage sinFondo = quitarFondo(recorte, obtenerColoresFondo(recorte), 24);
                frames[fila][columna] = sinFondo;
                contornos[fila][columna] =
                    generarContorno(sinFondo, GROSOR_CONTORNO, COLOR_CONTORNO);
            }
        }
    }

    private static BufferedImage cargarHoja(String path) {
        try {
            String rutaRecurso = path.startsWith("/") ? path.substring(1) : path;
            return CargadorRecursos.cargarImagen(rutaRecurso);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("No se pudo cargar la hoja de sprites: " + path, e);
        }
    }

    private static void validarConfiguracion(BufferedImage hoja, int[] limites,
                                             int[] cantidadesPorFila, int[] columnasPorFila,
                                             int[] inicioColumna) {
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

    private static int[] obtenerColoresFondo(BufferedImage imagen) {
        int ancho = imagen.getWidth();
        int alto = imagen.getHeight();
        int[] esquinas = {
            imagen.getRGB(0, 0),
            imagen.getRGB(ancho - 1, 0),
            imagen.getRGB(0, alto - 1),
            imagen.getRGB(ancho - 1, alto - 1)
        };
        int[] candidatos = new int[esquinas.length];
        int cantidadCandidatos = 0;

        for (int esquina : esquinas) {
            int coincidencias = 0;
            for (int otraEsquina : esquinas) {
                if (esColorDeFondo(otraEsquina, new int[] {esquina}, 24)) {
                    coincidencias++;
                }
            }
            if (coincidencias < 2) {
                continue;
            }

            boolean yaIncluido = false;
            for (int indice = 0; indice < cantidadCandidatos; indice++) {
                if (esColorDeFondo(esquina, new int[] {candidatos[indice]}, 24)) {
                    yaIncluido = true;
                    break;
                }
            }
            if (!yaIncluido) {
                candidatos[cantidadCandidatos++] = esquina;
            }
        }

        if (cantidadCandidatos == 0) {
            return esquinas;
        }
        return java.util.Arrays.copyOf(candidatos, cantidadCandidatos);
    }

    public BufferedImage getFrame(int indice) {
        return getFrame(0, Math.max(0, Math.min(indice, frames[0].length - 1)));
    }

    
    public BufferedImage getFrame(int fila, int indice) {
        if (fila < 0 || fila >= frames.length || indice < 0 || indice >= frames[fila].length) {
            throw new IndexOutOfBoundsException("El frame solicitado no existe en esa fila");
        }
        return frames[fila][indice];
    }

    public BufferedImage getContorno(int indice) {
        return contornos[0][Math.max(0, Math.min(indice, contornos[0].length - 1))];
    }

    public int getCantidadFrames() {
        return frames[0].length;
    }

    public int getCantidadFrames(int fila) {
        validarFila(fila);
        return frames[fila].length;
    }

    private void validarFila(int fila) {
        if (fila < 0 || fila >= frames.length) {
            throw new IndexOutOfBoundsException("La fila solicitada no existe en la hoja");
        }
    }
 
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
       
        int color = origen.getRGB(x, y);
        if (esColorDeFondo(color, coloresFondo, tolerancia)) {
            esFondo[indice] = true;
            pendientes.add(indice);
        }
    }

    private static boolean esColorDeFondo(int color, int[] coloresFondo, int tolerancia) {
        if ((color >>> 24) == 0) {
            return true;
        }
        // Las esquinas pueden tener tonos distintos; aceptamos una pequeña variación por canal.
        for (int fondo : coloresFondo) {
            if ((fondo >>> 24) == 0) {
                continue;
            }
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
