package view;

import java.io.IOException;

import com.example.application.CardOnHandController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import model.Card;

public class CardViewOnHand extends Pane {

    private CardOnHandController controller;

    private ImageView cardImage;
    private Label cardTitle;

    public CardViewOnHand(CardOnHandController controller, boolean cardIsSelected) {
        super();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sae201timeline/FXML/CardView.fxml"));
        try {
            this.controller = controller;
            Parent root = loader.load();
            setScaleX(0.8);
            setScaleY(0.8);
            controller.setView(this);
            cardTitle = ((Label) root.lookup("#nomLabel"));
            cardImage = ((ImageView) root.lookup("#Image"));


            if (cardIsSelected) {
                root.setScaleX(1.);
                root.setScaleY(1.);
            }

            this.getChildren().add(root);
            controller.initView();


            this.setOnDragDetected(event -> {
                Dragboard db = this.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString("card");
                db.setContent(content);

                SnapshotParameters snapParams = new SnapshotParameters();
                snapParams.setFill(Color.TRANSPARENT);
                db.setDragView(this.snapshot(snapParams, null));

                event.consume();
            });

            this.setOnDragDone(event -> {
                if (!event.isDropCompleted()) {
                    this.setVisible(true);
                }
            });

        } catch (IOException e) {
            System.err.println("Problem while loading the card fxml");
        }
    }

    public void setDate(String text) {
        Label dateLabel = (Label) this.lookup("#DateLabel");
        if (dateLabel != null) {
            dateLabel.setText(text);
            dateLabel.setVisible(true);
        }
    }

    public Card getCard() {
        return controller.getCard();
    }

    public void setTitle(String text) {
        cardTitle.setText(text);
    }

    public void setCardImage(Image image) {
        cardImage.setImage(image);
    }

    //FeedBack de carte placer
    public void setDateColor(String couleur) {
        Label dateLabel = (Label) this.lookup("#DateLabel");
        if (dateLabel != null) {
            dateLabel.setStyle("-fx-text-fill: " + couleur + ";");
        }
    }

}
