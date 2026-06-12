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

import mx.unam.musicdb.dao.RolaDAO;
import mx.unam.musicdb.model.Rola;
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

    private final RolaDAO rolaDAO;
    public Minero(RolaDAO rolaDAO) {
        this.rolaDAO = rolaDAO;
    }

    /**
     * Punto de entrada del minero.
     * @param rutaDirectorio Directorio raíz a recorrer
     * @return Lista de canciones procesadas exitosamente
     */
    public List<Rola> minar(String rutaDirectorio) {
        List<Rola> procesadas = new ArrayList<>();
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
                        Rola c = procesarArchivo(archivo.toFile());
                        if (c != null) {
                            rolaDAO.insertar(c);
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
    private Rola procesarArchivo(File archivo) {
        try {
            AudioFile audioFile = AudioFileIO.read(archivo);
            Tag tag = audioFile.getTag();

            if (tag == null) {
                LOG.warning("Sin etiquetas ID3: " + archivo.getName());
                return null;
            }

            String title  = leerCampo(tag, FieldKey.TITLE,  archivo.getName());
            String genre  = leerCampo(tag, FieldKey.GENRE,  "");
            String anioStr = leerCampo(tag, FieldKey.YEAR,  "0");
            String trackStr = leerCampo(tag, FieldKey.TRACK, "0");
            int year  = parsearEntero(anioStr, 0);
            int track = parsearEntero(trackStr, 0);

            // idPerformer e idAlbum se resolverán en la Fase 2
            // cuando el minero busque o cree el performer y album correspondiente
            return new Rola(0, 0, archivo.getAbsolutePath(), title, track, year, genre);

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
