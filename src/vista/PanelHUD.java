package vista;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Locale;

import recursos.CargadorRecursos;
import modelos.personajes.Personaje;

public class PanelHUD {
    private final SpriteSheet barraVidaSprite;
    private final BufferedImage spritePuntaje;
    private final BufferedImage spriteTemporizador;
    private static final double ESCALA_BARRA_VIDA = 0.5;
    private static final int ANCHO_PUNTAJE = 330;
    private static final int ALTO_PUNTAJE = 110;
    private static final int TAMANO_FUENTE_PUNTAJE = 25;
    private static final int ANCHO_TEMPORIZADOR = 300;
    private static final int ALTO_TEMPORIZADOR = 150;
    private static final int TAMANO_FUENTE_TEMPORIZADOR = 23;
    private static final int SEPARACION_INDICADORES = 12;
    private static final int TEMPORIZADOR_Y = 0;
    private static final int PUNTAJE_Y = 12;

    //posicion donde se dibuja el hud, en este caso en la esquina superior izquierda
    private static final int HUD_X = 20;
    private static final int HUD_Y = 20;
    // separación vertical entre el nombre y la barra
    private static final int SEPARACION_BARRA = 10;
    //determinar el color y la fuente del texto del nombre del personaje
    // (se crean una sola vez, no en cada cuadro)
    private static final Color COLOR_NOMBRE = Color.WHITE;
    private static final Font FUENTE_NOMBRE = new Font("Arial", Font.BOLD, 14);

    // aqui se define la direccion donde se encuentra el 
    // la barra de vida y estamina del personaje, en este caso en la carpeta assets
    //se definio que la cantidad que tiene que leer los frames10
    public PanelHUD() {
        barraVidaSprite = new SpriteSheet("/assets/Hud/sprite_barra_vida_2.png", 10);
        spritePuntaje = CargadorRecursos.cargarImagen("assets/Hud/puntaje_proyecto.png");
        spriteTemporizador = CargadorRecursos.cargarImagen("assets/temporizador.png");
    }

    //la estamina puede ser cuadrado donde tendriamos 3 frames 
    // pero apartir de 100(esta bien la rata) , 50(medio cansada) , 0 (esta cansada la rata)
    public void dibujar(Graphics2D g, Personaje jugador, int puntaje,
                        long tiempoPartidaMillis, int anchoPanel) {
        // Si todavía no se eligió personaje no hay nada que dibujar
        if (jugador == null) return;
        if (puntaje < 0 || tiempoPartidaMillis < 0 || anchoPanel < 0) {
            throw new IllegalArgumentException("El puntaje, el tiempo y el ancho del panel deben ser válidos");
        }

        // Se trabaja sobre una copia del Graphics2D: los cambios de color, fuente,
        // transparencia e interpolación no se filtran al resto del juego
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            dibujarNombre(g2, jugador);
            dibujarBarraVida(g2, jugador);
            dibujarPuntaje(g2, puntaje, anchoPanel);
            dibujarTemporizador(g2, tiempoPartidaMillis, anchoPanel);
        } finally {
            g2.dispose();
        }
    }

    // dibujar el nombre del personaje
    private void dibujarNombre(Graphics2D g, Personaje jugador) {
        g.setColor(COLOR_NOMBRE);
        g.setFont(FUENTE_NOMBRE);
        g.drawString(jugador.getNombre(), HUD_X, HUD_Y);
    }

    // dibujar la barra de vida y estamina del personaje
    private void dibujarBarraVida(Graphics2D g, Personaje jugador) {
        int frameVida = calcularFrameVida(jugador);
        BufferedImage frame = barraVidaSprite.getFrame(frameVida);
        BufferedImage contorno = barraVidaSprite.getContorno(frameVida);

        int anchoDeseado = (int) (frame.getWidth() * ESCALA_BARRA_VIDA);
        int altoDeseado = (int) (frame.getHeight() * ESCALA_BARRA_VIDA);
        int yBarra = HUD_Y + SEPARACION_BARRA;

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        // Halo amarillo = estamina. Su opacidad baja a medida que se gasta.
        Composite compositeOriginal = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                calcularOpacidadEstamina(jugador)));
        g.drawImage(contorno, HUD_X, yBarra, anchoDeseado, altoDeseado, null);
        g.setComposite(compositeOriginal);

        // Cola (vida), dibujada encima del halo
        g.drawImage(frame, HUD_X, yBarra, anchoDeseado, altoDeseado, null);
    }

    private void dibujarPuntaje(Graphics2D g, int puntaje, int anchoPanel) {
        double escala = calcularEscalaIndicadores(anchoPanel);
        int ancho = (int) Math.round(ANCHO_PUNTAJE * escala);
        int alto = (int) Math.round(ALTO_PUNTAJE * escala);
        int x = Math.max(0, anchoPanel - HUD_X - ancho);
        int y = (int) Math.round(PUNTAJE_Y * escala);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(spritePuntaje, x, y, ancho, alto, null);

        Font fuenteAnterior = g.getFont();
        Color colorAnterior = g.getColor();
        g.setFont(new Font("Arial", Font.BOLD,
                Math.max(1, (int) Math.round(TAMANO_FUENTE_PUNTAJE * escala))));
        g.setColor(Color.WHITE);
        FontMetrics metricas = g.getFontMetrics();
        String texto = Integer.toString(puntaje);
        int textoX = x + (int) (ancho * 0.66)
                - metricas.stringWidth(texto) / 2;
        int textoY = y + (int) (alto * 0.62);
        g.drawString(texto, textoX, textoY);
        g.setFont(fuenteAnterior);
        g.setColor(colorAnterior);
    }

    private void dibujarTemporizador(Graphics2D g, long tiempoPartidaMillis, int anchoPanel) {
        double escala = calcularEscalaIndicadores(anchoPanel);
        int ancho = (int) Math.round(ANCHO_TEMPORIZADOR * escala);
        int alto = (int) Math.round(ALTO_TEMPORIZADOR * escala);
        int x = Math.max(0, (anchoPanel - ancho) / 2);
        int y = (int) Math.round(TEMPORIZADOR_Y * escala);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(spriteTemporizador, x, y, ancho, alto, null);

        long segundosTotales = tiempoPartidaMillis / 1_000;
        long minutos = segundosTotales / 60;
        long segundos = segundosTotales % 60;
        String texto = String.format(Locale.ROOT, "%02d:%02d", minutos, segundos);

        Font fuenteAnterior = g.getFont();
        Color colorAnterior = g.getColor();
        g.setFont(new Font("Arial", Font.BOLD,
                Math.max(1, (int) Math.round(TAMANO_FUENTE_TEMPORIZADOR * escala))));
        g.setColor(Color.WHITE);
        FontMetrics metricas = g.getFontMetrics();
        int textoX = x + (int) (ancho * 0.72)
                - metricas.stringWidth(texto) / 2;
        int textoY = y + (int) (alto * 0.60);
        g.drawString(texto, textoX, textoY);
        g.setFont(fuenteAnterior);
        g.setColor(colorAnterior);
    }

    private double calcularEscalaIndicadores(int anchoPanel) {
        double espacioDisponible = anchoPanel / 2.0 - HUD_X - SEPARACION_INDICADORES;
        double espacioRequerido = ANCHO_PUNTAJE + ANCHO_TEMPORIZADOR / 2.0;
        return Math.min(1.0, Math.max(0.0, espacioDisponible / espacioRequerido));
    }

    // Elige el frame de la barra según el porcentaje de vida
    private int calcularFrameVida(Personaje jugador) {
        double porcentajeVida = (double) jugador.getVida() / jugador.getVidaMaxima();
        int frameVida = (int) Math.ceil(porcentajeVida * barraVidaSprite.getCantidadFrames()) - 1;
        // Asegurarse de que frameVida esté dentro del rango válido
        return Math.max(0, Math.min(frameVida, barraVidaSprite.getCantidadFrames() - 1));
    }

    // Opacidad del halo (de 0 a 1) según la estamina actual respecto del máximo de cada rata
    private float calcularOpacidadEstamina(Personaje jugador) {
        double porcentajeEstamina = jugador.getEstamina() / jugador.getEstaminaMaxima();
        return (float) Math.max(0, Math.min(1, porcentajeEstamina));
    }
}
