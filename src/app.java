import javax.swing.*;
import modelos.Personaje;
import vista.PanelJuego;

public class app {
    //falta determinar el controlador principal para que sea minimo 
    //la app 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // crea un personaje con el nombre de la rata gris 
            Personaje jugador = new Personaje("Rata Gris");
            //nombre de la ventana del juego 
            JFrame ventana = new JFrame("Ghosts and Rats");
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            //mostramos el panel de juego con la barra de vida y estamina del personaje
            // creamos el panel de juego y lo agregamos a la ventana
            PanelJuego panel = new PanelJuego(jugador);
            ventana.add(panel);
            ventana.pack();
            ventana.setLocationRelativeTo(null);
            ventana.setResizable(false);
            ventana.setVisible(true);
            
            panel.requestFocusInWindow(); // para que reciba el teclado desde el arranque
        });
    }
}