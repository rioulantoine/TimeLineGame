package com.example.application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Card;
import com.fasterxml.jackson.databind.ObjectMapper;
import pojo.CardPOJO;
import pojo.CollectionPOJO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ModifierDeckController {

    @FXML
    private TextField deckNameField;

    @FXML
    private TextField searchField;

    @FXML
    private VBox availableCardsBox;

    @FXML
    private FlowPane deckCardsPane;

    private final List<Card> currentDeckCards = new ArrayList<>();
    private final List<Card> availableCards = new ArrayList<>();

    private Card selectedCard = null;

    private void loadDeckFromJson(String name) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            CollectionPOJO[] allDecks;

            File externalFile = new File("decks.json");

            if (externalFile.exists()) {
                // Lecture depuis le fichier modifiable par l'utilisateur
                allDecks = mapper.readValue(externalFile, CollectionPOJO[].class);
            } else {
                InputStream input = getClass().getResourceAsStream("/com/example/sae201timeline/data/data.json");
                allDecks = mapper.readValue(input, CollectionPOJO[].class);
            }

            for (CollectionPOJO deck : allDecks) {
                if (deck.name.equalsIgnoreCase(name)) {
                    deckNameField.setText(deck.name);

                    // Conversion + affichage des cartes
                    for (int i = 0; i < deck.cards.length; i++) {
                        CardPOJO c = deck.cards[i];
                        Card card = new Card(c.name, c.date, i + 1, c.url);
                        currentDeckCards.add(card);

                        Button btn = new Button(card.getTitle());
                        btn.setOnAction(e -> {
                            selectedCard = card;
                            updateCardSelectionVisual();
                        });
                        deckCardsPane.getChildren().add(btn);
                    }

                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger le deck sélectionné", Alert.AlertType.ERROR);
        }
    }

    private void updateCardSelectionVisual() {
        for (var node : deckCardsPane.getChildren()) {
            Button b = (Button) node;
            if (b.getText().equals(selectedCard.getTitle())) {
                b.setStyle("-fx-border-color: #00bfff; -fx-border-width: 2;");
            } else {
                b.setStyle("");
            }
        }
    }

    @FXML
    private void onRemoveCard() {
        if (selectedCard != null) {
            currentDeckCards.removeIf(c -> c.getTitle().equals(selectedCard.getTitle()));

            deckCardsPane.getChildren().removeIf(node ->
                    node instanceof Button && ((Button) node).getText().equals(selectedCard.getTitle())
            );

            selectedCard = null;
        } else {
            showAlert("Info", "Aucune carte sélectionnée", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void onSaveDeck() {
        String name = deckNameField.getText().trim();

        // On vérifie uniquement le nom, plus les cartes
        if (name.isEmpty()) {
            showAlert("Erreur", "Le nom du deck ne peut pas être vide", Alert.AlertType.WARNING);
            return;
        }

        // Convertir en CardPOJO[]
        CardPOJO[] cardPOJOS = currentDeckCards.stream().map(card -> {
            CardPOJO c = new CardPOJO();
            c.name = card.getTitle();
            c.date = card.getDate();
            c.url = card.getUrlImage();
            c.description = "À compléter";
            return c;
        }).toArray(CardPOJO[]::new);

        // Meme si vide, on crée quand meme un tableau vide
        if (cardPOJOS.length == 0) {
            cardPOJOS = new CardPOJO[0];
        }

        CollectionPOJO newDeck = new CollectionPOJO();
        newDeck.name = name;
        newDeck.cards = cardPOJOS;

        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File("decks.json");

            CollectionPOJO[] decks = file.exists()
                    ? mapper.readValue(file, CollectionPOJO[].class)
                    : new CollectionPOJO[0];

            List<CollectionPOJO> updatedDecks = new ArrayList<>(List.of(decks));

            updatedDecks.removeIf(d -> d.name.equalsIgnoreCase(name));
            updatedDecks.add(newDeck);

            mapper.writerWithDefaultPrettyPrinter().writeValue(file, updatedDecks);

            showAlert("Succès", "Deck sauvegardé avec succès", Alert.AlertType.INFORMATION);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de sauvegarder le deck", Alert.AlertType.ERROR);
        }
    }

    private String deckNameToLoad;

    public void setDeckName(String name) {
        this.deckNameToLoad = name;
        if (!name.isEmpty()) {
            loadDeckFromJson(name);
        }
    }

    @FXML
    void onAjouterCarte(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sae201timeline/FXML/ModifierCarte.fxml"));
            Parent root = loader.load();

            ModifierCarteController controller = loader.getController();
            controller.setDeckName(deckNameField.getText());

            Stage stage = (Stage) deckNameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir l'éditeur de cartes", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void retourMenu(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(TimeLineApplication.class.getResource("/com/example/sae201timeline/FXML/TimeLine_Acceuil.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}