/*
 * MusicDB - Base de datos musical con interfaz gráfica JavaFX
 * Copyright (C) 2024
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package mx.unam.musicdb.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import mx.unam.musicdb.dao.CancionDAO;
import mx.unam.musicdb.dao.CancionDAOImpl;
import mx.unam.musicdb.model.Cancion;

import java.util.List;

/**
 * Controlador MVC de la pantalla principal.
 * Coordina la vista (FXML) con el modelo (Cancion) a través del DAO.
 *
 * En MVC:
 *  - Este archivo es el CONTROLLER
 *  - main.fxml es la VIEW
 *  - Cancion.java es el MODEL
 */
public class MainController {

    // --- Componentes inyectados desde el FXML ---
    @FXML private TableView<Cancion>        tablaCanciones;
    @FXML private TableColumn<Cancion, Integer> colId;
    @FXML private TableColumn<Cancion, String>  colTitulo;
    @FXML private TableColumn<Cancion, String>  colArtista;
    @FXML private TableColumn<Cancion, String>  colAlbum;
    @FXML private TableColumn<Cancion, Integer> colAnio;
    @FXML private TableColumn<Cancion, String>  colGenero;

    @FXML private TextField campoBusqueda;
    @FXML private Label     etiquetaEstado;

    // --- DAO (capa de datos) ---
    private final CancionDAO cancionDAO = new CancionDAOImpl();

    // --- Lista observable: JavaFX actualiza la tabla automáticamente ---
    private final ObservableList<Cancion> canciones = FXCollections.observableArrayList();

    /**
     * initialize() es llamado automáticamente por JavaFX
     * después de cargar el FXML.
     */
    @FXML
    public void initialize() {
        configurarColumnas();
        tablaCanciones.setItems(canciones);
        cargarTodas();
    }

    /** Carga todas las canciones de la BD en la tabla. */
    @FXML
    public void cargarTodas() {
        List<Cancion> lista = cancionDAO.buscarTodos();
        canciones.setAll(lista);
        actualizarEstado(lista.size() + " canciones cargadas");
    }

    /** Busca por artista usando el campo de búsqueda. */
    @FXML
    public void buscar() {
        String texto = campoBusqueda.getText().trim();
        if (texto.isEmpty()) {
            cargarTodas();
            return;
        }
        List<Cancion> resultado = cancionDAO.buscarPorArtista(texto);
        canciones.setAll(resultado);
        actualizarEstado(resultado.size() + " resultados para: " + texto);
    }

    // --- Helpers ---

    private void configurarColumnas() {
        colId.setCellValueFactory      (new PropertyValueFactory<>("id"));
        colTitulo.setCellValueFactory  (new PropertyValueFactory<>("titulo"));
        colArtista.setCellValueFactory (new PropertyValueFactory<>("artista"));
        colAlbum.setCellValueFactory   (new PropertyValueFactory<>("album"));
        colAnio.setCellValueFactory    (new PropertyValueFactory<>("anio"));
        colGenero.setCellValueFactory  (new PropertyValueFactory<>("genero"));
    }

    private void actualizarEstado(String mensaje) {
        if (etiquetaEstado != null)
            etiquetaEstado.setText(mensaje);
    }
}
