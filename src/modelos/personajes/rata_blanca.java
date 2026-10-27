package modelos.personajes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import modelos.enemigos.enemigo;
import modelos.partida.ConfiguracionPartida;

// La rata blanca hereda el estado y las acciones compartidas, y acá definimos
// sus estadísticas y la recarga extra que obtiene al agacharse.
public class rata_blanca extends Personaje {

    // Costo de estamina por intentar usar la habilidad especial.
    private static final double COSTO_ESTAMINA_STUN = 15;
    private final List<ProyectilStun> proyectilesStun = new ArrayList<>();

    public rata_blanca(String nombre) {
        // Es más resistente, a cambio de moverse y saltar un poco menos.
        super(nombre, TipoRata.BLANCA, 100, 7, 7.5, 100);
    }

    @Override
    public boolean usarHabilidad(enemigo objetivo, double xDestino, double yDestino) {
        return tirarStun();
    }

    public boolean tirarStun() {
        if (!consumirEstamina(COSTO_ESTAMINA_STUN)) {
            return false;
        }
        double inicioX = getX() + ConfiguracionPartida.ANCHO_JUGADOR / 2.0;
        double inicioY = getY() + ConfiguracionPartida.ALTO_JUGADOR / 2.0 - 8.0;
        proyectilesStun.add(new ProyectilStun(inicioX, inicioY, estaMirandoIzquierda()));
        return true;
    }

    @Override
    public void actualizarHabilidad(List<enemigo> objetivos) {
        super.actualizarHabilidad(objetivos);
        Iterator<ProyectilStun> iterador = proyectilesStun.iterator();
        while (iterador.hasNext()) {
            ProyectilStun proyectil = iterador.next();
            proyectil.actualizar();
            for (enemigo objetivo : objetivos) {
                if (proyectil.impactarSiColisiona(objetivo)) {
                    break;
                }
            }
            if (!proyectil.estaActivo()) {
                iterador.remove();
            }
        }
    }

    @Override
    public List<ProyectilEspecial> getProyectilesEspeciales() {
        return List.copyOf(proyectilesStun);
    }

    @Override
    public void recargarEstamina() {
        // Agacharse permite recuperar estamina más rápido.
        double recarga = estaAgachada() ? 2 : 1;
        setEstamina(getEstamina() + recarga);
    }

}