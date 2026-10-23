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
- La cámara de presentación sigue suavemente al personaje con zoom 2× y respeta
  los límites del mapa. El escenario, los power-ups y el personaje se dibujan
  con el desplazamiento de cámara; el HUD y el aviso de nivel completado se
  dibujan en coordenadas de pantalla.
- Sprite del personaje (SpriteSheet): se recorta la hoja de sprites y se elimina
  el fondo original; el personaje se dibuja sin halo amarillo y el contorno del
  sprite se conserva para el indicador de estamina del HUD.

- Selección de personaje: `PanelSeleccionPersonaje` entrega la rata elegida a
  `PrincipalControlador`; `PanelJuego` consulta su tipo y carga la secuencia
  idle correspondiente mediante `SpritesRatas`.
- El selector presenta las cuatro ratas con su sprite y nombre, sin mostrar las
  estadísticas que permanecen definidas en sus clases del modelo.
- `GestorAnimacionSprite` controla el avance de la animación cada 160 ms; los
  frames idle se cargan desde la franja correspondiente de cada hoja completa y
  cada rata usa la grilla propia de su recurso. Se conserva el anclaje original
  de las celdas para evitar desplazamientos y cortes; si Swing se retrasa, se
  recuperan los frames transcurridos en vez de ralentizar la secuencia.
- Controles actuales: las flechas izquierda/derecha mueven a la rata y Espacio
  la hace saltar. El personaje colisiona con los tiles sólidos de cada sección.
