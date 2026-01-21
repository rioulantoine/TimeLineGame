package com.example.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import pojo.CollectionPOJO;

import java.io.File;
import java.io.IOException;

public class ConfigurationPartieController {

    @FXML
    private ChoiceBox<String> Decks;

    @FXML
    private ChoiceBox<String> TempsEntreCoup;

    private int nbrJoueur = 0;

    @FXML
    void initialize() {
        // Remplit les temps
        TempsEntreCoup.getItems().addAll("10", "20", "30", "45", "60", "Infini");
        TempsEntreCoup.setValue("30");

        // Charge les decks
        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File("decks.json");
            CollectionPOJO[] allDecks = mapper.readValue(file, CollectionPOJO[].class);

            for (CollectionPOJO deck : allDecks) {
                Decks.getItems().add(deck.name);
            }

            if (!Decks.getItems().isEmpty()) {
                Decks.setValue(Decks.getItems().get(0)); // sélection par défaut
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void PartieAlone(ActionEvent event) {
        nbrJoueur = 1;
    }

    @FXML
    void PartieADeux(ActionEvent event) {
        nbrJoueur = 2;
    }

    @FXML
    void ValiderConfiguration(ActionEvent event) throws IOException {
        int tempsParTour = getTempsParTour();
        String selectedDeckName = Decks.getValue();

        FXMLLoader loader;
        Scene scene;

        if (nbrJoueur == 1) {
            // Mode Solo
            loader = new FXMLLoader(getClass().getResource("/com/example/sae201timeline/FXML/JeuTimeline.fxml"));
            JeuxController controller = new JeuxController();
            controller.setDeckName(selectedDeckName);
            controller.setTempsParTour(tempsParTour);
            loader.setController(controller);
            scene = new Scene(loader.load());

        } else if (nbrJoueur == 2) {
            // Mode Deux Joueurs
            loader = new FXMLLoader(getClass().getResource("/com/example/sae201timeline/FXML/JeuTimelineADeux.fxml"));
            JeuxADeuxController controller = new JeuxADeuxController();
            controller.setTempsParTour(tempsParTour);

            // Charger deck JSON
            ObjectMapper mapper = new ObjectMapper();
            File file = new File("decks.json");
            CollectionPOJO[] allDecks = mapper.readValue(file, CollectionPOJO[].class);
            for (CollectionPOJO deck : allDecks) {
                if (deck.name.equalsIgnoreCase(selectedDeckName)) {
                    controller.setDeck(deck);
                    break;
                }
            }

            loader.setController(controller);
            scene = new Scene(loader.load());

        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Nombre Joueurs");
            alert.setContentText("Le nombre de joueur n'a pas été choisi.");
            alert.showAndWait();
            return;
        }

        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }

    private int getTempsParTour() {
        String selected = TempsEntreCoup.getValue();
        return selected.equals("Infini") ? -1 : Integer.parseInt(selected);
    }

    @FXML
    void retourMenu(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(TimeLineApplication.class.getResource("/com/example/sae201timeline/FXML/TimeLine_Acceuil.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }


}