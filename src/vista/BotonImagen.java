package vista;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import controlador.utilz.loadsave;

public final class BotonImagen extends JButton {
    public BotonImagen(String nombreAccesible, String rutaImagen, int ancho, int alto) {
        if (nombreAccesible == null || nombreAccesible.isBlank()
                || rutaImagen == null || rutaImagen.isBlank() || ancho <= 0 || alto <= 0) {
            throw new IllegalArgumentException("Se requieren un nombre, una imagen y dimensiones válidas");
        }

        BufferedImage original = loadsave.GetSpriteatlas(rutaImagen);
        double escala = Math.min(
            (double) ancho / original.getWidth(),
            (double) alto / original.getHeight()
        );
        int anchoImagen = (int) Math.round(original.getWidth() * escala);
        int altoImagen = (int) Math.round(original.getHeight() * escala);
        Image imagen = original.getScaledInstance(anchoImagen, altoImagen, Image.SCALE_SMOOTH);
        setIcon(new ImageIcon(imagen));
        setPreferredSize(new Dimension(ancho, alto));
        setMinimumSize(new Dimension(ancho, alto));
        setMaximumSize(new Dimension(ancho, alto));
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setOpaque(false);
        setMargin(new Insets(0, 0, 0, 0));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setToolTipText(nombreAccesible);
        getAccessibleContext().setAccessibleName(nombreAccesible);
    }
}
