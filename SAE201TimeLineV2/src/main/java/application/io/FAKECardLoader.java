package application.io;

import model.Card;


public class FAKECardLoader extends CardLoader {

    @Override
    public void load() {
        setTitle("Fake data Title");
        addCard(new Card("TITRE 2", "1950", 1, "https://upload.wikimedia.org/wikipedia/commons/a/a2/Person_Image_Placeholder.png"));
        addCard(new Card("TITRE 4", "1970", 2, "https://upload.wikimedia.org/wikipedia/commons/a/a2/Person_Image_Placeholder.png"));
        addCard(new Card("TITRE 6", "1980", 3, "https://upload.wikimedia.org/wikipedia/commons/a/a2/Person_Image_Placeholder.png"));
        addCard(new Card("TITRE 8", "1990", 4, "https://upload.wikimedia.org/wikipedia/commons/a/a2/Person_Image_Placeholder.png"));
        addCard(new Card("TITRE 3", "1965", 5, "https://upload.wikimedia.org/wikipedia/commons/a/a2/Person_Image_Placeholder.png"));
        addCard(new Card("TITRE 5", "1970", 2, "https://upload.wikimedia.org/wikipedia/commons/a/a2/Person_Image_Placeholder.png"));
        addCard(new Card("TITRE 7", "1986", 3, "https://upload.wikimedia.org/wikipedia/commons/a/a2/Person_Image_Placeholder.png"));
        addCard(new Card("TITRE 9", "2020", 4, "https://upload.wikimedia.org/wikipedia/commons/a/a2/Person_Image_Placeholder.png"));
        addCard(new Card("TITRE 1", "1933", 5, "https://upload.wikimedia.org/wikipedia/commons/a/a2/Person_Image_Placeholder.png"));

    }

}
