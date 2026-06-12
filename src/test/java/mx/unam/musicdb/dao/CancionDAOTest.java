/*
 * MusicDB - Base de datos musical con interfaz gráfica JavaFX
 * Copyright (C) 2024
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package mx.unam.musicdb.dao;

import mx.unam.musicdb.model.Cancion;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para CancionDAOImpl.
 * Usa una base de datos SQLite en memoria para no afectar datos reales.
 *
 * TODO: Configurar conexión a BD en memoria antes de que estos tests corran.
 * Se completará en la Fase 1 cuando el esquema esté definido.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CancionDAOTest {

    private static CancionDAO dao;
    private static Cancion cancionPrueba;

    @BeforeAll
    static void setUp() {
        // TODO: Inicializar BD en memoria con el esquema
        // dao = new CancionDAOImpl();
        cancionPrueba = new Cancion(
            "Bohemian Rhapsody", "Queen", "A Night at the Opera",
            1975, "Rock", 354, "/musica/bohemian.mp3"
        );
    }

    @Test
    @Order(1)
    @Disabled("Pendiente: configurar BD en memoria — Fase 1")
    void testInsertar() {
        dao.insertar(cancionPrueba);
        assertTrue(cancionPrueba.getId() > 0, "El ID debe ser generado por SQLite");
    }

    @Test
    @Order(2)
    @Disabled("Pendiente: configurar BD en memoria — Fase 1")
    void testBuscarPorId() {
        Optional<Cancion> resultado = dao.buscarPorId(cancionPrueba.getId());
        assertTrue(resultado.isPresent());
        assertEquals("Bohemian Rhapsody", resultado.get().getTitulo());
    }

    @Test
    @Order(3)
    @Disabled("Pendiente: configurar BD en memoria — Fase 1")
    void testBuscarPorArtista() {
        List<Cancion> lista = dao.buscarPorArtista("Queen");
        assertFalse(lista.isEmpty());
        assertTrue(lista.stream().allMatch(c -> c.getArtista().equals("Queen")));
    }

    @Test
    @Order(4)
    @Disabled("Pendiente: configurar BD en memoria — Fase 1")
    void testActualizar() {
        cancionPrueba.setGenero("Rock Progresivo");
        dao.actualizar(cancionPrueba);
        Optional<Cancion> actualizada = dao.buscarPorId(cancionPrueba.getId());
        assertEquals("Rock Progresivo", actualizada.get().getGenero());
    }

    @Test
    @Order(5)
    @Disabled("Pendiente: configurar BD en memoria — Fase 1")
    void testEliminar() {
        dao.eliminar(cancionPrueba.getId());
        Optional<Cancion> eliminada = dao.buscarPorId(cancionPrueba.getId());
        assertTrue(eliminada.isEmpty());
    }
}
