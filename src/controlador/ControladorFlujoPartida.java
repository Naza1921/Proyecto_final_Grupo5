package controlador;

import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import javax.swing.SwingWorker;
import aplicacion.NavegacionPartida;
import modelos.personajes.Personaje;
import persistencia.ResultadoPuntaje;
import servicios.audio.ReproductorMusica;
import vista.PanelJuego;

public final class ControladorFlujoPartida {
    private final FabricaPartida fabricaPartida;
    private final ReproductorMusica reproductorMusica;
    private final NavegacionPartida navegacion;
    private final Consumer<ResultadoPuntaje> solicitarGuardado;

    private Personaje jugador;
    private PanelJuego panelJuego;
    private ControladorPartida controladorPartida;
    private ControladorTecladoRata controladorTecladoRata;
    private GestorEfectosTemporales gestorEfectos;

    public ControladorFlujoPartida(
            FabricaPartida fabricaPartida,
            ReproductorMusica reproductorMusica,
            NavegacionPartida navegacion,
            Consumer<ResultadoPuntaje> solicitarGuardado) {
        if (fabricaPartida == null || reproductorMusica == null
                || navegacion == null || solicitarGuardado == null) {
            throw new IllegalArgumentException("Se requieren dependencias válidas para el flujo de partida");
        }
        this.fabricaPartida = fabricaPartida;
        this.reproductorMusica = reproductorMusica;
        this.navegacion = navegacion;
        this.solicitarGuardado = solicitarGuardado;
    }

    public void iniciar(Personaje personajeElegido) {
        if (personajeElegido == null) {
            throw new IllegalArgumentException("Se requiere un personaje para iniciar la partida");
        }
        detenerPartidaActual();
        limpiarReferenciasPartida();
        jugador = personajeElegido;
        navegacion.mostrarCargaPartida();

        new SwingWorker<PartidaCreada, Void>() {
            @Override
            protected PartidaCreada doInBackground() {
                return fabricaPartida.crear(
                    personajeElegido,
                    ControladorFlujoPartida.this::finalizarPartida,
                    ControladorFlujoPartida.this::pausar,
                    ControladorFlujoPartida.this::solicitarGuardadoResultado
                );
            }

            @Override
            protected void done() {
                try {
                    instalarPartida(get());
                } catch (InterruptedException error) {
                    Thread.currentThread().interrupt();
                    informarError(error);
                } catch (ExecutionException error) {
                    informarError(error.getCause());
                }
            }
        }.execute();
    }

    public void pausar() {
        if (controladorPartida == null || !controladorPartida.pausar()) {
            return;
        }
        controladorTecladoRata.limpiarEntradas();
        gestorEfectos.pausar();
        navegacion.mostrarPausa();
    }

    public void reanudar() {
        if (controladorPartida == null) {
            return;
        }
        gestorEfectos.reanudar();
        controladorPartida.reanudar();
        navegacion.instalarVistaPartida(panelJuego);
        panelJuego.requestFocusInWindow();
    }

    public void volverAlMenuDesdePausa() {
        detenerPartidaActual();
        limpiarReferenciasPartida();
        navegacion.mostrarMenu();
    }

    public void volverAlMenuDesdeGameOver() {
        detenerPartidaActual();
        limpiarReferenciasPartida();
        navegacion.mostrarMenu();
    }

    public void cerrar() {
        detenerPartidaActual();
        limpiarReferenciasPartida();
    }

    public ResultadoPuntaje obtenerResultadoActual() {
        if (jugador == null || controladorPartida == null) {
            throw new IllegalStateException("No hay una partida disponible para guardar");
        }
        return new ResultadoPuntaje(
            jugador.getNombre(),
            controladorPartida.obtenerPuntaje(),
            controladorPartida.obtenerTiempoPartidaSegundos()
        );
    }

    private void instalarPartida(PartidaCreada partida) {
        panelJuego = partida.panelJuego();
        gestorEfectos = partida.gestorEfectos();
        controladorPartida = partida.controladorPartida();
        controladorTecladoRata = partida.controladorTecladoRata();
        navegacion.instalarVistaPartida(panelJuego);
        controladorPartida.iniciar();
        reproductorMusica.habilitarReproduccion();
    }

    private void finalizarPartida() {
        if (controladorTecladoRata != null) {
            controladorTecladoRata.limpiarEntradas();
        }
        if (gestorEfectos != null) {
            gestorEfectos.limpiar();
        }
        navegacion.mostrarMenuGameOver();
    }

    private void solicitarGuardadoResultado() {
        solicitarGuardado.accept(obtenerResultadoActual());
    }

    private void detenerPartidaActual() {
        if (controladorPartida != null) {
            controladorPartida.detener();
        }
        if (gestorEfectos != null) {
            gestorEfectos.limpiar();
        }
        if (panelJuego != null) {
            navegacion.quitarVistaPartida(panelJuego);
        }
    }

    private void informarError(Throwable error) {
        Throwable causa = error == null || error.getCause() == null
            ? error : error.getCause();
        navegacion.mostrarErrorCreacionPartida(
            "No se pudo preparar la partida: "
                + (causa == null ? "error desconocido" : causa.getMessage())
        );
    }

    private void limpiarReferenciasPartida() {
        jugador = null;
        panelJuego = null;
        controladorPartida = null;
        controladorTecladoRata = null;
        gestorEfectos = null;
    }
}
