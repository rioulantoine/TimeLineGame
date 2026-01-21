package model;

import pojo.CollectionPOJO;

import java.util.ArrayList;
import java.util.List;

public class MainGameADeux {

    private static final int INITIAL_NB_CARDS = 3;
    private Player player1;
    private Player player2;
    private Deck deck;
    private List<Card> jeux = new ArrayList<>();

    // Constructeur

    public MainGameADeux() {
        super();
        setupGame();
    }

    public MainGameADeux(CollectionPOJO deckData) {
        player1 = new Player("Joueur 1");
        player2 = new Player("Joueur 2");
        deck = new Deck(deckData);

        for (int i = 0; i < INITIAL_NB_CARDS; i++) {
            player1.addInHandCard(deck.drawCard());
            player2.addInHandCard(deck.drawCard());
        }
    }

    // getters / setter

    public List<Card> getJeux() {
        return jeux;
    }

    public void addCarteToJeux(Card carte){
        jeux.add(carte);
    }

    public void setJeux(List<Card> jeux) {
        this.jeux = jeux;
    }

    public Hand getPlayerHand() {
        return player1.getHand();
    }

    public Player getPlayer1() {
        return player1;
    }
    public Player getPlayer2() {
        return player2;
    }

    public Deck getDeck() {
        return deck;
    }

    // logique

    private void setupGame() {
        player1 = new Player("Joueur 1");
        player2= new Player("Joueur 2");
        deck = new Deck();

        for (int i = 0; i < INITIAL_NB_CARDS; i++) {
            player1.addInHandCard(deck.drawCard());
            player2.addInHandCard(deck.drawCard());
        }
    }


}
