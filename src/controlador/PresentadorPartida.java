package controlador;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import aplicacion.EnemigoVista;
import aplicacion.HabilidadVista;
import aplicacion.JugadorVista;
import aplicacion.MonedaVista;
import aplicacion.NivelVista;
import aplicacion.PowerUpVista;
import aplicacion.ProyectilVista;
import aplicacion.TipoPowerUpVista;
import aplicacion.TipoRataVista;
import modelos.enemigos.ProyectilAcido;
import modelos.enemigos.fantasma;
import modelos.enemigos.ratainfestada;
import modelos.escenario.levels.level;
import modelos.items.Moneda;
import modelos.items.PowerUp;
import modelos.items.PowerUpLento;
import modelos.items.PowerUpPizza;
import modelos.items.PowerUpVelocidad;
import modelos.items.PowerUpVida;
import modelos.partida.ConfiguracionPartida;
import modelos.personajes.CharcoAcido;
import modelos.personajes.LamparaFuego;
import modelos.personajes.Personaje;
import modelos.personajes.ProyectilEspecial;
import modelos.personajes.rata_negra;
import modelos.personajes.rata_verde;

final class PresentadorPartida {
    private final Map<Object, Long> identificadores = new IdentityHashMap<>();
    private long siguienteIdentificador;

    JugadorVista presentar(Personaje jugador) {
        if (jugador == null) {
            throw new IllegalArgumentException("Se requiere el personaje de la partida");
        }

        List<ProyectilVista> proyectiles = jugador.getProyectilesEspeciales().stream()
            .map(this::presentarProyectil)
            .toList();
        return new JugadorVista(
            jugador.getNombre(),
            convertirTipoRata(jugador),
            jugador.getX(),
            jugador.getY(),
            jugador.getVida(),
            jugador.getVidaMaxima(),
            jugador.getEstamina(),
            jugador.getEstaminaMaxima(),
            jugador.estaEnElAire(),
            jugador.estaAgachada(),
            presentarHabilidades(jugador),
            proyectiles
        );
    }

    List<EnemigoVista> presentarEnemigos(
            List<fantasma> fantasmas, List<ratainfestada> ratasInfestadas) {
        if (fantasmas == null || ratasInfestadas == null
                || fantasmas.stream().anyMatch(enemigo -> enemigo == null)
                || ratasInfestadas.stream().anyMatch(rata -> rata == null)) {
            throw new IllegalArgumentException("Las listas de enemigos deben ser válidas");
        }

        List<EnemigoVista> presentados = new ArrayList<>(
            fantasmas.size() + ratasInfestadas.size()
        );
        for (fantasma fantasma : fantasmas) {
            presentados.add(new EnemigoVista(
                idDe(fantasma),
                EnemigoVista.Tipo.FANTASMA,
                fantasma.getX(),
                fantasma.getY(),
                fantasma.getAncho(),
                fantasma.getAlto(),
                fantasma.estavivo(),
                fantasma.estaMoviendoseDerecha(),
                List.of()
            ));
        }
        for (ratainfestada rata : ratasInfestadas) {
            List<ProyectilVista> proyectiles = rata.getProyectilesAcido().stream()
                .filter(ProyectilAcido::estaActivo)
                .map(this::presentarProyectil)
                .toList();
            presentados.add(new EnemigoVista(
                idDe(rata),
                EnemigoVista.Tipo.RATA_INFESTADA,
                rata.getX(),
                rata.getY(),
                ConfiguracionPartida.ANCHO_JUGADOR,
                ConfiguracionPartida.ALTO_JUGADOR,
                rata.estavivo(),
                false,
                proyectiles
            ));
        }
        return List.copyOf(presentados);
    }

    EnemigoVista presentarEnemigo(fantasma enemigo) {
        return presentarEnemigos(List.of(enemigo), List.of()).get(0);
    }

    EnemigoVista presentarEnemigo(ratainfestada rata) {
        return presentarEnemigos(List.of(), List.of(rata)).get(0);
    }

    List<MonedaVista> presentarMonedas(List<Moneda> monedas) {
        if (monedas == null || monedas.stream().anyMatch(moneda -> moneda == null)) {
            throw new IllegalArgumentException("La lista de monedas debe ser válida");
        }
        return monedas.stream()
            .map(moneda -> new MonedaVista(moneda.getX(), moneda.getY()))
            .toList();
    }

    List<PowerUpVista> presentarPowerUps(List<PowerUp> powerUps) {
        if (powerUps == null || powerUps.stream().anyMatch(powerUp -> powerUp == null)) {
            throw new IllegalArgumentException("La lista de power-ups debe ser válida");
        }
        return powerUps.stream()
            .map(powerUp -> new PowerUpVista(
                convertirTipoPowerUp(powerUp), powerUp.getX(), powerUp.getY()
            ))
            .toList();
    }

    static NivelVista presentarNivel(level nivel, boolean ultimaSeccion) {
        if (nivel == null) {
            throw new IllegalArgumentException("Se requiere el nivel actual");
        }
        int ancho = nivel.getAncho();
        int alto = nivel.getAlto();
        int[][] tiles = new int[ancho][alto];
        for (int columna = 0; columna < ancho; columna++) {
            for (int fila = 0; fila < alto; fila++) {
                tiles[columna][fila] = nivel.getspriteindex(columna, fila);
            }
        }
        return new NivelVista(ancho, alto, tiles, ultimaSeccion);
    }

    private List<HabilidadVista> presentarHabilidades(Personaje jugador) {
        return switch (jugador) {
            case rata_verde rataVerde -> {
                CharcoAcido charco = rataVerde.getCharcoAcido();
                if (charco == null) {
                    yield List.of();
                }
                yield List.of(new HabilidadVista(
                    idDe(charco),
                    HabilidadVista.Tipo.CHARCO_ACIDO,
                    HabilidadVista.Estado.ACTIVA,
                    charco.getX(),
                    charco.getY(),
                    CharcoAcido.ANCHO,
                    CharcoAcido.ALTO
                ));
            }
            case rata_negra rataNegra -> {
                LamparaFuego lampara = rataNegra.getLamparaFuego();
                if (lampara == null) {
                    yield List.of();
                }
                HabilidadVista.Estado estado = switch (lampara.getEstado()) {
                    case PREPARANDO -> HabilidadVista.Estado.PREPARANDO;
                    case VOLANDO -> HabilidadVista.Estado.VOLANDO;
                    case FUEGO -> HabilidadVista.Estado.FUEGO;
                    case TERMINADA -> null;
                };
                if (estado == null) {
                    yield List.of();
                }
                boolean enVuelo = estado == HabilidadVista.Estado.PREPARANDO
                    || estado == HabilidadVista.Estado.VOLANDO;
                yield List.of(new HabilidadVista(
                    idDe(lampara),
                    HabilidadVista.Tipo.LAMPARA_FUEGO,
                    estado,
                    enVuelo ? lampara.getXLampara() : lampara.getXFuego(),
                    enVuelo ? lampara.getYLampara() : lampara.getYFuego(),
                    enVuelo ? 36 : LamparaFuego.ANCHO_FUEGO,
                    enVuelo ? 36 : LamparaFuego.ALTO_FUEGO
                ));
            }
            default -> List.of();
        };
    }

    private long idDe(Object objeto) {
        return identificadores.computeIfAbsent(objeto, ignorado -> siguienteIdentificador++);
    }

    private ProyectilVista presentarProyectil(ProyectilEspecial proyectil) {
        return new ProyectilVista(
            proyectil.getX(), proyectil.getY(), proyectil.getAncho(), proyectil.getAlto()
        );
    }

    private ProyectilVista presentarProyectil(ProyectilAcido proyectil) {
        return new ProyectilVista(
            proyectil.getX(), proyectil.getY(), proyectil.getAncho(), proyectil.getAlto()
        );
    }

    private TipoRataVista convertirTipoRata(Personaje jugador) {
        return switch (jugador.getTipoRata()) {
            case BLANCA -> TipoRataVista.BLANCA;
            case GRIS -> TipoRataVista.GRIS;
            case NEGRA -> TipoRataVista.NEGRA;
            case VERDE -> TipoRataVista.VERDE;
        };
    }

    private TipoPowerUpVista convertirTipoPowerUp(PowerUp powerUp) {
        return switch (powerUp) {
            case PowerUpVelocidad ignored -> TipoPowerUpVista.VELOCIDAD;
            case PowerUpLento ignored -> TipoPowerUpVista.LENTO;
            case PowerUpVida ignored -> TipoPowerUpVista.VIDA;
            case PowerUpPizza ignored -> TipoPowerUpVista.INVULNERABILIDAD;
            default -> throw new IllegalArgumentException(
                "No hay una representación visual para " + powerUp.getClass().getName()
            );
        };
    }
}
