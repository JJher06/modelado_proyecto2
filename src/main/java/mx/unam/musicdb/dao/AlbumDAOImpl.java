package mx.unam.musicdb.dao;

import mx.unam.musicdb.model.Album;
import mx.unam.musicdb.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AlbumDAOImpl implements AlbumDAO {

    private static final String SQL_INSERTAR =
        "INSERT INTO albums (path, name, year) VALUES (?, ?, ?)";

    private static final String SQL_BUSCAR_POR_ID =
        "SELECT * FROM albums WHERE id_album = ?";

    private static final String SQL_BUSCAR_TODOS =
        "SELECT * FROM albums ORDER BY name";

    private static final String SQL_ACTUALIZAR =
        "UPDATE albums SET path=?, name=?, year=? WHERE id_album=?";

    private static final String SQL_ELIMINAR =
        "DELETE FROM albums WHERE id_album = ?";

    private static final String SQL_POR_NOMBRE_EXACTO =
        "SELECT * FROM albums WHERE name = ?";

    private static final String SQL_POR_NOMBRE =
        "SELECT * FROM albums WHERE name LIKE ? ORDER BY name";

    @Override
    public void insertar(Album a) {
        try (PreparedStatement ps = getConn().prepareStatement(
                SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, a.getPath());
            ps.setString(2, a.getName());
            ps.setInt(3, a.getYear());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) a.setIdAlbum(keys.getInt(1));
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar album: " + a, e);
        }
    }

    @Override
    public Optional<Album> buscarPorId(Integer id) {
        try (PreparedStatement ps = getConn().prepareStatement(SQL_BUSCAR_POR_ID)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar album con id=" + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Album> buscarTodos() {
        List<Album> lista = new ArrayList<>();
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(SQL_BUSCAR_TODOS)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener todos los albumes", e);
        }
        return lista;
    }

    @Override
    public void actualizar(Album a) {
        try (PreparedStatement ps = getConn().prepareStatement(SQL_ACTUALIZAR)) {
            ps.setString(1, a.getPath());
            ps.setString(2, a.getName());
            ps.setInt(3, a.getYear());
            ps.setInt(4, a.getIdAlbum());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar album: " + a, e);
        }
    }

    @Override
    public void eliminar(Integer id) {
        try (PreparedStatement ps = getConn().prepareStatement(SQL_ELIMINAR)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar album con id=" + id, e);
        }
    }

    @Override
    public Album buscarPorNombreExacto(String nombre) {
        try (PreparedStatement ps = getConn().prepareStatement(SQL_POR_NOMBRE_EXACTO)) {
            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar album por nombre: " + nombre, e);
        }
        return null;
    }

    @Override
    public List<Album> buscarPorNombre(String nombre) {
        List<Album> lista = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(SQL_POR_NOMBRE)) {
            ps.setString(1, "%" + nombre + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar album por nombre: " + nombre, e);
        }
        return lista;
    }

    private Album mapear(ResultSet rs) throws SQLException {
        Album a = new Album();
        a.setIdAlbum(rs.getInt("id_album"));
        a.setPath(rs.getString("path"));
        a.setName(rs.getString("name"));
        a.setYear(rs.getInt("year"));
        return a;
    }

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }
}
