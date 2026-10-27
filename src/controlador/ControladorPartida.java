package controlador;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import modelos.personajes.rata_negra;
import modelos.personajes.rata_verde;

// Coordina el ciclo temporal de la partida y solicita el repintado de la vista.
public final class ControladorPartida implements SeccionListener {
    private static final int INTERVALO_ACTUALIZACION_MS = 16;
    private static final long INTERVALO_RECARGA_ESTAMINA_NANOS = 250_000_000L;
    private static final int PENALIZACION_GOLPE_FANTASMA = 125;
    private static final int PUNTOS_DERROTAR_FANTASMA = 300;
    private static final int PUNTOS_DERROTAR_RATA_INFESTADA = 500;
    private static final int PUNTOS_MONEDA = 100;
    private static final long INTERVALO_PENALIZACION_TIEMPO_SEGUNDOS = 30;
    private static final int PENALIZACION_TIEMPO_PUNTOS = 10;

    private final ControladorRata controladorRata;
    private final GestorControlesRatas gestorControles;
    private final GestorEfectosTemporales gestorEfectos;
    private final GestorEfectosSonido efectosSonido;
    private final ControladorPuntaje controladorPuntaje;
    private final VistaPartida panelJuego;
    private final levelmanager niveles;
    private final ColisionNivel colisionNivel;
    private final GestorSecciones gestorSecciones;
    private final ConfiguradorEnemigosSeccion configuradorEnemigos;
    private final GestorRecogidaPowerUps gestorRecogidaPowerUps;
    private final Personaje jugador;
    private final List<fantasma> fantasmas;
    private final List<ratainfestada> ratasInfestadas;
    private List<fantasma> fantasmasVisibles = List.of();
    private List<ratainfestada> ratasInfestadasVisibles = List.of();
    private final Set<fantasma> fantasmasPremiados =
        Collections.newSetFromMap(new IdentityHashMap<>());
    private final Set<ratainfestada> ratasInfestadasPremiadas =
        Collections.newSetFromMap(new IdentityHashMap<>());
    private final Map<Integer, List<Moneda>> monedasPorSeccion;
    private final Timer temporizador;
    private boolean listenerTemporizadorConfigurado;
    private final Runnable alCompletarNivel;
    private long ultimaRecargaEstaminaNanos = System.nanoTime();
    private long inicioPartidaNanos;
    private long tiempoAcumuladoNanos;
    private boolean partidaEnCurso;
    private boolean pausada;
    private int seccionMonedasPresentada = -1;
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
        this.fantasmas = List.copyOf(fantasmas);
        this.ratasInfestadas = List.copyOf(ratasInfestadas);
        this.configuradorEnemigos = new ConfiguradorEnemigosSeccion(
            this.fantasmas, this.ratasInfestadas
        );
        this.monedasPorSeccion = copiarMonedasPorSeccion(monedasPorSeccion);
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
        configurarEnemigosVisibles(niveles.getSeccion());
        actualizarPowerUpsVisibles();
        actualizarMonedasVisibles();
    }

    private Map<Integer, List<Moneda>> copiarMonedasPorSeccion(
            Map<Integer, List<Moneda>> monedasPorSeccion) {
        Map<Integer, List<Moneda>> copia = new HashMap<>();
        for (Map.Entry<Integer, List<Moneda>> entrada : monedasPorSeccion.entrySet()) {
            Integer seccion = entrada.getKey();
            List<Moneda> monedas = entrada.getValue();
            if (seccion == null || seccion < 0 || monedas == null
                    || monedas.stream().anyMatch(moneda -> moneda == null)) {
                throw new IllegalArgumentException(
                    "Cada sección debe tener una lista válida de monedas"
                );
            }
            copia.put(seccion, new ArrayList<>(monedas));
        }
        return copia;
    }

    private void actualizarMonedasVisibles() {
        int seccionActual = gestorSecciones.getSeccionActual();
        List<Moneda> monedas = monedasPorSeccion.get(seccionActual);
        if (seccionActual != seccionMonedasPresentada) {
            panelJuego.actualizarMonedas(monedas == null ? List.of() : monedas);
            seccionMonedasPresentada = seccionActual;
        }
        if (monedas == null) {
            return;
        }

        boolean cambioInventario = false;
        Iterator<Moneda> iterador = monedas.iterator();
        while (iterador.hasNext()) {
            Moneda moneda = iterador.next();
            if (moneda.colisionaCon(jugador)) {
                iterador.remove();
                controladorPuntaje.agregarPuntos(PUNTOS_MONEDA);
                efectosSonido.reproducir(GestorEfectosSonido.Efecto.MONEDA);
                cambioInventario = true;
            }
        }
        if (cambioInventario) {
            panelJuego.actualizarMonedas(monedas);
            actualizarPuntajePresentado();
        }
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
    List<enemigo> enemigosActivos = new ArrayList<>(
        fantasmasVisibles.size() + ratasInfestadasVisibles.size()
    );
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
    for (fantasma enemigo : fantasmasVisibles) {
        enemigosActivos.add(enemigo);
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
    for (ratainfestada rataInfestada : ratasInfestadasVisibles) {
        enemigosActivos.add(rataInfestada);
        rataInfestada.atacar(jugador);
        rataInfestada.actualizar(
            jugador, niveles.getNivel(), ConfiguracionPartida.TAM_TILE
        );
        rataInfestada.actualizarProyectiles(jugador);
    }
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
    long tiempoNanos = obtenerTiempoPartidaNanos();
    panelJuego.actualizarTiempoPartida(tiempoNanos / 1_000_000);
    actualizarPuntajePresentado();
}

private void registrarEnemigosDerrotados() {
    for (fantasma enemigo : fantasmasVisibles) {
        if (!enemigo.estavivo() && fantasmasPremiados.add(enemigo)) {
            agregarPuntos(PUNTOS_DERROTAR_FANTASMA);
            panelJuego.mostrarMuerteFantasma(enemigo);
        }
    }
    for (ratainfestada rataInfestada : ratasInfestadasVisibles) {
        if (!rataInfestada.estavivo() && ratasInfestadasPremiadas.add(rataInfestada)) {
            agregarPuntos(PUNTOS_DERROTAR_RATA_INFESTADA);
            panelJuego.mostrarMuerteRataInfestada(rataInfestada);
        }
    }
}

@Override
public void alCambiarSeccion(int seccion) {
    configurarEnemigosVisibles(seccion);
}

private void configurarEnemigosVisibles(int seccion) {
    if (seccion < 0 || seccion != niveles.getSeccion()) {
        throw new IllegalArgumentException("La sección de enemigos debe coincidir con el nivel actual");
    }
    if (jugador instanceof rata_verde rataVerde) {
        rataVerde.limpiarCharcoAcido();
    }
    if (jugador instanceof rata_negra rataNegra) {
        rataNegra.limpiarLamparaFuego();
    }

    ConfiguradorEnemigosSeccion.EnemigosVisibles enemigos =
        configuradorEnemigos.configurar(niveles.getNivel(), seccion);
    fantasmasVisibles = enemigos.fantasmas();
    ratasInfestadasVisibles = enemigos.ratasInfestadas();
    fantasmasPremiados.clear();
    ratasInfestadasPremiadas.clear();
    panelJuego.actualizarEnemigosVisibles(fantasmasVisibles, ratasInfestadasVisibles);
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
    seccionMonedasPresentada = -1;
    iniciar();
}
}