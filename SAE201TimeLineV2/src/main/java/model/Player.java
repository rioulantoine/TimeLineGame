package model;

public class Player {

    private String name;
    private int score;
    private Hand hand;

    public Player(String name) {
        this.name = name;
        this.score = 0;
        hand = new Hand();
    }

    public void addPoints(int points) {
        score += points;
    }

    public int getScore() {
        return score;
    }

    public String getName() {
        return name;
    }

    public void addInHandCard(Card card) {
        hand.addCard(card);
    }

    public boolean hasMoreCardsInHand() {
        return hand.hasMoreCards();
    }

    public Hand getHand() {
        return hand;
    }

    public void setScore(int i) {
        this.score=i;
    }
}

