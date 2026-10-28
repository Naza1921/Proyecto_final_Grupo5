package modelos.personajes;

import java.util.List;
import modelos.enemigos.enemigo;

public class rata_gris extends Personaje {
    private boolean segundoSaltoDisponible = true;

    public rata_gris(String nombre) {
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
    public void prepararCambioDeSeccion() {  }

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
        setEstamina(getEstamina() + 2);
    }
}