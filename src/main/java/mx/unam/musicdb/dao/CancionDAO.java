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

import java.util.List;

/**
 * DAO específico para la entidad Cancion.
 * Extiende el DAO genérico y agrega búsquedas propias del dominio musical.
 */
public interface CancionDAO extends DAO<Cancion, Integer> {

    List<Cancion> buscarPorArtista(String artista);

    List<Cancion> buscarPorAlbum(String album);

    List<Cancion> buscarPorGenero(String genero);

    List<Cancion> buscarPorAnio(int anio);

    /**
     * Búsqueda flexible: recibe cualquier combinación de campos
     * y genera la query SQL correspondiente.
     * Se implementará en la Fase 4 (compilador de búsquedas).
     */
    List<Cancion> buscarPorCampos(String titulo, String artista,
                                  String album, String genero, Integer anio);
}
