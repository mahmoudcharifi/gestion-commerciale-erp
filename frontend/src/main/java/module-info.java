module com.gestion {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    
    opens com.gestion.model to javafx.base;
    opens com.gestion to javafx.fxml;
    opens com.gestion.controller to javafx.fxml;

    exports com.gestion;
}