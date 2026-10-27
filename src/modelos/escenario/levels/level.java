package modelos.escenario.levels;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class level {
    public static final int COLUMNAS_ATLAS = 7;
    public static final int COLUMNA_AIRE = 6;
    public static final int AGUJERO_SIGUIENTE = 28;
    public static final int AGUJERO_ANTERIOR = 29;
    private static final int CANAL_ROJO = 0;
    private static final int CANAL_VERDE = 1;
    private static final int CANAL_AZUL = 2;
    public static final int MAX_ENEMIGOS_POR_TIPO = 3;
    public static final int MAX_POWER_UPS_POR_NIVEL = 5;

    public enum TipoEnemigoMapa {
        INICIO_PATRULLA_FANTASMA(1),
        RATA_INFESTADA(2),
        FIN_PATRULLA_FANTASMA(3);

        private final int codigo;

        TipoEnemigoMapa(int codigo) {
            this.codigo = codigo;
        }

        private static TipoEnemigoMapa desdeCodigo(int codigo, int columna, int fila) {
            for (TipoEnemigoMapa tipo : values()) {
                if (tipo.codigo == codigo) {
                    return tipo;
                }
            }
            throw new IllegalArgumentException(
                "Código de enemigo desconocido " + codigo + " en (" + columna + ", " + fila + ")"
            );
        }
    }

    public enum TipoPowerUpMapa {
        VELOCIDAD(1),
        LENTO(2),
        INVULNERABILIDAD(3);

        private final int codigo;

        TipoPowerUpMapa(int codigo) {
            this.codigo = codigo;
        }

        private static TipoPowerUpMapa desdeCodigo(int codigo, int columna, int fila) {
            if (codigo == 4) {
                return INVULNERABILIDAD;
            }
            for (TipoPowerUpMapa tipo : values()) {
                if (tipo.codigo == codigo) {
                    return tipo;
                }
            }
            throw new IllegalArgumentException(
                "Código de power-up desconocido " + codigo + " en (" + columna + ", " + fila + ")"
            );
        }
    }

    public record MarcadorEnemigo(TipoEnemigoMapa tipo, int columna, int fila) { }
    public record MarcadorPowerUp(TipoPowerUpMapa tipo, int columna, int fila) { }
    public record PatrullaFantasma(MarcadorEnemigo inicio, MarcadorEnemigo fin) { }

    private final int[][] nivelDato;
    private final List<MarcadorEnemigo> marcadoresEnemigos;
    private final List<MarcadorPowerUp> marcadoresPowerUps;
    private final List<PatrullaFantasma> patrullasFantasma;

    public level(int[][] nivelDato) {
        this(canalesDeEscenario(nivelDato));
    }

    public level(int[][][] canales) {
        validarCanales(canales);
        int ancho = canales[CANAL_ROJO].length;
        int alto = canales[CANAL_ROJO][0].length;
        nivelDato = copiarCanal(canales[CANAL_ROJO], ancho, alto);

        List<MarcadorEnemigo> enemigos = new ArrayList<>();
        List<MarcadorPowerUp> powerUps = new ArrayList<>();
        for (int x = 0; x < ancho; x++) {
            for (int y = 0; y < alto; y++) {
                int codigoEnemigo = canales[CANAL_VERDE][x][y];
                if (codigoEnemigo != 0) {
                    enemigos.add(new MarcadorEnemigo(
                        TipoEnemigoMapa.desdeCodigo(codigoEnemigo, x, y), x, y
                    ));
                }

                int codigoPowerUp = canales[CANAL_AZUL][x][y];
                if (codigoPowerUp != 0) {
                    powerUps.add(new MarcadorPowerUp(
                        TipoPowerUpMapa.desdeCodigo(codigoPowerUp, x, y), x, y
                    ));
                }
            }
        }
        patrullasFantasma = validarMarcadoresEnemigos(enemigos);
        if (powerUps.size() > MAX_POWER_UPS_POR_NIVEL) {
            throw new IllegalArgumentException(
                "Un nivel admite como máximo " + MAX_POWER_UPS_POR_NIVEL + " power-ups"
            );
        }
        marcadoresEnemigos = List.copyOf(enemigos);
        marcadoresPowerUps = List.copyOf(powerUps);
    }

    private static int[][][] canalesDeEscenario(int[][] escenario) {
        if (escenario == null || escenario.length == 0
                || escenario[0] == null || escenario[0].length == 0) {
            throw new IllegalArgumentException("El mapa del nivel no puede estar vacío");
        }
        int alto = escenario[0].length;
        int[][] verde = new int[escenario.length][alto];
        int[][] azul = new int[escenario.length][alto];
        return new int[][][] { escenario, verde, azul };
    }

    private static void validarCanales(int[][][] canales) {
        if (canales == null || canales.length != 3 || canales[CANAL_ROJO] == null
                || canales[CANAL_ROJO].length == 0 || canales[CANAL_ROJO][0] == null
                || canales[CANAL_ROJO][0].length == 0) {
            throw new IllegalArgumentException("El mapa requiere canales RGB no vacíos");
        }
        int ancho = canales[CANAL_ROJO].length;
        int alto = canales[CANAL_ROJO][0].length;
        for (int canal = 0; canal < canales.length; canal++) {
            if (canales[canal] == null || canales[canal].length != ancho) {
                throw new IllegalArgumentException("Los canales RGB deben tener el mismo ancho");
            }
            for (int x = 0; x < ancho; x++) {
                if (canales[canal][x] == null || canales[canal][x].length != alto) {
                    throw new IllegalArgumentException("Los canales RGB deben tener la misma altura");
                }
            }
        }
    }

    private static int[][] copiarCanal(int[][] canal, int ancho, int alto) {
        int[][] copia = new int[ancho][alto];
        for (int x = 0; x < ancho; x++) {
            System.arraycopy(canal[x], 0, copia[x], 0, alto);
        }
        return copia;
    }

    private static List<PatrullaFantasma> validarMarcadoresEnemigos(
            List<MarcadorEnemigo> marcadores) {
        Map<Integer, List<MarcadorEnemigo>> marcadoresFantasmaPorFila = new TreeMap<>();
        int ratasInfestadas = 0;
        for (MarcadorEnemigo marcador : marcadores) {
            switch (marcador.tipo()) {
                case INICIO_PATRULLA_FANTASMA, FIN_PATRULLA_FANTASMA ->
                    marcadoresFantasmaPorFila
                        .computeIfAbsent(marcador.fila(), fila -> new ArrayList<>())
                        .add(marcador);
                case RATA_INFESTADA -> ratasInfestadas++;
            }
        }

        List<PatrullaFantasma> patrullas = new ArrayList<>();
        for (List<MarcadorEnemigo> marcadoresFila : marcadoresFantasmaPorFila.values()) {
            marcadoresFila.sort(Comparator.comparingInt(MarcadorEnemigo::columna));
            if (marcadoresFila.size() % 2 != 0) {
                throw new IllegalArgumentException(
                    "Cada fila con fantasmas debe tener pares de inicio y fin de patrulla"
                );
            }
            for (int indice = 0; indice < marcadoresFila.size(); indice += 2) {
                MarcadorEnemigo inicio = marcadoresFila.get(indice);
                MarcadorEnemigo fin = marcadoresFila.get(indice + 1);
                if (inicio.tipo() != TipoEnemigoMapa.INICIO_PATRULLA_FANTASMA
                        || fin.tipo() != TipoEnemigoMapa.FIN_PATRULLA_FANTASMA) {
                    throw new IllegalArgumentException(
                        "Los marcadores de patrulla deben alternarse como inicio y fin "
                            + "de izquierda a derecha"
                    );
                }
                patrullas.add(new PatrullaFantasma(inicio, fin));
            }
        }
        if (patrullas.size() > MAX_ENEMIGOS_POR_TIPO) {
            throw new IllegalArgumentException(
                "Un nivel admite como máximo " + MAX_ENEMIGOS_POR_TIPO + " fantasmas"
            );
        }
        if (ratasInfestadas > MAX_ENEMIGOS_POR_TIPO) {
            throw new IllegalArgumentException(
                "Un nivel admite como máximo " + MAX_ENEMIGOS_POR_TIPO + " ratas infestadas"
            );
        }
        return List.copyOf(patrullas);
    }

    public int getAncho() { return nivelDato.length; }
    public int getAlto()  { return nivelDato[0].length; }
    public List<MarcadorEnemigo> getMarcadoresEnemigos() { return marcadoresEnemigos; }
    public List<MarcadorPowerUp> getMarcadoresPowerUps() { return marcadoresPowerUps; }
    public List<PatrullaFantasma> getPatrullasFantasma() { return patrullasFantasma; }

    public Optional<MarcadorEnemigo> getMarcadorEnemigo(TipoEnemigoMapa tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("Se requiere el tipo de enemigo");
        }
        return marcadoresEnemigos.stream()
            .filter(marcador -> marcador.tipo() == tipo)
            .findFirst();
    }

    private boolean dentro(int x, int y) {
        return x >= 0 && y >= 0 && x < getAncho() && y < getAlto();
    }

    public int getspriteindex(int x, int y) {
        return dentro(x, y) ? nivelDato[x][y] : COLUMNA_AIRE;
    }

    public boolean esSolido(int x, int y) {
        if (!dentro(x, y)) return true;
        int v = nivelDato[x][y];
        if (v >= AGUJERO_SIGUIENTE) return false;
        return v % COLUMNAS_ATLAS != COLUMNA_AIRE;
    }

    public boolean haySolidoEn(double x, double y, double ancho, double alto, int tile) {
        int izq = (int) Math.floor(x / tile);
        int der = (int) Math.floor((x + ancho - 1) / tile);
        int arr = (int) Math.floor(y / tile);
        int aba = (int) Math.floor((y + alto - 1) / tile);
        for (int j = arr; j <= aba; j++)
            for (int i = izq; i <= der; i++)
                if (esSolido(i, j)) return true;
        return false;
    }

    public int agujeroEn(double x, double y, double ancho, double alto, int tile) {
        int izq = (int) Math.floor(x / tile), der = (int) Math.floor((x + ancho - 1) / tile);
        int arr = (int) Math.floor(y / tile), aba = (int) Math.floor((y + alto - 1) / tile);
        for (int j = arr; j <= aba; j++)
            for (int i = izq; i <= der; i++)
                if (dentro(i, j) && nivelDato[i][j] >= AGUJERO_SIGUIENTE) return nivelDato[i][j];
        return -1;
    }

    public int[] buscar(int tipo) {
        for (int j = 0; j < getAlto(); j++)
            for (int i = 0; i < getAncho(); i++)
                if (nivelDato[i][j] == tipo) return new int[]{i, j};
        return null;
    }
}