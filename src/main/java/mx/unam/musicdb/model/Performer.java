/*
 * MusicDB - Base de datos musical con interfaz gráfica JavaFX
 * Copyright (C) 2024  GNU GPL v3
 */
package mx.unam.musicdb.model;

/**
 * Representa un autor de alguna cancion.
 * Puede ser una persona, un grupo, o desconocido según su Type.
 */
public class Performer {

    private int idPerformer;
    private int idType;
    private String name;

    public Performer() {}

    public Performer(int idType, String name) {
        this.idType = idType;
        this.name = name;
    }

    public int getIdPerformer() { return idPerformer; }
    public void setIdPerformer(int idPerformer){ this.idPerformer = idPerformer; }

    public int getIdType() { return idType; }
    public void setIdType(int idType) { this.idType = idType; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return String.format("Performer{id=%d, name='%s', type=%d}",
                             idPerformer, name, idType);
    }
}
