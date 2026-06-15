package mx.unam.musicdb.dao;

import mx.unam.musicdb.model.Type;
import mx.unam.musicdb.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TypeDAOImpl implements TypeDAO {

    private static final String SQL_INSERTAR =
        "INSERT INTO types (id_type, description) VALUES (?, ?)";

    private static final String SQL_BUSCAR_POR_ID =
        "SELECT * FROM types WHERE id_type = ?";

    private static final String SQL_BUSCAR_TODOS =
        "SELECT * FROM types ORDER BY id_type";

    private static final String SQL_ACTUALIZAR =
        "UPDATE types SET description=? WHERE id_type=?";

    private static final String SQL_ELIMINAR =
        "DELETE FROM types WHERE id_type = ?";

    private static final String SQL_POR_DESCRIPCION =
        "SELECT * FROM types WHERE description = ?";

    @Override
    public void insertar(Type t) {
        try (PreparedStatement ps = getConn().prepareStatement(
                SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, t.getIdType());
            ps.setString(2, t.getDescription());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) t.setIdType(keys.getInt(1));
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar tipo: " + t, e);
        }
    }

    @Override
    public Optional<Type> buscarPorId(Integer id) {
        try (PreparedStatement ps = getConn().prepareStatement(SQL_BUSCAR_POR_ID)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar tipo con id=" + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Type> buscarTodos() {
        List<Type> lista = new ArrayList<>();
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(SQL_BUSCAR_TODOS)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener todos los tipos", e);
        }
        return lista;
    }

    @Override
    public void actualizar(Type t) {
        try (PreparedStatement ps = getConn().prepareStatement(SQL_ACTUALIZAR)) {
            ps.setString(1, t.getDescription());
            ps.setInt(2, t.getIdType());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar tipo: " + t, e);
        }
    }

    @Override
    public void eliminar(Integer id) {
        try (PreparedStatement ps = getConn().prepareStatement(SQL_ELIMINAR)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar tipo con id=" + id, e);
        }
    }

    @Override
    public Type buscarPorDescripcion(String descripcion) {
        try (PreparedStatement ps = getConn().prepareStatement(SQL_POR_DESCRIPCION)) {
            ps.setString(1, descripcion);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar tipo por descripción: " + descripcion, e);
        }
        return null;
    }

    private Type mapear(ResultSet rs) throws SQLException {
        Type t = new Type();
        t.setIdType(rs.getInt("id_type"));
        t.setDescription(rs.getString("description"));
        return t;
    }

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }
}
