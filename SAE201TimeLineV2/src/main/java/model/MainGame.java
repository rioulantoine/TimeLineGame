package model;

import pojo.CollectionPOJO;

import java.util.ArrayList;
import java.util.List;

public class MainGame {

    private static final int INITIAL_NB_CARDS = 4;
    private Player player1;
    private Deck deck;
    private List<Card> jeux = new ArrayList<>();
    // Constructeur

    public MainGame() {
        super();
        setupGame();
    }

    public MainGame(CollectionPOJO collection) {
        player1 = new Player("Joueur 1");
        deck = new Deck(collection);

        for (int i = 0; i < INITIAL_NB_CARDS; i++) {
            player1.addInHandCard(deck.drawCard());
        }
    }

    // getters / setter

    public Hand getPlayerHand() {
        return player1.getHand();
    }

    public Player getPlayer1() {
        return player1;
    }

    public Deck getDeck() {
        return deck;
    }
    public List<Card> getJeux() {
        return jeux;
    }

    public void addCarteToJeux(Card carte){
        jeux.add(carte);
    }

    public void setJeux(List<Card> jeux) {
        this.jeux = jeux;
    }
    // logique

    private void setupGame() {
        player1 = new Player("Joueur 1");
        deck = new Deck();

        for (int i = 0; i < INITIAL_NB_CARDS; i++) {
            player1.addInHandCard(deck.drawCard());
        }
    }
}
