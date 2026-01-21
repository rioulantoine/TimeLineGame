package com.example.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import pojo.CardPOJO;
import pojo.CollectionPOJO;
import javafx.stage.Stage;
import java.io.File;
import java.net.URL;
import java.util.Arrays;

public class ModifierCarteController {

    @FXML private ComboBox<String> cardSelector;
    @FXML private TextField nameField, dateField, urlField;
    @FXML private TextArea descriptionField;

    private CollectionPOJO[] allDecks;
    private CollectionPOJO currentDeck;
    private CardPOJO selectedCard;
    private File jsonFile;

    @FXML
    public void initialize() {
        try {
            // Chargement du fichier JSON via le classpath
            URL resourceUrl = getClass().getResource("/com/example/sae201timeline/data/data.json");
            if (resourceUrl == null) {
                throw new RuntimeException("Fichier JSON non trouvé dans les ressources !");
            }

            jsonFile = new File(resourceUrl.toURI());

            // Lecture du fichier JSON
            ObjectMapper mapper = new ObjectMapper();
            allDecks = mapper.readValue(jsonFile, CollectionPOJO[].class);

            // Action lors de la sélection d’une carte
            cardSelector.setOnAction(e -> {
                String selected = cardSelector.getValue();
                for (CardPOJO card : currentDeck.cards) {
                    if (card.name.equals(selected)) {
                        selectedCard = card;
                        nameField.setText(card.name);
                        dateField.setText(card.date);
                        descriptionField.setText(card.description);
                        urlField.setText(card.url);
                        break;
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les cartes :\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void saveChanges() {
        if (selectedCard != null) {
            selectedCard.name = nameField.getText();
            selectedCard.date = dateField.getText();
            selectedCard.description = descriptionField.getText();
            selectedCard.url = urlField.getText();

            try {
                ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
                mapper.writeValue(new File("decks.json"), allDecks);

                showAlert("Succès", "Modifications sauvegardées avec succès !", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Erreur", "Impossible de sauvegarder les modifications :\n" + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void goBack() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sae201timeline/FXML/ModifierDeck.fxml"));
            System.out.println(loader);
            Parent root = loader.load();

            Stage newStage = new Stage();
            newStage.setTitle("Modifier Deck");
            newStage.setScene(new Scene(root));
            newStage.show();

            Stage currentStage = (Stage) cardSelector.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de revenir à la page d'accueil", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void createNewCard() {

        CardPOJO newCard = new CardPOJO();
        newCard.name = "Nouvelle carte";
        newCard.date = "";
        newCard.description = "";
        newCard.url = "";

        currentDeck.cards = appendCard(currentDeck.cards, newCard);

        cardSelector.getItems().add(newCard.name);
        cardSelector.setValue(newCard.name);

        nameField.setText(newCard.name);
        dateField.setText(newCard.date);
        descriptionField.setText(newCard.description);
        urlField.setText(newCard.url);

        selectedCard = newCard;
    }

    private CardPOJO[] appendCard(CardPOJO[] cards, CardPOJO newCard) {
        CardPOJO[] result = new CardPOJO[cards.length + 1];
        System.arraycopy(cards, 0, result, 0, cards.length);
        result[cards.length] = newCard;
        return result;
    }

    @FXML
    private void deleteCard() {
        if (selectedCard == null) {
            showAlert("Aucune carte sélectionnée", "Veuillez sélectionner une carte à supprimer.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer la carte \"" + selectedCard.name + "\" ?");
        confirm.setContentText("Cette action est irréversible.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Supprimer la carte de la liste
                currentDeck.cards = removeCard(currentDeck.cards, selectedCard);
                cardSelector.getItems().remove(selectedCard.name);

                // Vider les champs
                cardSelector.setValue(null);
                nameField.clear();
                dateField.clear();
                descriptionField.clear();
                urlField.clear();

                selectedCard = null;

                // Sauvegarder le nouveau JSON
                try {
                    ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
                    mapper.writeValue(new File("decks.json"), allDecks);
                    showAlert("Carte supprimée", "La carte a été supprimée avec succès.", Alert.AlertType.INFORMATION);
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("Erreur", "Impossible de sauvegarder après suppression : " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }
    private String deckName;

    public void setDeckName(String name) {
        try {
            File file = new File("decks.json");
            ObjectMapper mapper = new ObjectMapper();
            allDecks = mapper.readValue(file, CollectionPOJO[].class);

            for (CollectionPOJO deck : allDecks) {
                if (deck.name.equalsIgnoreCase(name)) {
                    currentDeck = deck;
                    break;
                }
            }

            if (currentDeck == null) {
                throw new RuntimeException("Deck introuvable : " + name);
            }

            cardSelector.getItems().clear();
            for (CardPOJO card : currentDeck.cards) {
                cardSelector.getItems().add(card.name);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger le deck :\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private CardPOJO[] removeCard(CardPOJO[] cards, CardPOJO selectedCard) {
        return Arrays.stream(cards)
                .filter(card -> !card.equals(selectedCard))
                .toArray(CardPOJO[]::new);
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}