module com.gestion {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.gestion to javafx.fxml;
    exports com.gestion;
}
