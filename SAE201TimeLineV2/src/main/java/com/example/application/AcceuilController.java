package com.example.application;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class AcceuilController {
    @FXML
    void ContinuerButton(ActionEvent event) {

    }

    @FXML
    void CreditsButton(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoaderMenuJeu = new FXMLLoader(TimeLineApplication.class.getResource("/com/example/sae201timeline/FXML/Credits.fxml"));
        Scene sceneMenuJeu = new Scene(fxmlLoaderMenuJeu.load());
        Button btn1 = (Button) event.getSource();
        Stage stg1 = (Stage) btn1.getScene().getWindow();
        stg1.setScene(sceneMenuJeu);
    }

    @FXML
    void DecksButton(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoaderMenuJeu = new FXMLLoader(TimeLineApplication.class.getResource("/com/example/sae201timeline/FXML/MesDecks.fxml"));
        Scene sceneMenuJeu = new Scene(fxmlLoaderMenuJeu.load());
        Button btn1 = (Button) event.getSource();
        Stage stg1 = (Stage) btn1.getScene().getWindow();
        stg1.setScene(sceneMenuJeu);
    }

    @FXML
    void QuitterAction(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    void newGameButton(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoaderMenuJeu = new FXMLLoader(TimeLineApplication.class.getResource("/com/example/sae201timeline/FXML/ConfigurationPartie.fxml"));
        Scene sceneMenuJeu = new Scene(fxmlLoaderMenuJeu.load());
        Button btn1 = (Button) event.getSource();
        Stage stg1 = (Stage) btn1.getScene().getWindow();
        stg1.setScene(sceneMenuJeu);
    }

}