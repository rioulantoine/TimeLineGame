module com.example.sae201timeline {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires java.sql;
    requires java.smartcardio;
    requires java.desktop;


    opens com.example.application to javafx.fxml;
    opens pojo to com.fasterxml.jackson.databind;
    exports com.example.application;
}