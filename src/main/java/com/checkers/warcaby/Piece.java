package com.checkers.warcaby;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Piece extends StackPane {
    private static final int BOARD_SIZE = 8;
    private static final int TILE_SIZE = 80;
    private static final Color RED_COLOR = Color.RED;
    private static final Color BLACK_COLOR = Color.BLACK;

    private PieceType type;
    private GridPane board;
    private int currentRow;
    private int currentCol;
    private Piece[][] pieces;

    private boolean isSelected = false;

    public Piece(PieceType type, int row, int col, Piece[][] pieces, GridPane board) {
        this.type = type;
        this.currentRow = row;
        this.currentCol = col;
        this.pieces = pieces;
        this.board = board;

        Circle circle = new Circle(TILE_SIZE * 0.3);
        circle.setFill(type == PieceType.RED ? RED_COLOR : BLACK_COLOR);
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(TILE_SIZE * 0.03);

        getChildren().add(circle);

        setOnMouseClicked(this::handleMouseClicked);
    }

    public void handleMouseClicked(MouseEvent event) {
        if (!isSelected) {
            selectPiece();
        } else {
            int clickedRow = GridPane.getRowIndex(this);
            int clickedCol = GridPane.getColumnIndex(this);

            if (isValidMove(clickedRow, clickedCol)) {
                movePiece(clickedRow, clickedCol);
            }

            deselectPiece();
        }
    }

    private void selectPiece() {

        // Highlight the selected piece
        setStyle("-fx-border-color: yellow; -fx-border-width: 3px;");
        isSelected = true;

        // Highlight valid moves
        for (int row = 0; row < pieces.length; row++) {
            for (int col = 0; col < pieces[row].length; col++) {
                if (isValidMove(row, col)) {
                    Tile tile = getTileAt(row, col);
                    tile.highlight();
                    tile.setOnMouseClicked(this::handleMoveClick);
                }
            }
        }
    }

    private void deselectPiece() {
        // Deselect the piece
        setStyle("");
        isSelected = false;

        // Remove highlights and event handlers from tiles
        for (int row = 0; row < pieces.length; row++) {
            for (int col = 0; col < pieces[row].length; col++) {
                Tile tile = getTileAt(row, col);
                tile.removeHighlight();
                tile.setOnMouseClicked(null);
            }
        }
    }

    public void handleMoveClick(MouseEvent event) {
        Tile clickedTile = (Tile) event.getSource();
        int clickedRow = GridPane.getRowIndex(clickedTile);
        int clickedCol = GridPane.getColumnIndex(clickedTile);

        if (isValidMove(clickedRow, clickedCol)) {
            movePiece(clickedRow, clickedCol);
        }

        deselectPiece();
    }

    private void movePiece(int newRow, int newCol) {
        // Check if it is a capture move
        if (Math.abs(newRow - currentRow) == 2 && Math.abs(newCol - currentCol) == 2) {
            int opponentRow = currentRow + (newRow - currentRow) / 2;
            int opponentCol = currentCol + (newCol - currentCol) / 2;

            // Remove the opponent's piece
            Piece opponentPiece = pieces[opponentRow][opponentCol];
            board.getChildren().remove(opponentPiece);
            pieces[opponentRow][opponentCol] = null;
        }

        // Remove the piece from the current position
        board.getChildren().remove(this);
        pieces[currentRow][currentCol] = null;

        // Update the piece's position
        GridPane.setRowIndex(this, newRow);
        GridPane.setColumnIndex(this, newCol);

        // Add the piece to the new position
        board.getChildren().add(this);
        pieces[newRow][newCol] = this;

        // Update the piece's currentRow and currentCol
        currentRow = newRow;
        currentCol = newCol;
    }


    private boolean isValidMove(int newRow, int newCol) {
        // Sprawdzenie, czy docelowe pole jest puste
        if (pieces[newRow][newCol] == null) {
            // Sprawdzenie, czy jest dostępne bicie
            if (hasAvailableCapture()) {
                // Sprawdzenie, czy to bicie jest wykonywane
                if (Math.abs(newRow - currentRow) == 2 && Math.abs(newCol - currentCol) == 2) {
                    int opponentRow = currentRow + (newRow - currentRow) / 2;
                    int opponentCol = currentCol + (newCol - currentCol) / 2;

                    // Sprawdzenie, czy istnieje przeciwnik na środkowym polu
                    if (pieces[opponentRow][opponentCol] != null && pieces[opponentRow][opponentCol].getType() != type) {
                        return true;
                    }
                }
            }
            // Jeśli nie ma dostępnego bicia, ruch bez bicia jest dozwolony
            else {
                if (Math.abs(newRow - currentRow) == 1 && Math.abs(newCol - currentCol) == 1) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean hasAvailableCapture() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Piece piece = pieces[row][col];
                if (piece != null && piece.getType() == type) {
                    // Sprawdzenie możliwości bicia dla danego pionka
                    if (canCapture(piece, row, col)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean canCapture(Piece piece, int currentRow, int currentCol) {
        // Sprawdzenie dostępnych możliwości bicia dla pionka
        int[][] captureMoves = {{-2, -2}, {-2, 2}, {2, -2}, {2, 2}};
        for (int[] captureMove : captureMoves) {
            int opponentRow = currentRow + captureMove[0] / 2;
            int opponentCol = currentCol + captureMove[1] / 2;
            int newRow = currentRow + captureMove[0];
            int newCol = currentCol + captureMove[1];

            // Sprawdzenie, czy sąsiedni pionek przeciwnika istnieje i docelowe pole jest puste
            if (isWithinBounds(newRow, newCol) && pieces[opponentRow][opponentCol] != null && pieces[opponentRow][opponentCol].getType() != piece.getType() && pieces[newRow][newCol] == null) {
                return true;
            }
        }
        return false;
    }

    private boolean isWithinBounds(int row, int col) {
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;
    }

    private int getCol() {
        return currentCol;
    }

    private int getRow() {
        return currentRow;
    }


    private Tile getTileAt(int row, int col) {
        // Helper method to get the Tile at a specific position on the board
        for (Node node : board.getChildren()) {
            if (node instanceof Tile && GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col) {
                return (Tile) node;
            }
        }
        return null;
    }
    public PieceType getType() {
        return type;
    }

}
