module edu.uic.cs342.poker {
    requires javafx.controls;
    requires javafx.fxml;
    exports edu.uic.cs342.poker;
    exports edu.uic.cs342.poker.model;
    exports edu.uic.cs342.poker.logic;
    exports edu.uic.cs342.poker.service;
    opens edu.uic.cs342.poker.controller to javafx.fxml;
}
