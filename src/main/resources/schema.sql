-- schema.sql
-- Esquema provisional de MusicDB
-- NOTA: Este archivo se reemplazará con el esquema oficial del profesor.
--
-- Ejecutar manualmente o al iniciar la aplicación para crear las tablas.

PRAGMA foreign_keys = ON;

-- Tabla principal de canciones
CREATE TABLE IF NOT EXISTS canciones (
    id                INTEGER PRIMARY KEY AUTOINCREMENT,
    titulo            TEXT    NOT NULL,
    artista           TEXT    NOT NULL DEFAULT 'Desconocido',
    album             TEXT             DEFAULT 'Sin álbum',
    anio              INTEGER,
    genero            TEXT,
    duracion_segundos INTEGER,
    ruta_archivo      TEXT    NOT NULL UNIQUE
);

-- Índices para acelerar búsquedas frecuentes
CREATE INDEX IF NOT EXISTS idx_artista ON canciones(artista);
CREATE INDEX IF NOT EXISTS idx_album   ON canciones(album);
CREATE INDEX IF NOT EXISTS idx_genero  ON canciones(genero);
