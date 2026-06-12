/*
 * MusicDB - Base de datos musical con interfaz gráfica JavaFX
 * Copyright (C) 2024
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package mx.unam.musicdb.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestiona la conexión a la base de datos SQLite.
 * Implementa el patrón Singleton para que toda la aplicación
 * comparta una única conexión.
 *
 * Uso:
 *   Connection conn = DatabaseConnection.getInstance().getConnection();
 */
public class DatabaseConnection {

    private static final String DB_PATH = "musicdb.db";
    private static final String URL     = "jdbc:sqlite:" + DB_PATH;

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() throws SQLException {
        this.connection = DriverManager.getConnection(URL);
        // Activar foreign keys en SQLite (están desactivadas por defecto)
        this.connection.createStatement().execute("PRAGMA foreign_keys = ON;");
    }

    /**
     * Devuelve la instancia única de DatabaseConnection.
     * Si la conexión se cerró, la reabre automáticamente.
     */
    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null || instance.getConnection().isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    /** Cierra la conexión. Llamar al cerrar la aplicación. */
    public void cerrar() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
