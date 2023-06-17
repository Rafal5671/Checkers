package com.checkers.warcaby;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class CheckersBoard extends Canvas {
    CheckersData board;
    boolean gameInProgress;
    int currentPlayer;
    int selectedRow, selectedCol;
    Checkers.CheckersMove[] legalMoves;
    private final Button newGameButton;
    private final Button resignButton;
    private final Label message;

    CheckersBoard(Button newGame, Button resignButton, Label message) {
        super(648,648);
        this.message = message;
        this.newGameButton = newGame;
        this.resignButton = resignButton;
        board = new CheckersData();
        doNewGame();
    }

    void doNewGame() {
        if (gameInProgress) {
            message.setText("Finish the current game first!");
            return;
        }
        board.setUpGame();   // Set up the pieces.
        currentPlayer = CheckersData.RED;   // RED moves first.
        legalMoves = board.getLegalMoves(CheckersData.RED);  // Get RED's legal moves.
        selectedRow = -1;   // RED has not yet selected a piece to move.
        message.setText("Red:  Make your move.");
        gameInProgress = true;
        newGameButton.setDisable(true);
        resignButton.setDisable(false);
        drawBoard();
    }

    void doResign() {
        if (!gameInProgress) {  // Should be impossible.
            message.setText("There is no game in progress!");
            return;
        }
        if (currentPlayer == CheckersData.RED)
            gameOver("RED resigns.  BLACK wins.");
        else
            gameOver("BLACK resigns.  RED wins.");
    }
    void gameOver(String str) {
        message.setText(str);
        newGameButton.setDisable(false);
        resignButton.setDisable(true);
        gameInProgress = false;
    }

    void doClickSquare(int row, int col) {

        for (Checkers.CheckersMove legalMove : legalMoves)
            if (legalMove.fromRow == row && legalMove.fromCol == col) {
                selectedRow = row;
                selectedCol = col;
                if (currentPlayer == CheckersData.RED)
                    message.setText("RED:  Make your move.");
                else
                    message.setText("BLACK:  Make your move.");
                drawBoard();
                return;
            }

        if (selectedRow < 0) {
            message.setText("Click the piece you want to move.");
            return;
        }

        for (Checkers.CheckersMove legalMove : legalMoves)
            if (legalMove.fromRow == selectedRow && legalMove.fromCol == selectedCol
                    && legalMove.toRow == row && legalMove.toCol == col) {
                doMakeMove(legalMove);
                return;
            }

        message.setText("Click the square you want to move to.");

    }
    void doMakeMove(Checkers.CheckersMove move) {

        board.makeMove(move);

        if (move.isJump()) {
            legalMoves = board.getLegalJumpsFrom(currentPlayer,move.toRow,move.toCol);
            if (legalMoves != null) {
                if (currentPlayer == CheckersData.RED)
                    message.setText("RED:  You must continue jumping.");
                else
                    message.setText("BLACK:  You must continue jumping.");
                selectedRow = move.toRow;  // Since only one piece can be moved, select it.
                selectedCol = move.toCol;
                drawBoard();
                return;
            }
        }

        if (currentPlayer == CheckersData.RED) {
            currentPlayer = CheckersData.BLACK;
            legalMoves = board.getLegalMoves(currentPlayer);
            if (legalMoves == null)
                gameOver("BLACK has no moves.  RED wins.");
            else if (legalMoves[0].isJump())
                message.setText("BLACK:  Make your move.  You must jump.");
            else
                message.setText("BLACK:  Make your move.");
        }
        else {
            currentPlayer = CheckersData.RED;
            legalMoves = board.getLegalMoves(currentPlayer);
            if (legalMoves == null)
                gameOver("RED has no moves.  BLACK wins.");
            else if (legalMoves[0].isJump())
                message.setText("RED:  Make your move.  You must jump.");
            else
                message.setText("RED:  Make your move.");
        }

        selectedRow = -1;

        if (legalMoves != null) {
            boolean sameStartSquare = true;
            for (int i = 1; i < legalMoves.length; i++)
                if (legalMoves[i].fromRow != legalMoves[0].fromRow
                        || legalMoves[i].fromCol != legalMoves[0].fromCol) {
                    sameStartSquare = false;
                    break;
                }
            if (sameStartSquare) {
                selectedRow = legalMoves[0].fromRow;
                selectedCol = legalMoves[0].fromCol;
            }
        }

        drawBoard();

    }

    public void drawBoard() {

        GraphicsContext g = getGraphicsContext2D();
        g.setFont( Font.font(18) );

        g.setStroke(Color.DARKRED);
        g.setLineWidth(2);
        g.strokeRect(1, 1, 644, 644);

        int square = 80;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if ( row % 2 == col % 2 )
                    g.setFill(Color.LIGHTGRAY);
                else
                    g.setFill(Color.GRAY);
                g.fillRect(4 + col*square, 4 + row*square, square, square);
                switch (board.pieceAt(row, col)) {
                    case CheckersData.RED -> {
                        g.setFill(Color.RED);
                        g.fillOval(16 + col * square, 16 + row * square, 56, 56);
                    }
                    case CheckersData.BLACK -> {
                        g.setFill(Color.BLACK);
                        g.fillOval(16 + col * square, 16 + row * square, 56, 56);
                    }
                    case CheckersData.RED_KING -> {
                        g.setFill(Color.RED);
                        g.fillOval(16 + col * square, 16 + row * square, 56, 56);
                        g.setFill(Color.WHITE);
                        g.fillText("K", 30 + col * square, 58 + row * square);
                    }
                    case CheckersData.BLACK_KING -> {
                        g.setFill(Color.BLACK);
                        g.fillOval(16 + col * square, 16 + row * square, 56, 56);
                        g.setFill(Color.WHITE);
                        g.fillText("K", 30 + col * square, 58 + row * square);
                    }
                }
            }
        }

        if (gameInProgress) {
            g.setStroke(Color.CYAN);
            g.setLineWidth(4);
            for (Checkers.CheckersMove legalMove : legalMoves) {
                g.strokeRect(4 + legalMove.fromCol * square,  4+legalMove.fromRow * square, 80, 80);
            }
            if (selectedRow >= 0) {
                g.setStroke(Color.YELLOW);
                g.setLineWidth(4);
                g.strokeRect( 4 + selectedCol*square,  4 + selectedRow*square, 80, 80);
                g.setStroke(Color.LIME);
                g.setLineWidth(4);
                for (Checkers.CheckersMove legalMove : legalMoves) {
                    if (legalMove.fromCol == selectedCol && legalMove.fromRow == selectedRow) {
                        g.strokeRect(4+legalMove.toCol * square, 4+legalMove.toRow * square, 80, 80);
                    }
                }
            }
        }

    }
    public void mousePressed(MouseEvent evt) {
        if (!gameInProgress)
            message.setText("Click \"New Game\" to start a new game.");
        else {
            int col = (int)((evt.getX() - 4) / 80);
            int row = (int)((evt.getY() - 4) / 80);
            if (col >= 0 && col < 8 && row >= 0 && row < 8)
                doClickSquare(row,col);
        }
    }
}