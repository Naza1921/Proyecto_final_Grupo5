package controlador;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.Timer;
import aplicacion.VistaPartida;
import modelos.enemigos.enemigo;
import modelos.enemigos.fantasma;
import modelos.enemigos.ratainfestada;
import modelos.items.Moneda;
import modelos.items.PowerUp;
import modelos.partida.ConfiguracionPartida;
import modelos.escenario.levels.level;
import modelos.personajes.Personaje;

// Coordina el ciclo temporal de la partida y solicita el repintado de la vista.
public final class ControladorPartida implements SeccionListener {
    private static final int INTERVALO_ACTUALIZACION_MS = 16;
    private static final long INTERVALO_RECARGA_ESTAMINA_NANOS = 250_000_000L;
    private static final int PENALIZACION_GOLPE_FANTASMA = 125;

    private final ControladorRata controladorRata;
    private final GestorControlesRatas gestorControles;
    private final GestorEfectosTemporales gestorEfectos;
    private final GestorEfectosSonido efectosSonido;
    private final ControladorPuntaje controladorPuntaje;
    private final VistaPartida panelJuego;
    private final levelmanager niveles;
    private final ColisionNivel colisionNivel;
    private final GestorSecciones gestorSecciones;
    private final GestorRecogidaPowerUps gestorRecogidaPowerUps;
    private final GestorEnemigosPartida gestorEnemigos;
    private final GestorMonedasPartida gestorMonedas;
    private final GestorTiempoPartida gestorTiempo;
    private final Personaje jugador;
    private final Timer temporizador;
    private boolean listenerTemporizadorConfigurado;
    private final Runnable alCompletarNivel;
    private long ultimaRecargaEstaminaNanos = System.nanoTime();
    private boolean partidaEnCurso;
    private boolean pausada;
    private boolean sonidoMuerteReproducido;

    public ControladorPartida(ControladorRata controladorRata,
                              GestorControlesRatas gestorControles,
                              GestorEfectosTemporales gestorEfectos,
                              GestorEfectosSonido efectosSonido,
                              ControladorPuntaje controladorPuntaje,
                              VistaPartida panelJuego,
                              levelmanager niveles,
                              Map<Integer, List<PowerUp>> powerUpsPorNivel,
                              Map<Integer, List<Moneda>> monedasPorSeccion,
                              Personaje jugador,
                              List<fantasma> fantasmas,
                              List<ratainfestada> ratasInfestadas,
                              Runnable alCompletarNivel) {
        if (controladorRata == null || gestorControles == null
                || gestorEfectos == null || efectosSonido == null
                || controladorPuntaje == null || panelJuego == null || niveles == null
                || powerUpsPorNivel == null || monedasPorSeccion == null
                || jugador == null || fantasmas == null || ratasInfestadas == null
                || fantasmas.size() > level.MAX_ENEMIGOS_POR_TIPO
                || ratasInfestadas.size() > level.MAX_ENEMIGOS_POR_TIPO
                || fantasmas.stream().anyMatch(enemigo -> enemigo == null)
                || ratasInfestadas.stream().anyMatch(rata -> rata == null)
                || alCompletarNivel == null) {
            throw new IllegalArgumentException(
                "Se requieren controladores, puntaje, vista e inventario válidos"
            );
        }
        this.controladorRata = controladorRata;
        this.gestorControles = gestorControles;
        this.gestorEfectos = gestorEfectos;
        this.efectosSonido = efectosSonido;
        this.controladorPuntaje = controladorPuntaje;
        this.panelJuego = panelJuego;
        this.niveles = niveles;
        this.jugador = jugador;
        ConfiguradorEnemigosSeccion configuradorEnemigos =
            new ConfiguradorEnemigosSeccion(fantasmas, ratasInfestadas);
        this.gestorEnemigos = new GestorEnemigosPartida(
            configuradorEnemigos,
            this.panelJuego::actualizarEnemigosVisibles,
            this.jugador,
            this.niveles
        );
        this.gestorTiempo = new GestorTiempoPartida(this.controladorPuntaje);
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
        this.temporizador = new Timer(INTERVALO_ACTUALIZACION_MS, null);
        this.gestorRecogidaPowerUps = new GestorRecogidaPowerUps(
            powerUpsPorNivel,
            gestorEfectos,
            controladorPuntaje::agregarPuntos,
            controladorPuntaje::penalizar,
            efectosSonido::reproducir,
            panelJuego::actualizarPowerUps,
            this::actualizarPuntajePresentado
        );
        this.gestorMonedas = new GestorMonedasPartida(
            monedasPorSeccion,
            jugador,
            controladorPuntaje,
            efectosSonido,
            panelJuego,
            this::actualizarPuntajePresentado
        );
        configurarEnemigosVisibles(niveles.getSeccion());
        actualizarPowerUpsVisibles();
        actualizarMonedasVisibles();
    }

    private void actualizarMonedasVisibles() {
        gestorMonedas.actualizar(gestorSecciones.getSeccionActual());
    }

    private void actualizarPowerUpsVisibles() {
        gestorRecogidaPowerUps.actualizar(niveles.getIndiceNivelActual());
    }

    public boolean usarHabilidad(enemigo objetivo, double xDestino, double yDestino) {
        return gestorControles.usarHabilidad(objetivo, xDestino, yDestino);
    }

    public boolean activarHabilidad() {
        if (!partidaEnCurso || pausada || !jugador.estaViva()) {
            return false;
        }
        boolean activada = usarHabilidad(null, jugador.getX(), jugador.getY());
        if (activada) {
            panelJuego.mostrarAnimacionHabilidad();
            efectosSonido.reproducir(GestorEfectosSonido.Efecto.HABILIDAD);
        }
        return activada;
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
            reproducirSonidoMuerte();
            return;
        }
        if (!listenerTemporizadorConfigurado) {
            temporizador.addActionListener(evento -> actualizar());
            listenerTemporizadorConfigurado = true;
        }
        gestorSecciones.setListener(this);
        gestorTiempo.iniciar();
        partidaEnCurso = true;
        pausada = false;
        temporizador.start();
    }

    public boolean pausar() {
        if (!partidaEnCurso) {
            return false;
        }
        temporizador.stop();
        gestorTiempo.pausar();
        partidaEnCurso = false;
        pausada = true;
        actualizarTiempoPartida();
        return true;
    }

    public void reanudar() {
        if (!pausada) {
            return;
        }
        gestorTiempo.iniciar();
        partidaEnCurso = true;
        pausada = false;
        temporizador.start();
    }

    public void detener() {
        temporizador.stop();
        if (partidaEnCurso) {
            gestorTiempo.detener();
            partidaEnCurso = false;
            actualizarTiempoPartida();
        }
        pausada = false;
    }

    public long obtenerTiempoPartidaSegundos() {
        return gestorTiempo.obtenerTiempoSegundos();
    }

    public int obtenerPuntaje() {
        return gestorTiempo.obtenerPuntajeActual();
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
    List<enemigo> enemigosActivos = gestorEnemigos.actualizar(
        niveles.getNivel(),
        ConfiguracionPartida.TAM_TILE,
        ConfiguracionPartida.ANCHO_JUGADOR,
        ConfiguracionPartida.ALTO_JUGADOR,
        controladorPuntaje::penalizar,
        PENALIZACION_GOLPE_FANTASMA
    );
    jugador.actualizarHabilidad(enemigosActivos);
    registrarEnemigosDerrotados();
    if (!jugador.estaViva()) {
        panelJuego.mostrarAnimacionMuerte();
        reproducirSonidoMuerte();
        detener();
        return;
    }
    if (jugador.getVida() < vidaAntesDeActualizar) {
        panelJuego.mostrarAnimacionDanio();
    }
    actualizarMonedasVisibles();
    actualizarPowerUpsVisibles();
    actualizarTiempoPartida();
    panelJuego.actualizarPresentacion();
    
}

private void reproducirSonidoMuerte() {
    if (!sonidoMuerteReproducido) {
        sonidoMuerteReproducido = true;
        efectosSonido.reproducir(GestorEfectosSonido.Efecto.MUERTE);
    }
}

private void actualizarTiempoPartida() {
    panelJuego.actualizarTiempoPartida(gestorTiempo.obtenerTiempoMilisegundos());
    actualizarPuntajePresentado();
}

private void registrarEnemigosDerrotados() {
    gestorEnemigos.registrarEnemigosDerrotados(
        this::agregarPuntos,
        panelJuego::mostrarMuerteFantasma,
        panelJuego::mostrarMuerteRataInfestada
    );
}

@Override
public void alCambiarSeccion(int seccion) {
    configurarEnemigosVisibles(seccion);
}

private void configurarEnemigosVisibles(int seccion) {
    gestorEnemigos.configurarVisibles(seccion);
}

@Override
public void alTerminarNivel() {
    detener();
    efectosSonido.reproducir(GestorEfectosSonido.Efecto.NIVEL_COMPLETADO);
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
    gestorRecogidaPowerUps.reiniciarPresentacion();
    gestorMonedas.reiniciarPresentacion();
    iniciar();
}
}