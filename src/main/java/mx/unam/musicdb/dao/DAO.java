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

import java.util.List;
import java.util.Optional;

/**
 * Interfaz genérica DAO (Data Access Object).
 * Define las operaciones CRUD básicas para cualquier entidad.
 *
 * @param <T>  Tipo de la entidad (ej. Cancion, Album)
 * @param <ID> Tipo del identificador (generalmente Integer)
 */
public interface DAO<T, ID> {

    /** Inserta una nueva entidad en la base de datos. */
    void insertar(T entidad);

    /** Busca una entidad por su ID. Devuelve Optional para manejar el caso "no encontrado". */
    Optional<T> buscarPorId(ID id);

    /** Devuelve todas las entidades de esta tabla. */
    List<T> buscarTodos();

    /** Actualiza los datos de una entidad existente. */
    void actualizar(T entidad);

    /** Elimina una entidad por su ID. */
    void eliminar(ID id);
}
