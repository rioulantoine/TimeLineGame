package com.example.application;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import pojo.CollectionPOJO;
import java.io.File;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MesDecksController implements Initializable {

    @FXML private TilePane deckTilePane;
    @FXML private Button modifierDeckBtn;
    @FXML private Button supprimerDeckBtn;


    private Label selectedDeck = null;
    private String selectedDeckName;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadDecksFromJson();
    }

    private void loadDecksFromJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            CollectionPOJO[] decks;

            File externalFile = new File("decks.json");

            if (externalFile.exists()) {
                decks = mapper.readValue(externalFile, CollectionPOJO[].class);
            } else {
                InputStream input = getClass().getResourceAsStream("/com/example/sae201timeline/data/data.json");
                decks = mapper.readValue(input, CollectionPOJO[].class);
            }

            for (CollectionPOJO deck : decks) {
                VBox card = createDeckCard(deck, deck.name);
                deckTilePane.getChildren().add(card);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les decks", Alert.AlertType.ERROR);
        }
    }

    private VBox createDeckCard(CollectionPOJO deck, String filename) {
        VBox container = new VBox();
        container.setSpacing(5);
        container.setPrefSize(150, 180);
        container.getStyleClass().add("deck-card");

        String url = (deck.cards != null && deck.cards.length > 0 && deck.cards[0].url != null && !deck.cards[0].url.isEmpty())
                ? deck.cards[0].url
                : getClass().getResource("/com/example/sae201timeline/data/default.png").toExternalForm();

        ImageView img = new ImageView(new Image(url));
        img.setFitWidth(120);
        img.setFitHeight(100);
        img.setPreserveRatio(true);

        Label name = new Label(deck.name);
        name.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        name.setWrapText(true);
        name.setMaxWidth(120);
        name.setAlignment(Pos.CENTER);

        container.setAlignment(Pos.CENTER);
        container.getChildren().addAll(img, name);

        container.setOnMouseClicked(event -> {
            clearSelections();
            container.getStyleClass().add("selected");
            selectedDeck = name;
            selectedDeckName = deck.name;
            modifierDeckBtn.setDisable(false);
            supprimerDeckBtn.setDisable(false);
        });

        return container;
    }

    private void clearSelections() {
        for (var node : deckTilePane.getChildren()) {
            node.getStyleClass().remove("selected");
        }
    }

    @FXML
    private void onSupprimerDeck() {
        if (selectedDeck == null) {
            showAlert("Erreur", "Aucun deck sélectionné", Alert.AlertType.WARNING);
            return;
        }

        String deckName = selectedDeck.getText();

        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File("decks.json");

            if (!file.exists()) {
                showAlert("Erreur", "Aucun fichier decks.json trouvé", Alert.AlertType.ERROR);
                return;
            }

            CollectionPOJO[] decks = mapper.readValue(file, CollectionPOJO[].class);
            List<CollectionPOJO> deckList = new ArrayList<>(List.of(decks));

            boolean removed = deckList.removeIf(d -> d.name.equalsIgnoreCase(deckName));
            if (!removed) {
                showAlert("Erreur", "Deck introuvable", Alert.AlertType.WARNING);
                return;
            }

            mapper.writerWithDefaultPrettyPrinter().writeValue(file, deckList);

            showAlert("Succès", "Deck supprimé", Alert.AlertType.INFORMATION);
            deckTilePane.getChildren().clear();
            loadDecksFromJson();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Échec de la suppression", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onCreerDeck() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sae201timeline/FXML/ModifierDeck.fxml"));
            Parent root = loader.load();

            ModifierDeckController controller = loader.getController();
            controller.setDeckName("");
            Stage stage = (Stage) deckTilePane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Créer un nouveau deck");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la page de création", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onModifierDeck() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sae201timeline/FXML/ModifierDeck.fxml"));
            Parent root = loader.load();

            ModifierDeckController controller = loader.getController();
            controller.setDeckName(selectedDeckName);

            Stage stage = (Stage) modifierDeckBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier un deck");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la page de modification", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sae201timeline/FXML/TimeLine_Acceuil.fxml"));
            Parent root = loader.load();

            Stage currentStage = (Stage) deckTilePane.getScene().getWindow();
            currentStage.close();

            Stage newStage = new Stage();
            newStage.setTitle("TimeLine");
            newStage.setScene(new Scene(root));
            newStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de revenir à la page d'accueil", Alert.AlertType.ERROR);
        }
    }
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
