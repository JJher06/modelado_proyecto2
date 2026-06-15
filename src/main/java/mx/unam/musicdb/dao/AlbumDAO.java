package mx.unam.musicdb.dao;

import mx.unam.musicdb.model.Album;

import java.util.List;

public interface AlbumDAO extends DAO<Album, Integer> {

    Album buscarPorNombreExacto(String nombre);

    List<Album> buscarPorNombre(String nombre);
}
