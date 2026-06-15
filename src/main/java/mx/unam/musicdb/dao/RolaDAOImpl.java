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

    private static final String COLS =
        "r.id_rola, r.id_performer, r.id_album, r.path, r.title, r.track, r.year, r.genre, " +
        "p.name AS performer_name, a.name AS album_name";

    private static final String FROM =
        " FROM rolas r JOIN performers p ON r.id_performer = p.id_performer " +
        "JOIN albums a ON r.id_album = a.id_album";

    private static final String SQL_INSERTAR =
        "INSERT INTO rolas (id_performer, id_album, path, title, track, year, genre) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_BUSCAR_POR_ID =
        "SELECT " + COLS + FROM + " WHERE r.id_rola = ?";

    private static final String SQL_BUSCAR_POR_PATH =
        "SELECT " + COLS + FROM + " WHERE r.path = ?";

    private static final String SQL_BUSCAR_TODOS =
        "SELECT " + COLS + FROM + " ORDER BY p.name, a.name, r.track";

    private static final String SQL_ACTUALIZAR =
        "UPDATE rolas SET id_performer=?, id_album=?, path=?, title=?, " +
        "track=?, year=?, genre=? WHERE id_rola=?";

    private static final String SQL_ELIMINAR =
        "DELETE FROM rolas WHERE id_rola = ?";

    private static final String SQL_POR_TITULO =
        "SELECT " + COLS + FROM + " WHERE r.title LIKE ? ORDER BY r.title";

    private static final String SQL_POR_PERFORMER =
        "SELECT " + COLS + FROM + " WHERE r.id_performer = ? ORDER BY a.name, r.track";

    private static final String SQL_POR_ALBUM =
        "SELECT " + COLS + FROM + " WHERE r.id_album = ? ORDER BY r.track";

    private static final String SQL_POR_GENERO =
        "SELECT " + COLS + FROM + " WHERE r.genre = ? ORDER BY r.title";

    private static final String SQL_POR_ANIO =
        "SELECT " + COLS + FROM + " WHERE r.year = ? ORDER BY r.title";

    private static final String SQL_BUSCAR_PERSONALIZADO =
        "SELECT " + COLS + FROM + " WHERE ";

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
    public Rola buscarPorPath(String path) {
        try (PreparedStatement ps = getConn().prepareStatement(SQL_BUSCAR_POR_PATH)) {
            ps.setString(1, path);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar rola por path: " + path, e);
        }
        return null;
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
        StringBuilder sql = new StringBuilder("1=1");
        List<Object> params = new ArrayList<>();

        if (titulo != null && !titulo.isBlank()) {
            sql.append(" AND r.title LIKE ?");
            params.add("%" + titulo + "%");
        }
        if (genero != null && !genero.isBlank()) {
            sql.append(" AND r.genre = ?");
            params.add(genero);
        }
        if (anio != null) {
            sql.append(" AND r.year = ?");
            params.add(anio);
        }
        if (idPerformer != null) {
            sql.append(" AND r.id_performer = ?");
            params.add(idPerformer);
        }
        if (idAlbum != null) {
            sql.append(" AND r.id_album = ?");
            params.add(idAlbum);
        }
        return buscarPersonalizado(sql.toString(), params);
    }

    // --- Helpers privados ---

    private Rola mapear(ResultSet rs) throws SQLException {
        Rola r = new Rola();
        r.setIdRola(rs.getInt("id_rola"));
        r.setIdPerformer(rs.getInt("id_performer"));
        r.setIdAlbum(rs.getInt("id_album"));
        r.setPath(rs.getString("path"));
        r.setTitle(rs.getString("title"));
        r.setTrack(rs.getInt("track"));
        r.setYear(rs.getInt("year"));
        r.setGenre(rs.getString("genre"));
        r.setPerformerName(rs.getString("performer_name"));
        r.setAlbumName(rs.getString("album_name"));
        return r;
    }

    @Override
    public List<Rola> buscarPersonalizado(String whereClause, List<Object> params) {
        String sql = SQL_BUSCAR_PERSONALIZADO + whereClause + " ORDER BY r.title";
        List<Rola> lista = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++)
                ps.setObject(i + 1, params.get(i));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error en búsqueda personalizada", e);
        }
        return lista;
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
