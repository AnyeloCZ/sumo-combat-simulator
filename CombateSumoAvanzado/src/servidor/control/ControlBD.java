package servidor.control;

import servidor.modelo.IRikishiDAO;
import servidor.modelo.Rikishi;
import servidor.modelo.RikishiDAO;

import java.util.List;

/**
 * Controlador que coordina las operaciones sobre la base de datos.
 * Es el unico que instancia objetos Rikishi a partir de datos recibidos.
 * Envia parametros individuales al DAO — no objetos Rikishi completos.
 * Usa IRikishiDAO para desacoplarse de la implementacion concreta (D del SOLID).
 * Los errores del DAO se propagan como RuntimeException y salen por la vista.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class ControlBD {

    /** DAO para acceso a datos. */
    private IRikishiDAO dao;

    /**
     * Constructor que inicializa el DAO concreto.
     */
    public ControlBD() {
        this.dao = new RikishiDAO();
    }

    /**
     * Registra un luchador en la BD enviando parametros individuales al DAO.
     * No se envia el objeto Rikishi completo — solo sus datos primitivos.
     *
     * @param nombre    Nombre del luchador.
     * @param peso      Peso en kg.
     * @param kimarites Lista de tecnicas.
     * @return true si fue guardado exitosamente.
     */
    public boolean registrarLuchador(String nombre, double peso, List<String> kimarites) {
        String kimStr = String.join(",", kimarites);
        return dao.insertar(nombre, peso, 0, kimStr, false);
    }

    /**
     * Consulta luchadores disponibles (que no han participado).
     *
     * @return Lista de Rikishi disponibles.
     */
    public List<Rikishi> obtenerDisponibles() {
        return dao.consultarDisponibles();
    }

    /**
     * Consulta todos los luchadores registrados.
     *
     * @return Lista completa de Rikishi.
     */
    public List<Rikishi> obtenerTodos() {
        return dao.consultarTodos();
    }

    /**
     * Actualiza las victorias de un luchador enviando solo parametros al DAO.
     *
     * @param id        ID del luchador.
     * @param victorias Nuevo valor de victorias.
     */
    public void actualizarVictorias(int id, int victorias) {
        dao.actualizarVictorias(id, victorias);
    }

    /**
     * Marca un luchador como participante enviando solo el ID al DAO.
     *
     * @param id ID del luchador.
     */
    public void marcarParticipacion(int id) {
        dao.marcarParticipacion(id);
    }

    /**
     * Retorna el total de luchadores registrados.
     *
     * @return Cantidad de luchadores.
     */
    public int contarLuchadores() {
        return dao.contarLuchadores();
    }

    /**
     * Consulta un luchador por su ID directamente desde la BD.
     *
     * @param id ID del luchador.
     * @return Rikishi con datos actualizados de la BD.
     */
    public Rikishi obtenerPorId(int id) {
        return dao.consultarPorId(id);
    }
}
