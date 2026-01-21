package com.example.application;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.Card;
import view.CardViewOnHand;

import java.util.Objects;

public class CardOnHandController {

    private JeuxController mainController;
    private CardViewOnHand view;
    private Card controlledCard;
    @FXML
    private Label DateLabel;

    @FXML
    private ImageView Image;

    @FXML
    private Label nomLabel;


    public CardOnHandController(Card aCard, JeuxController controllerMainScreen) {
        this.controlledCard = aCard;
        mainController = controllerMainScreen;
    }

    public Card getCard() {
        return this.controlledCard;  // si le champ s'appelle card
    }

    public void initView() {
        view.setTitle(controlledCard.getTitle());
        view.setCardImage(util.ImageManager.getInstance().getImage(controlledCard.getUrlImage()));
    }



    public void setView(CardViewOnHand cardViewOnHand) {
        view = cardViewOnHand;
    }

    public Object getMainController() {
        return mainController;
    }

}
