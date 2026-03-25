package servidor.modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implementacion concreta del DAO para Rikishi.
 * Encapsula todas las operaciones SQL sobre la tabla rikishi.
 * Los errores se propagan como excepciones — no se imprime nada en consola.
 * El control es quien decide como mostrar los errores (por la vista).
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class RikishiDAO implements IRikishiDAO {

    /**
     * Retorna la conexion activa del Singleton.
     *
     * @return Connection activo.
     */
    private Connection getConexion() {
        return ConexionBD.getInstancia().getConexion();
    }

    /** {@inheritDoc} */
    @Override
    public boolean insertar(String nombre, double peso, int victorias,
                             String kimarites, boolean participo) {
        String sql = "INSERT INTO rikishi (nombre, peso, victorias, kimarites, participo) " +
                     "VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setDouble(2, peso);
            ps.setInt(3, victorias);
            ps.setString(4, kimarites);
            ps.setBoolean(5, participo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar luchador: " + e.getMessage());
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<Rikishi> consultarTodos() {
        List<Rikishi> lista = new ArrayList<>();
        String sql = "SELECT * FROM rikishi";
        try (Statement st = getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar luchadores: " + e.getMessage());
        }
        return lista;
    }

    /** {@inheritDoc} */
    @Override
    public List<Rikishi> consultarDisponibles() {
        List<Rikishi> lista = new ArrayList<>();
        String sql = "SELECT * FROM rikishi WHERE participo = false";
        try (Statement st = getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar disponibles: " + e.getMessage());
        }
        return lista;
    }

    /** {@inheritDoc} */
    @Override
    public boolean actualizarVictorias(int id, int victorias) {
        String sql = "UPDATE rikishi SET victorias = ? WHERE id = ?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, victorias);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar victorias: " + e.getMessage());
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean marcarParticipacion(int id) {
        String sql = "UPDATE rikishi SET participo = true WHERE id = ?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al marcar participacion: " + e.getMessage());
        }
    }

    /** {@inheritDoc} */
    @Override
    public int contarLuchadores() {
        String sql = "SELECT COUNT(*) FROM rikishi";
        try (Statement st = getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("Error al contar luchadores: " + e.getMessage());
        }
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public Rikishi consultarPorId(int id) {
        String sql = "SELECT * FROM rikishi WHERE id = ?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar por id: " + e.getMessage());
        }
        return null;
    }

    /**
     * Mapea una fila del ResultSet a un objeto Rikishi.
     *
     * @param rs ResultSet posicionado en la fila.
     * @return Objeto Rikishi mapeado.
     * @throws SQLException Si ocurre error al leer.
     */
    private Rikishi mapear(ResultSet rs) throws SQLException {
        List<String> kimarites = new ArrayList<>(
            Arrays.asList(rs.getString("kimarites").split(","))
        );
        return new Rikishi(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getDouble("peso"),
            rs.getInt("victorias"),
            kimarites,
            rs.getBoolean("participo")
        );
    }
}
