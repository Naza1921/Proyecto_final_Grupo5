package vista;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import modelos.escenario.levels.level;
import modelos.partida.ConfiguracionPartida;
import recursos.CargadorRecursos;

final class RenderizadorNivel {
    private static final int TAMANO_TILE_ATLAS = 32;

    private final BufferedImage[] spritesNivel;
    private final BufferedImage[] spritesAgujero = new BufferedImage[3];

    RenderizadorNivel() {
        BufferedImage atlasNivel = CargadorRecursos.cargarImagen(
            CargadorRecursos.NIVEL_ATLAS
        );
        int columnas = atlasNivel.getWidth() / TAMANO_TILE_ATLAS;
        int filas = atlasNivel.getHeight() / TAMANO_TILE_ATLAS;
        spritesNivel = new BufferedImage[columnas * filas];
        for (int fila = 0; fila < filas; fila++) {
            for (int columna = 0; columna < columnas; columna++) {
                spritesNivel[fila * columnas + columna] = atlasNivel.getSubimage(
                    columna * TAMANO_TILE_ATLAS,
                    fila * TAMANO_TILE_ATLAS,
                    TAMANO_TILE_ATLAS,
                    TAMANO_TILE_ATLAS
                );
            }
        }

        BufferedImage atlasAgujeros = CargadorRecursos.cargarImagen(
            CargadorRecursos.AGUJERO_ATLAS
        );
        for (int indice = 0; indice < spritesAgujero.length; indice++) {
            spritesAgujero[indice] = atlasAgujeros.getSubimage(
                indice * TAMANO_TILE_ATLAS,
                0,
                TAMANO_TILE_ATLAS,
                TAMANO_TILE_ATLAS
            );
        }
    }

    void dibujar(Graphics2D graphics, level nivel, boolean esUltimaSeccion) {
        int tamTile = ConfiguracionPartida.TAM_TILE;
        for (int fila = 0; fila < nivel.getAlto(); fila++) {
            for (int columna = 0; columna < nivel.getAncho(); columna++) {
                int indiceSprite = nivel.getspriteindex(columna, fila);
                if (indiceSprite >= level.AGUJERO_SIGUIENTE) {
                    dibujarAgujero(
                        graphics,
                        columna * tamTile,
                        fila * tamTile,
                        indiceSprite,
                        esUltimaSeccion
                    );
                    continue;
                }
                if (indiceSprite % level.COLUMNAS_ATLAS == level.COLUMNA_AIRE) {
                    continue;
                }
                if (indiceSprite >= 0 && indiceSprite < spritesNivel.length) {
                    graphics.drawImage(
                        spritesNivel[indiceSprite],
                        columna * tamTile,
                        fila * tamTile,
                        tamTile,
                        tamTile,
                        null
                    );
                }
            }
        }
    }

    private void dibujarAgujero(
            Graphics2D graphics,
            int x,
            int y,
            int tipo,
            boolean esUltimaSeccion) {
        int indiceSprite = tipo == level.AGUJERO_SIGUIENTE
            ? (esUltimaSeccion ? 2 : 0)
            : 1;
        graphics.drawImage(
            spritesAgujero[indiceSprite],
            x,
            y,
            ConfiguracionPartida.TAM_TILE,
            ConfiguracionPartida.TAM_TILE,
            null
        );
    }
}
