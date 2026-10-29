package edu.uic.cs342.poker.controller;

import edu.uic.cs342.poker.model.Player;
import edu.uic.cs342.poker.service.GameService;
import edu.uic.cs342.poker.ui.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import java.util.List;

public class GameController implements ScreenController {
    @FXML private Label phaseLabel, statusLabel, p1Info, p2Info, p1Decision, p2Decision, errorLabel;
    @FXML private HBox dealerCards, p1Cards, p2Cards;
    @FXML private Spinner<Integer> ante1, ante2, pp1, pp2;
    @FXML private CheckBox ppOn1, ppOn2;
    @FXML private Button dealButton, play1, play2, fold1, fold2, menuButton;
    private Navigator navigator;
    private GameService game;
    private boolean revealing;
    @FXML private void initialize() {
        for (Spinner<Integer> spinner : List.of(ante1, ante2, pp1, pp2)) {
            spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 25, 10));
            spinner.setEditable(false); // Bounded values prevent invalid text entries.
        }
        pp1.getValueFactory().setValue(5); pp2.getValueFactory().setValue(5);
        pp1.disableProperty().bind(ppOn1.selectedProperty().not().or(ppOn1.disabledProperty()));
        pp2.disableProperty().bind(ppOn2.selectedProperty().not().or(ppOn2.disabledProperty()));
    }
    @Override public void attach(Navigator navigator) {
        this.navigator = navigator; game = navigator.game();
        if (game.getPlayer(0).getCarriedAnte() > 0) ante1.getValueFactory().setValue(game.getPlayer(0).getCarriedAnte());
        if (game.getPlayer(1).getCarriedAnte() > 0) ante2.getValueFactory().setValue(game.getPlayer(1).getCarriedAnte());
        refresh();
    }
    private void refresh() {
        boolean betting = game.getPhase() == GameService.Phase.BETTING;
        phaseLabel.setText("Round " + game.getRound() + (betting ? " / PLACE WAGERS" : " / PLAY OR FOLD"));
        statusLabel.setText(betting ? "Set an Ante for each player. Pair Plus is optional. Then select Deal Cards." : "Each player must choose Play or Fold. The dealer stays hidden until both decide.");
        dealButton.setDisable(!betting || revealing);
        ante1.setDisable(!betting || game.getPlayer(0).getCarriedAnte() > 0);
        ante2.setDisable(!betting || game.getPlayer(1).getCarriedAnte() > 0);
        ppOn1.setDisable(!betting); ppOn2.setDisable(!betting);
        CardView.render(dealerCards, List.of(), true);
        for (int i = 0; i < 2; i++) {
            Player p = game.getPlayer(i);
            CardView.render(i == 0 ? p1Cards : p2Cards, p.getHand(), false);
            Label info = i == 0 ? p1Info : p2Info;
            info.setText("Ante: $" + p.getAnte() + "   Pair Plus: $" + p.getPairPlus() + "   Play: $" + p.getPlay() + "\nCumulative net winnings: " + money(p.getTotalWinnings()) + (p.getCarriedAnte() > 0 ? "\nAnte carried from last round: $" + p.getCarriedAnte() + " (locked; no new Ante)" : ""));
            Label decision = i == 0 ? p1Decision : p2Decision;
            decision.setText(betting ? "Ante $5-$25 / Pair Plus Off or $5-$25" : switch (p.getDecision()) {
                case WAITING -> "Choose Play or Fold"; case PLAY -> "PLAYED - $" + p.getPlay(); case FOLD -> "FOLDED - $" + (p.getAnte()+p.getPairPlus()) + " forfeited";
            });
            Button play = i == 0 ? play1 : play2, fold = i == 0 ? fold1 : fold2;
            boolean unavailable = betting || revealing || p.getDecision() != Player.Decision.WAITING;
            play.setDisable(unavailable); fold.setDisable(unavailable);
            play.setText(betting ? "Play (after deal)" : "Play - wager $" + p.getAnte());
        }
    }
    public static String money(int value) { return (value < 0 ? "-$" : "+$") + Math.abs(value); }
    @FXML private void deal() {
        try {
            game.deal(ante1.getValue(), ppOn1.isSelected() ? pp1.getValue() : 0, ante2.getValue(), ppOn2.isSelected() ? pp2.getValue() : 0);
            errorLabel.setText(""); refresh();
            CardView.deal(p1Cards); CardView.deal(p2Cards); CardView.deal(dealerCards);
        } catch (IllegalArgumentException | IllegalStateException e) { errorLabel.setText(e.getMessage()); }
    }
    private void decide(int index, boolean fold) {
        Player p = game.getPlayer(index);
        if (fold && !navigator.confirm("Fold Player " + (index+1) + "?", "You will lose your $" + p.getAnte() + " Ante and $" + p.getPairPlus() + " Pair Plus wager.")) return;
        game.decide(index, fold ? Player.Decision.FOLD : Player.Decision.PLAY);
        revealing = game.getPhase() == GameService.Phase.RESULTS;
        refresh();
        if (revealing) {
            menuButton.setDisable(true);
            statusLabel.setText("Both players have decided. Revealing the dealer...");
            CardView.reveal(dealerCards, game.getDealerHand(), () -> {
                PauseTransition pause = new PauseTransition(Duration.millis(400));
                pause.setOnFinished(e -> navigator.results()); pause.play();
            });
        }
    }
    @FXML private void play1() { decide(0, false); }
    @FXML private void play2() { decide(1, false); }
    @FXML private void fold1() { decide(0, true); }
    @FXML private void fold2() { decide(1, true); }
    @FXML private void menu() { navigator.mainMenu(); }
    @FXML private void help() { navigator.help(); }
    @FXML private void theme() { navigator.toggleTheme(); }
}
