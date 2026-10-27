package vista;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.Timer;
import animacion.GestorAnimacionSprite;
import modelos.enemigos.ProyectilAcido;
import modelos.enemigos.fantasma;
import modelos.enemigos.ratainfestada;
import modelos.partida.ConfiguracionPartida;
import modelos.personajes.CharcoAcido;
import modelos.personajes.LamparaFuego;
import modelos.personajes.Personaje;
import modelos.personajes.ProyectilEspecial;
import modelos.personajes.rata_negra;
import modelos.personajes.rata_verde;

final class RenderizadorEntidades {
    private static final long DURACION_FRAME_NANOS = 160_000_000L;
    private static final long DURACION_ANIMACION_PREPARACION_NANOS = 1_500_000_000L;
    private static final long DURACION_FRAME_HABILIDAD_BLANCA_NANOS = 60_000_000L;
    private static final double AJUSTE_BASE_RATA_VERDE = 1.0;

    private final Personaje jugador;
    private final Runnable alTerminarAnimacionMuerte;
    private final Runnable solicitarRepintado;
    private final Map<fantasma, Long> iniciosMuerteFantasmaNanos = new IdentityHashMap<>();
    private final Map<ratainfestada, EstadoSpriteRata> estadosSpriteRata =
        new IdentityHashMap<>();
    private final Map<SpritesRatas.Accion, GestorAnimacionSprite> animacionesRata =
        new EnumMap<>(SpritesRatas.Accion.class);
    private final BufferedImage[] framesMuerte;
    private final BufferedImage[] framesDanio;
    private final Timer temporizadorAnimacionMuerte;
    private final Timer temporizadorAnimacionDanio;
    private final GestorAnimacionSprite animacionFantasmaDerecha;
    private final GestorAnimacionSprite animacionFantasmaIzquierda;
    private final BufferedImage[] framesMuerteFantasma;
    private final SpriteProyectilStun spriteProyectilStun = new SpriteProyectilStun();
    private final SpriteAcidoCharco spriteAcidoCharco = new SpriteAcidoCharco();
    private final SpriteLamparaFuego spriteLamparaFuego = new SpriteLamparaFuego();
    private final SpriteAcido spriteAcido = new SpriteAcido();
    private List<fantasma> fantasmas = List.of();
    private List<ratainfestada> ratasInfestadas = List.of();
    private SpritesRatas.Accion accionAnimacionRata = SpritesRatas.Accion.QUIETA;
    private double ultimaXPresentada;
    private boolean mirandoIzquierda;
    private BufferedImage frameBaseCalculada;
    private int ultimaFilaVisibleRata;
    private int indiceFrameMuerte;
    private int indiceFrameDanio;
    private boolean listenersAnimacionConfigurados;
    private long finAnimacionHabilidadNanos;

    RenderizadorEntidades(Personaje jugador, List<fantasma> fantasmas,
                          List<ratainfestada> ratasInfestadas,
                          Runnable alTerminarAnimacionMuerte,
                          Runnable solicitarRepintado) {
        if (jugador == null || fantasmas == null || ratasInfestadas == null
                || alTerminarAnimacionMuerte == null || solicitarRepintado == null) {
            throw new IllegalArgumentException("Se requieren modelos y acciones de renderizado");
        }
        this.jugador = jugador;
        this.alTerminarAnimacionMuerte = alTerminarAnimacionMuerte;
        this.solicitarRepintado = solicitarRepintado;
        actualizarEnemigosVisibles(fantasmas, ratasInfestadas);

        for (SpritesRatas.Accion accion : SpritesRatas.Accion.values()) {
            if (accion == SpritesRatas.Accion.MUERTE
                    || accion == SpritesRatas.Accion.RECIBIR_DANIO
                    || !CatalogoSpritesRatas.tiene(jugador.getTipoRata(), accion)) {
                continue;
            }
            BufferedImage[] frames = SpritesRatas.cargarFrames(jugador.getTipoRata(), accion);
            animacionesRata.put(
                accion,
                new GestorAnimacionSprite(frames, obtenerDuracionFrame(accion, frames.length))
            );
        }
        framesMuerte = SpritesRatas.cargarFrames(
            jugador.getTipoRata(), SpritesRatas.Accion.MUERTE
        );
        temporizadorAnimacionMuerte = new Timer(
            (int) (DURACION_FRAME_NANOS / 1_000_000L), null
        );
        temporizadorAnimacionMuerte.setCoalesce(true);
        framesDanio = SpritesRatas.cargarFrames(
            jugador.getTipoRata(), SpritesRatas.Accion.RECIBIR_DANIO
        );
        temporizadorAnimacionDanio = new Timer(
            (int) (DURACION_FRAME_NANOS / 1_000_000L), null
        );
        temporizadorAnimacionDanio.setCoalesce(true);
        ultimaXPresentada = jugador.getX();
        animacionFantasmaDerecha = new GestorAnimacionSprite(
            SpritesFantasma.cargarPatrulla(true), 180_000_000L
        );
        animacionFantasmaIzquierda = new GestorAnimacionSprite(
            SpritesFantasma.cargarPatrulla(false), 180_000_000L
        );
        framesMuerteFantasma = SpritesFantasma.cargarMuerte();
    }

    double getAnchoSprite() {
        return obtenerFrameRata().getWidth();
    }

    double getAltoSprite() {
        return obtenerFrameRata().getHeight();
    }

    void actualizar() {
        double xActual = jugador.getX();
        double desplazamientoX = xActual - ultimaXPresentada;
        if (Math.abs(desplazamientoX) > 0.01) {
            mirandoIzquierda = desplazamientoX < 0;
        }
        ultimaXPresentada = xActual;

        spriteAcido.actualizar();
        spriteProyectilStun.actualizar();
        spriteLamparaFuego.actualizar();
        if (jugador instanceof rata_verde rataVerde
                && rataVerde.getCharcoAcido() != null) {
            spriteAcidoCharco.actualizar(rataVerde.getCharcoAcido());
        }
        actualizarSpritesRatasInfestadas();

        if (accionAnimacionRata == SpritesRatas.Accion.MUERTE
                || accionAnimacionRata == SpritesRatas.Accion.RECIBIR_DANIO) {
            actualizarAnimacionesFantasma();
            solicitarRepintado.run();
            return;
        }

        if (accionAnimacionRata == SpritesRatas.Accion.HAB_ESPECIAL) {
            animacionesRata.get(SpritesRatas.Accion.HAB_ESPECIAL).actualizar();
            actualizarAnimacionesFantasma();
            if (System.nanoTime() >= finAnimacionHabilidadNanos) {
                accionAnimacionRata = obtenerAccionAnimacion(desplazamientoX);
                animacionesRata.get(accionAnimacionRata).reiniciar();
                frameBaseCalculada = null;
            }
            solicitarRepintado.run();
            return;
        }

        SpritesRatas.Accion accionActual = obtenerAccionAnimacion(desplazamientoX);
        if (accionActual != accionAnimacionRata) {
            accionAnimacionRata = accionActual;
            animacionesRata.get(accionAnimacionRata).reiniciar();
        }
        animacionesRata.get(accionAnimacionRata).actualizar();
        actualizarAnimacionesFantasma();
        solicitarRepintado.run();
    }

    void mostrarAnimacionHabilidad() {
        GestorAnimacionSprite animacion = animacionesRata.get(SpritesRatas.Accion.HAB_ESPECIAL);
        if (animacion == null) {
            return;
        }
        accionAnimacionRata = SpritesRatas.Accion.HAB_ESPECIAL;
        animacion.reiniciar();
        finAnimacionHabilidadNanos = System.nanoTime()
            + obtenerDuracionFrame(SpritesRatas.Accion.HAB_ESPECIAL, animacion.getCantidadFrames())
                * animacion.getCantidadFrames();
        frameBaseCalculada = null;
        solicitarRepintado.run();
    }

    void mostrarAnimacionMuerte() {
        if (accionAnimacionRata == SpritesRatas.Accion.MUERTE) {
            return;
        }
        configurarListenersAnimacion();
        temporizadorAnimacionDanio.stop();
        accionAnimacionRata = SpritesRatas.Accion.MUERTE;
        indiceFrameMuerte = 0;
        frameBaseCalculada = null;
        temporizadorAnimacionMuerte.start();
        solicitarRepintado.run();
    }

    void mostrarAnimacionDanio() {
        if (accionAnimacionRata == SpritesRatas.Accion.MUERTE) {
            return;
        }
        configurarListenersAnimacion();
        accionAnimacionRata = SpritesRatas.Accion.RECIBIR_DANIO;
        indiceFrameDanio = 0;
        frameBaseCalculada = null;
        temporizadorAnimacionDanio.restart();
        solicitarRepintado.run();
    }

    void actualizarEnemigosVisibles(
            List<fantasma> fantasmas, List<ratainfestada> ratasInfestadas) {
        if (fantasmas == null || ratasInfestadas == null
                || fantasmas.size() > modelos.escenario.levels.level.MAX_ENEMIGOS_POR_TIPO
                || ratasInfestadas.size() > modelos.escenario.levels.level.MAX_ENEMIGOS_POR_TIPO
                || fantasmas.stream().anyMatch(enemigo -> enemigo == null)
                || ratasInfestadas.stream().anyMatch(rata -> rata == null)) {
            throw new IllegalArgumentException("Las listas de enemigos deben ser válidas");
        }
        this.fantasmas = List.copyOf(fantasmas);
        this.ratasInfestadas = List.copyOf(ratasInfestadas);
        iniciosMuerteFantasmaNanos.clear();
        estadosSpriteRata.clear();
        for (ratainfestada rata : this.ratasInfestadas) {
            estadosSpriteRata.put(rata, new EstadoSpriteRata(rata.getX()));
        }
        solicitarRepintado.run();
    }

    void mostrarMuerteFantasma(fantasma enemigo) {
        if (enemigo == null || !fantasmas.contains(enemigo)) {
            throw new IllegalArgumentException("Se requiere un fantasma visible");
        }
        iniciosMuerteFantasmaNanos.putIfAbsent(enemigo, System.nanoTime());
        solicitarRepintado.run();
    }

    void mostrarMuerteRataInfestada(ratainfestada rata) {
        EstadoSpriteRata estado = estadosSpriteRata.get(rata);
        if (estado == null) {
            throw new IllegalArgumentException("Se requiere una rata infestada visible");
        }
        estado.sprite.setAnimacion(SpriteRataInfestada.Animacion.MUERTE);
        solicitarRepintado.run();
    }

    void dibujar(Graphics2D g) {
        dibujarHabilidades(g);
        dibujarFantasmas(g);
        dibujarRatasInfestadas(g);
        dibujarJugador(g);
        for (ProyectilEspecial proyectil : jugador.getProyectilesEspeciales()) {
            spriteProyectilStun.dibujar(g, proyectil);
        }
    }

    private void dibujarHabilidades(Graphics2D g) {
        if (jugador instanceof rata_verde rataVerde) {
            CharcoAcido charcoAcido = rataVerde.getCharcoAcido();
            if (charcoAcido != null) {
                spriteAcidoCharco.dibujar(g, charcoAcido);
            }
        }
        if (jugador instanceof rata_negra rataNegra) {
            LamparaFuego lamparaFuego = rataNegra.getLamparaFuego();
            if (lamparaFuego != null) {
                spriteLamparaFuego.dibujar(g, lamparaFuego);
            }
        }
    }

    private void dibujarFantasmas(Graphics2D g) {
        for (fantasma enemigo : fantasmas) {
            BufferedImage cuadro;
            if (enemigo.estavivo()) {
                GestorAnimacionSprite animacion = enemigo.estaMoviendoseDerecha()
                    ? animacionFantasmaDerecha : animacionFantasmaIzquierda;
                cuadro = animacion.getFrameActual();
            } else {
                long inicioMuerte = iniciosMuerteFantasmaNanos.computeIfAbsent(
                    enemigo, ignorado -> System.nanoTime()
                );
                long transcurrido = System.nanoTime() - inicioMuerte;
                int indice = (int) Math.min(
                    framesMuerteFantasma.length - 1,
                    transcurrido / DURACION_FRAME_NANOS
                );
                cuadro = framesMuerteFantasma[indice];
            }
            g.drawImage(
                cuadro, (int) enemigo.getX(), (int) enemigo.getY(),
                (int) enemigo.getAncho(), (int) enemigo.getAlto(), null
            );
        }
    }

    private void dibujarRatasInfestadas(Graphics2D g) {
        for (ratainfestada rata : ratasInfestadas) {
            EstadoSpriteRata estado = estadosSpriteRata.get(rata);
            estado.sprite.dibujar(
                g,
                (int) Math.round(rata.getX()),
                (int) Math.round(rata.getY()),
                ConfiguracionPartida.ANCHO_JUGADOR,
                ConfiguracionPartida.ALTO_JUGADOR
            );
            if (rata.estavivo()) {
                for (ProyectilAcido proyectil : rata.getProyectilesAcido()) {
                    spriteAcido.dibujar(g, proyectil);
                }
            }
        }
    }

    private void dibujarJugador(Graphics2D g) {
        BufferedImage cuadro = obtenerFrameRata();
        if (cuadro == null) {
            return;
        }
        g.setRenderingHint(
            RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
        );
        boolean mostrandoHabilidad = accionAnimacionRata == SpritesRatas.Accion.HAB_ESPECIAL;
        double escala = Math.min(
            (double) ConfiguracionPartida.ANCHO_JUGADOR / cuadro.getWidth(),
            (double) ConfiguracionPartida.ALTO_JUGADOR / cuadro.getHeight()
        );
        if (mostrandoHabilidad) {
            escala *= 1.25;
        }
        int anchoDibujo = (int) Math.round(cuadro.getWidth() * escala);
        int altoDibujo = (int) Math.round(cuadro.getHeight() * escala);
        if (cuadro != frameBaseCalculada) {
            ultimaFilaVisibleRata = obtenerUltimaFilaVisible(cuadro);
            frameBaseCalculada = cuadro;
        }
        int xDibujo = (int) jugador.getX();
        if (mostrandoHabilidad) {
            xDibujo = (int) Math.round(
                jugador.getX() - (anchoDibujo - ConfiguracionPartida.ANCHO_JUGADOR) / 2.0
            );
        }
        double ajusteBase = jugador.getTipoRata() == Personaje.TipoRata.VERDE
            ? AJUSTE_BASE_RATA_VERDE : 0;
        int yDibujo = (int) Math.round(
            jugador.getY() + ConfiguracionPartida.ALTO_JUGADOR
                - (ultimaFilaVisibleRata + 1) * (double) altoDibujo / cuadro.getHeight()
                + ajusteBase
        );
        if (mirandoIzquierda) {
            AffineTransform transformacion = g.getTransform();
            g.translate(xDibujo + anchoDibujo, yDibujo);
            g.scale(-1, 1);
            g.drawImage(cuadro, 0, 0, anchoDibujo, altoDibujo, null);
            g.setTransform(transformacion);
        } else {
            g.drawImage(cuadro, xDibujo, yDibujo, anchoDibujo, altoDibujo, null);
        }
    }

    private void actualizarSpritesRatasInfestadas() {
        long ahora = System.nanoTime();
        for (ratainfestada rata : ratasInfestadas) {
            EstadoSpriteRata estado = estadosSpriteRata.get(rata);
            double desplazamientoX = rata.getX() - estado.ultimaX;
            SpriteRataInfestada.Animacion animacion;
            if (!rata.estavivo()) {
                animacion = SpriteRataInfestada.Animacion.MUERTE;
            } else if (Math.abs(desplazamientoX) > 0.01) {
                animacion = desplazamientoX < 0
                    ? SpriteRataInfestada.Animacion.CAMINAR_IZQUIERDA
                    : SpriteRataInfestada.Animacion.CAMINAR_DERECHA;
            } else {
                animacion = SpriteRataInfestada.Animacion.QUIETA;
            }
            estado.sprite.setAnimacion(animacion);
            long deltaMilisegundos = (ahora - estado.ultimaActualizacionNanos) / 1_000_000L;
            estado.sprite.actualizar(deltaMilisegundos);
            estado.ultimaActualizacionNanos = ahora;
            estado.ultimaX = rata.getX();
        }
    }

    private SpritesRatas.Accion obtenerAccionAnimacion(double desplazamientoX) {
        if (jugador.estaEnElAire()) {
            return SpritesRatas.Accion.SALTAR;
        }
        if (jugador.estaAgachada()) {
            return SpritesRatas.Accion.AGACHADA;
        }
        if (Math.abs(desplazamientoX) > 0.01) {
            return SpritesRatas.Accion.CAMINAR;
        }
        return SpritesRatas.Accion.QUIETA;
    }

    private long obtenerDuracionFrame(SpritesRatas.Accion accion, int cantidadFrames) {
        if (accion != SpritesRatas.Accion.HAB_ESPECIAL) {
            return DURACION_FRAME_NANOS;
        }
        return switch (jugador.getTipoRata()) {
            case BLANCA -> DURACION_FRAME_HABILIDAD_BLANCA_NANOS;
            case VERDE, NEGRA -> DURACION_ANIMACION_PREPARACION_NANOS / cantidadFrames;
            case GRIS -> DURACION_FRAME_NANOS;
        };
    }

    private BufferedImage obtenerFrameRata() {
        return switch (accionAnimacionRata) {
            case MUERTE -> framesMuerte[indiceFrameMuerte];
            case RECIBIR_DANIO -> framesDanio[indiceFrameDanio];
            default -> animacionesRata.get(accionAnimacionRata).getFrameActual();
        };
    }

    private void actualizarAnimacionesFantasma() {
        animacionFantasmaDerecha.actualizar();
        animacionFantasmaIzquierda.actualizar();
    }

    private void configurarListenersAnimacion() {
        if (listenersAnimacionConfigurados) {
            return;
        }
        temporizadorAnimacionMuerte.addActionListener(evento -> avanzarAnimacionMuerte());
        temporizadorAnimacionDanio.addActionListener(evento -> avanzarAnimacionDanio());
        listenersAnimacionConfigurados = true;
    }

    private void avanzarAnimacionMuerte() {
        if (indiceFrameMuerte >= framesMuerte.length - 1) {
            temporizadorAnimacionMuerte.stop();
            alTerminarAnimacionMuerte.run();
            return;
        }
        indiceFrameMuerte++;
        solicitarRepintado.run();
    }

    private void avanzarAnimacionDanio() {
        if (indiceFrameDanio >= framesDanio.length - 1) {
            temporizadorAnimacionDanio.stop();
            accionAnimacionRata = SpritesRatas.Accion.QUIETA;
            frameBaseCalculada = null;
            solicitarRepintado.run();
            return;
        }
        indiceFrameDanio++;
        solicitarRepintado.run();
    }

    private int obtenerUltimaFilaVisible(BufferedImage imagen) {
        for (int y = imagen.getHeight() - 1; y >= 0; y--) {
            for (int x = 0; x < imagen.getWidth(); x++) {
                if ((imagen.getRGB(x, y) >>> 24) != 0) {
                    return y;
                }
            }
        }
        throw new IllegalStateException("El frame de la rata no contiene píxeles visibles");
    }

    private static final class EstadoSpriteRata {
        private final SpriteRataInfestada sprite = new SpriteRataInfestada();
        private double ultimaX;
        private long ultimaActualizacionNanos = System.nanoTime();

        private EstadoSpriteRata(double ultimaX) {
            this.ultimaX = ultimaX;
        }
    }
}
