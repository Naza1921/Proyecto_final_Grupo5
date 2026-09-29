import javax.swing.SwingUtilities;
import controlador.PrincipalControlador;
// Clase principal que inicia la aplicación del juego Ghosts and Rats
public class app {
    public static void main(String[] args) throws Exception {
        // Iniciar la aplicación en el hilo de despacho de eventos de Swing 
        // para asegurar la seguridad de los hilos
        SwingUtilities.invokeLater(() -> {
            new PrincipalControlador();
        });
    }
}