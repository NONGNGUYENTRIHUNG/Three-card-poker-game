package edu.uic.cs342.poker.controller;

import edu.uic.cs342.poker.model.Player;
import edu.uic.cs342.poker.logic.ThreeCardLogic;
import edu.uic.cs342.poker.service.*;
import edu.uic.cs342.poker.ui.*;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

public class ResultsController implements ScreenController {
    @FXML private Label roundLabel, dealerInfo, p1Summary, p2Summary, p1Outcome, p2Outcome, p1Total, p2Total;
    @FXML private HBox dealerCards, p1Cards, p2Cards;
    private Navigator navigator;
    @Override public void attach(Navigator navigator) {
        this.navigator = navigator;
        GameService game = navigator.game();
        roundLabel.setText("Round " + game.getRound() + " / COMPLETE");
        CardView.render(dealerCards, game.getDealerHand(), false);
        dealerInfo.setText(ThreeCardLogic.handName(game.getDealerHand()) + " / " + (game.dealerQualifies() ? "Dealer qualifies" : "Dealer does not qualify - Play returned; Ante carried for nonfolded hands"));
        for (int i = 0; i < 2; i++) {
            Player p = game.getPlayer(i); RoundResult r = game.getResult(i);
            CardView.render(i == 0 ? p1Cards : p2Cards, p.getHand(), false);
            (i == 0 ? p1Outcome : p2Outcome).setText(ThreeCardLogic.handName(p.getHand()) + " / " + r.outcome());
            (i == 0 ? p1Summary : p2Summary).setText("Ante wager $" + p.getAnte() + " / net " + GameController.money(r.anteNet())
                    + "\nPlay wager $" + p.getPlay() + " / net " + GameController.money(r.playNet())
                    + "\nPair Plus wager $" + p.getPairPlus() + " / net " + GameController.money(r.pairPlusNet())
                    + "\nReturned original stakes: $" + r.returnedStakes()
                    + "\nAnte held for next round: $" + r.carriedAnte()
                    + "\nRound net: " + GameController.money(r.roundNet()));
            Label total = i == 0 ? p1Total : p2Total;
            total.setText("Cumulative net: " + GameController.money(r.totalNet()));
            ScaleTransition pulse = new ScaleTransition(Duration.millis(320), total);
            pulse.setFromX(1); pulse.setFromY(1); pulse.setToX(1.07); pulse.setToY(1.07);
            pulse.setAutoReverse(true); pulse.setCycleCount(2); pulse.play();
        }
    }
    @FXML private void next() { navigator.nextRound(); }
    @FXML private void menu() { navigator.mainMenu(); }
    @FXML private void help() { navigator.help(); }
    @FXML private void theme() { navigator.toggleTheme(); }
}
