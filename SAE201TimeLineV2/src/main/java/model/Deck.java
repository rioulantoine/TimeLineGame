package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import application.io.CardLoader;
import application.io.FAKECardLoader;
import pojo.CardPOJO;
import pojo.CollectionPOJO;

public class Deck {

    private String title;
    private List<Card> cards;

    public Deck() {
        cards = new ArrayList<>();

        setup();
    }

    public Deck(CollectionPOJO collection) {
        this.title = collection.name;
        this.cards = new ArrayList<>();
        int i = 0;
        for (CardPOJO pojo : collection.cards) {
            this.cards.add(new Card(pojo, i++));
        }
        Collections.shuffle(this.cards);
    }

    private void setup() {
        CardLoader loader = new FAKECardLoader();
        loader.load();
        cards = loader.getCards();
        title = loader.getTitle();
        Collections.shuffle(cards);
    }

    public Card drawCard() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.remove(0);
    }

    public boolean hasMoreCards() {
        return !cards.isEmpty();
    }

    public String getTitle() {
        return title;
    }


}
