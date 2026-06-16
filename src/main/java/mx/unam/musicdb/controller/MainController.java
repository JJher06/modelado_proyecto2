package mx.unam.musicdb.controller;

import javafx.application.Platform;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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
        TextField pathField = new TextField();
        pathField.setPromptText("/ruta/a/carpeta/con/mp3");

        Button examinarBtn = new Button("Examinar...");
        examinarBtn.setOnAction(e -> {
            DirectoryChooser dc = new DirectoryChooser();
            dc.setTitle("Seleccionar carpeta con archivos MP3");
            File dir = dc.showDialog(tablaRolas.getScene().getWindow());
            if (dir != null) pathField.setText(dir.getAbsolutePath());
        });

        HBox inputRow = new HBox(8, pathField, examinarBtn);
        inputRow.setStyle("-fx-padding: 8 0;");
        HBox.setHgrow(pathField, Priority.ALWAYS);

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Importar MP3");
        dialog.setHeaderText("Selecciona la carpeta con archivos MP3:");
        dialog.getDialogPane().setContent(inputRow);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(btn -> btn == ButtonType.OK ? pathField.getText() : null);
        dialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
        pathField.textProperty().addListener((obs, o, n) ->
            dialog.getDialogPane().lookupButton(ButtonType.OK)
                .setDisable(n == null || n.isBlank()));

        dialog.initOwner(tablaRolas.getScene().getWindow());

        dialog.showAndWait().ifPresent(ruta -> {
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
                } catch (Exception ex) {
                    Platform.runLater(() ->
                        actualizarEstado("Error en importacion: " + ex.getMessage()));
                }
            }).start();
        });
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
