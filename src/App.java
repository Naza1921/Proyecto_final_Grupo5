import java.util.function.Consumer;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import controlador.CreadorPartida;
import controlador.PrincipalControlador;
import persistencia.ConexionSQLite;
import persistencia.PreferenciasAjustesSistema;
import persistencia.SQLitePuntajeDAO;
import servicios.ServicioPuntajes;
import servicios.audio.ReproductorEfectosSonidoJavaFx;
import servicios.audio.ReproductorMusicaJavaFx;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Consumer<String> notificadorErrorAudio = mensaje ->
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                    null,
                    mensaje,
                    "Error de audio",
                    JOptionPane.ERROR_MESSAGE
                ));
            var reproductorMusica = new ReproductorMusicaJavaFx(notificadorErrorAudio);
            var reproductorEfectosSonido =
                new ReproductorEfectosSonidoJavaFx(notificadorErrorAudio);
            new PrincipalControlador(
                new ServicioPuntajes(
                    new SQLitePuntajeDAO(ConexionSQLite.obtenerInstancia())
                ),
                reproductorMusica,
                reproductorEfectosSonido,
                new CreadorPartida(reproductorEfectosSonido),
                new PreferenciasAjustesSistema()
            );
        });
    }
}