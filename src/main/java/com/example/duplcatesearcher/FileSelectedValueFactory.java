package com.example.duplcatesearcher;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class FileSelectedValueFactory
        implements Callback<TableColumn.CellDataFeatures<FileData, CheckBox>, ObservableValue<CheckBox>>{

    @Override
    public ObservableValue<CheckBox> call(TableColumn.CellDataFeatures<FileData, CheckBox> param) {
        FileData fileData = param.getValue();
        CheckBox checkBox = new CheckBox();
        checkBox.selectedProperty().setValue(fileData.isSelected());
        checkBox.selectedProperty().addListener((ov, old_val, new_val) -> {
            fileData.setSelected(new_val);
        });
        return new SimpleObjectProperty<>(checkBox);
    }
}
