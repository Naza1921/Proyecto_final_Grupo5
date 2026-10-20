package vista;

import java.awt.*;
import java.awt.image.BufferedImage;

import controlador.utilz.loadsave;
import modelos.personajes.Personaje;

public class PanelHUD {
    private final SpriteSheet barraVidaSprite;
    private final BufferedImage spritePuntaje;
    private static final double ESCALA_BARRA_VIDA = 0.5;
    private static final int ANCHO_PUNTAJE = 330;
    private static final int ALTO_PUNTAJE = 110;
    private static final int TAMANO_FUENTE_PUNTAJE = 25;

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
        barraVidaSprite = new SpriteSheet("/assets/Hud/sprite_barra_vida.jpeg", 10);
        spritePuntaje = loadsave.GetSpriteatlas("assets/Hud/puntaje_proyecto.png");
    }

    //la estamina puede ser cuadrado donde tendriamos 3 frames 
    // pero apartir de 100(esta bien la rata) , 50(medio cansada) , 0 (esta cansada la rata)
    public void dibujar(Graphics2D g, Personaje jugador, int puntaje, int anchoPanel) {
        // Si todavía no se eligió personaje no hay nada que dibujar
        if (jugador == null) return;
        if (puntaje < 0 || anchoPanel < 0) {
            throw new IllegalArgumentException("El puntaje y el ancho del panel deben ser válidos");
        }

        // Se trabaja sobre una copia del Graphics2D: los cambios de color, fuente,
        // transparencia e interpolación no se filtran al resto del juego
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            dibujarNombre(g2, jugador);
            dibujarBarraVida(g2, jugador);
            dibujarPuntaje(g2, puntaje, anchoPanel);
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
        int x = Math.max(0, anchoPanel - HUD_X - ANCHO_PUNTAJE);
        int y = HUD_Y;
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(spritePuntaje, x, y, ANCHO_PUNTAJE, ALTO_PUNTAJE, null);

        Font fuenteAnterior = g.getFont();
        Color colorAnterior = g.getColor();
        g.setFont(new Font("Arial", Font.BOLD, TAMANO_FUENTE_PUNTAJE));
        g.setColor(Color.WHITE);
        FontMetrics metricas = g.getFontMetrics();
        String texto = Integer.toString(puntaje);
        int textoX = x + (int) (ANCHO_PUNTAJE * 0.66)
                - metricas.stringWidth(texto) / 2;
        int textoY = y + (int) (ALTO_PUNTAJE * 0.62);
        g.drawString(texto, textoX, textoY);
        g.setFont(fuenteAnterior);
        g.setColor(colorAnterior);
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
