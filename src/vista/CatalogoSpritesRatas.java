package vista;

import java.util.EnumMap;
import java.util.Map;
import modelos.personajes.Personaje.TipoRata;

final class CatalogoSpritesRatas {
    private static final Map<TipoRata, Map<SpritesRatas.Accion, DefinicionSprite>> DEFINICIONES =
        new EnumMap<>(TipoRata.class);

    static {
        registrar(TipoRata.BLANCA, SpritesRatas.Accion.QUIETA,
            "/assets/rata_blanca/rata_blanca_v2_quieta.jpg", 4);
        registrar(TipoRata.BLANCA, SpritesRatas.Accion.CAMINAR,
            "/assets/rata_blanca/rata_blanca_v2_caminar.jpg", 6);
        registrar(TipoRata.BLANCA, SpritesRatas.Accion.SALTAR,
            "/assets/rata_blanca/rata_blanca_v2_salta.jpg", 7);
        registrar(TipoRata.BLANCA, SpritesRatas.Accion.AGACHADA,
            "/assets/rata_blanca/rata_blanca_v2_agache.jpg", 6);
        registrar(TipoRata.BLANCA, SpritesRatas.Accion.MUERTE,
            "/assets/rata_blanca/rata_blanca_v2_muerte.jpg", 10);
        registrar(TipoRata.BLANCA, SpritesRatas.Accion.RECIBIR_DANIO,
            "/assets/rata_blanca/rata_blanca_v2_recibir_danio.png", 10);

        registrar(TipoRata.NEGRA, SpritesRatas.Accion.QUIETA,
            "/assets/rata_negra/rata_negra_v2_quieta.jpg", 1);
        registrar(TipoRata.NEGRA, SpritesRatas.Accion.AGACHADA,
            "/assets/rata_negra/rata_negra_v2_quieta.jpg", 1);
        registrar(TipoRata.NEGRA, SpritesRatas.Accion.CAMINAR,
            "/assets/rata_negra/rata_negra_v2_caminar.jpg", 8);
        registrar(TipoRata.NEGRA, SpritesRatas.Accion.SALTAR,
            "/assets/rata_negra/rata_negra_v2_salta.jpg", 10);
        registrar(TipoRata.NEGRA, SpritesRatas.Accion.MUERTE,
            "/assets/rata_negra/rata_negra_v2_muerte.jpg", 10);
        registrar(TipoRata.NEGRA, SpritesRatas.Accion.RECIBIR_DANIO,
            "/assets/rata_negra/rata_negra_v2_recibir_danio.jpg", 10);

        registrar(TipoRata.GRIS, SpritesRatas.Accion.QUIETA,
            "/assets/rata_sprites_gris/rata_gris_v2_quieta.jpg", 4);
        registrar(TipoRata.GRIS, SpritesRatas.Accion.CAMINAR,
            "/assets/rata_sprites_gris/rata_gris_v2_camina.jpg", 6);
        registrar(TipoRata.GRIS, SpritesRatas.Accion.SALTAR,
            "/assets/rata_sprites_gris/rata_gris_v2_salta.jpg", 6);
        registrar(TipoRata.GRIS, SpritesRatas.Accion.AGACHADA,
            "/assets/rata_sprites_gris/rata_gris_v2_agache.jpg", 6);
        registrar(TipoRata.GRIS, SpritesRatas.Accion.MUERTE,
            "/assets/rata_sprites_gris/rata_gris_v2_muerte.jpg", 10);
        registrar(TipoRata.GRIS, SpritesRatas.Accion.RECIBIR_DANIO,
            "/assets/rata_sprites_gris/rata_gris_v2_recibir_danio.jpg", 10);

        registrar(TipoRata.VERDE, SpritesRatas.Accion.QUIETA,
            "/assets/rata_verde/rata_verde_v2_quieta.jpg", 4);
        registrar(TipoRata.VERDE, SpritesRatas.Accion.CAMINAR,
            "/assets/rata_verde/rata_verde_v2_corre.jpg", 6);
        registrar(TipoRata.VERDE, SpritesRatas.Accion.SALTAR,
            "/assets/rata_verde/rata_verde_v2_salta.jpg", 7);
        registrar(TipoRata.VERDE, SpritesRatas.Accion.AGACHADA,
            "/assets/rata_verde/rata_verde_v2_agacha.jpg", 6);
        registrar(TipoRata.VERDE, SpritesRatas.Accion.MUERTE,
            "/assets/rata_verde/rata_verde_v2_muerte.jpg", 10);
        registrar(TipoRata.VERDE, SpritesRatas.Accion.RECIBIR_DANIO,
            "/assets/rata_verde/rata_verde_v2_recibir_danio.jpg", 10);
    }

    private CatalogoSpritesRatas() {
    }

    static SpriteSheet cargar(TipoRata tipo, SpritesRatas.Accion accion) {
        if (tipo == null || accion == null) {
            throw new IllegalArgumentException("Se requieren la rata y la acción de animación");
        }

        Map<SpritesRatas.Accion, DefinicionSprite> porAccion = DEFINICIONES.get(tipo);
        DefinicionSprite definicion = porAccion == null ? null : porAccion.get(accion);
        if (definicion == null) {
            throw new IllegalArgumentException(
                "No hay sprites registrados para " + tipo + " en la acción " + accion);
        }
        return new SpriteSheet(definicion.ruta, definicion.cantidadFrames);
    }

    private static void registrar(TipoRata tipo, SpritesRatas.Accion accion,
                                  String ruta, int cantidadFrames) {
        Map<SpritesRatas.Accion, DefinicionSprite> porAccion =
            DEFINICIONES.computeIfAbsent(tipo, clave -> new EnumMap<>(SpritesRatas.Accion.class));
        porAccion.put(accion, new DefinicionSprite(ruta, cantidadFrames));
    }

    private static final class DefinicionSprite {
        private final String ruta;
        private final int cantidadFrames;

        private DefinicionSprite(String ruta, int cantidadFrames) {
            this.ruta = ruta;
            this.cantidadFrames = cantidadFrames;
        }
    }
}
