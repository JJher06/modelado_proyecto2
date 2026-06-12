/*
 * MusicDB - Base de datos musical con interfaz gráfica JavaFX
 * Copyright (C) 2024  GNU GPL v3
 */
package mx.unam.musicdb.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.stream.Collectors;

/**
 * Gestiona la conexión a la base de datos SQLite.
 * Patrón Singleton — una sola conexión para toda la aplicación.
 * Inicializa el esquema automáticamente si la BD no existe todavía.
 */
public class DatabaseConnection {

    private static final String DB_PATH = System.getenv("DB_PATH") != null
            ? System.getenv("DB_PATH")
            : "musicdb.db";    private static final String URL     = "jdbc:sqlite:" + DB_PATH;

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() throws SQLException {
        this.connection = DriverManager.getConnection(URL);
        this.connection.createStatement().execute("PRAGMA foreign_keys = ON;");
        inicializarEsquema();
    }

    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null || instance.getConnection().isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void cerrar() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    /**
     * Lee schema.sql del classpath y lo ejecuta si las tablas no existen.
     * Esto evita tener que crear la BD manualmente.
     */
    private void inicializarEsquema() {
        try {
            // Verificar si las tablas ya existen
            var rs = connection.getMetaData().getTables(null, null, "rolas", null);
            if (rs.next()) return; // Ya existe, no hacer nada

            // Leer schema.sql del classpath
            var stream = getClass().getResourceAsStream("/schema.sql");
            if (stream == null) {
                System.err.println("ADVERTENCIA: No se encontró schema.sql en el classpath");
                return;
            }

            String sql = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

            // Ejecutar cada sentencia separada por ;
            for (String sentencia : sql.split(";")) {
                String s = sentencia.trim();
                if (!s.isEmpty()) {
                    connection.createStatement().execute(s);
                }
            }

            System.out.println("Base de datos inicializada correctamente.");

        } catch (Exception e) {
            throw new RuntimeException("Error al inicializar el esquema de la BD", e);
        }
    }
}
