package modelos.personajes;

public interface EfectoHabilidad {
    String getClaveSprite();

    double getX();

    double getY();

    double getAncho();

    double getAlto();

    EtapaEfecto getEtapa();
}
