package controlador;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.Timer;
import modelos.enemigos.enemigo;
import modelos.items.PowerUp;
import vista.PanelJuego;

// Coordina el ciclo temporal de la partida y solicita el repintado de la vista.
public class ControladorPartida implements SeccionListener {
    private static final int INTERVALO_ACTUALIZACION_MS = 16;
    private static final long INTERVALO_RECARGA_ESTAMINA_NANOS = 250_000_000L;

    private final ControladorRata controladorRata;
    private final GestorControlesRatas gestorControles;
    private final GestorEfectosTemporales gestorEfectos;
    private final ControladorPuntaje controladorPuntaje;
    private final PanelJuego panelJuego;
    private final Map<Integer, List<PowerUp>> powerUpsPorSeccion;
    private final Timer temporizador;
    private long ultimaRecargaEstaminaNanos = System.nanoTime();
    private long inicioPartidaNanos;
    private long tiempoAcumuladoNanos;
    private boolean partidaEnCurso;
    private boolean pausada;
    private int seccionPowerUpsPresentada = -1;

    public ControladorPartida(ControladorRata controladorRata,
                            GestorControlesRatas gestorControles,
                              GestorEfectosTemporales gestorEfectos,
                              ControladorPuntaje controladorPuntaje,
                              PanelJuego panelJuego) {
        this(controladorRata, gestorControles, gestorEfectos, controladorPuntaje,
            panelJuego, Map.of());
    }

    public ControladorPartida(ControladorRata controladorRata,
                              GestorControlesRatas gestorControles,
                              GestorEfectosTemporales gestorEfectos,
                              ControladorPuntaje controladorPuntaje,
                              PanelJuego panelJuego,
                              Map<Integer, List<PowerUp>> powerUpsPorSeccion) {
        if (controladorRata == null || gestorControles == null
                || gestorEfectos == null || controladorPuntaje == null || panelJuego == null
                || powerUpsPorSeccion == null) {
            throw new IllegalArgumentException(
                "Se requieren controladores, puntaje, vista e inventario válidos"
            );
        }
        this.controladorRata = controladorRata;
        this.gestorControles = gestorControles;
        this.gestorEfectos = gestorEfectos;
        this.controladorPuntaje = controladorPuntaje;
        this.panelJuego = panelJuego;
        this.powerUpsPorSeccion = copiarPowerUpsPorSeccion(powerUpsPorSeccion);
        this.temporizador = new Timer(INTERVALO_ACTUALIZACION_MS, evento -> actualizar());
        panelJuego.getGestorSecciones().setListener(this);
        actualizarPowerUpsVisibles();
    }

    public boolean recogerPowerUp(PowerUp powerUp) {
        if (powerUp == null) {
            throw new IllegalArgumentException("Se requiere un power-up");
        }
        List<PowerUp> disponibles = powerUpsPorSeccion.get(
            panelJuego.getGestorSecciones().getSeccionActual()
        );
        if (disponibles == null || !disponibles.contains(powerUp)
                || !gestorEfectos.recogerSiColisiona(powerUp)) {
            return false;
        }
        disponibles.remove(powerUp);
        panelJuego.actualizarPowerUps(disponibles);
        return true;
    }

    private Map<Integer, List<PowerUp>> copiarPowerUpsPorSeccion(
            Map<Integer, List<PowerUp>> powerUpsPorSeccion) {
        Map<Integer, List<PowerUp>> copia = new HashMap<>();
        for (Map.Entry<Integer, List<PowerUp>> entrada : powerUpsPorSeccion.entrySet()) {
            Integer seccion = entrada.getKey();
            List<PowerUp> powerUps = entrada.getValue();
            if (seccion == null || seccion < 0 || powerUps == null) {
                throw new IllegalArgumentException("Cada sección debe tener una lista válida de power-ups");
            }
            for (PowerUp powerUp : powerUps) {
                if (powerUp == null) {
                    throw new IllegalArgumentException(
                        "Cada sección debe tener una lista válida de power-ups"
                    );
                }
            }
            copia.put(seccion, new ArrayList<>(powerUps));
        }
        return copia;
    }

    private void actualizarPowerUpsVisibles() {
        int seccionActual = panelJuego.getGestorSecciones().getSeccionActual();
        if (seccionActual != seccionPowerUpsPresentada) {
            panelJuego.actualizarPowerUps(
                powerUpsPorSeccion.getOrDefault(seccionActual, List.of())
            );
            seccionPowerUpsPresentada = seccionActual;
        }

        List<PowerUp> powerUps = powerUpsPorSeccion.get(seccionActual);
        if (powerUps == null) {
            return;
        }

        boolean cambioInventario = false;
        Iterator<PowerUp> iterador = powerUps.iterator();
        while (iterador.hasNext()) {
            if (gestorEfectos.recogerSiColisiona(iterador.next())) {
                iterador.remove();
                cambioInventario = true;
            }
        }
        if (cambioInventario) {
            panelJuego.actualizarPowerUps(powerUps);
        }
    }

    public boolean usarHabilidad(enemigo objetivo, double xDestino, double yDestino) {
        return gestorControles.usarHabilidad(objetivo, xDestino, yDestino);
    }

    public void agregarPuntos(int puntos) {
        controladorPuntaje.agregarPuntos(puntos);
        panelJuego.actualizarPresentacion();
    }

    public void iniciar() {
        if (partidaEnCurso) {
            return;
        }
        inicioPartidaNanos = System.nanoTime();
        partidaEnCurso = true;
        pausada = false;
        temporizador.start();
    }

    public boolean pausar() {
        if (!partidaEnCurso) {
            return false;
        }
        temporizador.stop();
        tiempoAcumuladoNanos += System.nanoTime() - inicioPartidaNanos;
        partidaEnCurso = false;
        pausada = true;
        actualizarTiempoPartida();
        return true;
    }

    public void reanudar() {
        if (!pausada) {
            return;
        }
        inicioPartidaNanos = System.nanoTime();
        partidaEnCurso = true;
        pausada = false;
        temporizador.start();
    }

    public void detener() {
        temporizador.stop();
        if (partidaEnCurso) {
            tiempoAcumuladoNanos += System.nanoTime() - inicioPartidaNanos;
            partidaEnCurso = false;
            actualizarTiempoPartida();
        }
        pausada = false;
    }

private void actualizar() {
    long ahora = System.nanoTime();
    if (ahora - ultimaRecargaEstaminaNanos >= INTERVALO_RECARGA_ESTAMINA_NANOS) {
        controladorRata.recargarEstamina();
        ultimaRecargaEstaminaNanos = ahora;
    }

    double xMinimo = 0;
    double xMaximo = Math.max(0, panelJuego.getAnchoNivelPx() - panelJuego.getAnchoHitbox());
    gestorControles.actualizarMovimiento(xMinimo, xMaximo);

    gestorEfectos.actualizar();
    controladorRata.actualizar(Double.POSITIVE_INFINITY);
    panelJuego.resolverColisiones();
    actualizarPowerUpsVisibles();
    actualizarTiempoPartida();
    panelJuego.actualizarPresentacion();
    
}

private void actualizarTiempoPartida() {
    long tiempoNanos = tiempoAcumuladoNanos;
    if (partidaEnCurso) {
        tiempoNanos += System.nanoTime() - inicioPartidaNanos;
    }
    panelJuego.actualizarTiempoPartida(tiempoNanos / 1_000_000);
}

@Override
public void alCambiarSeccion(int seccion) { }

@Override
public void alTerminarNivel() {
    detener();
    panelJuego.mostrarNivelCompletado();
}
}