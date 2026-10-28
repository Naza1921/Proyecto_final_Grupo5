package modelos.personajes;

import java.util.List;
import modelos.enemigos.enemigo;

// La rata gris reutiliza el comportamiento común y solo cambia sus estadísticas
// y la cantidad de estamina que recupera en cada actualización.
public class rata_gris extends Personaje {
    private boolean segundoSaltoDisponible = true;

    // Pasamos sus valores iniciales al padre para no duplicar estado en esta subclase.
    public rata_gris(String nombre) {
        // Tiene estadísticas equilibradas y recupera estamina más rápido.
        super(nombre, TipoRata.GRIS, 120, 5, 8, 100);
    }

    @Override
    public boolean usarHabilidad(enemigo objetivo, double xDestino, double yDestino) {
        return false;
    }

    @Override
    public void actualizarHabilidad(List<enemigo> objetivos) {
        validarObjetivosHabilidad(objetivos);
    }

    @Override
    public void prepararCambioDeSeccion() {
        // La rata gris no mantiene una habilidad temporal entre secciones.
    }

    @Override
    public boolean saltar() {
        if (!estaEnElAire()) {
            segundoSaltoDisponible = true;
            return super.saltar();
        } else if (segundoSaltoDisponible) {
            setVelocidadY(-getFuerzaSalto());
            segundoSaltoDisponible = false;
            return true;
        }
        return false;
    }

    @Override
    public void aterrizar(double yPiso) {
        super.aterrizar(yPiso);
        segundoSaltoDisponible = true;
    }

    @Override
    public void recargarEstamina() {
        // Esta rata recupera dos puntos por actualización, hasta el máximo.
        setEstamina(getEstamina() + 2);
    }
}