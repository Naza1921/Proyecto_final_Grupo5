# Integración del patrón Decorator en los power-ups

## Objetivo

Los power-ups modifican temporalmente el comportamiento del personaje. El patrón Decorator permite agregar o retirar esas modificaciones sin crear una subclase del personaje para cada combinación de efectos. El gestor coordina las duraciones, la pausa y el vencimiento de cada capa.

## Estructura final

- `PersonajeDecorable` declara las operaciones que pueden modificarse: consultar velocidad y recibir daño.
- `Personaje` implementa el comportamiento base y mantiene la cadena decorada actual.
- `DecoradorBase` envuelve un `PersonajeDecorable` y delega las operaciones que no modifica.
- `DecoradorVelocidad` aumenta la velocidad; `DecoradorLentitud` la reduce; `DecoradorInmortal` bloquea el daño.
- `PowerUp` expone directamente la categoría, recompensa, duración y las operaciones para aplicar o revertir el power-up. Ya no contiene un objeto `EfectoTemporal`.
- `PowerUpBase` reúne el comportamiento común. Los power-ups temporales registran su constructor de decorador, identificado por categoría. `PowerUpVida` aplica la curación instantáneamente y no añade una capa.
- `GestorEfectosTemporales` administra los temporizadores y delega la aplicación/reversión a cada `PowerUp`.

La relación de un power-up temporal es directa:

```text
PowerUpVelocidad -> DecoradorVelocidad -> Personaje
PowerUpLento     -> DecoradorLentitud  -> Personaje
PowerUpPizza     -> DecoradorInmortal  -> Personaje
```

## Cómo se construye y se retira la cadena

`Personaje` conserva un componente base y un registro ordenado de decoradores activos. Cuando se agrega o quita uno, reconstruye la cadena a partir del comportamiento base y de las capas que quedan. La velocidad del personaje y la recepción de daño consultan el componente decorado, así que los cambios tienen efecto en las operaciones reales del juego.

La identificación de las capas temporales se hace con `CategoriaEfectoTemporal`. Esto permite al gestor retirar un decorador específico y reconstruir la cadena aunque estuviera en medio de ella. Los demás decoradores permanecen activos; no se reaplican sus temporizadores ni se pierde su tiempo restante.

## Reglas del gestor

1. Los efectos de categorías diferentes coexisten y mantienen sus propios temporizadores.
2. Los power-ups distintos de una misma categoría se reemplazan. Por ejemplo, lentitud reemplaza velocidad porque ambas pertenecen a `VELOCIDAD`.
3. Si se recoge otra vez el mismo tipo de power-up temporal, se suma su duración al tiempo restante y no se agrega una segunda capa.
4. Cuando una duración vence, el gestor quita su decorador y elimina ese temporizador.
5. Al pausar, el tiempo de juego y los temporizadores quedan congelados. Al reanudar, las duraciones continúan desde el tiempo restante.
6. La curación es instantánea, no tiene temporizador y no necesita revertirse.
7. Al limpiar el gestor, se retiran todos los decoradores que él activó.

El tiempo se mide con `System.nanoTime()` para evitar depender de ajustes del reloj del sistema.

## Integración con el juego

- `CreadorPartida` crea un `GestorEfectosTemporales` asociado al personaje y lo entrega al controlador.
- `ControladorPartida` consulta colisiones con power-ups y llama al gestor en cada ciclo de actualización.
- `GestorRecogidaPowerUps` obtiene de cada power-up su recompensa, sonido y puntuación, y lo retira del nivel después de recogerlo.
- `PrincipalControlador` pausa y reanuda el gestor junto con la partida, y lo limpia cuando se abandona o termina la partida.
- `SpritesPowerUps` identifica cada power-up por su tipo concreto, sin depender de un objeto de estrategia.

## Limpieza de la implementación anterior

Se eliminaron `EfectoTemporal`, `EfectoCuracion`, `EfectoVelocidad`, `EfectoLento` y `EfectoInvulnerabilidad`: las responsabilidades de categoría, recompensa y aplicación pasaron a los propios power-ups. También se retiraron el diagrama anterior de Strategy y el archivo vacío y mal escrito `gestorDeEfectosTempotales.java`; el gestor integrado es `GestorEfectosTemporales`.

La aplicación de los cambios se valida compilando el subsistema Java y probando que una capa intermedia pueda vencer sin quitar otra capa activa, además de la extensión, pausa, reanudación y limpieza.
