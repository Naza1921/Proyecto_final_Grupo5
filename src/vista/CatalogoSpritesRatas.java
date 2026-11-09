package vista;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import recursos.CargadorRecursos;

final class CatalogoSpritesRatas {
    private static final Map<String, Map<SpritesRatas.Accion, DefinicionSprite>> DEFINICIONES =
        new HashMap<>();

    static {
        registrar("blanca", SpritesRatas.Accion.QUIETA,
            "/assets/rata_blanca/rata_blanca_v2_quieta.jpg", 4);
        registrar("blanca", SpritesRatas.Accion.CAMINAR,
            "/assets/rata_blanca/rata_blanca_v2_caminar.jpg", 6);
        registrar("blanca", SpritesRatas.Accion.SALTAR,
            "/assets/rata_blanca/rata_blanca_v2_salta.jpg", 7);
        registrar("blanca", SpritesRatas.Accion.AGACHADA,
            "/assets/rata_blanca/rata_blanca_v2_agache.jpg", 6);
        registrar("blanca", SpritesRatas.Accion.MUERTE,
            "/assets/rata_blanca/rata_blanca_v2_muerte.jpg", 10);
        registrar("blanca", SpritesRatas.Accion.RECIBIR_DANIO,
            "/assets/rata_blanca/rata_blanca_v2_recibir_danio.png", 10);
        registrar("blanca", SpritesRatas.Accion.HAB_ESPECIAL,
            "assets/rata_blanca/Animacion_de_ataque_energetico_del_raton.png", 12);

        registrar("negra", SpritesRatas.Accion.QUIETA,
            "/assets/rata_negra/rata_negra_v2_quieta.jpg", 1);
        registrar("negra", SpritesRatas.Accion.AGACHADA,
            "/assets/rata_negra/rata_negra_v2_quieta.jpg", 1);
        registrar("negra", SpritesRatas.Accion.CAMINAR,
            "/assets/rata_negra/rata_negra_v2_caminar.jpg", 8);
        registrar("negra", SpritesRatas.Accion.SALTAR,
            "/assets/rata_negra/rata_negra_v2_salta.jpg", 10);
        registrar("negra", SpritesRatas.Accion.MUERTE,
            "/assets/rata_negra/rata_negra_v2_muerte.jpg", 10);
        registrar("negra", SpritesRatas.Accion.RECIBIR_DANIO,
            "/assets/rata_negra/rata_negra_v2_recibir_danio.jpg", 10);
        registrar("negra", SpritesRatas.Accion.HAB_ESPECIAL,
            "/assets/rata_negra/rata_peste_lanzando_lampara_8x2_transparente.png", 16);

        registrar("gris", SpritesRatas.Accion.QUIETA,
            "/assets/rata_sprites_gris/rata_gris_v2_quieta.jpg", 4);
        registrar("gris", SpritesRatas.Accion.CAMINAR,
            "/assets/rata_sprites_gris/rata_gris_v2_camina.jpg", 6);
        registrar("gris", SpritesRatas.Accion.SALTAR,
            "/assets/rata_sprites_gris/rata_gris_v2_salta.jpg", 6);
        registrar("gris", SpritesRatas.Accion.AGACHADA,
            "/assets/rata_sprites_gris/rata_gris_v2_agache.jpg", 6);
        registrar("gris", SpritesRatas.Accion.MUERTE,
            "/assets/rata_sprites_gris/rata_gris_v2_muerte.jpg", 10);
        registrar("gris", SpritesRatas.Accion.RECIBIR_DANIO,
            "/assets/rata_sprites_gris/rata_gris_v2_recibir_danio.jpg", 10);

        registrar("verde", SpritesRatas.Accion.QUIETA,
            "/assets/rata_verde/rata_verde_v2_quieta.jpg", 4);
        registrar("verde", SpritesRatas.Accion.CAMINAR,
            "/assets/rata_verde/rata_verde_v2_corre.jpg", 6);
        registrar("verde", SpritesRatas.Accion.SALTAR,
            "/assets/rata_verde/rata_verde_v2_salta.jpg", 7);
        registrar("verde", SpritesRatas.Accion.AGACHADA,
            "/assets/rata_verde/rata_verde_v2_agacha.jpg", 6);
        registrar("verde", SpritesRatas.Accion.MUERTE,
            "/assets/rata_verde/rata_verde_v2_muerte.jpg", 10);
        registrar("verde", SpritesRatas.Accion.RECIBIR_DANIO,
            "/assets/rata_verde/rata_verde_v2_recibir_danio.jpg", 10);
        registrar("verde", SpritesRatas.Accion.HAB_ESPECIAL,
            "/assets/rata_verde/rata_lanzando_veneno_6x2_transparente.png", 12);
    }

    private CatalogoSpritesRatas() {
    }

    static SpriteSheet cargar(String clave, SpritesRatas.Accion accion) {
        if (clave == null || accion == null) {
            throw new IllegalArgumentException("Se requieren la rata y la acción de animación");
        }

        Map<SpritesRatas.Accion, DefinicionSprite> porAccion = DEFINICIONES.get(clave);
        DefinicionSprite definicion = porAccion == null ? null : porAccion.get(accion);
        if (definicion == null) {
            throw new IllegalArgumentException(
                "No hay sprites registrados para " + clave + " en la acción " + accion);
        }
        if (definicion.limitesX != null) {
            return new SpriteSheet(
                CargadorRecursos.cargarImagen(definicion.ruta),
                definicion.limitesX,
                definicion.y,
                definicion.altoRegion
            );
        }
        if (definicion.anchoRegion > 0) {
            return new SpriteSheet(
                CargadorRecursos.cargarImagen(definicion.ruta),
                definicion.cantidadFrames,
                definicion.x,
                definicion.y,
                definicion.anchoRegion,
                definicion.altoRegion
            );
        }
        return new SpriteSheet(definicion.ruta, definicion.cantidadFrames);
    }

    static boolean tiene(String clave, SpritesRatas.Accion accion) {
        Map<SpritesRatas.Accion, DefinicionSprite> porAccion = DEFINICIONES.get(clave);
        return porAccion != null && porAccion.containsKey(accion);
    }

    private static void registrar(String clave, SpritesRatas.Accion accion,
                                  String ruta, int cantidadFrames) {
        Map<SpritesRatas.Accion, DefinicionSprite> porAccion =
            DEFINICIONES.computeIfAbsent(clave, ignorada -> new EnumMap<>(SpritesRatas.Accion.class));
        porAccion.put(accion, new DefinicionSprite(
            ruta, cantidadFrames, 0, 0, 0, 0, null
        ));
    }

    private static void registrar(String clave, SpritesRatas.Accion accion,
                                  String ruta, int cantidadFrames, int x, int y,
                                  int anchoRegion, int altoRegion) {
        Map<SpritesRatas.Accion, DefinicionSprite> porAccion =
            DEFINICIONES.computeIfAbsent(clave, ignorada -> new EnumMap<>(SpritesRatas.Accion.class));
        porAccion.put(accion, new DefinicionSprite(
            ruta, cantidadFrames, x, y, anchoRegion, altoRegion, null
        ));
    }

    private static void registrar(String clave, SpritesRatas.Accion accion,
                                  String ruta, int[] limitesX, int y, int altoRegion) {
        Map<SpritesRatas.Accion, DefinicionSprite> porAccion =
            DEFINICIONES.computeIfAbsent(clave, ignorada -> new EnumMap<>(SpritesRatas.Accion.class));
        porAccion.put(accion, new DefinicionSprite(
            ruta, limitesX.length - 1, 0, y, 0, altoRegion, limitesX.clone()
        ));
    }

    private static final class DefinicionSprite {
        private final String ruta;
        private final int cantidadFrames;
        private final int x;
        private final int y;
        private final int anchoRegion;
        private final int altoRegion;
        private final int[] limitesX;

        private DefinicionSprite(String ruta, int cantidadFrames, int x, int y,
                                 int anchoRegion, int altoRegion, int[] limitesX) {
            this.ruta = ruta;
            this.cantidadFrames = cantidadFrames;
            this.x = x;
            this.y = y;
            this.anchoRegion = anchoRegion;
            this.altoRegion = altoRegion;
            this.limitesX = limitesX;
        }
    }
}
