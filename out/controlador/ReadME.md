## En esta carpeta se maneja el control de la aplicacion

PrincipalControlador se encarga de armar la ventana principal y manejar
el cambio entre las distintas pantallas del juego (intro, menu, juego)
usando CardLayout, ademas de conectar los botones del menu con sus acciones.

## Cambios aplicados en esta entrega

- Se conecto la pantalla de introduccion (PantallaIntro) con el menu principal:
  al terminar el video, se muestra el menu automaticamente en vez de usar un
  temporizador fijo.
- Se habilito el redimensionado de la ventana (antes estaba fijo), con un
  tamaño minimo para que no se rompa el HUD ni los botones.
- El boton "Jugar" crea al personaje, ubica su posicion inicial en el escenario
  y muestra el panel de juego (PanelJuego).