package com.example.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class TimeLineApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TimeLineApplication.class.getResource("/com/example/sae201timeline/FXML/TimeLine_Acceuil.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 960, 520);
        stage.setTitle("TimeLine");
        stage.setScene(scene);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
