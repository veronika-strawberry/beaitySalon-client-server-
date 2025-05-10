module com.example.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.management;


    opens controllers to javafx.fxml;
    exports controllers;
    exports main;
    opens main to javafx.fxml;
}