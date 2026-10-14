### Factory: creación de personajes

`PersonajeFactory` centraliza la creación de las distintas subclases de `Personaje`. Recibe un nombre y un valor de `Personaje.TipoRata`, y devuelve la instancia concreta correspondiente:

```java
Personaje jugador = PersonajeFactory.crearPersonaje(
    "Rata Gris",
    Personaje.TipoRata.GRIS
);
```

La pantalla de selección utiliza esta fábrica para crear la rata elegida. El resto del juego puede trabajar con el tipo común `Personaje`, sin tener que construir directamente `rata_gris`, `rata_blanca`, `rata_negra` o `rata_verde`.

En esta implementación se trata de una **Simple Factory**: una clase con un método estático decide qué clase concreta instanciar. No es el patrón GoF *Factory Method*, que normalmente delega la creación en subclases o métodos sobrescribibles.

**Por qué es útil para el juego:**

- Mantiene en un único lugar la correspondencia entre tipo de rata y clase concreta.
- Evita repetir condicionales de creación en las vistas y controladores.
- Facilita añadir o ajustar tipos de rata sin propagar la lógica de construcción por todo el proyecto.
**IMPORTATNE:**
La aplicasion  de este patron nos pormite respetar los principios SOLiD devido a que:
- Evitas un acoplamiento fuerte entre el creador y los productos concretos.
- Principio de responsabilidad única. Puedes mover el código de creación de producto a un lugar del programa, haciendo que el código sea más fácil de mantener.
- Principio de abierto/cerrado. Puedes incorporar nuevos tipos de productos en el programa sin descomponer el código cliente existente.
