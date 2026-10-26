package controlador;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.Timer;
import modelos.enemigos.enemigo;
import modelos.enemigos.fantasma;
import modelos.items.EfectoInvulnerabilidad;
import modelos.items.EfectoLento;
import modelos.items.EfectoVelocidad;
import modelos.items.PowerUp;
import modelos.personajes.Personaje;
import vista.PanelJuego;

// Coordina el ciclo temporal de la partida y solicita el repintado de la vista.
public class ControladorPartida implements SeccionListener {
    private static final int INTERVALO_ACTUALIZACION_MS = 16;
    private static final long INTERVALO_RECARGA_ESTAMINA_NANOS = 250_000_000L;
    private static final double POSICION_Y_FANTASMA = 390.0;
    private static final int PUNTOS_VELOCIDAD = 475;
    private static final int PUNTOS_INVULNERABILIDAD = 1_000;
    private static final int PENALIZACION_LENTO = 300;
    private static final long INTERVALO_PENALIZACION_TIEMPO_SEGUNDOS = 30;
    private static final int PENALIZACION_TIEMPO_PUNTOS = 10;

    private final ControladorRata controladorRata;
    private final GestorControlesRatas gestorControles;
    private final GestorEfectosTemporales gestorEfectos;
    private final ControladorPuntaje controladorPuntaje;
    private final PanelJuego panelJuego;
    private final Personaje jugador;
    private final fantasma enemigo;
    private final Map<Integer, List<PowerUp>> powerUpsPorSeccion;
    private final Timer temporizador;
    private final Runnable alCompletarNivel;
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
            panelJuego, Map.of(), null, null, () -> { });
    }

    public ControladorPartida(ControladorRata controladorRata,
                              GestorControlesRatas gestorControles,
                              GestorEfectosTemporales gestorEfectos,
                              ControladorPuntaje controladorPuntaje,
                              PanelJuego panelJuego,
                              Map<Integer, List<PowerUp>> powerUpsPorSeccion) {
        this(controladorRata, gestorControles, gestorEfectos, controladorPuntaje,
            panelJuego, powerUpsPorSeccion, null, null, () -> { });
    }

    public ControladorPartida(ControladorRata controladorRata,
                              GestorControlesRatas gestorControles,
                              GestorEfectosTemporales gestorEfectos,
                              ControladorPuntaje controladorPuntaje,
                              PanelJuego panelJuego,
                              Map<Integer, List<PowerUp>> powerUpsPorSeccion,
                              Personaje jugador,
                              fantasma enemigo,
                              Runnable alCompletarNivel) {
        if (controladorRata == null || gestorControles == null
                || gestorEfectos == null || controladorPuntaje == null || panelJuego == null
                || powerUpsPorSeccion == null || (jugador == null) != (enemigo == null)
                || alCompletarNivel == null) {
            throw new IllegalArgumentException(
                "Se requieren controladores, puntaje, vista e inventario válidos"
            );
        }
        this.controladorRata = controladorRata;
        this.gestorControles = gestorControles;
        this.gestorEfectos = gestorEfectos;
        this.controladorPuntaje = controladorPuntaje;
        this.panelJuego = panelJuego;
        this.jugador = jugador;
        this.enemigo = enemigo;
        this.powerUpsPorSeccion = copiarPowerUpsPorSeccion(powerUpsPorSeccion);
        this.alCompletarNivel = alCompletarNivel;
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
                || !recogerPowerUpSiColisiona(powerUp)) {
            return false;
        }
        disponibles.remove(powerUp);
        panelJuego.actualizarPowerUps(disponibles);
        actualizarPuntajePresentado();
        return true;
    }

    private Map<Integer, List<PowerUp>> copiarPowerUpsPorSeccion( Map<Integer, List<PowerUp>> powerUpsPorSeccion) {
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
            if (recogerPowerUpSiColisiona(iterador.next())) {
                iterador.remove();
                cambioInventario = true;
            }
        }
        if (cambioInventario) {
            panelJuego.actualizarPowerUps(powerUps);
        }
    }

    private boolean recogerPowerUpSiColisiona(PowerUp powerUp) {
        int puntos;
        boolean penalizacion;
        if (powerUp.getEfecto() instanceof EfectoVelocidad) {
            puntos = PUNTOS_VELOCIDAD;
            penalizacion = false;
        } else if (powerUp.getEfecto() instanceof EfectoInvulnerabilidad) {
            puntos = PUNTOS_INVULNERABILIDAD;
            penalizacion = false;
        } else if (powerUp.getEfecto() instanceof EfectoLento) {
            puntos = PENALIZACION_LENTO;
            penalizacion = true;
        } else {
            throw new IllegalArgumentException("No hay una regla de puntaje para este power-up");
        }

        if (!gestorEfectos.recogerSiColisiona(powerUp)) {
            return false;
        }
        if (penalizacion) {
            controladorPuntaje.penalizar(puntos);
        } else {
            controladorPuntaje.agregarPuntos(puntos);
        }
        return true;
    }

    public boolean usarHabilidad(enemigo objetivo, double xDestino, double yDestino) {
        return gestorControles.usarHabilidad(objetivo, xDestino, yDestino);
    }

    public void agregarPuntos(int puntos) {
        controladorPuntaje.agregarPuntos(puntos);
        actualizarPuntajePresentado();
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

    public long obtenerTiempoPartidaSegundos() {
        return obtenerTiempoPartidaNanos() / 1_000_000_000L;
    }

    private long obtenerTiempoPartidaNanos() {
        long tiempoNanos = tiempoAcumuladoNanos;
        if (partidaEnCurso) {
            tiempoNanos += System.nanoTime() - inicioPartidaNanos;
        }
        return tiempoNanos;
    }

    public int obtenerPuntaje() {
        long intervalosCompletos = obtenerTiempoPartidaSegundos()
            / INTERVALO_PENALIZACION_TIEMPO_SEGUNDOS;
        long penalizacion = intervalosCompletos * PENALIZACION_TIEMPO_PUNTOS;
        return (int) Math.max(0, controladorPuntaje.obtenerPuntaje() - penalizacion);
    }

    private void actualizarPuntajePresentado() {
        panelJuego.actualizarPuntajePresentado(obtenerPuntaje());
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
    if (enemigo != null) {
        enemigo.actualizarPatrulla(POSICION_Y_FANTASMA);
        enemigo.atacar(jugador, panelJuego.getAnchoHitbox(), panelJuego.getAltoHitbox());
    }
    actualizarPowerUpsVisibles();
    actualizarTiempoPartida();
    panelJuego.actualizarPresentacion();
    
}

private void actualizarTiempoPartida() {
    long tiempoNanos = obtenerTiempoPartidaNanos();
    panelJuego.actualizarTiempoPartida(tiempoNanos / 1_000_000);
    actualizarPuntajePresentado();
}

@Override
public void alCambiarSeccion(int seccion) { }

@Override
public void alTerminarNivel() {
    detener();
    panelJuego.mostrarNivelCompletado();
    alCompletarNivel.run();
}
}