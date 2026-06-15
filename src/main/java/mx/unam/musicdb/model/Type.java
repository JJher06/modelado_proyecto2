/*
 * MusicDB - Base de datos musical con interfaz gráfica JavaFX
 * Copyright (C) 2024  GNU GPL v3
 */
package mx.unam.musicdb.model;

/**
 * Catálogo de tipos de performer.
 * Valores posibles: Person(0), Group(1), Unknown(2)
 */
public class Type {

    private int idType;
    private String description;

    public Type() {}

    public Type(int idType, String description) {
        this.idType = idType;
        this.description = description;
    }

    public int getIdType() { return idType; }
    public void setIdType(int idType) { this.idType = idType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return description;
    }
}
