/*
 * MusicDB - Base de datos musical con interfaz gráfica JavaFX
 * Copyright (C) 2024  GNU GPL v3
 */
package mx.unam.musicdb.model;

/**
 * Representa un grupo o banda.
 * Se relaciona con Performer mediante id_performer.
 */
public class Group {

    private int idGroup;
    private String name;
    private String startDate;
    private String endDate;

    public Group() {}

    public Group(String name, String startDate, String endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getIdGroup() { return idGroup; }
    public void setIdGroup(int idGroup) { this.idGroup = idGroup; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String d) { this.startDate = d; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String d) { this.endDate = d; }

    @Override
    public String toString() {
        return String.format("Group{id=%d, name='%s'}",
                             idGroup, name);
    }
}
