package com.example.duplcatesearcher;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;


public class MainViewController {
    @FXML
    private Button CleanFiles;
    @FXML
    private Button CleanLogs;
    @FXML
    private CheckBox ContentCheckBox;
    @FXML
    private CheckBox DateCheckBox;
    @FXML
    private Button DirectoryChooseButton;
    @FXML
    private TextField DirectoryPath;
    @FXML
    private TextField FileNameField;
    @FXML
    private TextArea FilesArea;
    @FXML
    private Label InfoLabel;
    @FXML
    private AnchorPane LogsAnchorPane;
    @FXML
    private TextArea LogsArea;
    @FXML
    private AnchorPane MainWindow;
    @FXML
    private CheckBox NameCheckBox;
    @FXML
    private Button SearchButton;
    @FXML
    private CheckBox SizeCheckBox;

    //В хэшмапе хранятся ключ - оригинал файла, значение - найденный дубликат
    private HashMap<Path, List<Path>> originalsAndDuplicates;
    //В ридере хранится список файлов выбранной директории
    private DirectoryFileListGetter fileListGetter;
    //Метод для отображения списка файлов в указанной директории
    private void makeListOfFiles(){
        //По выбранной директори выполняем поиск файлов в новом потоке

        fileListGetter = new DirectoryFileListGetter(Paths.get(DirectoryPath.getText()));

        fileListGetter.run(this);
    }
    public void writeFileList(){
        List<Path> list = fileListGetter.getListOfFilesFromMainDirectory();//Список файлов директории и вложенных директорий

        //Проверка кол-ва файло
        if(list.size() > 100){
            //Если файлов много записываем в файл
            try {

                File foundFiles = new File("Out.txt");
                FileWriter fw = new FileWriter(foundFiles, false);
                PrintWriter printWriter = new PrintWriter(fw);

                for(Path path : list)
                    printWriter.println(path);

                printWriter.close();

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }


            FilesArea.setText("Создан новый файл Out.txt со списком файлов");

        }
        else{
            //Если немного выводим на экран

            for(Path file : list){
                //Вывод списка в окно программы
                FilesArea.appendText(file.toString() + "\n");
            }
        }
    }
    //Поиск вхождения введенной строки в имени файла
    private HashMap<Integer,Path> findMatches(DirectoryFileListGetter listGetter){
        String userFileName = FileNameField.getText();//Ввод пользователя
        String userRegEx = "";

        //Для введнной строки строится регулярное выражение для "жадного" поиска
        if(userFileName.indexOf('*')!=0)
            userRegEx = userFileName.replace("*",".+");


        HashMap<Integer,Path> matches = new HashMap<>();//найденные совпадения

        int key = 0;
        //Проходим по списку файлов директории и ищем в названии файла регулярное выражение
        for(Path file : listGetter.getListOfFilesFromMainDirectory()){
            String fileNameRegEx = file.getFileName().toString();

            if(Pattern.matches(userRegEx, fileNameRegEx)) {
                matches.put(key, file);
                key++;
            }
        }

        return matches;
    }
    //Метод для записи данных файла в строку
    private String readFileAsString(String filePath) throws IOException {
        StringBuffer fileData = new StringBuffer();
        BufferedReader reader = new BufferedReader(
                new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead = 0;
        while((numRead=reader.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();
        return fileData.toString();
    }
    //Сравниваем два файла в зависимости от установленных чекбоксов
    private Path compareFiles(Path firstPath, Path secondPath) throws IOException {
        File first = new File(firstPath.toString());
        File second = new File(secondPath.toString());

        if(NameCheckBox.isSelected() && SizeCheckBox.isSelected() && DateCheckBox.isSelected() && ContentCheckBox.isSelected()){
            if(first.getName().equals(second.getName()) &&
                    first.length() == second.length() &&
                    first.lastModified() == second.lastModified() &&
                    Arrays.equals(Files.readAllBytes(first.toPath()), Files.readAllBytes(second.toPath()))
            ) {
                return second.toPath();
            }

        }
        else if(NameCheckBox.isSelected() && SizeCheckBox.isSelected() && DateCheckBox.isSelected()){

            if(
                    first.getName().equals(second.getName()) &&
                            first.length() == second.length() &&
                            first.lastModified() == second.lastModified()

            ) {
                return second.toPath();
            }
        }
        else if (NameCheckBox.isSelected() && SizeCheckBox.isSelected() && ContentCheckBox.isSelected()) {
            if(
                    first.getName().equals(second.getName()) &&
                            first.length() == second.length() &&
                            Arrays.equals(Files.readAllBytes(first.toPath()), Files.readAllBytes(second.toPath()))
            ) {
                return second.toPath();
            }
        }
        else if(NameCheckBox.isSelected() && DateCheckBox.isSelected() && ContentCheckBox.isSelected()){

            if(first.getName().equals(second.getName()) &&
                    first.lastModified() == second.lastModified() &&
                    Arrays.equals(Files.readAllBytes(first.toPath()), Files.readAllBytes(second.toPath()))
            ) {
                return second.toPath();
            }
        }
        else if(SizeCheckBox.isSelected() && DateCheckBox.isSelected() && ContentCheckBox.isSelected()){
            if(
                    first.length() == second.length() &&
                            first.lastModified() == second.lastModified() &&
                            Arrays.equals(Files.readAllBytes(first.toPath()), Files.readAllBytes(second.toPath()))
            ) {
                return second.toPath();
            }
        }
        else if(NameCheckBox.isSelected() && SizeCheckBox.isSelected()){
            if(first.getName().equals(second.getName()) &&
                    first.length() == second.length()
            ) {
                return second.toPath();
            }
        }
        else if(NameCheckBox.isSelected() && ContentCheckBox.isSelected()){
            if(first.getName().equals(second.getName()) &&
                    Arrays.equals(Files.readAllBytes(first.toPath()), Files.readAllBytes(second.toPath()))
            ) {
                return second.toPath();
            }
        }
        else if(SizeCheckBox.isSelected() && DateCheckBox.isSelected()){
            if(
                    first.length() == second.length() &&
                            first.lastModified() == second.lastModified()

            ) {
                return second.toPath();
            }
        }
        else if(DateCheckBox.isSelected() && ContentCheckBox.isSelected()){
            if(
                    first.lastModified() == second.lastModified() &&
                            Arrays.equals(Files.readAllBytes(first.toPath()), Files.readAllBytes(second.toPath()))
            ) {
                return second.toPath();
            }
        }
        else if(NameCheckBox.isSelected()){
            if(first.getName().equals(second.getName())) {
                return second.toPath();
            }
        }
        else if(SizeCheckBox.isSelected()){
            if(first.length() == second.length()) {
                return second.toPath();
            }
        }
        else if(DateCheckBox.isSelected()){
            if(second.lastModified() == first.lastModified()) {
                return second.toPath();
            }
        }
        else if(ContentCheckBox.isSelected()){
            if(Arrays.equals(Files.readAllBytes(first.toPath()), Files.readAllBytes(second.toPath()))) {
                return second.toPath();
            }
        }

        return first.toPath();
    }
    public void findDuplicates() throws IOException {
        //Проверка полей на заполнение
        if(!DirectoryPath.getText().isEmpty() && !FileNameField.getText().isEmpty() && !fileListGetter.isWasCanceles()
                && (NameCheckBox.isSelected() || SizeCheckBox.isSelected() || DateCheckBox.isSelected())
                || ContentCheckBox.isSelected()
        ) {

            Alert waitAlert = new Alert(Alert.AlertType.INFORMATION,"Инициализая..",ButtonType.CANCEL);
            waitAlert.setHeaderText("Выполняется поиск, пожалуйста подождите");
            waitAlert.setTitle("Выполнение..");
            waitAlert.show();
            Task<Void> makeDuolicateListTask = new Task<Void>(){
                @Override
                protected Void call() throws Exception {

                    boolean isCanceled = false;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            waitAlert.setContentText("Поиск совпадений по имени..");
                        }
                    });

                    HashMap<Integer,Path> foundFiles = findMatches(fileListGetter);
                    //Записываем в хэшмап дубликаты в директории


                    //Получение ключей и значений словаря
                    Set<Integer> keys = foundFiles.keySet();
                    ArrayList<Path> values= new ArrayList<>(foundFiles.values());


                    List<Integer> indexes = new ArrayList<>();//Индексы найденных оригиналов и дубликатов
                    for(Path file : values){
                        indexes.add(values.indexOf(file));//Добавить индекс оригинала
                        List<Path> duplicatesList = new ArrayList<>();//Список дубликатов

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                waitAlert.setContentText("Поиск дубликата для файла: " + file.getFileName());
                            }
                        });

                        for(Path potential : values){
                            if(waitAlert.getResult()==ButtonType.CANCEL) {
                                isCanceled = true;
                                break;
                            }

                            //Если файл не является оригиналом/дубликатом для другого файла и файлы одинаковы
                            if(!indexes.contains(values.indexOf(potential))
                                    && potential.equals(compareFiles(file,potential))){
                                duplicatesList.add(potential);//Пополняем список дубликатов
                                indexes.add(values.indexOf(potential));//Добавляем индекс файла в список
                            }

                        }


                        //Если дуликаты для файла найдены пополним словарь
                        if(!duplicatesList.isEmpty()){
                            originalsAndDuplicates.put(file,duplicatesList);
                        }

                    }


                    if(!isCanceled) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                waitAlert.close();
                                openWindowWithResult();
                                originalsAndDuplicates.clear();
                                LogUpdate();
                            }
                        });
                    }
                    return null;
                }
            };

            Thread makeDuplicateListThread = new Thread(makeDuolicateListTask);
            makeDuplicateListThread.start();


        }
        else {
            //Вывод окна с ошибкой, если одно из полей не заполнено
            Alert alert;
            if(fileListGetter.isWasCanceles()){
                alert = new Alert(Alert.AlertType.ERROR,"Вы отменили поиск!", ButtonType.OK);
            }
            else {
                alert = new Alert(Alert.AlertType.ERROR, "Некоторые поля не заполнены!", ButtonType.OK);
            }

            alert.showAndWait();

            if (alert.getResult() == ButtonType.OK) {
                alert.close();
            }
        }


    }
    private void openWindowWithResult(){
        //Открытие окна со списком найденных файлов
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FoundFilesWindow.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            FoundFilesWindowController controller = fxmlLoader.getController();
            controller.setOriginalsAndDuplicatesMap(originalsAndDuplicates);
            controller.loadDuplicatesList();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setMinHeight(600);
            stage.setMinWidth(1250);
            stage.show();


        }
        catch (Exception e) {
            System.out.println(e.getMessage());

        }
    }
    public void WriteLog(int NumOfDeletedFiles){
        //Запись списка в файл
        try {

            File log = new File("Log.txt");
            FileWriter fw = new FileWriter(log, true);
            PrintWriter printWriter = new PrintWriter(fw);

            // Инициализация объекта date
            Date date = new Date();

            // Вывод текущей даты и времени с использованием toString()
            String currentDate = date.toString();

            printWriter.println(currentDate + "\nУдалено файлов: " + NumOfDeletedFiles );
            printWriter.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    public void LogUpdate()  {
        //Инициализация окна с логами
        try {
            File logFile = new File("Log.txt");
            if (logFile.createNewFile()) {
                LogsArea.setText("Новый файл с логами создан!");
            } else {
                LogsArea.setText(readFileAsString(logFile.getPath()));
            }
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }

    }


    //Инициализация переменных и обработка событий кнопок
    @FXML
    void initialize(){
        originalsAndDuplicates = new HashMap<>();

        //Начальное значение пути
        fileListGetter = new DirectoryFileListGetter();

        LogUpdate();

        //Обработка нажатия на кнопку выбора директории
        DirectoryChooseButton.setOnAction(actionEvent -> {
            FilesArea.clear();
            //Отктытие окна
            Stage stage = new Stage();
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Open Directory");
            String selectedDirectory = directoryChooser.showDialog(stage).toString();



            if(!selectedDirectory.isEmpty()){
                //Установка пути к директории в поле
                DirectoryPath.setText(selectedDirectory);

            }
        });


        //Поиск дубликатов
        SearchButton.setOnAction(actionEvent -> {
                makeListOfFiles();
        });



        //Логика для кнопок очистки
        CleanFiles.setOnAction(actionEvent -> {
            FilesArea.clear();
        });

        CleanLogs.setOnAction(actionEvent -> {
            LogsArea.clear();
            //Запись списка в файл
            try {
                File log = new File("Log.txt");
                FileWriter fw = new FileWriter(log, false);
                PrintWriter printWriter = new PrintWriter(fw);


                printWriter.println("");
                printWriter.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        });




    }


}
