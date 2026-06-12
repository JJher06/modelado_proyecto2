/*
 * MusicDB - Base de datos musical con interfaz gráfica JavaFX
 * Copyright (C) 2024
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package mx.unam.musicdb.dao;

import mx.unam.musicdb.model.Cancion;
import mx.unam.musicdb.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación de CancionDAO usando SQLite a través de JDBC.
 *
 * NOTA: El esquema de tabla es provisional y se actualizará
 * cuando el profesor entregue el esquema oficial del proyecto.
 */
public class CancionDAOImpl implements CancionDAO {

    // ---------------------------------------------------------------
    // Queries SQL centralizadas — si cambia la tabla, solo se edita aquí
    // ---------------------------------------------------------------
    private static final String SQL_INSERTAR =
        "INSERT INTO canciones (titulo, artista, album, anio, genero, duracion_segundos, ruta_archivo) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_BUSCAR_POR_ID =
        "SELECT * FROM canciones WHERE id = ?";

    private static final String SQL_BUSCAR_TODOS =
        "SELECT * FROM canciones ORDER BY artista, album, titulo";

    private static final String SQL_ACTUALIZAR =
        "UPDATE canciones SET titulo=?, artista=?, album=?, anio=?, genero=?, " +
        "duracion_segundos=?, ruta_archivo=? WHERE id=?";

    private static final String SQL_ELIMINAR =
        "DELETE FROM canciones WHERE id = ?";

    private static final String SQL_POR_ARTISTA =
        "SELECT * FROM canciones WHERE artista = ? ORDER BY album, titulo";

    private static final String SQL_POR_ALBUM =
        "SELECT * FROM canciones WHERE album = ? ORDER BY titulo";

    private static final String SQL_POR_GENERO =
        "SELECT * FROM canciones WHERE genero = ? ORDER BY artista, titulo";

    private static final String SQL_POR_ANIO =
        "SELECT * FROM canciones WHERE anio = ? ORDER BY artista, titulo";

    // ---------------------------------------------------------------
    // CRUD
    // ---------------------------------------------------------------

    @Override
    public void insertar(Cancion c) {
        try (PreparedStatement ps = getConn().prepareStatement(SQL_INSERTAR,
                                    Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getTitulo());
            ps.setString(2, c.getArtista());
            ps.setString(3, c.getAlbum());
            ps.setInt   (4, c.getAnio());
            ps.setString(5, c.getGenero());
            ps.setInt   (6, c.getDuracionSegundos());
            ps.setString(7, c.getRutaArchivo());
            ps.executeUpdate();

            // Recuperar el ID generado por SQLite
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) c.setId(keys.getInt(1));

        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar canción: " + c, e);
        }
    }

    @Override
    public Optional<Cancion> buscarPorId(Integer id) {
        try (PreparedStatement ps = getConn().prepareStatement(SQL_BUSCAR_POR_ID)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar canción con id=" + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Cancion> buscarTodos() {
        List<Cancion> lista = new ArrayList<>();
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(SQL_BUSCAR_TODOS)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener todas las canciones", e);
        }
        return lista;
    }

    @Override
    public void actualizar(Cancion c) {
        try (PreparedStatement ps = getConn().prepareStatement(SQL_ACTUALIZAR)) {
            ps.setString(1, c.getTitulo());
            ps.setString(2, c.getArtista());
            ps.setString(3, c.getAlbum());
            ps.setInt   (4, c.getAnio());
            ps.setString(5, c.getGenero());
            ps.setInt   (6, c.getDuracionSegundos());
            ps.setString(7, c.getRutaArchivo());
            ps.setInt   (8, c.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar canción: " + c, e);
        }
    }

    @Override
    public void eliminar(Integer id) {
        try (PreparedStatement ps = getConn().prepareStatement(SQL_ELIMINAR)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar canción con id=" + id, e);
        }
    }

    // ---------------------------------------------------------------
    // Búsquedas específicas
    // ---------------------------------------------------------------

    @Override
    public List<Cancion> buscarPorArtista(String artista) {
        return buscarPorCampoUnico(SQL_POR_ARTISTA, artista);
    }

    @Override
    public List<Cancion> buscarPorAlbum(String album) {
        return buscarPorCampoUnico(SQL_POR_ALBUM, album);
    }

    @Override
    public List<Cancion> buscarPorGenero(String genero) {
        return buscarPorCampoUnico(SQL_POR_GENERO, genero);
    }

    @Override
    public List<Cancion> buscarPorAnio(int anio) {
        List<Cancion> lista = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(SQL_POR_ANIO)) {
            ps.setInt(1, anio);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar por año: " + anio, e);
        }
        return lista;
    }

    /**
     * Búsqueda dinámica multiples campos (Fase 4 — compilador de búsquedas).
     * Construye la query dinámicamente según los campos no nulos.
     */
    @Override
    public List<Cancion> buscarPorCampos(String titulo, String artista,
                                          String album, String genero,
                                          Integer anio) {
        // Se implementará en la Fase 4
        throw new UnsupportedOperationException("Pendiente — Fase 4: compilador de búsquedas");
    }

    // ---------------------------------------------------------------
    // Helpers privados
    // ---------------------------------------------------------------

    /** Convierte una fila del ResultSet en un objeto Cancion. */
    private Cancion mapear(ResultSet rs) throws SQLException {
        Cancion c = new Cancion();
        c.setId              (rs.getInt   ("id"));
        c.setTitulo          (rs.getString("titulo"));
        c.setArtista         (rs.getString("artista"));
        c.setAlbum           (rs.getString("album"));
        c.setAnio            (rs.getInt   ("anio"));
        c.setGenero          (rs.getString("genero"));
        c.setDuracionSegundos(rs.getInt   ("duracion_segundos"));
        c.setRutaArchivo     (rs.getString("ruta_archivo"));
        return c;
    }

    private List<Cancion> buscarPorCampoUnico(String sql, String valor) {
        List<Cancion> lista = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, valor);
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
