package edu.uic.cs342.poker.ui;

import edu.uic.cs342.poker.Main;
import edu.uic.cs342.poker.controller.ScreenController;
import edu.uic.cs342.poker.service.GameService;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

public class Navigator {
    private final Stage stage;
    private GameService game = new GameService();
    private boolean light, inGame;
    public Navigator(Stage stage) { this.stage = stage; }
    public GameService game() { return game; }
    public void toggleTheme() { light = !light; applyTheme(); }
    private void applyTheme() {
        stage.getScene().getStylesheets().setAll(Main.class.getResource("/edu/uic/cs342/poker/css/" + (light ? "light.css" : "style.css")).toExternalForm());
    }
    public void welcome() { inGame = false; show("welcome"); }
    public void startGame() { game = new GameService(); inGame = true; show("game"); }
    public void gameScreen() { inGame = true; show("game"); }
    public void results() { show("results"); }
    public void nextRound() { game.nextRound(); gameScreen(); }
    public void mainMenu() { if (confirmExit()) welcome(); }
    public boolean confirmExit() {
        if (!inGame) return true;
        String carry = game.getPlayer(0).getCarriedAnte() > 0 || game.getPlayer(1).getCarriedAnte() > 0 ? " Any carried Ante will also be abandoned." : "";
        return confirm("Leave this game?", "Returning to the menu or closing resets session winnings and ends this game." + carry);
    }
    public boolean confirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.CANCEL, ButtonType.OK);
        alert.initOwner(stage); alert.setTitle(title); alert.setHeaderText(title);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
    public void exit() { if (confirmExit()) stage.close(); }
    public void help() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(stage); alert.setTitle("How to Play"); alert.setHeaderText("Two players, independently against one dealer");
        alert.setContentText("1. Each player places an Ante of $5-$25. Pair Plus is optional ($5-$25).\n2. Deal, then choose Play (adds a wager equal to Ante) or Fold (loses Ante and Pair Plus).\n3. After both decisions, the dealer reveals. Queen-high or any stronger category qualifies.\n4. Against a qualified dealer, Ante and Play pay 1:1 on a win; lose on a loss; both return on a tie.\n5. If the dealer does not qualify, Play returns and Ante stays on the table for the next round.\n6. Pair Plus is independent of the dealer for nonfolded hands: Straight Flush 40:1, Three of a Kind 30:1, Straight 6:1, Flush 3:1, Pair 1:1.\n\nRankings: Straight Flush > Three of a Kind > Straight > Flush > Pair > High Card. A-2-3 is the lowest straight; Q-K-A is highest. Suits do not break ties.\nTotals show net profit/loss, not returned stakes. A carried Ante is not a new wager and stays locked until settled.");
        alert.getDialogPane().setPrefWidth(650); alert.showAndWait();
    }
    private void show(String name) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/edu/uic/cs342/poker/fxml/" + name + ".fxml"));
            Parent root = loader.load();
            if (stage.getScene() == null) stage.setScene(new Scene(root, 1120, 820));
            else stage.getScene().setRoot(root);
            applyTheme();
            ((ScreenController) loader.getController()).attach(this);
        } catch (IOException e) { throw new IllegalStateException("Cannot load " + name + " screen", e); }
    }
}
