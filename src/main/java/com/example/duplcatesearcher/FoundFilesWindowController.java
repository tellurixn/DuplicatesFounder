package com.example.duplcatesearcher;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;


public class FoundFilesWindowController {
    @FXML
    private TableView<FileData> DuplicatesTable;
    @FXML
    private Button ExitButton;
    @FXML
    private Text numberOfDuplicates;
    @FXML
    private Button DeleteSelectedButton;
    @FXML
    private AnchorPane FFWAnchorPane;
    @FXML
    private TableColumn<FileData, String> FileNameColumn;
    @FXML
    private TableColumn<FileData, String> PathColumn;
    @FXML
    private TableColumn<FileData, Long> SizeColumn;
    @FXML
    private TableColumn<FileData, Date> DateColumn;
    @FXML
    private TableColumn<FileData, CheckBox> isSelectedColumn;
    @FXML
    private TableColumn<FileData, String> isOriginalColumn;
    private HashMap<Path,List<Path>> originalsAndDuplicatesMap;

    public void setOriginalsAndDuplicatesMap(HashMap<Path,List<Path>> originalsAndDuplicatesMap){
        this.originalsAndDuplicatesMap = originalsAndDuplicatesMap;
    }
    public HashMap<Path,List<Path>> getOriginalsAndDuplicatesMap(){
        return this.originalsAndDuplicatesMap;
    }
    private void openDeletingWindow(List<File> files){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Progress.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            ProgressController controller = fxmlLoader.getController();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
            controller.setTargets(files);
            controller.delete();

        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    private void deleteRow(List<FileData> rows){
        for(FileData row : rows){
            DuplicatesTable.getItems().remove(row);
        }
    }
    public void loadDuplicatesList(){
        //Установка столбцам таблицы соответсвующих значений класса FileData
        FileNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        SizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        DateColumn.setCellValueFactory(new PropertyValueFactory<>("createdTime"));
        PathColumn.setCellValueFactory(new PropertyValueFactory<>("path"));
        isOriginalColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));

        isSelectedColumn = (TableColumn<FileData, CheckBox>) DuplicatesTable.getColumns().get(1);
        isSelectedColumn.setCellValueFactory(new FileSelectedValueFactory());


        FileData fileDataDuplicates = new FileData();


        HashMap<Path,List<Path>> originsAndDuplics = getOriginalsAndDuplicatesMap();
        ObservableList<FileData> list = fileDataDuplicates.getFileDataFromHashMapList(originsAndDuplics);




        //Добавление строк в таблицу
        DuplicatesTable.setItems(list);
        numberOfDuplicates.setText("Файлов с дубликатами: " + originsAndDuplics.size());

    }


    @FXML
    void initialize() throws IOException {


        //Открытие файла по двойному нажатию мыши
        DuplicatesTable.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                    TablePosition pos = DuplicatesTable.getSelectionModel().getSelectedCells().get(0);
                    int row = pos.getRow();

                    FileData item = DuplicatesTable.getItems().get(row);
                    String data = item.getPath();

                    try {
                        Runtime.getRuntime().exec("explorer.exe /select," + data);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }


                }
            }
        });

        DeleteSelectedButton.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.isPrimaryButtonDown()){
                    List<File> targets = new ArrayList<>();
                    List<FileData> rowsToDelete = new ArrayList<>();

                    for(int i =0; i<DuplicatesTable.getItems().size(); i++) {
                        if(DuplicatesTable.getItems().get(i).isSelected()) {
                            targets.add(DuplicatesTable.getItems().get(i).getFile());
                            rowsToDelete.add(DuplicatesTable.getItems().get(i));
                        }
                    }

                    openDeletingWindow(targets);
                    deleteRow(rowsToDelete);





                }
            }
        });


        //Нажатие на кнопку закрыть
        ExitButton.setOnAction(actionEvent -> {
            Stage stage = (Stage)ExitButton.getScene().getWindow();
            stage.close();

        });



    }






}

