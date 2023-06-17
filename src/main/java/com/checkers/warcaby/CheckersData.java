package com.checkers.warcaby;

import java.util.ArrayList;
public class CheckersData {
    static final int
            EMPTY = 0,
            RED = 1,
            RED_KING = 2,
            BLACK = 3,
            BLACK_KING = 4;

    int[][] board;  // board[r][c] is the contents of row r, column c.

    CheckersData() {
        board = new int[8][8];
        setUpGame();
    }
    void setUpGame() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if ( row % 2 == col % 2 ) {
                    if (row < 3)
                        board[row][col] = BLACK;
                    else if (row > 4)
                        board[row][col] = RED;
                    else
                        board[row][col] = EMPTY;
                }
                else {
                    board[row][col] = EMPTY;
                }
            }
        }
    }  // end setUpGame()

    /**
     * Return the contents of the square in the specified row and column.
     */
    int pieceAt(int row, int col) {
        return board[row][col];
    }
    void makeMove(Checkers.CheckersMove move) {
        makeMove(move.fromRow, move.fromCol, move.toRow, move.toCol);
    }
    void makeMove(int fromRow, int fromCol, int toRow, int toCol) {
        board[toRow][toCol] = board[fromRow][fromCol];
        board[fromRow][fromCol] = EMPTY;
        if (fromRow - toRow == 2 || fromRow - toRow == -2) {
            int jumpRow = (fromRow + toRow) / 2;  // Row of the jumped piece.
            int jumpCol = (fromCol + toCol) / 2;  // Column of the jumped piece.
            board[jumpRow][jumpCol] = EMPTY;
        }
        if (toRow == 0 && board[toRow][toCol] == RED)
            board[toRow][toCol] = RED_KING;
        if (toRow == 7 && board[toRow][toCol] == BLACK)
            board[toRow][toCol] = BLACK_KING;
    }

    Checkers.CheckersMove[] getLegalMoves(int player) {

        if (player != RED && player != BLACK)
            return null;

        int playerKing;  // The constant representing a King belonging to player.
        if (player == RED)
            playerKing = RED_KING;
        else
            playerKing = BLACK_KING;

        ArrayList<Checkers.CheckersMove> moves = new ArrayList<>();  // Moves will be stored in this list.

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] == player || board[row][col] == playerKing) {
                    if (canJump(player, row, col, row+1, col+1, row+2, col+2))
                        moves.add(new Checkers.CheckersMove(row, col, row+2, col+2));
                    if (canJump(player, row, col, row-1, col+1, row-2, col+2))
                        moves.add(new Checkers.CheckersMove(row, col, row-2, col+2));
                    if (canJump(player, row, col, row+1, col-1, row+2, col-2))
                        moves.add(new Checkers.CheckersMove(row, col, row+2, col-2));
                    if (canJump(player, row, col, row-1, col-1, row-2, col-2))
                        moves.add(new Checkers.CheckersMove(row, col, row-2, col-2));
                }
            }
        }

        if (moves.size() == 0) {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (board[row][col] == player || board[row][col] == playerKing) {
                        if (canMove(player,row,col,row+1,col+1))
                            moves.add(new Checkers.CheckersMove(row,col,row+1,col+1));
                        if (canMove(player,row,col,row-1,col+1))
                            moves.add(new Checkers.CheckersMove(row,col,row-1,col+1));
                        if (canMove(player,row,col,row+1,col-1))
                            moves.add(new Checkers.CheckersMove(row,col,row+1,col-1));
                        if (canMove(player,row,col,row-1,col-1))
                            moves.add(new Checkers.CheckersMove(row,col,row-1,col-1));
                    }
                }
            }
        }

        if (moves.size() == 0)
            return null;
        else {
            Checkers.CheckersMove[] moveArray = new Checkers.CheckersMove[moves.size()];
            for (int i = 0; i < moves.size(); i++)
                moveArray[i] = moves.get(i);
            return moveArray;
        }

    }  // end getLegalMoves
    Checkers.CheckersMove[] getLegalJumpsFrom(int player, int row, int col) {
        if (player != RED && player != BLACK)
            return null;
        int playerKing;  // The constant representing a King belonging to player.
        if (player == RED)
            playerKing = RED_KING;
        else
            playerKing = BLACK_KING;
        ArrayList<Checkers.CheckersMove> moves = new ArrayList<>();  // The legal jumps will be stored in this list.
        if (board[row][col] == player || board[row][col] == playerKing) {
            if (canJump(player, row, col, row+1, col+1, row+2, col+2))
                moves.add(new Checkers.CheckersMove(row, col, row+2, col+2));
            if (canJump(player, row, col, row-1, col+1, row-2, col+2))
                moves.add(new Checkers.CheckersMove(row, col, row-2, col+2));
            if (canJump(player, row, col, row+1, col-1, row+2, col-2))
                moves.add(new Checkers.CheckersMove(row, col, row+2, col-2));
            if (canJump(player, row, col, row-1, col-1, row-2, col-2))
                moves.add(new Checkers.CheckersMove(row, col, row-2, col-2));
        }
        if (moves.size() == 0)
            return null;
        else {
            Checkers.CheckersMove[] moveArray = new Checkers.CheckersMove[moves.size()];
            for (int i = 0; i < moves.size(); i++)
                moveArray[i] = moves.get(i);
            return moveArray;
        }
    }  // end getLegalJumpsFrom()

    private boolean canJump(int player, int r1, int c1, int r2, int c2, int r3, int c3) {

        if (r3 < 0 || r3 >= 8 || c3 < 0 || c3 >= 8)
            return false;  // (r3,c3) is off the board.

        if (board[r3][c3] != EMPTY)
            return false;  // (r3,c3) already contains a piece.

        if (player == RED) {
            if (board[r1][c1] == RED && r3 > r1)
                return false;  // Regular red piece can only move up.
            return board[r2][c2] == BLACK || board[r2][c2] == BLACK_KING;  // There is no black piece to jump.
// The jump is legal.
        }
        else {
            if (board[r1][c1] == BLACK && r3 < r1)
                return false;  // Regular black piece can only move downn.
            return board[r2][c2] == RED || board[r2][c2] == RED_KING;  // There is no red piece to jump.
// The jump is legal.
        }

    }  // end canJump()

    private boolean canMove(int player, int r1, int c1, int r2, int c2) {

        if (r2 < 0 || r2 >= 8 || c2 < 0 || c2 >= 8)
            return false;  // (r2,c2) is off the board.

        if (board[r2][c2] != EMPTY)
            return false;  // (r2,c2) already contains a piece.

        if (player == RED) {
            return board[r1][c1] != RED || r2 <= r1;  // Regular red piece can only move down.
// The move is legal.
        }
        else {
            return board[r1][c1] != BLACK || r2 >= r1;  // Regular black piece can only move up.
// The move is legal.
        }

    }  // end canMove()

} // end class CheckersData