# Carga de partida en segundo plano

## Objetivo

Al elegir una rata, la preparación de la partida ya no debe bloquear el hilo de
eventos de Swing. Mientras se crean los modelos y se leen los recursos, la
ventana muestra la imagen de fondo y el nombre del archivo que se está leyendo.

## Qué ocurre al elegir el personaje

1. El botón entrega al controlador la fábrica del personaje seleccionado. La
   fábrica se ejecuta en segundo plano, no durante el evento del botón.
2. `PrincipalControlador` muestra `PanelCargaPartida` y arranca un
   `SwingWorker`.
3. En `doInBackground`, `CreadorPartida.preparar` crea el personaje y los
   modelos de la partida, inicia la carga paralela de los fondos, carga los
   mapas y prepara los recursos de renderizado: atlas, HUD, monedas, sprites,
   animaciones y power-ups.
4. `CargadorRecursos` informa la ruta de cada imagen al comenzar a leerla.
   `SwingWorker` entrega esas actualizaciones en `process`, que muestra el
   archivo actual desde el EDT.
5. Cuando la preparación termina, `done` recoge el resultado y
   `CreadorPartida.crear` construye `PanelJuego` y conecta los controles en el
   EDT. Después se muestra e inicia la partida.
6. Si la preparación o el montaje fallan, se vuelve a
   la selección y se muestra el error. La carga no se puede cancelar.

## Carga paralela de fondos

Los cinco fondos de los pisos se leen con un pool fijo de dos hilos. Cada hilo
toma otro fondo cuando termina el anterior; las otras tareas de preparación,
como cargar mapas y preparar sprites, avanzan mientras tanto en el hilo del
`SwingWorker`.

Cada fondo se guarda en la posición que tenía en la lista original de rutas.
Por eso el orden de los pisos en el arreglo no depende del orden en que terminan
los hilos. La preparación espera a que se carguen todos los fondos antes de
crear y mostrar el panel de juego.

La pantalla informa el avance con mensajes como `Cargando fondos (1 de 5)` a
medida que se completa cada imagen. Si ocurre un error durante la preparación,
se detiene el grupo de carga y se muestra el error en lugar de iniciar una
partida incompleta.

## Por qué se separaron los recursos de la vista

Los constructores del juego también leen imágenes y extraen cuadros de
animación. Ejecutarlos desde `doInBackground` crearía componentes Swing en un
hilo incorrecto; dejarlos en el constructor habitual de `PanelJuego` mantendría
parte del bloqueo. Por eso `PanelJuego.prepararRecursos` construye los objetos
de renderizado sin crear componentes visuales. `PanelJuego` recibe esos objetos
ya preparados y enlaza su repintado cuando se crea en el EDT.

## Qué se mantiene en el EDT

- Creación y montaje de los componentes Swing.
- Actualizaciones visibles del archivo que se está leyendo.
- Enlace de teclas, temporizadores Swing y activación de la partida.

El reloj de la partida continúa basándose en `System.nanoTime()`, y el ciclo de
simulación no se trasladó a otro hilo: ninguno era la causa de la espera previa
a que apareciera el juego.

## Archivos mostrados

La pantalla de carga muestra el nombre del archivo que se está leyendo en ese
momento y el tiempo transcurrido desde que se eligió el personaje. Las
actualizaciones llegan mientras el trabajador avanza por mapas, fondos y
sprites. No se muestra porcentaje ni barra de progreso.

El pool puede reducir el tiempo total si las lecturas de fondos se solapan con
otras tareas y el equipo puede aprovechar esos hilos. No se paralizan todas las
partes de la inicialización, y no se da por hecho que el tiempo vaya a mejorar:
hay que comparar mediciones en las mismas condiciones antes y después.

## Apariencia de la pantalla

`PanelCargaPartida` utiliza `src/assets/escenarios/fondo de carga.png` como
fondo. La imagen cubre el panel sin deformarse y recorta los bordes que excedan
la relación de aspecto. En la parte inferior se muestra solo la ruta del recurso
que se está cargando y el contador de tiempo; no hay barra ni porcentaje.
