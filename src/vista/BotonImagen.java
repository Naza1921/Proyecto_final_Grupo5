package vista;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import recursos.CargadorRecursos;

public final class BotonImagen extends JButton {
    public BotonImagen(String nombreAccesible, String rutaImagen, int ancho, int alto) {
        if (nombreAccesible == null || nombreAccesible.isBlank()
                || rutaImagen == null || rutaImagen.isBlank() || ancho <= 0 || alto <= 0) {
            throw new IllegalArgumentException("Se requieren un nombre, una imagen y dimensiones válidas");
        }

        BufferedImage original = quitarFondoClaro(
            CargadorRecursos.cargarImagen(rutaImagen)
        );
        double escala = Math.min(
            (double) ancho / original.getWidth(),
            (double) alto / original.getHeight()
        );
        int anchoImagen = Math.max(1, (int) Math.round(original.getWidth() * escala));
        int altoImagen = Math.max(1, (int) Math.round(original.getHeight() * escala));
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

    private static BufferedImage quitarFondoClaro(BufferedImage original) {
        int ancho = original.getWidth();
        int alto = original.getHeight();
        int[] pixeles = original.getRGB(0, 0, ancho, alto, null, 0, ancho);
        boolean[] visitados = new boolean[pixeles.length];
        ArrayDeque<Integer> pendientes = new ArrayDeque<>();

        for (int x = 0; x < ancho; x++) {
            agregarSiEsFondo(x, pendientes, visitados, pixeles);
            agregarSiEsFondo((alto - 1) * ancho + x, pendientes, visitados, pixeles);
        }
        for (int y = 0; y < alto; y++) {
            agregarSiEsFondo(y * ancho, pendientes, visitados, pixeles);
            agregarSiEsFondo(y * ancho + ancho - 1, pendientes, visitados, pixeles);
        }

        while (!pendientes.isEmpty()) {
            int indice = pendientes.removeFirst();
            int x = indice % ancho;
            int y = indice / ancho;
            if (x > 0) {
                agregarSiEsFondo(indice - 1, pendientes, visitados, pixeles);
            }
            if (x + 1 < ancho) {
                agregarSiEsFondo(indice + 1, pendientes, visitados, pixeles);
            }
            if (y > 0) {
                agregarSiEsFondo(indice - ancho, pendientes, visitados, pixeles);
            }
            if (y + 1 < alto) {
                agregarSiEsFondo(indice + ancho, pendientes, visitados, pixeles);
            }
        }

        BufferedImage resultado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        resultado.setRGB(0, 0, ancho, alto, pixeles, 0, ancho);
        return resultado;
    }

    private static void agregarSiEsFondo(int indice, ArrayDeque<Integer> pendientes,
                                         boolean[] visitados, int[] pixeles) {
        if (visitados[indice] || !esFondoClaro(pixeles[indice])) {
            return;
        }
        visitados[indice] = true;
        pixeles[indice] &= 0x00FFFFFF;
        pendientes.addLast(indice);
    }

    private static boolean esFondoClaro(int pixel) {
        int rojo = pixel >>> 16 & 0xFF;
        int verde = pixel >>> 8 & 0xFF;
        int azul = pixel & 0xFF;
        int minimo = Math.min(rojo, Math.min(verde, azul));
        int maximo = Math.max(rojo, Math.max(verde, azul));
        return minimo >= 185 && maximo - minimo <= 42
            && Math.max(Math.abs(rojo - 230),
                Math.max(Math.abs(verde - 230), Math.abs(azul - 230))) <= 48;
    }
}
