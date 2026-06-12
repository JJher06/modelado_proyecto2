/*
 * MusicDB - Base de datos musical con interfaz gráfica JavaFX
 * Copyright (C) 2024
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package mx.unam.musicdb.miner;

import mx.unam.musicdb.dao.CancionDAO;
import mx.unam.musicdb.model.Cancion;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Minero de archivos MP3.
 * Recorre un directorio recursivamente, lee las etiquetas ID3v2.4
 * de cada archivo MP3 encontrado y los persiste en la base de datos
 * a través del CancionDAO.
 */
public class Minero {

    private static final Logger LOG = Logger.getLogger(Minero.class.getName());

    private final CancionDAO cancionDAO;

    public Minero(CancionDAO cancionDAO) {
        this.cancionDAO = cancionDAO;
    }

    /**
     * Punto de entrada del minero.
     * @param rutaDirectorio Directorio raíz a recorrer
     * @return Lista de canciones procesadas exitosamente
     */
    public List<Cancion> minar(String rutaDirectorio) {
        List<Cancion> procesadas = new ArrayList<>();
        Path inicio = Paths.get(rutaDirectorio);

        if (!Files.exists(inicio) || !Files.isDirectory(inicio)) {
            LOG.warning("Directorio no encontrado: " + rutaDirectorio);
            return procesadas;
        }

        try {
            Files.walkFileTree(inicio, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path archivo,
                                                  BasicFileAttributes attrs) {
                    if (archivo.toString().toLowerCase().endsWith(".mp3")) {
                        Cancion c = procesarArchivo(archivo.toFile());
                        if (c != null) {
                            cancionDAO.insertar(c);
                            procesadas.add(c);
                            LOG.info("Procesado: " + archivo.getFileName());
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path archivo,
                                                        java.io.IOException e) {
                    LOG.warning("No se pudo leer: " + archivo + " — " + e.getMessage());
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (java.io.IOException e) {
            throw new RuntimeException("Error al recorrer el directorio: " + rutaDirectorio, e);
        }

        LOG.info("Minado completado. Canciones procesadas: " + procesadas.size());
        return procesadas;
    }

    /**
     * Lee las etiquetas ID3 de un archivo MP3 y construye un objeto Cancion.
     * @return Cancion con los metadatos, o null si ocurrió un error
     */
    private Cancion procesarArchivo(File archivo) {
        try {
            AudioFile audioFile = AudioFileIO.read(archivo);
            Tag tag = audioFile.getTag();

            if (tag == null) {
                LOG.warning("Sin etiquetas ID3: " + archivo.getName());
                return null;
            }

            String titulo   = leerCampo(tag, FieldKey.TITLE,  archivo.getName());
            String artista  = leerCampo(tag, FieldKey.ARTIST, "Desconocido");
            String album    = leerCampo(tag, FieldKey.ALBUM,  "Sin álbum");
            String genero   = leerCampo(tag, FieldKey.GENRE,  "Sin género");
            String anioStr  = leerCampo(tag, FieldKey.YEAR,   "0");
            int anio        = parsearEntero(anioStr, 0);
            int duracion    = audioFile.getAudioHeader().getTrackLength();

            return new Cancion(titulo, artista, album, anio, genero,
                               duracion, archivo.getAbsolutePath());

        } catch (Exception e) {
            LOG.warning("Error procesando archivo " + archivo.getName() + ": " + e.getMessage());
            return null;
        }
    }

    private String leerCampo(Tag tag, FieldKey campo, String porDefecto) {
        try {
            String valor = tag.getFirst(campo);
            return (valor != null && !valor.isBlank()) ? valor : porDefecto;
        } catch (Exception e) {
            return porDefecto;
        }
    }

    private int parsearEntero(String texto, int porDefecto) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            return porDefecto;
        }
    }
}
