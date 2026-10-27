package vista;

import javax.swing.*;
import java.awt.*;
import controlador.utilz.loadsave;

public class MenuPrincipal extends JPanel {
    private static final int ANCHO_BOTON = 252;
    private static final int ALTO_BOTON = 104;
    private static final int ANCHO_TITULO = 640;
    private static final int ALTO_TITULO = 320;

    private final BotonImagen btnJugar;
    private final BotonImagen btnOpciones;
    private final BotonImagen btnRanking;
    private final BotonImagen btnSalir;
    private final Image fondo;
    private final Image titulo;

    public MenuPrincipal() {
        setPreferredSize(new Dimension(800, 600));
        setLayout(new GridBagLayout());

        fondo = loadsave.GetSpriteatlas("assets/Portada_rata.png");
        titulo = loadsave.GetSpriteatlas("assets/TITULO 2.png");

        btnJugar = crearBoton("Jugar", "boton_jugar.png");
        btnOpciones = crearBoton("Opciones", "boton_opciones.png");
        btnRanking = crearBoton("Ranking", "boton_ranking.png");
        btnSalir = crearBoton("Salir", "boton_salir.png");

        JPanel botonera = new JPanel(new GridLayout(2, 2, 10, 4));
        botonera.setOpaque(false);
        botonera.add(btnJugar);
        botonera.add(btnOpciones);
        botonera.add(btnRanking);
        botonera.add(btnSalir);

        GridBagConstraints restricciones = new GridBagConstraints();
        restricciones.gridx = 0;
        restricciones.gridy = 0;
        restricciones.weighty = 1;
        restricciones.anchor = GridBagConstraints.SOUTH;
        restricciones.insets = new Insets(0, 0, 16, 0);
        add(botonera, restricciones);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (fondo != null) {
            g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
        }
        if (titulo != null) {
            double escala = Math.min(
                (double) getWidth() / ANCHO_TITULO,
                (double) getHeight() / ALTO_TITULO
            );
            int ancho = (int) Math.round(ANCHO_TITULO * escala);
            int alto = (int) Math.round(ALTO_TITULO * escala);
            g.drawImage(titulo, (getWidth() - ancho) / 2, 12, ancho, alto, this);
        }
    }

    private static BotonImagen crearBoton(String nombre, String archivo) {
        return new BotonImagen(
            nombre, "assets/botones/" + archivo, ANCHO_BOTON, ALTO_BOTON
        );
    }

    public JButton getBtnJugar() {
        return btnJugar;
    }

    public JButton getBtnOpciones() {
        return btnOpciones;
    }

    public JButton getBtnRanking() {
        return btnRanking;
    }

    public JButton getBtnSalir() {
        return btnSalir;
    }
}