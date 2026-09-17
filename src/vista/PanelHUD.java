package vista;

import java.awt.*;
import java.awt.image.BufferedImage;
import modelos.Personaje;

public class PanelHUD {
    private final SpriteSheet barraVidaSprite;
    private static final double ESCALA_BARRA_VIDA = 0.5;
    // aqui se define la direccion donde se encuentra el 
    // la barra de vida y estamina del personaje, en este caso en la carpeta assets
    //se definio que la cantidad que tiene que leer los frames10
    public PanelHUD() {
        barraVidaSprite = new SpriteSheet("/assets/sprite_barra_vida.jpeg", 10);
    }
    //la estamina puede ser cuadrado donde tendriamos 3 frames 
    // pero apartir de 100(esta bien la rata) , 50(medio cansada) , 0 (esta cansada la rata)
    public void dibujar(Graphics2D g, Personaje jugador) {
       //posicion donde se dibuja el hud, en este caso en la esquina superior izquierda
        int x = 20, y = 20;
        //determinar el color y la fuente del texto del nombre del personaje
        // dibujar el nombre del personaje
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString(jugador.getNombre(), x, y);
        // dibujar la barra de vida y estamina del personaje
        double porcentajeVida = (double) jugador.getVida() / jugador.getVidaMaxima();
        int frameVida = (int) Math.ceil(porcentajeVida * barraVidaSprite.getCantidadFrames()) - 1;
        // Asegurarse de que frameVida esté dentro del rango válido
        BufferedImage frame = barraVidaSprite.getFrame(frameVida);
        BufferedImage contorno = barraVidaSprite.getContorno(frameVida);

        int anchoDeseado = (int) (frame.getWidth() * ESCALA_BARRA_VIDA);
        int altoDeseado = (int) (frame.getHeight() * ESCALA_BARRA_VIDA);

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

            // Halo amarillo = estamina. Su opacidad baja a medida que se gasta.
                double porcentajeEstamina = Math.max(0, Math.min(1, jugador.getEstamina() / 100.0));
                Composite compositeOriginal = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) porcentajeEstamina));
        g.drawImage(contorno, x, y + 10, anchoDeseado, altoDeseado, null);
        g.setComposite(compositeOriginal);

        // Cola (vida), dibujada encima del halo
        g.drawImage(frame, x, y + 10, anchoDeseado, altoDeseado, null);
    }
}