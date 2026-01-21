package model;

import java.util.ArrayList;
import java.util.List;

public class Hand {

    private Card selectedCard;
    private List<Card> cards;

    public Hand() {
        cards = new ArrayList<>();
        selectedCard = null;
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public boolean hasMoreCards() {
        return !cards.isEmpty();
    }

    public List<Card> getCards() {
        return cards;
    }

    public boolean hasOneCardSelected() {
        return selectedCard != null;
    }

    public Card getSelectedCard() {
        return selectedCard;
    }

    public void removeCard(Card card) {
        cards.remove(card);
    }

}
