package com.example.duplcatesearcher;

import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DirectoryFileListGetter implements Runnable {

    private Path mainDirectoryPath;//Путь директори, которую выбрал пользователь

    private HashMap<Integer, Path> allFilesFromMainDirectory;//Хэшмап для хранения файлов

    private int filesCompletedCounter;//Счетчик файлов


    DirectoryFileListGetter(){
        mainDirectoryPath = null;
        allFilesFromMainDirectory = new HashMap<Integer, Path>();
        filesCompletedCounter = 0;
    }



    DirectoryFileListGetter(Path path){
        mainDirectoryPath = path;
        allFilesFromMainDirectory = new HashMap<>();
        filesCompletedCounter = 0;


    }


    public HashMap<Integer,Path> getHashMapOfFilesFromMainDirectory() {
        return allFilesFromMainDirectory;
    }


    public List<Path> getListOfFilesFromMainDirectory(){
        return new ArrayList<>(allFilesFromMainDirectory.values());
    }

    public void makeListOfFilesFromDirectory(Path directory, HashMap<Integer,Path> list){


            System.out.println("THREAD " + Thread.currentThread().getName());
            Path currentDirectory = directory; //Текущая папка
            File[] files = currentDirectory.toFile().listFiles();//Список файлов в текущей папке

            //Проверка папки на пустоту
            if (files != null) {
                //Проверка файлов в папке,
                //Если найден файл, то добавляем в список
                //если найдена папка, то рекурсивно вызываем текущий метод
                for (File file : files) {
                    if (file.isFile()) {
                        // System.out.println("Новый файл " + file.getName());
                        list.put(filesCompletedCounter, Paths.get(file.getAbsolutePath()));
                        filesCompletedCounter++;
                        System.out.println("Каунт " + filesCompletedCounter);

                    /*Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            controller.setCurrentFileName(file.getName());
                            controller.addProgress(file.length()* controller.getPercent());
                            if(fileCounter == sizeOfMainDirectory) {
                                controller.showSuccess();
                            }
                        }
                    });*/
                    } else if (file.isDirectory()) {

                                makeListOfFilesFromDirectory(Paths.get(file.getAbsolutePath()), list);

                        //System.out.println("THIS IS FOLDER I CANT DO ANYTHING");
                    }
                }



            }




    }

    public int getFileCounter(){
        return filesCompletedCounter;
    }


    @Override
    public void run() {
            if (mainDirectoryPath.toFile().listFiles() != null)
                makeListOfFilesFromDirectory(mainDirectoryPath, allFilesFromMainDirectory);
            else
                System.out.println("Директория не инициализирована\n");

    }


}
