package controlador;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.Timer;
import modelos.enemigos.enemigo;
import modelos.enemigos.fantasma;
import modelos.enemigos.ratainfestada;
import modelos.items.EfectoInvulnerabilidad;
import modelos.items.EfectoLento;
import modelos.items.EfectoVelocidad;
import modelos.items.PowerUp;
import modelos.partida.ConfiguracionPartida;
import modelos.personajes.Personaje;
import vista.PanelJuego;

// Coordina el ciclo temporal de la partida y solicita el repintado de la vista.
public class ControladorPartida implements SeccionListener {
    private static final int INTERVALO_ACTUALIZACION_MS = 16;
    private static final long INTERVALO_RECARGA_ESTAMINA_NANOS = 250_000_000L;
    private static final int PUNTOS_VELOCIDAD = 475;
    private static final int PUNTOS_INVULNERABILIDAD = 1_000;
    private static final int PENALIZACION_LENTO = 300;
    private static final int PENALIZACION_GOLPE_FANTASMA = 125;
    private static final long INTERVALO_PENALIZACION_TIEMPO_SEGUNDOS = 30;
    private static final int PENALIZACION_TIEMPO_PUNTOS = 10;
    private static final List<ConfiguracionEnemigosSeccion> ENEMIGOS_POR_SECCION = List.of(
        new ConfiguracionEnemigosSeccion(
            new PosicionFantasma(190, 420, 390),
            new PosicionRataInfestada(600, 640)
        ),
        new ConfiguracionEnemigosSeccion(
            new PosicionFantasma(500, 760, 310),
            null
        ),
        new ConfiguracionEnemigosSeccion(
            null,
            new PosicionRataInfestada(900, 640)
        ),
        new ConfiguracionEnemigosSeccion(
            new PosicionFantasma(700, 1000, 290),
            new PosicionRataInfestada(300, 640)
        ),
        new ConfiguracionEnemigosSeccion(
            new PosicionFantasma(820, 1120, 350),
            null
        )
    );

    private record PosicionFantasma(double xMinimo, double xMaximo, double y) { }
    private record PosicionRataInfestada(double x, double y) { }
    private record ConfiguracionEnemigosSeccion(
        PosicionFantasma fantasma,
        PosicionRataInfestada rataInfestada
    ) { }

    private final ControladorRata controladorRata;
    private final GestorControlesRatas gestorControles;
    private final GestorEfectosTemporales gestorEfectos;
    private final ControladorPuntaje controladorPuntaje;
    private final PanelJuego panelJuego;
    private final levelmanager niveles;
    private final ColisionNivel colisionNivel;
    private final GestorSecciones gestorSecciones;
    private final Personaje jugador;
    private final fantasma enemigo;
    private final ratainfestada rataInfestada;
    private boolean fantasmaVisible;
    private boolean rataInfestadaVisible;
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
                              PanelJuego panelJuego,
                              levelmanager niveles,
                              Map<Integer, List<PowerUp>> powerUpsPorSeccion,
                              Personaje jugador,
                              fantasma enemigo,
                              ratainfestada rataInfestada,
                              Runnable alCompletarNivel) {
        if (controladorRata == null || gestorControles == null
                || gestorEfectos == null || controladorPuntaje == null || panelJuego == null
                || niveles == null
                || powerUpsPorSeccion == null || jugador == null || enemigo == null
                || rataInfestada == null
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
        this.niveles = niveles;
        this.jugador = jugador;
        this.enemigo = enemigo;
        this.rataInfestada = rataInfestada;
        this.powerUpsPorSeccion = copiarPowerUpsPorSeccion(powerUpsPorSeccion);
        this.alCompletarNivel = alCompletarNivel;
        this.colisionNivel = new ColisionNivel(
            niveles.getNivel(),
            ConfiguracionPartida.TAM_TILE,
            ConfiguracionPartida.ANCHO_JUGADOR,
            ConfiguracionPartida.ALTO_JUGADOR
        );
        this.gestorSecciones = new GestorSecciones(
            niveles,
            colisionNivel,
            ConfiguracionPartida.TAM_TILE,
            ConfiguracionPartida.ANCHO_JUGADOR,
            ConfiguracionPartida.ALTO_JUGADOR
        );
        this.temporizador = new Timer(INTERVALO_ACTUALIZACION_MS, evento -> actualizar());
        gestorSecciones.setListener(this);
        alCambiarSeccion(niveles.getSeccion());
        actualizarPowerUpsVisibles();
    }

    public boolean recogerPowerUp(PowerUp powerUp) {
        if (powerUp == null) {
            throw new IllegalArgumentException("Se requiere un power-up");
        }
        List<PowerUp> disponibles = powerUpsPorSeccion.get(
            gestorSecciones.getSeccionActual()
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
        int seccionActual = gestorSecciones.getSeccionActual();
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
        if (!jugador.estaViva()) {
            panelJuego.mostrarAnimacionMuerte();
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
    int vidaAntesDeActualizar = jugador.getVida();
    if (ahora - ultimaRecargaEstaminaNanos >= INTERVALO_RECARGA_ESTAMINA_NANOS) {
        controladorRata.recargarEstamina();
        ultimaRecargaEstaminaNanos = ahora;
    }

    double xMinimo = 0;
    double anchoNivel = niveles.getNivel().getAncho() * ConfiguracionPartida.TAM_TILE;
    double xMaximo = Math.max(0, anchoNivel - ConfiguracionPartida.ANCHO_JUGADOR);
    gestorControles.actualizarMovimiento(xMinimo, xMaximo);

    gestorEfectos.actualizar();
    controladorRata.actualizar(Double.POSITIVE_INFINITY);
    colisionNivel.resolver(jugador);
    gestorSecciones.actualizar(jugador);
    if (fantasmaVisible) {
        enemigo.actualizarPatrulla(enemigo.getY());
        int vidaAntesDelGolpe = jugador.getVida();
        enemigo.atacar(
            jugador,
            ConfiguracionPartida.ANCHO_JUGADOR,
            ConfiguracionPartida.ALTO_JUGADOR
        );
        if (jugador.getVida() < vidaAntesDelGolpe) {
            controladorPuntaje.penalizar(PENALIZACION_GOLPE_FANTASMA);
        }
    }
    if (rataInfestadaVisible) {
        rataInfestada.atacar(jugador);
        rataInfestada.actualizar(
            jugador, niveles.getNivel(), ConfiguracionPartida.TAM_TILE
        );
        rataInfestada.actualizarProyectiles(jugador);
    }
    if (!jugador.estaViva()) {
        panelJuego.mostrarAnimacionMuerte();
        detener();
        return;
    }
    if (jugador.getVida() < vidaAntesDeActualizar) {
        panelJuego.mostrarAnimacionDanio();
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
public void alCambiarSeccion(int seccion) {
    if (seccion < 0 || seccion >= ENEMIGOS_POR_SECCION.size()) {
        throw new IllegalArgumentException("La sección no tiene una configuración de enemigos");
    }

    ConfiguracionEnemigosSeccion configuracion = ENEMIGOS_POR_SECCION.get(seccion);
    PosicionFantasma posicionFantasma = configuracion.fantasma();
    fantasmaVisible = posicionFantasma != null;
    if (fantasmaVisible) {
        enemigo.reiniciarPatrulla(
            posicionFantasma.xMinimo(),
            posicionFantasma.xMaximo(),
            posicionFantasma.y()
        );
    }

    PosicionRataInfestada posicionRata = configuracion.rataInfestada();
    rataInfestadaVisible = posicionRata != null;
    if (rataInfestadaVisible) {
        rataInfestada.reiniciar(posicionRata.x(), posicionRata.y());
    }
    panelJuego.actualizarEnemigosVisibles(fantasmaVisible, rataInfestadaVisible);
}

@Override
public void alTerminarNivel() {
    detener();
    if (niveles.hayPisoSiguiente()) {
        panelJuego.mostrarNivelCompletado(this::continuarConSiguientePiso);
    } else {
        panelJuego.mostrarNivelCompletado();
        alCompletarNivel.run();
    }
}

private void continuarConSiguientePiso() {
    if (!niveles.avanzarPiso()) {
        throw new IllegalStateException("No hay un piso siguiente para continuar");
    }
    jugador.setX(ConfiguracionPartida.POSICION_INICIAL_X);
    jugador.setY(ConfiguracionPartida.POSICION_INICIAL_Y);
    gestorSecciones.reiniciar(jugador);
    seccionPowerUpsPresentada = -1;
    iniciar();
}
}