package mx.unam.musicdb.dao;

import mx.unam.musicdb.model.Performer;

import java.util.List;

public interface PerformerDAO extends DAO<Performer, Integer> {

    Performer buscarPorNombreExacto(String nombre);

    List<Performer> buscarPorNombre(String nombre);
}
