package mx.unam.musicdb.dao;

import mx.unam.musicdb.model.Performer;
import mx.unam.musicdb.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PerformerDAOImpl implements PerformerDAO {

    private static final String SQL_INSERTAR =
        "INSERT INTO performers (id_type, name) VALUES (?, ?)";

    private static final String SQL_BUSCAR_POR_ID =
        "SELECT * FROM performers WHERE id_performer = ?";

    private static final String SQL_BUSCAR_TODOS =
        "SELECT * FROM performers ORDER BY name";

    private static final String SQL_ACTUALIZAR =
        "UPDATE performers SET id_type=?, name=? WHERE id_performer=?";

    private static final String SQL_ELIMINAR =
        "DELETE FROM performers WHERE id_performer = ?";

    private static final String SQL_POR_NOMBRE_EXACTO =
        "SELECT * FROM performers WHERE name = ?";

    private static final String SQL_POR_NOMBRE =
        "SELECT * FROM performers WHERE name LIKE ? ORDER BY name";

    @Override
    public void insertar(Performer p) {
        try (PreparedStatement ps = getConn().prepareStatement(
                SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getIdType());
            ps.setString(2, p.getName());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) p.setIdPerformer(keys.getInt(1));
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar performer: " + p, e);
        }
    }

    @Override
    public Optional<Performer> buscarPorId(Integer id) {
        try (PreparedStatement ps = getConn().prepareStatement(SQL_BUSCAR_POR_ID)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar performer con id=" + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Performer> buscarTodos() {
        List<Performer> lista = new ArrayList<>();
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(SQL_BUSCAR_TODOS)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener todos los performers", e);
        }
        return lista;
    }

    @Override
    public void actualizar(Performer p) {
        try (PreparedStatement ps = getConn().prepareStatement(SQL_ACTUALIZAR)) {
            ps.setInt(1, p.getIdType());
            ps.setString(2, p.getName());
            ps.setInt(3, p.getIdPerformer());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar performer: " + p, e);
        }
    }

    @Override
    public void eliminar(Integer id) {
        try (PreparedStatement ps = getConn().prepareStatement(SQL_ELIMINAR)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar performer con id=" + id, e);
        }
    }

    @Override
    public Performer buscarPorNombreExacto(String nombre) {
        try (PreparedStatement ps = getConn().prepareStatement(SQL_POR_NOMBRE_EXACTO)) {
            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar performer por nombre: " + nombre, e);
        }
        return null;
    }

    @Override
    public List<Performer> buscarPorNombre(String nombre) {
        List<Performer> lista = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(SQL_POR_NOMBRE)) {
            ps.setString(1, "%" + nombre + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar performer por nombre: " + nombre, e);
        }
        return lista;
    }

    private Performer mapear(ResultSet rs) throws SQLException {
        Performer p = new Performer();
        p.setIdPerformer(rs.getInt("id_performer"));
        p.setIdType(rs.getInt("id_type"));
        p.setName(rs.getString("name"));
        return p;
    }

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }
}
