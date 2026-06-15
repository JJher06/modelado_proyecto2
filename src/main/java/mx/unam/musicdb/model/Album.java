/*
 * MusicDB - Base de datos musical con interfaz gráfica JavaFX
 * Copyright (C) 2024  GNU GPL v3
 */
package mx.unam.musicdb.model;

/**
 * Representa un álbum musical.
 * El campo path apunta a la carpeta del álbum en disco.
 */
public class Album {

    private int idAlbum;
    private String path;
    private String name;
    private int year;

    public Album() {}

    public Album(String path, String name, int year) {
        this.path = path;
        this.name = name;
        this.year = year;
    }

    public int getIdAlbum() { return idAlbum; }
    public void setIdAlbum(int idAlbum)  { this.idAlbum = idAlbum; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    @Override
    public String toString() {
        return String.format("Album{id=%d, name='%s', year=%d}",
                             idAlbum, name, year);
    }
}
