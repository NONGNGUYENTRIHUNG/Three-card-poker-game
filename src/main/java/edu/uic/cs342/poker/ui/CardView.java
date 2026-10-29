package edu.uic.cs342.poker.ui;

import edu.uic.cs342.poker.model.Card;
import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import java.util.List;

/** Creates card visuals only; it does not evaluate or deal cards. */
public final class CardView {
    private CardView() { }
    public static void render(HBox box, List<Card> hand, boolean hidden) {
        box.getChildren().clear();
        for (int i = 0; i < 3; i++) {
            Card card = hand.isEmpty() ? null : hand.get(i);
            Label label = new Label(hidden ? "?" : card == null ? "—" : card.toString());
            label.getStyleClass().add("card");
            if (hidden) label.getStyleClass().add("card-back");
            if (!hidden && card != null && (card.suit() == 'H' || card.suit() == 'D')) label.getStyleClass().add("red-card");
            label.setAccessibleText(hidden ? "Hidden dealer card" : card == null ? "Empty card slot" : card.rank() + " of " + switch(card.suit()) { case 'H' -> "hearts"; case 'D' -> "diamonds"; case 'C' -> "clubs"; default -> "spades"; });
            box.getChildren().add(label);
        }
    }
    public static void deal(HBox box) {
        int i = 0;
        for (Node node : box.getChildren()) {
            TranslateTransition move = new TranslateTransition(Duration.millis(260), node);
            move.setFromY(-35); move.setToY(0); move.setFromX(-18); move.setToX(0);
            move.setDelay(Duration.millis(i++ * 80)); move.play();
        }
    }
    public static void reveal(HBox box, List<Card> hand, Runnable finished) {
        ScaleTransition hide = new ScaleTransition(Duration.millis(190), box);
        hide.setFromX(1); hide.setToX(0);
        hide.setOnFinished(e -> {
            render(box, hand, false);
            ScaleTransition show = new ScaleTransition(Duration.millis(190), box);
            show.setFromX(0); show.setToX(1);
            show.setOnFinished(event -> finished.run()); show.play();
        }); hide.play();
    }
}
