/*
 * MusicDB - Base de datos musical con interfaz gráfica JavaFX
 * Copyright (C) 2024  GNU GPL v3
 */
package mx.unam.musicdb.dao;

import mx.unam.musicdb.model.Rola;
import mx.unam.musicdb.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación de RolaDAO usando SQLite a través de JDBC.
 */
public class RolaDAOImpl implements RolaDAO {

    private static final String SQL_INSERTAR =
        "INSERT INTO rolas (id_performer, id_album, path, title, track, year, genre) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_BUSCAR_POR_ID =
        "SELECT * FROM rolas WHERE id_rola = ?";

    private static final String SQL_BUSCAR_TODOS =
        "SELECT * FROM rolas ORDER BY id_performer, id_album, track";

    private static final String SQL_ACTUALIZAR =
        "UPDATE rolas SET id_performer=?, id_album=?, path=?, title=?, " +
        "track=?, year=?, genre=? WHERE id_rola=?";

    private static final String SQL_ELIMINAR =
        "DELETE FROM rolas WHERE id_rola = ?";

    private static final String SQL_POR_TITULO =
        "SELECT * FROM rolas WHERE title LIKE ? ORDER BY title";

    private static final String SQL_POR_PERFORMER =
        "SELECT * FROM rolas WHERE id_performer = ? ORDER BY id_album, track";

    private static final String SQL_POR_ALBUM =
        "SELECT * FROM rolas WHERE id_album = ? ORDER BY track";

    private static final String SQL_POR_GENERO =
        "SELECT * FROM rolas WHERE genre = ? ORDER BY title";

    private static final String SQL_POR_ANIO =
        "SELECT * FROM rolas WHERE year = ? ORDER BY title";

    // --- CRUD ---

    @Override
    public void insertar(Rola r) {
        try (PreparedStatement ps = getConn().prepareStatement(
                SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt   (1, r.getIdPerformer());
            ps.setInt   (2, r.getIdAlbum());
            ps.setString(3, r.getPath());
            ps.setString(4, r.getTitle());
            ps.setInt   (5, r.getTrack());
            ps.setInt   (6, r.getYear());
            ps.setString(7, r.getGenre());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) r.setIdRola(keys.getInt(1));
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar rola: " + r, e);
        }
    }

    @Override
    public Optional<Rola> buscarPorId(Integer id) {
        try (PreparedStatement ps = getConn().prepareStatement(SQL_BUSCAR_POR_ID)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar rola con id=" + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Rola> buscarTodos() {
        List<Rola> lista = new ArrayList<>();
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(SQL_BUSCAR_TODOS)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener todas las rolas", e);
        }
        return lista;
    }

    @Override
    public void actualizar(Rola r) {
        try (PreparedStatement ps = getConn().prepareStatement(SQL_ACTUALIZAR)) {
            ps.setInt   (1, r.getIdPerformer());
            ps.setInt   (2, r.getIdAlbum());
            ps.setString(3, r.getPath());
            ps.setString(4, r.getTitle());
            ps.setInt   (5, r.getTrack());
            ps.setInt   (6, r.getYear());
            ps.setString(7, r.getGenre());
            ps.setInt   (8, r.getIdRola());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar rola: " + r, e);
        }
    }

    @Override
    public void eliminar(Integer id) {
        try (PreparedStatement ps = getConn().prepareStatement(SQL_ELIMINAR)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar rola con id=" + id, e);
        }
    }

    // --- Búsquedas específicas ---

    @Override
    public List<Rola> buscarPorTitulo(String titulo) {
        List<Rola> lista = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(SQL_POR_TITULO)) {
            ps.setString(1, "%" + titulo + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar por título: " + titulo, e);
        }
        return lista;
    }

    @Override
    public List<Rola> buscarPorPerformer(int idPerformer) {
        return buscarPorCampoInt(SQL_POR_PERFORMER, idPerformer);
    }

    @Override
    public List<Rola> buscarPorAlbum(int idAlbum) {
        return buscarPorCampoInt(SQL_POR_ALBUM, idAlbum);
    }

    @Override
    public List<Rola> buscarPorGenero(String genero) {
        List<Rola> lista = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(SQL_POR_GENERO)) {
            ps.setString(1, genero);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar por género: " + genero, e);
        }
        return lista;
    }

    @Override
    public List<Rola> buscarPorAnio(int anio) {
        return buscarPorCampoInt(SQL_POR_ANIO, anio);
    }

    /**
     * Búsqueda dinámica — construye la query según los campos no nulos.
     * Pendiente de implementación completa en Fase 4.
     */
    @Override
    public List<Rola> buscarPorCampos(String titulo, String genero,
                                       Integer anio, Integer idPerformer,
                                       Integer idAlbum) {
        StringBuilder sql = new StringBuilder("SELECT * FROM rolas WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (titulo != null && !titulo.isBlank()) {
            sql.append(" AND title LIKE ?");
            params.add("%" + titulo + "%");
        }
        if (genero != null && !genero.isBlank()) {
            sql.append(" AND genre = ?");
            params.add(genero);
        }
        if (anio != null) {
            sql.append(" AND year = ?");
            params.add(anio);
        }
        if (idPerformer != null) {
            sql.append(" AND id_performer = ?");
            params.add(idPerformer);
        }
        if (idAlbum != null) {
            sql.append(" AND id_album = ?");
            params.add(idAlbum);
        }
        sql.append(" ORDER BY title");

        List<Rola> lista = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++)
                ps.setObject(i + 1, params.get(i));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error en búsqueda dinámica", e);
        }
        return lista;
    }

    // --- Helpers privados ---

    private Rola mapear(ResultSet rs) throws SQLException {
        Rola r = new Rola();
        r.setIdRola      (rs.getInt   ("id_rola"));
        r.setIdPerformer (rs.getInt   ("id_performer"));
        r.setIdAlbum     (rs.getInt   ("id_album"));
        r.setPath        (rs.getString("path"));
        r.setTitle       (rs.getString("title"));
        r.setTrack       (rs.getInt   ("track"));
        r.setYear        (rs.getInt   ("year"));
        r.setGenre       (rs.getString("genre"));
        return r;
    }

    private List<Rola> buscarPorCampoInt(String sql, int valor) {
        List<Rola> lista = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, valor);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error en búsqueda con valor=" + valor, e);
        }
        return lista;
    }

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }
}
