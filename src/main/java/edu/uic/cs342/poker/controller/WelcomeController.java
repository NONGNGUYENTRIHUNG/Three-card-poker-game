package edu.uic.cs342.poker.controller;

import edu.uic.cs342.poker.ui.Navigator;
import javafx.fxml.FXML;

public class WelcomeController implements ScreenController {
    private Navigator navigator;
    @Override public void attach(Navigator navigator) {
        this.navigator = navigator;
    }
    @FXML private void start() { navigator.startGame(); }
    @FXML private void help() { navigator.help(); }
    @FXML private void exit() { navigator.exit(); }
    @FXML private void theme() { navigator.toggleTheme(); }
}
