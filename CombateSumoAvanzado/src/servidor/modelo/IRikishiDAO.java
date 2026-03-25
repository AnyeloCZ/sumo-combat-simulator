package servidor.modelo;

import java.util.List;

/**
 * Interfaz DAO para Rikishi.
 * Define las operaciones de acceso a datos.
 * Entre el control y el DAO viajan parametros primitivos, no objetos completos.
 * Aplica el principio de Inversion de Dependencias (D del SOLID).
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public interface IRikishiDAO {

    /**
     * Inserta un luchador en la BD con sus parametros individuales.
     * Viajan parametros, no el objeto Rikishi completo.
     *
     * @param nombre    Nombre del luchador.
     * @param peso      Peso en kg.
     * @param victorias Victorias iniciales.
     * @param kimarites Kimarites separados por coma.
     * @param participo Estado de participacion.
     * @return true si fue exitoso.
     */
    boolean insertar(String nombre, double peso, int victorias,
                      String kimarites, boolean participo);

    /**
     * Consulta todos los luchadores registrados.
     *
     * @return Lista completa de Rikishi.
     */
    List<Rikishi> consultarTodos();

    /**
     * Consulta luchadores que no han participado.
     *
     * @return Lista de Rikishi disponibles.
     */
    List<Rikishi> consultarDisponibles();

    /**
     * Actualiza victorias de un luchador por su ID.
     *
     * @param id        ID del luchador.
     * @param victorias Nuevo valor de victorias.
     * @return true si fue exitoso.
     */
    boolean actualizarVictorias(int id, int victorias);

    /**
     * Marca un luchador como participante por su ID.
     *
     * @param id ID del luchador.
     * @return true si fue exitoso.
     */
    boolean marcarParticipacion(int id);

    /**
     * Cuenta el total de luchadores registrados.
     *
     * @return Cantidad total.
     */
    int contarLuchadores();

    /**
     * Consulta un luchador por su ID.
     *
     * @param id ID del luchador.
     * @return Rikishi encontrado o null.
     */
    Rikishi consultarPorId(int id);
}
