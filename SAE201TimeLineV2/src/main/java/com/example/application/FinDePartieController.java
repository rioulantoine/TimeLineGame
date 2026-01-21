package com.example.application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class FinDePartieController {
    @FXML
    private Label textLabel;

    public void initialize(String resultMessage) {
        textLabel.setText(resultMessage);
    }

    // Bouton retour au menu principal
    @FXML
    void MenuButton(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoaderMenuJeu = new FXMLLoader(getClass().getResource("/com/example/sae201timeline/FXML/ConfigurationPartie.fxml"));
        Scene sceneMenuJeu = new Scene(fxmlLoaderMenuJeu.load());
        Button btn1 = (Button) event.getSource();
        Stage stage = (Stage) btn1.getScene().getWindow();
        stage.setScene(sceneMenuJeu);
    }

    // Bouton quitter
    @FXML
    void QuitButton(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoaderMenuJeu = new FXMLLoader(getClass().getResource("/com/example/sae201timeline/FXML/TimeLine_Acceuil.fxml"));
        Scene sceneMenuJeu = new Scene(fxmlLoaderMenuJeu.load());
        Button btn1 = (Button) event.getSource();
        Stage stage = (Stage) btn1.getScene().getWindow();
        stage.setScene(sceneMenuJeu);
    }
}
