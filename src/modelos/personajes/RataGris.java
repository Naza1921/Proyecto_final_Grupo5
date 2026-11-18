package modelos.personajes;

import java.util.List;
import modelos.enemigos.Enemigo;

public class RataGris extends Personaje {
    private boolean segundoSaltoDisponible = true;

    public RataGris(String nombre) {
        super(nombre, TipoRata.GRIS, 120, 5, 8, 100);
    }

    @Override
    public boolean usarHabilidad(Enemigo objetivo, double xDestino, double yDestino) {
        return false;
    }

    @Override
    public void actualizarHabilidad(List<Enemigo> objetivos) {
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