package mx.unam.musicdb.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import mx.unam.musicdb.compilador.CompiladorBusqueda;
import mx.unam.musicdb.dao.RolaDAO;
import mx.unam.musicdb.dao.RolaDAOImpl;
import mx.unam.musicdb.miner.Minero;
import mx.unam.musicdb.model.Rola;

import java.io.File;
import java.util.List;

public class MainController {

    @FXML private TableView<Rola>               tablaRolas;
    @FXML private TableColumn<Rola, Integer>    colId;
    @FXML private TableColumn<Rola, String>     colTitulo;
    @FXML private TableColumn<Rola, String>     colPerformer;
    @FXML private TableColumn<Rola, String>     colAlbum;
    @FXML private TableColumn<Rola, Integer>    colTrack;
    @FXML private TableColumn<Rola, Integer>    colAnio;
    @FXML private TableColumn<Rola, String>     colGenero;

    @FXML private TextField campoBusqueda;
    @FXML private Label     etiquetaEstado;

    private final RolaDAO rolaDAO = new RolaDAOImpl();
    private final CompiladorBusqueda compilador = new CompiladorBusqueda(rolaDAO);
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
        try {
            List<Rola> resultado = compilador.buscar(texto);
            rolas.setAll(resultado);
            actualizarEstado(resultado.size() + " resultados para: " + texto);
        } catch (Exception e) {
            actualizarEstado("Error: " + e.getMessage());
        }
    }

    @FXML
    public void importarMP3() {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Seleccionar carpeta con archivos MP3");
        File dir = dc.showDialog(tablaRolas.getScene().getWindow());
        if (dir == null) return;

        String ruta = dir.getAbsolutePath();
        actualizarEstado("Importando desde: " + ruta + " ...");

        new Thread(() -> {
            try {
                Minero minero = new Minero(
                    new mx.unam.musicdb.dao.PerformerDAOImpl(),
                    new mx.unam.musicdb.dao.AlbumDAOImpl(),
                    rolaDAO
                );
                List<Rola> insertadas = minero.minar(ruta);
                Platform.runLater(() -> {
                    cargarTodas();
                    actualizarEstado("Importacion completada: " + insertadas.size()
                            + " rolas insertadas desde " + ruta);
                });
            } catch (Exception e) {
                Platform.runLater(() ->
                    actualizarEstado("Error en importacion: " + e.getMessage()));
            }
        }).start();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory       (new PropertyValueFactory<>("idRola"));
        colTitulo.setCellValueFactory   (new PropertyValueFactory<>("title"));
        colPerformer.setCellValueFactory(new PropertyValueFactory<>("performerName"));
        colAlbum.setCellValueFactory    (new PropertyValueFactory<>("albumName"));
        colTrack.setCellValueFactory    (new PropertyValueFactory<>("track"));
        colAnio.setCellValueFactory     (new PropertyValueFactory<>("year"));
        colGenero.setCellValueFactory   (new PropertyValueFactory<>("genre"));
    }

    private void actualizarEstado(String mensaje) {
        if (etiquetaEstado != null)
            etiquetaEstado.setText(mensaje);
    }
}
