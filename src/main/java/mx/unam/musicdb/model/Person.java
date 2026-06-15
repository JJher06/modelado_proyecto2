/*
 * MusicDB - Base de datos musical con interfaz gráfica JavaFX
 * Copyright (C) 2024  GNU GPL v3
 */
package mx.unam.musicdb.model;

/**
 * Representa un artista individual.
 * Se relaciona con Performer mediante id_performer.
 */
public class Person {

    private int idPerson;
    private String stageName;
    private String realName;
    private String birthDate;
    private String deathDate;

    public Person() {}

    public Person(String stageName, String realName,
                  String birthDate, String deathDate) {
        this.stageName = stageName;
        this.realName = realName;
        this.birthDate = birthDate;
        this.deathDate = deathDate;
    }

    public int getIdPerson() { return idPerson; }
    public void setIdPerson(int idPerson) { this.idPerson = idPerson; }

    public String getStageName() { return stageName; }
    public void setStageName(String stageName) { this.stageName = stageName; }

    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }

    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }

    public String getDeathDate() { return deathDate; }
    public void setDeathDate(String deathDate) { this.deathDate = deathDate; }

    @Override
    public String toString() {
        return String.format("Person{id=%d, stageName='%s', realName='%s'}",
                             idPerson, stageName, realName);
    }
}
