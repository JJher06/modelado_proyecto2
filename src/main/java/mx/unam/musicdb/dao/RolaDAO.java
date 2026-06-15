/*
 * MusicDB - Base de datos musical con interfaz gráfica JavaFX
 * Copyright (C) 2024  GNU GPL v3
 */
package mx.unam.musicdb.dao;

import mx.unam.musicdb.model.Rola;

import java.util.List;
import java.util.Map;

/**
 * DAO específico para la entidad Rola.
 * Extiende el DAO genérico y agrega búsquedas propias del dominio musical.
 */
public interface RolaDAO extends DAO<Rola, Integer> {

    Rola buscarPorPath(String path);

    List<Rola> buscarPorTitulo(String titulo);

    List<Rola> buscarPorPerformer(int idPerformer);

    List<Rola> buscarPorAlbum(int idAlbum);

    List<Rola> buscarPorGenero(String genero);

    List<Rola> buscarPorAnio(int anio);

    /**
     * Búsqueda dinámica multicampo — Fase 4 (compilador de búsquedas).
     * Los parámetros nulos se ignoran en la query generada.
     */
    List<Rola> buscarPorCampos(String titulo, String genero,
                                Integer anio, Integer idPerformer,
                                Integer idAlbum);

    List<Rola> buscarPersonalizado(String whereClause, List<Object> parametros);
}
