package modelos;

import java.util.ArrayList;
import java.util.List;

public class ratamercader  {
    // Atributos para la tienda y el inventario del mercader
    private final List<item> tienda;
    private final List<item> inventario;

    public ratamercader() {
        this.inventario = new ArrayList<>();
        this.tienda = new ArrayList<>();
    }

    public void agregarItem(item item) {
        inventario.add(item);
    }

    public void quitarItem(item item) {
        inventario.remove(item);
    }

    public List<item> getInventario() {
        return inventario;
    }

    public void agregarItemTienda(item newItem) {
        tienda.add(newItem);
    }

    public void quitarItemTienda(item removeItem) {
        tienda.remove(removeItem);
    }

    public List<item> getTienda() {
        return tienda;
    }
}