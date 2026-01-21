package com.example.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import model.Card;
import model.MainGame;
import model.Player;
import pojo.CollectionPOJO;
import view.CardViewOnHand;

import javax.smartcardio.CommandAPDU;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class JeuxController {
    private MainGame game;
    @FXML
    private HBox handBox;
    @FXML
    private HBox handBox1;
    @FXML
    private HBox timelineBox;
    @FXML
    private Label TempsEntreTours;
    @FXML
    private Label titreDeck;
    @FXML
    private Label timerLabel;
    @FXML
    private Label score;

    private Timeline TimerPartie;
    private Timeline TimerTourJoueur;
    private int tempsParTour;
    private int secondesPasses = 0;
    private int tempsRestantTour;
    private Card selectedCard;
    private MainGame mainGame;
    Player currentPlayer;
    private int scoreP1=0;
    private CollectionPOJO currentDeck;

    public JeuxController() {
        super();
    }


    @FXML
    public void initialize() {
        System.out.println(mainGame.getJeux());
        initUI(); // Nettoie la main
        if (mainGame == null) {
            System.err.println(" Erreur");
            return;
        }
        // Récupère le joueur 1 pour simplifier la suite
        currentPlayer = mainGame.getPlayer1();
        // Affiche les cartes de la main
        initUIFromModel();

        // Place la première carte sur la timeline
        Card timelineCard = mainGame.getDeck().drawCard();
        if (timelineCard != null) {
            addCardToTimeline(timelineCard);
        } else {
            System.err.println(" Le deck est vide ");
        }

        // Empêche le drag/drop sans effet
        timelineBox.setOnDragOver(event -> event.consume());
        timelineBox.setOnDragDropped(event -> {
            event.setDropCompleted(false);
            event.consume();
        });

        // Démarre les timers (jeu + tour)
        startChrono();
        // démarre après affichage UI
        Platform.runLater(this::startChronoTour);
    }

    private void initUI() {
        handBox.getChildren().clear();
        selectedCard = null;
    }

    private void initUIFromModel() {
        titreDeck.setText(mainGame.getDeck().getTitle());
        ajouterCarteMainDuJoueur();
    }

    private void ajouterCarteMainDuJoueur(){
        for (Card card : currentPlayer.getHand().getCards()) {
            System.out.println("Carte ajoutée à la main : " + card.getTitle() + " (" + card.getDate() + ")");
            CardOnHandController controller = new CardOnHandController(card, this);
            CardViewOnHand view = new CardViewOnHand(controller, false);
            handBox.getChildren().add(view);
        }
    }

    // Ajoute une carte à la timeline
    private void addCardToTimeline(Card card) {
        CardOnHandController controller = new CardOnHandController(card, this);
        CardViewOnHand view = new CardViewOnHand(controller, false);

        if (timelineBox.getChildren().isEmpty()) {
            timelineBox.getChildren().add(createDropPane(view, true)); // à gauche
            timelineBox.getChildren().add(view);
            timelineBox.getChildren().add(createDropPane(view, false)); // à droite
            view.setDate(card.getDate().toString());
        } else {
            timelineBox.getChildren().remove(timelineBox.getChildren().size() - 1); // retire dernier drop
            timelineBox.getChildren().add(view);
            timelineBox.getChildren().add(createDropPane(view, false)); // nouveau drop à droite
        }
    }



    // Crée une zone de drop
    private Pane createDropPane(CardViewOnHand draggedCardView, boolean isLeft) {
        Pane dropPane = new Pane();

        dropPane.setPrefWidth(0);
        dropPane.setMinWidth(20);
        dropPane.setMaxWidth(80);
        dropPane.setPrefHeight(108);
        dropPane.setMinHeight(108);
        dropPane.setMaxHeight(108);
        dropPane.setStyle("-fx-background-color: transparent; -fx-background-radius: 15;");

        dropPane.setOnDragOver(event -> {
            if (event.getGestureSource() instanceof CardViewOnHand && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        dropPane.setOnDragEntered(event -> {
            dropPane.setPrefWidth(80);
            dropPane.setStyle(
                    "-fx-background-color: #796969;" +
                            "-fx-background-radius: 15;" +
                            "-fx-border-color: white;" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 15;"
            );
        });

        dropPane.setOnDragExited(event -> {
            dropPane.setPrefWidth(0);
            dropPane.setStyle("-fx-background-color: transparent; -fx-background-radius: 15;");
        });

        dropPane.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (event.getGestureSource() instanceof CardViewOnHand && db.hasString()) {
                CardViewOnHand dragged = (CardViewOnHand) event.getGestureSource();
                Card cardModel = dragged.getCard();
                int index = timelineBox.getChildren().indexOf(dropPane);

                // Vérifie si la position est correcte
                if (!isPositionOk(cardModel, index)) {
                    index = trouverBonIndex(cardModel);

                    cardModel.setBienPlacer(false);
                    mainGame.addCarteToJeux(cardModel);

                    System.out.println("Mauvais emplacement !");
                    ajouterCarteSupplementaire();
                    TimerTourJoueur.stop();
                    startChronoTour();
                    event.setDropCompleted(false);
                    event.consume();
                    int scoreJ1 = mainGame.getPlayer1().getScore()-1;
                    scoreP1--;
                    mainGame.getPlayer1().setScore(scoreJ1);
                    score.setText(String.valueOf(scoreJ1));

                    // Supprimer de la main
                    currentPlayer.getHand().removeCard(cardModel);
                    handBox.getChildren().remove(dragged);

                    // Créer la carte et afficher la date
                    CardOnHandController controller = new CardOnHandController(cardModel, this);
                    CardViewOnHand newView = new CardViewOnHand(controller, false);
                    newView.setDate(cardModel.getDate());
                    newView.setDateColor("#FF0000");

                    // Ajouter les drop zone
                    Pane newLeft = createDropPane(newView, true);
                    Pane newRight = createDropPane(newView, false);

                    // Ajouter à la timeline
                    timelineBox.getChildren().add(index, newRight);
                    timelineBox.getChildren().add(index, newView);
                }
                else {

                    cardModel.setBienPlacer(true);
                    mainGame.addCarteToJeux(cardModel);

                    int scoreJ1 = mainGame.getPlayer1().getScore()+3;
                    scoreP1+=3;
                    ajouterCarteSupplementaire();
                    mainGame.getPlayer1().setScore(scoreJ1);
                    score.setText(String.valueOf(scoreJ1));
                    System.out.println("Bon emplacement !");

                    // Supprimer de la main
                    currentPlayer.getHand().removeCard(cardModel);
                    handBox.getChildren().remove(dragged);

                    // Créer la carte et afficher la date
                    CardOnHandController controller = new CardOnHandController(cardModel, this);
                    CardViewOnHand newView = new CardViewOnHand(controller, false);
                    newView.setDate(cardModel.getDate());
                    newView.setDateColor("#00FF00");

                    // Ajouter les drop zone
                    Pane newLeft = createDropPane(newView, true);
                    Pane newRight = createDropPane(newView, false);

                    // Ajouter à la timeline
                    timelineBox.getChildren().remove(dropPane);
                    timelineBox.getChildren().add(index, newRight);
                    timelineBox.getChildren().add(index, newView);
                    timelineBox.getChildren().add(index, newLeft);

                    success = true;
                }

            }

            //Si le placement est bon, on relance le timer du Tour en arretant le précedent avant.
            TimerTourJoueur.stop();
            startChronoTour();
            event.setDropCompleted(success);
            event.consume();
            //Vérifie si fin de partie
            checkFinPartie();
        });

        return dropPane;
    }

    private int trouverBonIndex(Card card) {
        int index = 0;

        for (int i = 0; i < timelineBox.getChildren().size(); i++) {
            if (timelineBox.getChildren().get(i) instanceof CardViewOnHand view) {
                Card currentCard = view.getCard();
                if (Integer.valueOf(card.getDate()) < Integer.valueOf(currentCard.getDate())) {
                    break;
                }
            }
            index++;
        }

        return index;
    }

    private boolean isPositionOk(Card card, int index) {
        Integer date = Integer.parseInt(card.getDate());
        Integer gauche = null;
        Integer droite = null;

        for (int i = index - 1; i >= 0 && gauche == null; i--) {
            if (timelineBox.getChildren().get(i) instanceof CardViewOnHand view) {
                gauche = Integer.parseInt(view.getCard().getDate());
            }
        }

        for (int i = index + 1; i < timelineBox.getChildren().size() && droite == null; i++) {
            if (timelineBox.getChildren().get(i) instanceof CardViewOnHand view) {
                droite = Integer.parseInt(view.getCard().getDate());
            }
        }

        if (gauche != null && droite != null)
            return date >= gauche && date <= droite;
        if (gauche != null)
            return date >= gauche;
        if (droite != null)
            return date <= droite;

        return true;

    }

    //Vérifie si la main du joueur est vide pour un affichage de fin de partie
    private void checkFinPartie() {
        //Si la main du joueur est vide :
        int nbrCarte = nombreCarteBienPlace();

        if (currentPlayer.getHand().getCards().isEmpty() && !mainGame.getDeck().hasMoreCards()) {


            TimerPartie.stop(); // stop le chrono

            int minutes = secondesPasses / 60;
            int seconds = secondesPasses % 60;

            String resultMessage =  "Le joueur "+mainGame.getPlayer1().getName()+" a perdu, il n'y a plus de carte dans le deck\n" +
                    "Vous avez tout de même bien placé "+ nbrCarte + " cartes\n"+
                    "La partie à durée : " + String.format("%02d:%02d", minutes, seconds);
            finDePartie(resultMessage);

        }
        if (currentPlayer.getHand().getCards().isEmpty()) {
            TimerPartie.stop(); // stop le chrono

            int minutes = secondesPasses / 60;
            int seconds = secondesPasses % 60;

            String resultMessage = "Félicitations ! " + currentPlayer.getName() + " a gagné avec  " + nbrCarte + " cartes bien placées.\nTemps : " + String.format("%02d:%02d", minutes, seconds);
            finDePartie(resultMessage);

        } else if (tempsRestantTour <= 0) {
            // Si le temps est écoulé, afficher une autre page modale avec un message de perte
            String resultMessage = "Temps écoulé ! Vous avez perdu.\nVous avez perdu avec : " + nbrCarte + " cartes bien placées.";
            finDePartie(resultMessage);
        }
    }

    private int nombreCarteBienPlace() {
        int compte=0;
        for (Card carte : mainGame.getJeux()){
            if (carte.isBienPlacer()){
                compte++;
            }
        }
        return compte;
    }

    //Affichage de fin
    public void finDePartie(String resultMessage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sae201timeline/FXML/FinDePartie.fxml"));
            Scene sceneFinPartie = new Scene(loader.load());
            FinDePartieController finDePartieController = loader.getController();
            finDePartieController.initialize(resultMessage);
            Stage stage = (Stage) handBox.getScene().getWindow();
            stage.setScene(sceneFinPartie);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors du chargement de la scène de fin de partie.");
            alert.showAndWait();
        }
    }

    //Rajoute une carte a la main du joueur si il s'est trompé :
    private void ajouterCarteSupplementaire() {
        Card nouvelleCarte = mainGame.getDeck().drawCard();
        if (nouvelleCarte != null) {
            currentPlayer.getHand().addCard(nouvelleCarte);
            CardOnHandController controller = new CardOnHandController(nouvelleCarte, this);
            CardViewOnHand view = new CardViewOnHand(controller, false);
            handBox.getChildren().add(view);
        }
    }

    //Timer de la partie - lancement
    private void startChrono() {
        TimerPartie = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            secondesPasses++;
            int minutes = secondesPasses / 60;
            int seconds = secondesPasses % 60;
            timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
        }));
        TimerPartie.setCycleCount(Timeline.INDEFINITE);
        TimerPartie.play();
    }

    //Timer du tour du joueur
    private void startChronoTour() {
        if (tempsParTour == -1) {
            TempsEntreTours.setText("Temps illimité");
            return;
        }

        tempsRestantTour = tempsParTour;
        TempsEntreTours.setText(String.valueOf(tempsRestantTour));

        TimerTourJoueur = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            tempsRestantTour--;
            if (tempsRestantTour > 0) {
                TempsEntreTours.setText(String.valueOf(tempsRestantTour));
            } else {
                TimerTourJoueur.stop();
                TempsEntreTours.setText("Temps écoulé !");
                System.out.println(" Temps écoulé : perdu !");
                String resultMessage = "Temps écoulé ! Vous avez perdu.\nScore : " + scoreP1 + " cartes bien placées.";
                finDePartie(resultMessage);
            }
        }));

        TimerTourJoueur.setCycleCount(tempsParTour); // 60 tours = 60s
        TimerTourJoueur.play();
    }

    // Bouton retour au menu
    @FXML
    void retourMenu(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoaderMenuJeu = new FXMLLoader(TimeLineApplication.class.getResource("/com/example/sae201timeline/FXML/TimeLine_Acceuil.fxml"));
        Scene sceneMenuJeu = new Scene(fxmlLoaderMenuJeu.load());
        Button btn1 = (Button) event.getSource();
        Stage stg1 = (Stage) btn1.getScene().getWindow();
        stg1.setScene(sceneMenuJeu);
    }

    public void setTempsParTour(int tempsParTour) {
        this.tempsParTour = tempsParTour;
    }


    public void setDeckName(String selectedDeckName) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File("decks.json");
            CollectionPOJO[] allDecks = mapper.readValue(file, CollectionPOJO[].class);

            for (CollectionPOJO deck : allDecks) {
                if (deck.name.equalsIgnoreCase(selectedDeckName)) {
                    currentDeck = deck;
                    break;
                }
            }

            if (currentDeck == null) {
                throw new RuntimeException("Deck non trouvé : " + selectedDeckName);
            }

            // Création du vrai jeu avec le deck sélectionné
            mainGame = new MainGame(currentDeck);
            currentPlayer = mainGame.getPlayer1();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors du chargement du deck :\n" + e.getMessage());
            alert.showAndWait();
        }
    }
    @FXML
    void afficherRegles(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(Objects.requireNonNull(TimeLineApplication.class.getResource("/com/example/sae201timeline/FXML/Regles.fxml")));
        stage.setScene(new Scene(root));
        stage.setTitle("Règles");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(((Node)event.getSource()).getScene().getWindow() );
        stage.initStyle(StageStyle.UNIFIED);
        stage.show();
    }

}