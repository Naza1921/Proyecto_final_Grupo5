package controlador;

import java.util.ArrayList;
import java.util.List;

import aplicacion.EstadoNivel;
import modelos.escenario.levels.level;
import recursos.CargadorRecursos;

public class levelmanager implements EstadoNivel {

    private static final String[][] RUTAS = {
        { "assets/escenarios/Secciones/Piso_1_1.png", "assets/escenarios/Secciones/Piso_1_2.png", "assets/escenarios/Secciones/Piso_1_3.png", "assets/escenarios/Secciones/Piso_1_4.png", "assets/escenarios/Secciones/Piso_1_5.png" },
        { "assets/escenarios/Secciones/Piso_2_1.png", "assets/escenarios/Secciones/Piso_2_2.png", "assets/escenarios/Secciones/Piso_2_4.png", "assets/escenarios/Secciones/Piso_2_5.png" },
        { "assets/escenarios/Secciones/Piso_3_1.png", "assets/escenarios/Secciones/Piso_3_2.png", "assets/escenarios/Secciones/Piso_3_4.png", "assets/escenarios/Secciones/Piso_3_5.png" },
        { "assets/escenarios/Secciones/Piso_4_1.png", "assets/escenarios/Secciones/Piso_4_2.png", "assets/escenarios/Secciones/Piso_4_4.png", "assets/escenarios/Secciones/Piso_4_5.png" },
        { "assets/escenarios/Secciones/Piso_5_1.png", "assets/escenarios/Secciones/Piso_5_2.png", "assets/escenarios/Secciones/Piso_5_3.png", "assets/escenarios/Secciones/Piso_5_4.png", "assets/escenarios/Secciones/Piso_5_5.png" }
    };

    private level[][] niveles;
    private int piso = 0, seccion = 0;

    public levelmanager() {
        niveles = new level[RUTAS.length][];
        for (int p = 0; p < RUTAS.length; p++) {
            niveles[p] = new level[RUTAS[p].length];
            for (int s = 0; s < RUTAS[p].length; s++)
                if (RUTAS[p][s] != null)
                    niveles[p][s] = new level(
                        CargadorRecursos.obtenerCanalesNivel(RUTAS[p][s])
                    );
        }
    }

    @Override
    public level getNivel() { return niveles[piso][seccion]; }

    @Override
    public int getPiso() { return piso + 1; }

    public int getSeccion() { return seccion; }

    public int getIndiceNivelActual() {
        int indice = seccion;
        for (int p = 0; p < piso; p++) {
            indice += niveles[p].length;
        }
        return indice;
    }

    public List<level> getNiveles() {
        List<level> resultado = new ArrayList<>();
        for (level[] nivelesPiso : niveles) {
            for (level nivel : nivelesPiso) {
                if (nivel != null) {
                    resultado.add(nivel);
                }
            }
        }
        return List.copyOf(resultado);
    }

    private boolean existe(int p, int s) {
        return p >= 0 && p < niveles.length && s >= 0 && s < niveles[p].length && niveles[p][s] != null;
    }

    @Override
    public boolean esUltimaSeccion() { return !existe(piso, seccion + 1); }

    public boolean cambiarSeccion(int delta) {
        if (!existe(piso, seccion + delta)) return false;
        seccion += delta;
        return true;
    }

    public boolean hayPisoSiguiente() {
        return existe(piso + 1, 0);
    }

    public boolean avanzarPiso() {
        if (!hayPisoSiguiente()) return false;
        piso++;
        seccion = 0;
        return true;
    }

    @Override
    public boolean esPrimeraSeccion() {
        return piso == 0 && seccion == 0;
    }
}