package com.example.duplcatesearcher;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DeletingController {

    @FXML
    private Button ExitButton;

    @FXML
    private Text SuccessMessage;

    @FXML
    private TextField fileNameText;

    @FXML
    private Label processLabel;

    @FXML
    private ProgressBar progressBar;
    List<File> targets;//Список файлов для удаления

    public void setTargets(List<File> targets){
        this.targets = targets;
    }

    public List<File> getTargets() {
        return targets;
    }

    public void delete(){
        targets = getTargets();

        double percent = 1000 / (double) targets.size();

        for(File file : targets){
            fileNameText.setText(file.getName());
            file.delete();
            progressBar.setProgress(progressBar.getProgress()+percent);
        }

        fileNameText.setVisible(false);
        processLabel.setVisible(false);
        SuccessMessage.setVisible(true);


        //Обновление логов
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainView.fxml"));
        try {
            Parent root = (Parent) fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MainViewController controller = fxmlLoader.getController();
        controller.WriteLog(targets.size());
        controller.LogUpdate();


    }

    private long Size;

    public void SetSize (long Size){
        this.Size = Size;
    }

    public void setCurrentFileName(String name){
        fileNameText.setText(name);
    }

    public long GetCize(){
        return  Size;
    }

    public void setProgress(double progress){
        progressBar.setProgress(progress);
    }

    public void addProgress(double Size){
        progressBar.setProgress(progressBar.getProgress()+Size);
    }
    public double getPercent(){
        return 1/ (double) this.Size;
    }
    public void showSuccess(){
        fileNameText.setVisible(false);
        processLabel.setVisible(false);
        SuccessMessage.setVisible(true);
    }

    public double getProgress(){
        return  progressBar.getProgress();
    }
    @FXML
    void initialize() {
        targets = new ArrayList<>();
        Size = 0;

        ExitButton.setOnAction(actionEvent -> {
            //Закрытие окна
            Stage stage = (Stage)ExitButton.getScene().getWindow();
            stage.close();
        });
    }

}
