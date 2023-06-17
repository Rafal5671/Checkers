package com.checkers.warcaby;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;

public class Checkers extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    CheckersBoard board;

    private Button newGameButton;

    private Button resignButton;

    private Label message;

    public void start(Stage stage) {

        message = new Label("Click \"New Game\" to begin.");
        message.setTextFill( Color.rgb(100,255,100) ); // Light green.
        message.setFont( Font.font(null, FontWeight.BOLD, 18) );

        newGameButton = new Button("New Game");
        resignButton = new Button("Resign");

        board = new CheckersBoard(newGameButton,resignButton,message); // a subclass of Canvas, defined below
        board.drawBoard();  // draws the content of the checkerboard

        newGameButton.setOnAction( e -> board.doNewGame() );
        resignButton.setOnAction( e -> board.doResign() );
        board.setOnMousePressed( e -> board.mousePressed(e) );


        board.relocate(20,20);
        newGameButton.relocate(700, 240);
        resignButton.relocate(700, 400);
        message.relocate(40, 800);

        /* Set the sizes of the buttons.  For this to have an effect, make
         * the butons "unmanaged."  If they are managed, the Pane will set
         * their sizes. */

        resignButton.setManaged(false);
        resignButton.resize(200,60);
        newGameButton.setManaged(false);
        newGameButton.resize(200,60);

        /* Create the Pane and give it a preferred size.  If the
         * preferred size were not set, the unmanaged buttons would
         * not be included in the Pane's computed preferred size. */

        Pane root = new Pane();

        root.setPrefWidth(1000);
        root.setPrefHeight(840);

        /* Add the child nodes to the Pane and set up the rest of the GUI */

        root.getChildren().addAll(board, newGameButton, resignButton, message);
        root.setStyle("-fx-background-color: darkgreen; "
                + "-fx-border-color: darkred; -fx-border-width:3");
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Checkers!");
        stage.show();

    } // end start()
    public static class CheckersMove {
        int fromRow, fromCol;  // Position of piece to be moved.
        int toRow, toCol;      // Square it is to move to.
        CheckersMove(int r1, int c1, int r2, int c2) {
            // Constructor.  Just set the values of the instance variables.
            fromRow = r1;
            fromCol = c1;
            toRow = r2;
            toCol = c2;
        }
        boolean isJump() {
            return (fromRow - toRow == 2 || fromRow - toRow == -2);
        }
    }  // end class CheckersMove.


} // end class Checkers
