## En esta carpeta se realizo la implementacion visual

se implemento la interfaz grafica en la carpeta grafica
asi como utilizar un Principal Controlador que se encargue de cargar
todo la parte grafica del proyecto 
asi mostrar los avances de este en la pantalla 

##Implementacion de un fondo del nivel 1 
se implemento para la 4ta entrega que es un prototipo visual

## Cambios aplicados en esta entrega

- Pantalla de introduccion (PantallaIntro): se reproduce un video (intro_fx.mp4)
  con musica usando JavaFX (JFXPanel + MediaPlayer + MediaView), y al terminar
  pasa automaticamente al menu principal.
- Menu principal (MenuPrincipal): pantalla con fondo (Portada_rata.png) y los
  botones Jugar, Opciones y Salir.
- Panel de juego (PanelJuego): muestra el fondo del Nivel 1 (Escenario_nivel1.jpeg)
  y dibuja al personaje con un sprite animado en lugar de una figura sin forma.
- Sprite del personaje (SpriteSheet): se recorta la hoja de sprites, se elimina
  el fondo negro original y se genera un contorno alrededor del cuerpo para que
  se distinga mejor sobre el escenario.
