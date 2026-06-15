package mx.unam.musicdb.dao;

import mx.unam.musicdb.model.Type;

public interface TypeDAO extends DAO<Type, Integer> {

    Type buscarPorDescripcion(String descripcion);
}
