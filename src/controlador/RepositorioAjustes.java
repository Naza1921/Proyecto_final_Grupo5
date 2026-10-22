package controlador;

import java.io.IOException;
import java.util.Objects;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import modelos.PreferenciasAjustes;

public final class RepositorioAjustes {

    private static final String CLAVE_MUSICA_ACTIVADA = "musicaActivada";
    private static final String CLAVE_CANCION_SELECCIONADA = "cancionSeleccionada";
    private static final String CLAVE_VOLUMEN = "volumen";
    private static final String CLAVE_TEMA_OSCURO = "temaOscuro";
    private static final String CLAVE_PANTALLA_COMPLETA = "pantallaCompleta";

    private final Preferences preferencias;

    public RepositorioAjustes() {
        this(Preferences.userNodeForPackage(RepositorioAjustes.class));
    }

    public RepositorioAjustes(Preferences preferencias) {
        this.preferencias = Objects.requireNonNull(preferencias, "preferencias");
    }

    public PreferenciasAjustes cargar() {
        PreferenciasAjustes estandar = PreferenciasAjustes.ESTANDAR;
        return new PreferenciasAjustes(
                preferencias.getBoolean(CLAVE_MUSICA_ACTIVADA, estandar.musicaActivada()),
                preferencias.getInt(CLAVE_CANCION_SELECCIONADA, estandar.cancionSeleccionada()),
                preferencias.getInt(CLAVE_VOLUMEN, estandar.volumen()),
                preferencias.getBoolean(CLAVE_TEMA_OSCURO, estandar.temaOscuro()),
                preferencias.getBoolean(CLAVE_PANTALLA_COMPLETA, estandar.pantallaCompleta()));
    }

    public void guardar(PreferenciasAjustes ajustes) throws IOException {
        preferencias.putBoolean(CLAVE_MUSICA_ACTIVADA, ajustes.musicaActivada());
        preferencias.putInt(CLAVE_CANCION_SELECCIONADA, ajustes.cancionSeleccionada());
        preferencias.putInt(CLAVE_VOLUMEN, ajustes.volumen());
        preferencias.putBoolean(CLAVE_TEMA_OSCURO, ajustes.temaOscuro());
        preferencias.putBoolean(CLAVE_PANTALLA_COMPLETA, ajustes.pantallaCompleta());
        try {
            preferencias.flush();
        } catch (BackingStoreException error) {
            throw new IOException("No se pudieron guardar las preferencias.", error);
        }
    }
}
