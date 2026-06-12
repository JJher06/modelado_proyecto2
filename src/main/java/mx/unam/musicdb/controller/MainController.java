/*
 * MusicDB - Base de datos musical con interfaz gráfica JavaFX
 * Copyright (C) 2024  GNU GPL v3
 */
package mx.unam.musicdb.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import mx.unam.musicdb.dao.RolaDAO;
import mx.unam.musicdb.dao.RolaDAOImpl;
import mx.unam.musicdb.model.Rola;

import java.util.List;

/**
 * Controlador MVC de la pantalla principal.
 *
 * En MVC:
 *  - Este archivo es el CONTROLLER
 *  - main.fxml es la VIEW
 *  - Rola.java es el MODEL
 */
public class MainController {

    @FXML private TableView<Rola>               tablaRolas;
    @FXML private TableColumn<Rola, Integer>    colId;
    @FXML private TableColumn<Rola, String>     colTitulo;
    @FXML private TableColumn<Rola, Integer>    colPerformer;
    @FXML private TableColumn<Rola, Integer>    colAlbum;
    @FXML private TableColumn<Rola, Integer>    colTrack;
    @FXML private TableColumn<Rola, Integer>    colAnio;
    @FXML private TableColumn<Rola, String>     colGenero;

    @FXML private TextField campoBusqueda;
    @FXML private Label     etiquetaEstado;

    private final RolaDAO rolaDAO = new RolaDAOImpl();
    private final ObservableList<Rola> rolas = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarColumnas();
        tablaRolas.setItems(rolas);
        cargarTodas();
    }

    @FXML
    public void cargarTodas() {
        List<Rola> lista = rolaDAO.buscarTodos();
        rolas.setAll(lista);
        actualizarEstado(lista.size() + " rolas cargadas");
    }

    @FXML
    public void buscar() {
        String texto = campoBusqueda.getText().trim();
        if (texto.isEmpty()) {
            cargarTodas();
            return;
        }
        List<Rola> resultado = rolaDAO.buscarPorTitulo(texto);
        rolas.setAll(resultado);
        actualizarEstado(resultado.size() + " resultados para: " + texto);
    }

    private void configurarColumnas() {
        colId.setCellValueFactory       (new PropertyValueFactory<>("idRola"));
        colTitulo.setCellValueFactory   (new PropertyValueFactory<>("title"));
        colPerformer.setCellValueFactory(new PropertyValueFactory<>("idPerformer"));
        colAlbum.setCellValueFactory    (new PropertyValueFactory<>("idAlbum"));
        colTrack.setCellValueFactory    (new PropertyValueFactory<>("track"));
        colAnio.setCellValueFactory     (new PropertyValueFactory<>("year"));
        colGenero.setCellValueFactory   (new PropertyValueFactory<>("genre"));
    }

    private void actualizarEstado(String mensaje) {
        if (etiquetaEstado != null)
            etiquetaEstado.setText(mensaje);
    }
}
