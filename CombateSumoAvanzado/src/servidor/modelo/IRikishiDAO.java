package servidor.modelo;

import java.util.List;

/**
 * Interfaz DAO para Rikishi.
 * Aplica el principio de Inversion de Dependencias (D del SOLID).
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public interface IRikishiDAO {

    /**
     * Inserta un Rikishi en la BD.
     * @param rikishi Luchador a insertar.
     * @return true si fue exitoso.
     */
    boolean insertar(Rikishi rikishi);

    /**
     * Consulta todos los luchadores.
     * @return Lista completa.
     */
    List<Rikishi> consultarTodos();

    /**
     * Consulta luchadores que no han participado.
     * @return Lista de disponibles.
     */
    List<Rikishi> consultarDisponibles();

    /**
     * Actualiza victorias de un luchador.
     * @param id        ID del luchador.
     * @param victorias Nuevo valor.
     * @return true si fue exitoso.
     */
    boolean actualizarVictorias(int id, int victorias);

    /**
     * Marca un luchador como participante.
     * @param id ID del luchador.
     * @return true si fue exitoso.
     */
    boolean marcarParticipacion(int id);

    /**
     * Cuenta el total de luchadores registrados.
     * @return Cantidad total.
     */
    int contarLuchadores();

    /**
     * Consulta un luchador por su ID.
     * @param id ID del luchador.
     * @return Rikishi encontrado o null.
     */
    Rikishi consultarPorId(int id);
}
