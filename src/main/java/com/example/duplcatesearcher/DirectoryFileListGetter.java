package com.example.duplcatesearcher;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DirectoryFileListGetter {

    private Path mainDirectoryPath;//Путь директори, которую выбрал пользователь

    private HashMap<Integer, Path> allFilesFromMainDirectory;//Хэшмап для хранения файлов

    private volatile int filesCompletedCounter;//Счетчик файлов

    private Alert infoAlert;

    private boolean isFinished;

    private boolean isCanceled;



    private Scene scene;
    DirectoryFileListGetter(){
        mainDirectoryPath = null;
        allFilesFromMainDirectory = new HashMap<Integer, Path>();
        filesCompletedCounter = 0;
        isFinished = false;
        isCanceled = false;
    }



    DirectoryFileListGetter(Path path){
        mainDirectoryPath = path;
        allFilesFromMainDirectory = new HashMap<>();
        filesCompletedCounter = 0;

        isFinished = false;
        isCanceled = false;

        infoAlert = new Alert(Alert.AlertType.INFORMATION,"Поиск файлов", ButtonType.CANCEL);
        infoAlert.initModality(Modality.NONE);
        infoAlert.setTitle("Выполнение..");
        infoAlert.setHeaderText("Выполняется поиск файлов, пожалуйста подождите");


    }


    public HashMap<Integer,Path> getHashMapOfFilesFromMainDirectory() {
        return allFilesFromMainDirectory;
    }

    public boolean isWasCanceles(){
        return isCanceled;
    }

    public List<Path> getListOfFilesFromMainDirectory(){
        return new ArrayList<>(allFilesFromMainDirectory.values());
    }

    public synchronized void makeListOfFilesFromDirectory(Path directory, HashMap<Integer,Path> list){
            Path currentDirectory = directory; //Текущая папка
            File[] files = currentDirectory.toFile().listFiles();//Список файлов в текущей папке

            //Проверка папки на пустоту
            if (files != null) {
                //Проверка файлов в папке,
                //Если найден файл, то добавляем в список
                //если найдена папка, то рекурсивно вызываем текущий метод
                for (File file : files) {
                    if(infoAlert.getResult() == ButtonType.CANCEL){
                        isCanceled = true;
                        return;
                    }



                    if (file.isFile()) {
                        filesCompletedCounter++;
                        list.put(filesCompletedCounter, Paths.get(file.getAbsolutePath()));

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                /*progressController.setCurrentFileName(file.getName());
                                progressController.updateNumberOfFiles(filesCompletedCounter);*/
                                infoAlert.setContentText("Файлов найдено: " + Integer.toString(filesCompletedCounter));
                                    }
                                });

                            } else if (file.isDirectory()) {
                                makeListOfFilesFromDirectory(Paths.get(file.getAbsolutePath()), list);
                            }
                        }

            }


    }


   public void run(MainViewController controller) {
       if (mainDirectoryPath.toFile().listFiles() != null) {

           infoAlert.show();
           Task<Void> findFiles = new Task<Void>() {
               @Override
               protected Void call() throws Exception {
                   makeListOfFilesFromDirectory(mainDirectoryPath, allFilesFromMainDirectory);
                   isFinished = true;

                   if (!isCanceled){
                       Platform.runLater(new Runnable() {
                           @Override
                           public void run() {
                               infoAlert.close();
                               controller.writeFileList();
                           }
                       });

                   }


                   return null;
               }
             };





               Thread thread = new Thread(findFiles);
               thread.start();




       } else
           System.out.println("Директория не инициализирована\n");


   }

}
