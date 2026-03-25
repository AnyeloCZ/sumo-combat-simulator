package servidor.control;

import servidor.modelo.Rikishi;

import java.util.List;

/**
 * Controlador responsable de crear y serializar objetos Rikishi.
 * Unico que instancia Rikishi en el lado del cliente.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class ControlRikishi {

    /**
     * Crea un Rikishi con nombre y peso.
     *
     * @param nombre Nombre del luchador.
     * @param peso   Peso en kg.
     * @return Nuevo Rikishi.
     */
    public Rikishi crearRikishi(String nombre, double peso) {
        return new Rikishi(nombre, peso);
    }

    /**
     * Asigna la lista de kimarites a un Rikishi.
     *
     * @param rikishi   Luchador al que se le asignan las tecnicas.
     * @param kimarites Lista de nombres de tecnicas.
     */
    public void asignarKimarites(Rikishi rikishi, List<String> kimarites) {
        rikishi.setKimarites(kimarites);
    }

    /**
     * Convierte un Rikishi a cadena para enviarlo por socket.
     * Formato: "NOMBRE|PESO|kimarite1,kimarite2,..."
     *
     * @param rikishi Luchador a convertir.
     * @return Cadena lista para enviar por socket.
     */
    public String convertirACadena(Rikishi rikishi) {
        return rikishi.getNombre() + "|" + rikishi.getPeso() + "|" +
               String.join(",", rikishi.getKimarites());
    }
}
