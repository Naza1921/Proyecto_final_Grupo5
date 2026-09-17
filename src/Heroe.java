
import java.util.ArrayList;
import java.util.List;

public class Heroe extends Personaje
{
    private int mana = 0;
    private List<String> armas;

    public Heroe()
    {
        super(); // llama al constructor por defecto de Personaje
        this.armas = new ArrayList<>();
        this.armas.add("Espada");   // primera arma
        this.armas.add("Arco");     // segunda arma
        // opcional: puedes agregar más armas aquí
    }

    public int getMana()
    {
        return mana;
    }

    public void setMana(int mana)
    {
        this.mana = mana;
    }

    public List<String> getArmas()
    {
        return armas;
    }

    public void setArmas(List<String> armas)
    {
        this.armas = armas;
    }
}

