package vista;

import java.util.HashMap;
import java.util.Map;

final class CatalogoSpritesEfectos {
    private final Map<String, SpriteEfecto> sprites = new HashMap<>();

    CatalogoSpritesEfectos() {
        registrar(new SpriteAcidoCharco());
        registrar(new SpriteLamparaFuego());
    }

    SpriteEfecto obtener(String clave) {
        SpriteEfecto sprite = sprites.get(clave);
        if (sprite == null) {
            throw new IllegalArgumentException("No hay un sprite registrado para el efecto " + clave);
        }
        return sprite;
    }

    void actualizarAnimaciones() {
        for (SpriteEfecto sprite : sprites.values()) {
            sprite.actualizarAnimacion();
        }
    }

    private void registrar(SpriteEfecto sprite) {
        sprites.put(sprite.getClaveSprite(), sprite);
    }
}
