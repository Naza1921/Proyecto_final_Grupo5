## Polimorfismo aplicado:

Aplicamos polimorfismo sobrescribiendo el método `recargarEstamina()` de `Personaje` en la clase `rata_blanca`.
En este caso, la rata blanca tiene un comportamiento diferente al recargar su estamina: 
si está agachada, recarga 2 puntos, recuperándose más rápido hasta llegar al máximo establecido. 
Si no está agachada, recarga 1 punto normalmente.

Este comportamiento también podría ser aplicado a las demás clases de ratas,
haciendo que cada una tenga su propia forma de recargar estamina. 
La rata gris sería la excepción,
ya que la recarga rápida de estamina forma parte de su habilidad característica.

De esta forma, cada tipo de rata puede modificar el comportamiento heredado de `Personaje`
según sus propias características.

## Enemigo fantasma

- `enemigos.fantasma` patrulla horizontalmente entre dos límites y conserva su posición y hitbox en el modelo.
- Si la hitbox del fantasma y la del personaje se superponen, resta 10 puntos de vida una sola vez por cada contacto nuevo. No hay invulnerabilidad temporal: el siguiente daño solo puede ocurrir tras separarse y volver a tocarse.
- `ControladorPartida` actualiza la patrulla y coordina el chequeo de contacto; `PanelJuego` únicamente dibuja el fantasma.

## Cambios aplicados en esta entrega

- Se corrigio Personaje.setX, que antes lanzaba una excepcion sin implementar
  (UnsupportedOperationException), y se agrego Personaje.setY para poder
  ubicar al personaje en el escenario. 