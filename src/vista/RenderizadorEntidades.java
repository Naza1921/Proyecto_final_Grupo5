package vista;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.Timer;

import aplicacion.EnemigoVista;
import aplicacion.HabilidadVista;
import aplicacion.JugadorVista;
import aplicacion.ProyectilVista;
import aplicacion.TipoRataVista;
import controlador.GestorAnimacionSprite;
import modelos.partida.ConfiguracionPartida;

final class RenderizadorEntidades {
    private static final long DURACION_FRAME_NANOS = 160_000_000L;
    private static final long DURACION_ANIMACION_PREPARACION_NANOS = 1_500_000_000L;
    private static final long DURACION_FRAME_HABILIDAD_BLANCA_NANOS = 60_000_000L;
    private static final double AJUSTE_BASE_RATA_VERDE = 1.0;

    private JugadorVista jugador;
    private final Runnable alTerminarAnimacionMuerte;
    private final Runnable solicitarRepintado;
    private final Map<Long, Long> iniciosMuerteFantasmaNanos = new HashMap<>();
    private final Map<Long, EstadoSpriteRata> estadosSpriteRata = new HashMap<>();
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
    private List<EnemigoVista> enemigos = List.of();
    private SpritesRatas.Accion accionAnimacionRata = SpritesRatas.Accion.QUIETA;
    private double ultimaXPresentada;
    private boolean mirandoIzquierda;
    private BufferedImage frameBaseCalculada;
    private int ultimaFilaVisibleRata;
    private int indiceFrameMuerte;
    private int indiceFrameDanio;
    private boolean listenersAnimacionConfigurados;
    private long finAnimacionHabilidadNanos;

    RenderizadorEntidades(JugadorVista jugador, List<EnemigoVista> enemigos,
                          Runnable alTerminarAnimacionMuerte,
                          Runnable solicitarRepintado) {
        if (jugador == null || enemigos == null
                || alTerminarAnimacionMuerte == null || solicitarRepintado == null) {
            throw new IllegalArgumentException("Se requieren datos y acciones de renderizado");
        }
        this.jugador = jugador;
        this.alTerminarAnimacionMuerte = alTerminarAnimacionMuerte;
        this.solicitarRepintado = solicitarRepintado;
        actualizarEnemigosVisibles(enemigos);

        for (SpritesRatas.Accion accion : SpritesRatas.Accion.values()) {
            if (accion == SpritesRatas.Accion.MUERTE
                    || accion == SpritesRatas.Accion.RECIBIR_DANIO
                    || !CatalogoSpritesRatas.tiene(jugador.tipo(), accion)) {
                continue;
            }
            BufferedImage[] frames = SpritesRatas.cargarFrames(jugador.tipo(), accion);
            animacionesRata.put(
                accion,
                new GestorAnimacionSprite(frames, obtenerDuracionFrame(accion, frames.length))
            );
        }
        framesMuerte = SpritesRatas.cargarFrames(
            jugador.tipo(), SpritesRatas.Accion.MUERTE
        );
        temporizadorAnimacionMuerte = new Timer(
            (int) (DURACION_FRAME_NANOS / 1_000_000L), null
        );
        temporizadorAnimacionMuerte.setCoalesce(true);
        framesDanio = SpritesRatas.cargarFrames(
            jugador.tipo(), SpritesRatas.Accion.RECIBIR_DANIO
        );
        temporizadorAnimacionDanio = new Timer(
            (int) (DURACION_FRAME_NANOS / 1_000_000L), null
        );
        temporizadorAnimacionDanio.setCoalesce(true);
        ultimaXPresentada = jugador.x();
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

    void actualizar(JugadorVista jugador) {
        if (jugador == null || jugador.tipo() != this.jugador.tipo()) {
            throw new IllegalArgumentException("Se requieren datos del mismo jugador");
        }
        this.jugador = jugador;
        double xActual = jugador.x();
        double desplazamientoX = xActual - ultimaXPresentada;
        if (Math.abs(desplazamientoX) > 0.01) {
            mirandoIzquierda = desplazamientoX < 0;
        }
        ultimaXPresentada = xActual;

        spriteAcido.actualizar();
        spriteProyectilStun.actualizar();
        spriteLamparaFuego.actualizar();
        for (HabilidadVista habilidad : jugador.habilidades()) {
            if (habilidad.tipo() == HabilidadVista.Tipo.CHARCO_ACIDO) {
                spriteAcidoCharco.actualizar(habilidad);
            }
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

    void actualizarEnemigosVisibles(List<EnemigoVista> enemigos) {
        if (enemigos == null || enemigos.size() > 6
                || enemigos.stream().anyMatch(enemigo -> enemigo == null)) {
            throw new IllegalArgumentException("Las listas de enemigos deben ser válidas");
        }
        this.enemigos = List.copyOf(enemigos);
        Set<Long> idsPresentes = new HashSet<>();
        for (EnemigoVista enemigo : this.enemigos) {
            idsPresentes.add(enemigo.id());
            if (enemigo.tipo() == EnemigoVista.Tipo.RATA_INFESTADA) {
                estadosSpriteRata.computeIfAbsent(
                    enemigo.id(), ignorado -> new EstadoSpriteRata(enemigo.x())
                );
            }
        }
        estadosSpriteRata.keySet().retainAll(idsPresentes);
        iniciosMuerteFantasmaNanos.keySet().retainAll(idsPresentes);
        solicitarRepintado.run();
    }

    void mostrarMuerteFantasma(EnemigoVista enemigo) {
        if (enemigo == null || enemigo.tipo() != EnemigoVista.Tipo.FANTASMA
                || enemigos.stream().noneMatch(actual -> actual.id() == enemigo.id())) {
            throw new IllegalArgumentException("Se requiere un fantasma visible");
        }
        iniciosMuerteFantasmaNanos.putIfAbsent(enemigo.id(), System.nanoTime());
        solicitarRepintado.run();
    }

    void mostrarMuerteRataInfestada(EnemigoVista rata) {
        EstadoSpriteRata estado = rata == null ? null : estadosSpriteRata.get(rata.id());
        if (estado == null || rata.tipo() != EnemigoVista.Tipo.RATA_INFESTADA) {
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
        for (ProyectilVista proyectil : jugador.proyectiles()) {
            spriteProyectilStun.dibujar(g, proyectil);
        }
    }

    private void dibujarHabilidades(Graphics2D g) {
        for (HabilidadVista habilidad : jugador.habilidades()) {
            switch (habilidad.tipo()) {
                case CHARCO_ACIDO -> spriteAcidoCharco.dibujar(g, habilidad);
                case LAMPARA_FUEGO -> spriteLamparaFuego.dibujar(g, habilidad);
            }
        }
    }

    private void dibujarFantasmas(Graphics2D g) {
        for (EnemigoVista enemigo : enemigos) {
            if (enemigo.tipo() != EnemigoVista.Tipo.FANTASMA) {
                continue;
            }
            BufferedImage cuadro;
            if (enemigo.vivo()) {
                GestorAnimacionSprite animacion = enemigo.moviendoseDerecha()
                    ? animacionFantasmaDerecha : animacionFantasmaIzquierda;
                cuadro = animacion.getFrameActual();
            } else {
                long inicioMuerte = iniciosMuerteFantasmaNanos.computeIfAbsent(
                    enemigo.id(), ignorado -> System.nanoTime()
                );
                long transcurrido = System.nanoTime() - inicioMuerte;
                int indice = (int) Math.min(
                    framesMuerteFantasma.length - 1,
                    transcurrido / DURACION_FRAME_NANOS
                );
                cuadro = framesMuerteFantasma[indice];
            }
            g.drawImage(
                cuadro, (int) enemigo.x(), (int) enemigo.y(),
                (int) enemigo.ancho(), (int) enemigo.alto(), null
            );
        }
    }

    private void dibujarRatasInfestadas(Graphics2D g) {
        for (EnemigoVista rata : enemigos) {
            if (rata.tipo() != EnemigoVista.Tipo.RATA_INFESTADA) {
                continue;
            }
            EstadoSpriteRata estado = estadosSpriteRata.get(rata.id());
            estado.sprite.dibujar(
                g,
                (int) Math.round(rata.x()),
                (int) Math.round(rata.y()),
                (int) rata.ancho(),
                (int) rata.alto()
            );
            if (rata.vivo()) {
                for (ProyectilVista proyectil : rata.proyectiles()) {
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
        int xDibujo = (int) jugador.x();
        if (mostrandoHabilidad) {
            xDibujo = (int) Math.round(
                jugador.x() - (anchoDibujo - ConfiguracionPartida.ANCHO_JUGADOR) / 2.0
            );
        }
        double ajusteBase = jugador.tipo() == TipoRataVista.VERDE
            ? AJUSTE_BASE_RATA_VERDE : 0;
        int yDibujo = (int) Math.round(
            jugador.y() + ConfiguracionPartida.ALTO_JUGADOR
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
        for (EnemigoVista rata : enemigos) {
            if (rata.tipo() != EnemigoVista.Tipo.RATA_INFESTADA) {
                continue;
            }
            EstadoSpriteRata estado = estadosSpriteRata.get(rata.id());
            double desplazamientoX = rata.x() - estado.ultimaX;
            SpriteRataInfestada.Animacion animacion;
            if (!rata.vivo()) {
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
            estado.ultimaX = rata.x();
        }
    }

    private SpritesRatas.Accion obtenerAccionAnimacion(double desplazamientoX) {
        if (jugador.enElAire()) {
            return SpritesRatas.Accion.SALTAR;
        }
        if (jugador.agachado()) {
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
        return switch (jugador.tipo()) {
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
