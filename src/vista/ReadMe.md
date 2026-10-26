## En esta carpeta se realizo la implementacion visual

se implemento la interfaz grafica en la carpeta grafica
asi como utilizar un Principal Controlador que se encargue de cargar
todo la parte grafica del proyecto 
asi mostrar los avances de este en la pantalla 

##Implementacion de un fondo del nivel 1 
se implemento para la 4ta entrega que es un prototipo visual

## Cambios aplicados en esta entrega

- Pantalla de introduccion (PantallaIntro): se reproduce el video
  (intro_concepto_final/concepto_inicio_juego_10s_con_musica.mp4)
  con musica usando JavaFX (JFXPanel + MediaPlayer + MediaView), y al terminar
  pasa automaticamente al menu principal.
- Menu principal (MenuPrincipal): pantalla con fondo (Portada_rata.png) y los
  botones Jugar, Opciones y Salir.
- Panel de ajustes (MenuAjustesVista): controles de música y volumen, tema claro
  u oscuro, pantalla completa, guardar/restaurar preferencias y volver al menú.
- Panel de juego (PanelJuego): muestra el fondo del nivel
  (escenarios/Escenario_nivel3.jpeg)
  y dibuja al personaje con un sprite animado en lugar de una figura sin forma.
- La cámara de presentación sigue suavemente al personaje con zoom 2× en modo
  ventana y 2,5× en pantalla completa, respetando los límites del mapa. El
  escenario, los power-ups y el personaje se dibujan con el desplazamiento de
  cámara; el HUD y el aviso de nivel completado se dibujan en coordenadas de
  pantalla.
- Sprite del personaje (SpriteSheet): se recorta la hoja de sprites y se elimina
  el fondo original; el personaje se dibuja sin halo amarillo y el contorno del
  sprite se conserva para el indicador de estamina del HUD.

- Selección de personaje: `PanelSeleccionPersonaje` entrega la rata elegida a
  `PrincipalControlador`; `SpritesRatas` usa sus nuevos assets y proporciona
  la secuencia quieta para la vista previa y el juego.
- El selector presenta las cuatro ratas con su sprite y nombre, sin mostrar las
  estadísticas que permanecen definidas en sus clases del modelo.
- `GestorAnimacionSprite` controla el avance cada 160 ms. `PanelJuego` elige
  reposo, caminar/correr, salto o agachado según el estado y el desplazamiento
  del personaje; también refleja horizontalmente los sprites al moverse a la
  izquierda. Cada acción carga su hoja desde la carpeta de la rata y alinea
  sus cuadros. La rata negra no tiene hoja de agachado, por lo que conserva el
  sprite quieto en esa postura.
- Cuando la vida de la rata llega a cero, `ControladorPartida` detiene el ciclo
  de juego y `PanelJuego` reproduce una vez la hoja de muerte; el último cuadro
  queda visible. Todavía no hay una pantalla de Game Over ni una opción de
  reinicio en ese estado.
- Si la rata recibe daño y sobrevive, `PanelJuego` reproduce la hoja de daño
  correspondiente durante la secuencia y luego retoma la animación según su
  movimiento. Si el golpe la deja sin vida, prevalece la animación de muerte.
- `ControladorPartida` coordina el ciclo de juego, las colisiones y los cambios
  de sección. `PanelJuego` recibe el nivel para dibujarlo, pero ya no crea ni
  actualiza los gestores de colisión o secciones; las dimensiones compartidas
  de tiles y jugador se definen en `ConfiguracionPartida`.
- El personaje se dibuja alineando la última fila visible del frame con la base
  de su hitbox; así los márgenes transparentes de las hojas no lo hacen parecer
  suspendido ni cambian su posición o sus colisiones. La rata verde recibe un
  ajuste visual adicional de un píxel de mundo.
- `SpritesFantasma` recorta las animaciones de patrulla del recurso del fantasma;
  `PanelJuego` alterna los cuadros según la dirección y lo dibuja dentro del
  mundo con la transformación de cámara.
- Controles actuales: las flechas izquierda/derecha mueven a la rata y Espacio
  la hace saltar. El personaje colisiona con los tiles sólidos de cada sección.
