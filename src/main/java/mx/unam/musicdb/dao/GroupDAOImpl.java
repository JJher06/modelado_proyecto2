package mx.unam.musicdb.dao;

import mx.unam.musicdb.model.Group;
import mx.unam.musicdb.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GroupDAOImpl implements GroupDAO {

    private static final String SQL_INSERTAR =
        "INSERT INTO groups (name, start_date, end_date) VALUES (?, ?, ?)";

    private static final String SQL_BUSCAR_POR_ID =
        "SELECT * FROM groups WHERE id_group = ?";

    private static final String SQL_BUSCAR_TODOS =
        "SELECT * FROM groups ORDER BY name";

    private static final String SQL_ACTUALIZAR =
        "UPDATE groups SET name=?, start_date=?, end_date=? WHERE id_group=?";

    private static final String SQL_ELIMINAR =
        "DELETE FROM groups WHERE id_group = ?";

    @Override
    public void insertar(Group g) {
        try (PreparedStatement ps = getConn().prepareStatement(
                SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, g.getName());
            ps.setString(2, g.getStartDate());
            ps.setString(3, g.getEndDate());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) g.setIdGroup(keys.getInt(1));
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar grupo: " + g, e);
        }
    }

    @Override
    public Optional<Group> buscarPorId(Integer id) {
        try (PreparedStatement ps = getConn().prepareStatement(SQL_BUSCAR_POR_ID)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar grupo con id=" + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Group> buscarTodos() {
        List<Group> lista = new ArrayList<>();
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(SQL_BUSCAR_TODOS)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener todos los grupos", e);
        }
        return lista;
    }

    @Override
    public void actualizar(Group g) {
        try (PreparedStatement ps = getConn().prepareStatement(SQL_ACTUALIZAR)) {
            ps.setString(1, g.getName());
            ps.setString(2, g.getStartDate());
            ps.setString(3, g.getEndDate());
            ps.setInt(4, g.getIdGroup());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar grupo: " + g, e);
        }
    }

    @Override
    public void eliminar(Integer id) {
        try (PreparedStatement ps = getConn().prepareStatement(SQL_ELIMINAR)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar grupo con id=" + id, e);
        }
    }

    private Group mapear(ResultSet rs) throws SQLException {
        Group g = new Group();
        g.setIdGroup(rs.getInt("id_group"));
        g.setName(rs.getString("name"));
        g.setStartDate(rs.getString("start_date"));
        g.setEndDate(rs.getString("end_date"));
        return g;
    }

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }
}
