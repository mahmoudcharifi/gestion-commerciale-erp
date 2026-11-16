package com.gestion.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class CategoryDeleteConfirmationController {

    @FXML
    private Label categoryNameLabel;

    private boolean confirmed = false;

    public void setCategoryName(String name) {
        categoryNameLabel.setText(name);
    }

    @FXML
    private void confirmDelete() {
        confirmed = true;
        closeWindow();
    }

    @FXML
    private void cancel() {
        confirmed = false;
        closeWindow();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    private void closeWindow() {
        Stage stage = (Stage) categoryNameLabel.getScene().getWindow();
        stage.close();
    }
}
