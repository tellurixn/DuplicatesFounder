package com.example.duplcatesearcher;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

public class FileData {
    private String name;
    private long size;
    private Date createdTime;
    private String path;
    private Boolean selected;
    private  String comment;

    public String getName(){
        return this.name;
    }

    public long getSize(){
        return  this.size;
    }

    public Date getCreatedTime(){
        return this.createdTime;
    }

    public String getPath(){
        return  this.path;
    }

    public String getComment(){
        return this.comment;
    }

    public Boolean  isSelected(){
        return this.selected;
    }

    public void setSelected(Boolean value) {
        this.selected = value;
    }

    public void setComment(String comment){
        this.comment = comment;
    }
    FileData(String name, long size,Date createdTime,String path, Boolean selected){
        this.name = name;
        this.size = size;
        this.createdTime = createdTime;
        this.path = path;
        this.selected = selected;
        this.comment = "";
    }

    FileData(File file){
        this.name = file.getName();
        this.size = file.length()/(1024*1024);
        this.createdTime = new Date(file.lastModified());
        this.path = file.getAbsolutePath();
        this.selected = true;
        this.comment = "";
    }

    public File getFile(){
        return new File(this.path);
    }

    FileData(){
        this.name = "";
        this.size = 0;
        this.createdTime = new Date();
        this.path = "";
        this.selected = true;
        this.comment = "";
    }
    public ObservableList<FileData> getFileDataFromDuplicateList(List<Path> list){
        ObservableList<FileData> result = FXCollections.observableArrayList();

        for(Path file : list){
            FileData fileData = new FileData(file.toFile());
            fileData.setComment("Дубликат");
            result.add(fileData);
        }

        return result;
    }

    public ObservableList<FileData> getFileDataFromHashMapList(HashMap<Path,List<Path>> hashMapList){
        ObservableList<FileData> result = FXCollections.observableArrayList();

        Set<Path> keys = hashMapList.keySet();
        for(Path file : keys){
            FileData keyData = new FileData(file.toFile());
            keyData.setSelected(false);
            keyData.setComment("Оригинал");
            result.add(keyData);
        }

        List<List<Path>> values = new ArrayList<>(hashMapList.values());

        for(List<Path> listOfFiles : values){
            for(Path file : listOfFiles){
                FileData valueData = new FileData(file.toFile());
                valueData.setSelected(true);
                valueData.setComment("Дубликат");
                result.add(valueData);
            }
        }



        return result;
    }

    public ObservableList<FileData> getFileDataFromOriginalList(List<Path> list){
        ObservableList<FileData> result = FXCollections.observableArrayList();

        for(Path file : list){
            FileData fileData = new FileData(file.toFile());
            fileData.setComment("Оригинал");
            fileData.setSelected(false);
            result.add(fileData);
        }

        return result;
    }

}