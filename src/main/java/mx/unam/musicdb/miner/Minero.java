/*
 * MusicDB - Base de datos musical con interfaz gráfica JavaFX
 * Copyright (C) 2024  GNU GPL v3
 */
package mx.unam.musicdb.miner;

import mx.unam.musicdb.dao.*;
import mx.unam.musicdb.model.*;
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
 *
 * Recorre un directorio recursivamente, lee etiquetas ID3v2.4
 * de cada MP3 encontrado y puebla la base de datos.
 *
 * Políticas:
 * - Performer: siempre se inserta como Unknown (id_type=2)
 * - Album:     se busca por nombre; si existe se reutiliza, si no se inserta
 * - Rola:      se busca por path; si existe se ignora, si no se inserta
 *
 * Fallbacks para etiquetas vacías:
 * - title     → nombre del archivo sin extensión
 * - performer → "Unknown"
 * - album     → nombre de la carpeta contenedora
 * - year      → 0
 * - track     → 0
 * - genre     → ""
 */
public class Minero {

    private static final Logger LOG = Logger.getLogger(Minero.class.getName());

    // id_type=2 corresponde a Unknown en la tabla types
    private static final int TIPO_UNKNOWN = 2;

    private final PerformerDAO performerDAO;
    private final AlbumDAO     albumDAO;
    private final RolaDAO      rolaDAO;

    public Minero(PerformerDAO performerDAO, AlbumDAO albumDAO, RolaDAO rolaDAO) {
        this.performerDAO = performerDAO;
        this.albumDAO     = albumDAO;
        this.rolaDAO      = rolaDAO;
    }

    /**
     * Punto de entrada del minero.
     * @param rutaDirectorio Directorio raíz a recorrer
     * @return Lista de rolas insertadas exitosamente
     */
    public List<Rola> minar(String rutaDirectorio) {
        List<Rola> insertadas = new ArrayList<>();
        Path inicio = Paths.get(rutaDirectorio);

        if (!Files.exists(inicio) || !Files.isDirectory(inicio)) {
            LOG.warning("Directorio no encontrado: " + rutaDirectorio);
            return insertadas;
        }

        try {
            Files.walkFileTree(inicio, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path archivo,
                                                 BasicFileAttributes attrs) {
                    if (archivo.toString().toLowerCase().endsWith(".mp3")) {
                        Rola rola = procesarArchivo(archivo.toFile());
                        if (rola != null) insertadas.add(rola);
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

        LOG.info("Minado completado. Rolas insertadas: " + insertadas.size());
        return insertadas;
    }

    /**
     * Procesa un archivo MP3:
     * 1. Lee etiquetas ID3
     * 2. Resuelve performer
     * 3. Resuelve album
     * 4. Inserta la rola si no existe
     */
    private Rola procesarArchivo(File archivo) {
        try {
            // --- Leer etiquetas ID3 ---
            AudioFile audioFile = AudioFileIO.read(archivo);
            Tag tag = audioFile.getTag();

            String title     = leerCampo(tag, FieldKey.TITLE,  nombreSinExtension(archivo));
            String performer = leerCampo(tag, FieldKey.ARTIST, "Unknown");
            String album     = leerCampo(tag, FieldKey.ALBUM,  nombreCarpeta(archivo));
            String genre     = leerCampo(tag, FieldKey.GENRE,  "");
            int year         = parsearEntero(leerCampo(tag, FieldKey.YEAR,  "0"), 0);
            int track        = parsearEntero(leerCampo(tag, FieldKey.TRACK, "0"), 0);

            // --- Verificar si la rola ya existe ---
            String pathRola = archivo.getAbsolutePath();
            if (rolaDAO.buscarPorPath(pathRola) != null) {
                LOG.info("Rola ya existe, ignorando: " + pathRola);
                return null;
            }

            // --- Resolver performer (siempre Unknown) ---
            int idPerformer = resolverPerformer(performer);

            // --- Resolver album ---
            int idAlbum = resolverAlbum(album, nombreCarpeta(archivo), year);

            // --- Insertar rola ---
            Rola rola = new Rola(idPerformer, idAlbum, pathRola,
                    title, track, year, genre);
            rolaDAO.insertar(rola);
            LOG.info("Rola insertada: " + title);
            return rola;

        } catch (Exception e) {
            LOG.warning("Error procesando: " + archivo.getName() + " — " + e.getMessage());
            return null;
        }
    }

    /**
     * Siempre inserta el performer como Unknown.
     * @return id_performer generado
     */
    private int resolverPerformer(String nombre) {
        Performer existente = performerDAO.buscarPorNombreExacto(nombre);
        if (existente != null) return existente.getIdPerformer();
        Performer p = new Performer(TIPO_UNKNOWN, nombre);
        performerDAO.insertar(p);
        return p.getIdPerformer();
    }

    /**
     * Busca el album por nombre. Si no existe lo inserta.
     * @return id_album existente o recién insertado
     */
    private int resolverAlbum(String nombre, String pathCarpeta, int year) {
        Album existente = albumDAO.buscarPorNombreExacto(nombre);
        if (existente != null) return existente.getIdAlbum();
        Album nuevo = new Album(pathCarpeta, nombre, year);
        albumDAO.insertar(nuevo);
        return nuevo.getIdAlbum();
    }

    // --- Helpers ---

    private String leerCampo(Tag tag, FieldKey campo, String porDefecto) {
        if (tag == null) return porDefecto;
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

    private String nombreSinExtension(File archivo) {
        String nombre = archivo.getName();
        int punto = nombre.lastIndexOf('.');
        return punto > 0 ? nombre.substring(0, punto) : nombre;
    }

    private String nombreCarpeta(File archivo) {
        return archivo.getParentFile().getName();
    }
}