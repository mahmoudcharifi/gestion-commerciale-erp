package com.gestion.navigation;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class NavigationManager {

    private final StackPane contentArea;

    public NavigationManager(StackPane contentArea) {
        this.contentArea = contentArea;
    }

    public void navigateTo(String pageName) throws IOException {

        FXMLLoader loader = new FXMLLoader(
            getClass().getResource(
                "/com/gestion/views/" + pageName + ".fxml"
            )
        );

        Parent page = loader.load();

        contentArea.getChildren().setAll(page);
    }
}
