package servidor.control;

import servidor.modelo.ArchivoAccesoAleatorio;
import servidor.modelo.Rikishi;

import java.io.IOException;

/**
 * Coordina la escritura y lectura del archivo de acceso aleatorio.
 * Los datos escritos en el RAF provienen de consultas a la BD via ControlBD.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class ControlRAF {

    /** Modelo de acceso aleatorio. */
    private ArchivoAccesoAleatorio raf;

    /**
     * Constructor que inicializa el archivo de acceso aleatorio.
     */
    public ControlRAF() {
        this.raf = new ArchivoAccesoAleatorio();
    }

    /**
     * Guarda el resultado de un combate en el RAF.
     * Los datos del ganador y perdedor vienen de la BD.
     * @param numCombate Numero del combate (1, 2 o 3).
     * @param ganador    Rikishi ganador (con datos de BD).
     * @param perdedor   Rikishi perdedor (con datos de BD).
     */
    public void guardarResultado(int numCombate, Rikishi ganador, Rikishi perdedor) {
        try {
            raf.escribirResultado(
                numCombate,
                ganador.getNombre(),
                perdedor.getNombre(),
                "GANADOR",
                "PERDEDOR",
                ganador.getVictorias(),
                perdedor.getVictorias()
            );
        } catch (IOException e) {
            System.err.println("Error al escribir RAF: " + e.getMessage());
        }
    }

    /**
     * Lee todos los registros del RAF.
     * @param totalCombates Total de combates registrados.
     * @return Contenido formateado del RAF.
     */
    public String leerResultados(int totalCombates) {
        try {
            return raf.leerTodos(totalCombates);
        } catch (IOException e) {
            return "Error al leer RAF: " + e.getMessage();
        }
    }
}
