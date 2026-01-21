package com.example.application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class CreditsController {

    @FXML
    void retourMenu(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoaderMenuJeu = new FXMLLoader(TimeLineApplication.class.getResource("/com/example/sae201timeline/FXML/TimeLine_Acceuil.fxml"));
        Scene sceneMenuJeu = new Scene(fxmlLoaderMenuJeu.load());
        Button btn1 = (Button) event.getSource();
        Stage stg1 = (Stage) btn1.getScene().getWindow();
        stg1.setScene(sceneMenuJeu);
    }
}
