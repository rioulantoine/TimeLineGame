package com.example.application;

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
import model.MainGameADeux;
import model.Player;
import pojo.CollectionPOJO;
import view.CardViewOnHand;

import java.io.IOException;
import java.util.Objects;

public class JeuxADeuxController extends JeuxController{
    @FXML
    private HBox handBoxJoueurUn;
    @FXML
    private HBox handBoxJoueurDeux;
    @FXML
    private HBox timelineBox;
    @FXML
    private Label TempsEntreTours;
    @FXML
    private Label titreDeck;
    @FXML
    private Label timerLabel;
    @FXML
    private Label scoreJoueurUn;
    @FXML
    private Label scoreJoueurDeux;
    @FXML
    private Label numeroJoueurAcuelLabel;
    @FXML
    private Pane bancJoueurDeux;
    @FXML
    private Pane bancJoueurUn;

    private Timeline TimerPartie;
    private Timeline TimerTourJoueur;
    private int tempsParTour;
    private int secondesPasses = 0;
    private int tempsRestantTour;
    private Card selectedCard;
    private MainGameADeux mainGame = new MainGameADeux();
    Player currentPlayer;
    private boolean deckInjecte = false;
    public JeuxADeuxController() {
        super();
    }

    public void initialize(){
        initUI(); // Nettoie la main
        if (!deckInjecte) {
            mainGame = new MainGameADeux();
        }

        currentPlayer = mainGame.getPlayer1();
        initUIFromModel(); // Affiche les cartes en main

        // Ajoute une première carte à la timeline
        Card timelineCard = mainGame.getDeck().drawCard();
        if (timelineCard != null) {
            addCardToTimeline(timelineCard);
        } else {
            System.err.println("Le deck est vide.");
        }

        timelineBox.setOnDragOver(event -> event.consume());
        timelineBox.setOnDragDropped(event -> {
            event.setDropCompleted(false);
            event.consume();
        });

        numeroJoueurAcuelLabel.setText("1");

        startChrono();
        Platform.runLater(this::startChronoTour);
    }

    private void initUI() {
        handBoxJoueurUn.getChildren().clear();
        handBoxJoueurDeux.getChildren().clear();
        selectedCard = null;
    }

    private void initUIFromModel() {
        titreDeck.setText(mainGame.getDeck().getTitle());
        ajouterCarteMainDuJoueur();
    }

    private void ajouterCarteMainDuJoueur(){
        initUI();
        for (Card card : currentPlayer.getHand().getCards()) {
            if (currentPlayer==mainGame.getPlayer1()){
                CardOnHandController controller =  new CardOnHandController( card , this);
                CardViewOnHand view = new CardViewOnHand(controller,false);
                handBoxJoueurUn.getChildren().add(view);
            }
            else {
                CardOnHandController controller =  new CardOnHandController( card , this);
                CardViewOnHand view = new CardViewOnHand(controller,false);
                handBoxJoueurDeux.getChildren().add(view);
            }
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
        dropPane.setMouseTransparent(false);

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

                cardModel.setJoueur(currentPlayer);

                int index = timelineBox.getChildren().indexOf(dropPane);

                // Vérifie si la position est correcte
                if (!isPositionOk(cardModel, index)) {
                    index = trouverBonIndex(cardModel);

                    cardModel.setBienPlacer(false);
                    cardModel.setJoueur(currentPlayer);
                    mainGame.addCarteToJeux(cardModel);

                    // Supprimer de la main
                    currentPlayer.getHand().removeCard(cardModel);
                    if (currentPlayer==mainGame.getPlayer1()) {
                        handBoxJoueurUn.getChildren().remove(dragged);
                    } else {
                        handBoxJoueurDeux.getChildren().remove(dragged);
                    }

                    System.out.println("Mauvais emplacement !");
                    ajouterCarteSupplementaire();
                    changerJoueurQuiJoue();
                    TimerTourJoueur.stop();
                    startChronoTour();
                    event.setDropCompleted(false);
                    event.consume();
                    currentPlayer.setScore(currentPlayer.getScore()-1);
                    afficherScore();



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
                    cardModel.setJoueur(currentPlayer);
                    mainGame.addCarteToJeux(cardModel);

                    cardModel.setBienPlacer(true);
                    currentPlayer.setScore(currentPlayer.getScore()+3);
                    afficherScore();
                    System.out.println("Bon emplacement !");

                    // Supprimer de la main la carte
                    currentPlayer.getHand().removeCard(cardModel);
                    if (currentPlayer==mainGame.getPlayer1()) {
                        handBoxJoueurUn.getChildren().remove(dragged);
                    } else {
                        handBoxJoueurDeux.getChildren().remove(dragged);
                    }

                    // Créer la carte et afficher la date
                    CardOnHandController controller = new CardOnHandController(cardModel, this);
                    CardViewOnHand newView = new CardViewOnHand(controller, false);
                    newView.setDate(cardModel.getDate());
                    newView.setDateColor("#00FF00");

                    // Ajouter drop zones
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

            //Vérifie si fin de partie
            checkFinPartie();
            changerJoueurQuiJoue();

            event.setDropCompleted(success);
            event.consume();
        });


        return dropPane;
    }

    private void changerJoueurQuiJoue() {
        inverserMain();
        changerCurrentPlayer();
        ajouterCarteMainDuJoueur();
    }

    private void changerCurrentPlayer() {
        if (currentPlayer==mainGame.getPlayer1()){
            currentPlayer=mainGame.getPlayer2();
            numeroJoueurAcuelLabel.setText(mainGame.getPlayer2().getName());
        }
        else {
            currentPlayer=mainGame.getPlayer1();
            numeroJoueurAcuelLabel.setText(mainGame.getPlayer1().getName());
        }
    }

    private void inverserMain() {
        if (currentPlayer==mainGame.getPlayer1()){
            bancJoueurDeux.setVisible(true);
            bancJoueurUn.setVisible(false);
        }
        else {
            bancJoueurDeux.setVisible(false);
            bancJoueurUn.setVisible(true);
        }

    }

    private void afficherScore() {
        scoreJoueurUn.setText(String.valueOf(mainGame.getPlayer1().getScore()));
        scoreJoueurDeux.setText(String.valueOf(mainGame.getPlayer2().getScore()));
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
        Player Gagant = currentPlayer;
        changerCurrentPlayer();
        Player Perdant = currentPlayer;

        if (currentPlayer.getHand().getCards().isEmpty() && !mainGame.getDeck().hasMoreCards()) {
            Player temp = Gagant;
            Gagant = Perdant;
            Perdant = temp;

            TimerPartie.stop(); // stop le chrono

            int minutes = secondesPasses / 60;
            int seconds = secondesPasses % 60;

            String resultMessage =  "Félicitations !\n" +
                    Gagant.getName() + " a gagné la partie parce que "+Perdant.getName()+" n'avait plus de carte à pioché"+
                    "\nLa partie à durée : " + String.format("%02d:%02d", minutes, seconds);
            finDePartie(resultMessage);

        }
        //Si la main du joueur est vide :
        if (currentPlayer.getHand().getCards().isEmpty()) {
            TimerPartie.stop(); // stop le chrono

            int minutes = secondesPasses / 60;
            int seconds = secondesPasses % 60;

            String resultMessage = "Félicitations ! \n" + Gagant.getName() + " a gagné avec " + nombreCarteBienPlace(Gagant) + " \n "+ Perdant.getName()+"Perd avec "+ nombreCarteBienPlace(Perdant) +" bien placées \nLa partie à durée : " + String.format("%02d:%02d", minutes, seconds);
            finDePartie(resultMessage);

        } else if (tempsRestantTour <= 0) {
            // Si le temps est écoulé, afficher une autre page modale avec un message de perte
            String resultMessage = "Temps écoulé ! Vous avez perdu.\nScore : " + mainGame.getPlayer1().getScore() + " cartes bien placées pour J1 et "+ mainGame.getPlayer2().getScore() + " cartes bien placées pour J2";
            finDePartie(resultMessage);
        }
    }

    private int nombreCarteBienPlace(Player joueur) {
        int compte=0;
        for (Card carte : mainGame.getJeux()){
            if (carte.isBienPlacer()){
                if (carte.getJoueur()==joueur){
                    compte++;
                }
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
            Stage stage = (Stage) timelineBox.getScene().getWindow();
            stage.setScene(sceneFinPartie);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
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
            if (currentPlayer==mainGame.getPlayer1()) {
                handBoxJoueurUn.getChildren().add(view);
            } else {
                handBoxJoueurDeux.getChildren().add(view);
            }

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
                String resultMessage = "Temps écoulé ! Vous avez perdu.\nScore : " + mainGame.getPlayer1().getScore() + " cartes bien placées pour J1 \n "+ mainGame.getPlayer2().getScore() + " cartes bien placées pour J2" ;
                finDePartie(resultMessage);
            }
        }));

        TimerTourJoueur.setCycleCount(tempsParTour); // 60 tours = 60s
        TimerTourJoueur.play();
    }


    public void setDeck(CollectionPOJO selectedDeck) {
        mainGame = new MainGameADeux(selectedDeck);
        deckInjecte = true;
    }

    public void setTempsParTour(int tempsParTour) {
        this.tempsParTour = tempsParTour;
    }


    @FXML
    void afficherRegles(ActionEvent event) throws IOException {
        System.out.println("==> Bouton cliqué !");

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