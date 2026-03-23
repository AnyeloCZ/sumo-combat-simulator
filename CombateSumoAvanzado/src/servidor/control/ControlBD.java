package servidor.control;

import servidor.modelo.IRikishiDAO;
import servidor.modelo.Rikishi;
import servidor.modelo.RikishiDAO;

import java.util.List;

/**
 * Coordina las operaciones sobre la base de datos de luchadores.
 * Unico que instancia Rikishi a partir de datos recibidos del cliente.
 * Usa IRikishiDAO para desacoplarse de la implementacion concreta (D del SOLID).
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
     * Crea un Rikishi con los datos del cliente y lo guarda en la BD.
     * @param nombre    Nombre del luchador.
     * @param peso      Peso del luchador.
     * @param kimarites Lista de tecnicas.
     * @return true si fue guardado exitosamente.
     */
    public boolean registrarLuchador(String nombre, double peso, List<String> kimarites) {
        Rikishi rikishi = new Rikishi(nombre, peso);
        rikishi.setKimarites(kimarites);
        return dao.insertar(rikishi);
    }

    /**
     * Consulta luchadores que no han participado en ningun combate.
     * @return Lista de disponibles.
     */
    public List<Rikishi> obtenerDisponibles() {
        return dao.consultarDisponibles();
    }

    /**
     * Consulta todos los luchadores registrados.
     * @return Lista completa.
     */
    public List<Rikishi> obtenerTodos() {
        return dao.consultarTodos();
    }

    /**
     * Actualiza victorias de un luchador en la BD.
     * @param id        ID del luchador.
     * @param victorias Nuevo valor.
     */
    public void actualizarVictorias(int id, int victorias) {
        dao.actualizarVictorias(id, victorias);
    }

    /**
     * Marca un luchador como participante en la BD.
     * @param id ID del luchador.
     */
    public void marcarParticipacion(int id) {
        dao.marcarParticipacion(id);
    }

    /**
     * Retorna el total de luchadores registrados.
     * @return Cantidad de luchadores.
     */
    public int contarLuchadores() {
        return dao.contarLuchadores();
    }

    /**
     * Consulta un luchador por su ID directamente desde la BD.
     * @param id ID del luchador.
     * @return Rikishi con datos actualizados de la BD.
     */
    public Rikishi obtenerPorId(int id) {
        return dao.consultarPorId(id);
    }
}
