/*
 * MusicDB - Base de datos musical con interfaz gráfica JavaFX
 * Copyright (C) 2024
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package mx.unam.musicdb.model;

/**
 * Modelo que representa una canción en la base de datos.
 * Este es un esquema provisional — se actualizará cuando
 * el profesor entregue el esquema oficial.
 */
public class Cancion {

    private int    id;
    private String titulo;
    private String artista;
    private String album;
    private int    anio;
    private String genero;
    private int    duracionSegundos;
    private String rutaArchivo;

    public Cancion() {}

    public Cancion(String titulo, String artista, String album,
                   int anio, String genero, int duracionSegundos,
                   String rutaArchivo) {
        this.titulo           = titulo;
        this.artista          = artista;
        this.album            = album;
        this.anio             = anio;
        this.genero           = genero;
        this.duracionSegundos = duracionSegundos;
        this.rutaArchivo      = rutaArchivo;
    }

    // --- Getters y Setters ---

    public int getId()                     { return id; }
    public void setId(int id)              { this.id = id; }

    public String getTitulo()              { return titulo; }
    public void setTitulo(String titulo)   { this.titulo = titulo; }

    public String getArtista()             { return artista; }
    public void setArtista(String artista) { this.artista = artista; }

    public String getAlbum()               { return album; }
    public void setAlbum(String album)     { this.album = album; }

    public int getAnio()                   { return anio; }
    public void setAnio(int anio)          { this.anio = anio; }

    public String getGenero()              { return genero; }
    public void setGenero(String genero)   { this.genero = genero; }

    public int getDuracionSegundos()       { return duracionSegundos; }
    public void setDuracionSegundos(int d) { this.duracionSegundos = d; }

    public String getRutaArchivo()         { return rutaArchivo; }
    public void setRutaArchivo(String r)   { this.rutaArchivo = r; }

    @Override
    public String toString() {
        return String.format("Cancion{id=%d, titulo='%s', artista='%s', album='%s'}",
                             id, titulo, artista, album);
    }
}
