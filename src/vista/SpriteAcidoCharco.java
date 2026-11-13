package vista;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

import controlador.GestorAnimacionSprite;
import modelos.personajes.EfectoHabilidad;
import recursos.CargadorRecursos;

public final class SpriteAcidoCharco implements SpriteEfecto {
    private static final String CLAVE_SPRITE = "charco_acido";
    private static final String RUTA_HOJA = "assets/rata_verde/acido_5frames_transparente.png";
    private static final int CANTIDAD_FRAMES = 5;
    private static final int ANCHO_DIBUJO = 72;
    private static final int ALTO_DIBUJO = 30;
    private static final long DURACION_FRAME_NANOS = 140_000_000L;

    private final BufferedImage[] frames;
    private GestorAnimacionSprite animacion;
    private EfectoHabilidad efectoAnimado;

    static List<String> rutasImagenes() {
        return List.of(RUTA_HOJA);
    }

    public SpriteAcidoCharco() {
        frames = cargarFrames();
    }

    @Override
    public String getClaveSprite() {
        return CLAVE_SPRITE;
    }

    @Override
    public void actualizarAnimacion() {
    }

    @Override
    public void actualizar(EfectoHabilidad efecto) {
        if (efecto == null) {
            throw new IllegalArgumentException("Se requiere el efecto que se va a animar");
        }
        if (efecto != efectoAnimado) {
            efectoAnimado = efecto;
            animacion = new GestorAnimacionSprite(frames, DURACION_FRAME_NANOS);
        } else {
            animacion.actualizar();
        }
    }

    @Override
    public void dibujar(Graphics2D graficos, EfectoHabilidad efecto) {
        if (graficos == null || efecto == null) {
            throw new IllegalArgumentException("Se requieren los gráficos y el efecto de ácido");
        }
        if (efecto != efectoAnimado) {
            actualizar(efecto);
        }
        double escala = Math.min(
            (double) ANCHO_DIBUJO / animacion.getAncho(),
            (double) ALTO_DIBUJO / animacion.getAlto()
        );
        int ancho = (int) Math.round(animacion.getAncho() * escala);
        int alto = (int) Math.round(animacion.getAlto() * escala);
        int x = (int) Math.round(efecto.getX() + efecto.getAncho() / 2.0 - ancho / 2.0);
        int y = (int) Math.round(efecto.getY() + efecto.getAlto() - alto);
        graficos.drawImage(animacion.getFrameActual(), x, y, ancho, alto, null);
    }

    private BufferedImage[] cargarFrames() {
        BufferedImage hoja = CargadorRecursos.cargarImagen(RUTA_HOJA);
        if (hoja.getWidth() % CANTIDAD_FRAMES != 0) {
            throw new IllegalArgumentException("La hoja del charco debe tener cinco frames iguales");
        }
        int anchoFrame = hoja.getWidth() / CANTIDAD_FRAMES;
        BufferedImage[] recortados = new BufferedImage[CANTIDAD_FRAMES];
        int anchoLienzo = 0;
        int altoLienzo = 0;
        for (int indice = 0; indice < CANTIDAD_FRAMES; indice++) {
            recortados[indice] = recortarTransparencia(
                hoja.getSubimage(indice * anchoFrame, 0, anchoFrame, hoja.getHeight())
            );
            anchoLienzo = Math.max(anchoLienzo, recortados[indice].getWidth());
            altoLienzo = Math.max(altoLienzo, recortados[indice].getHeight());
        }

        BufferedImage[] alineados = new BufferedImage[CANTIDAD_FRAMES];
        for (int indice = 0; indice < CANTIDAD_FRAMES; indice++) {
            BufferedImage frame = recortados[indice];
            BufferedImage alineado = new BufferedImage(
                anchoLienzo, altoLienzo, BufferedImage.TYPE_INT_ARGB
            );
            Graphics2D graficos = alineado.createGraphics();
            graficos.drawImage(
                frame,
                (anchoLienzo - frame.getWidth()) / 2,
                altoLienzo - frame.getHeight(),
                null
            );
            graficos.dispose();
            alineados[indice] = alineado;
        }
        return alineados;
    }

    private BufferedImage recortarTransparencia(BufferedImage imagen) {
        int izquierda = imagen.getWidth();
        int superior = imagen.getHeight();
        int derecha = -1;
        int inferior = -1;
        for (int y = 0; y < imagen.getHeight(); y++) {
            for (int x = 0; x < imagen.getWidth(); x++) {
                if ((imagen.getRGB(x, y) >>> 24) != 0) {
                    izquierda = Math.min(izquierda, x);
                    superior = Math.min(superior, y);
                    derecha = Math.max(derecha, x);
                    inferior = Math.max(inferior, y);
                }
            }
        }
        if (derecha < izquierda || inferior < superior) {
            throw new IllegalArgumentException("La hoja del charco contiene un frame vacío");
        }
        return imagen.getSubimage(
            izquierda, superior, derecha - izquierda + 1, inferior - superior + 1
        );
    }
}
