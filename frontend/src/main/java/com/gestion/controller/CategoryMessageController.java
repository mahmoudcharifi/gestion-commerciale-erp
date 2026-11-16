package com.gestion.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class CategoryMessageController {

    @FXML
    private Label iconLabel;

    @FXML
    private Label titleLabel;

    @FXML
    private Label messageLabel;

    public void setMessage(String title, String message, boolean success) {
        titleLabel.setText(title);
        messageLabel.setText(message);

        iconLabel.setText(success ? "✓" : "✕");
        iconLabel.setStyle(
            success
                ? "-fx-text-fill: #23845B;"
                : "-fx-text-fill: #C74747;"
        );
    }

    @FXML
    private void close() {
        Stage stage = (Stage) titleLabel.getScene().getWindow();
        stage.close();
    }
}
