module com.checkers.warcaby {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.checkers.warcaby to javafx.fxml;
    exports com.checkers.warcaby;
}