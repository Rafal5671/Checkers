package com.checkers.warcaby;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Checkers extends Application {
    private static final int BOARD_SIZE = 8;
    private static final int TILE_SIZE = 80;

    private GridPane board;
    public Piece[][] pieces;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Warcaby");
        board = new GridPane();
        pieces = new Piece[BOARD_SIZE][BOARD_SIZE];
        createBoard();
        Scene scene = new Scene(board, BOARD_SIZE * TILE_SIZE, BOARD_SIZE * TILE_SIZE);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void createBoard() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Tile tile = new Tile((row + col) % 2 == 0, TILE_SIZE);
                board.add(tile, col, row);

                if (row < 3 && (row + col) % 2 != 0) {
                    Piece piece = new Piece(PieceType.RED, row, col, pieces,board);
                    pieces[row][col] = piece;
                    board.add(piece, col, row);
                }

                if (row > 4 && (row + col) % 2 != 0) {
                    Piece piece = new Piece(PieceType.BLACK, row, col, pieces,board);
                    pieces[row][col] = piece;
                    board.add(piece, col, row);
                }
            }
        }
    }
}