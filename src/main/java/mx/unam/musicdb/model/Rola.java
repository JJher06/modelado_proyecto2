/*
 * MusicDB - Base de datos musical con interfaz gráfica JavaFX
 * Copyright (C) 2024  GNU GPL v3
 */
package mx.unam.musicdb.model;

/**
 * Representa una rola en la base de datos.
 *
 * El campo path apunta al archivo MP3 específico en disco.
 */
public class Rola {

    private int idRola;
    private int idPerformer;
    private int idAlbum;
    private String path;
    private String title;
    private int track;
    private int year;
    private String genre;
    private String performerName;
    private String albumName;

    public Rola() {}

    public Rola(int idPerformer, int idAlbum, String path,
                String title, int track, int year, String genre) {
        this.idPerformer = idPerformer;
        this.idAlbum = idAlbum;
        this.path = path;
        this.title = title;
        this.track = track;
        this.year = year;
        this.genre = genre;
    }

    public int    getIdRola(){ return idRola; }
    public void   setIdRola(int idRola){ this.idRola = idRola; }

    public int    getIdPerformer(){ return idPerformer; }
    public void   setIdPerformer(int id){ this.idPerformer = id; }

    public int    getIdAlbum(){ return idAlbum; }

    public void   setIdAlbum(int id){ this.idAlbum = id; }
    public String getPath(){ return path; }

    public void   setPath(String path){ this.path = path; }
    public String getTitle(){ return title; }

    public void   setTitle(String title){ this.title = title; }
    public int    getTrack(){ return track; }

    public void   setTrack(int track){ this.track = track; }
    public int    getYear(){ return year; }
    public void   setYear(int year){ this.year = year; }
    public String getGenre(){ return genre; }
    public void   setGenre(String genre){ this.genre = genre; }

    public String getPerformerName(){ return performerName; }
    public void   setPerformerName(String s){ this.performerName = s; }

    public String getAlbumName(){ return albumName; }
    public void   setAlbumName(String s){ this.albumName = s; }

    @Override
    public String toString() {
        return String.format("Rola{id=%d, title='%s', performer='%s', album='%s', track=%d, year=%d}",
                             idRola, title, performerName, albumName, track, year);
    }
}
