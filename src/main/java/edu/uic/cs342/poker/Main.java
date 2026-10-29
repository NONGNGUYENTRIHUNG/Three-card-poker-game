package edu.uic.cs342.poker;

import javafx.application.Application;
import javafx.stage.Stage;
import edu.uic.cs342.poker.ui.Navigator;

public class Main extends Application {
    @Override public void start(Stage stage) {
        Navigator navigator = new Navigator(stage);
        stage.setTitle("Three Card Poker");
        stage.setMinWidth(980); stage.setMinHeight(740);
        stage.setOnCloseRequest(event -> { if (!navigator.confirmExit()) event.consume(); });
        navigator.welcome();
        stage.show();
    }
    public static void main(String[] args) { launch(args); }
}
