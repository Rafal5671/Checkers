package com.checkers.warcaby;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Tile extends Rectangle {

    private static final Color LIGHT_COLOR = Color.valueOf("#F0D9B5");
    private static final Color DARK_COLOR = Color.valueOf("#B58863");
    private static final Color HIGHLIGHT_COLOR = Color.valueOf("#0000FF");


    public Tile(boolean light, int tileSize) {
        setWidth(tileSize);
        setHeight(tileSize);
        setFill(light ? LIGHT_COLOR : DARK_COLOR);
    }
    public void highlight() {
        setFill(HIGHLIGHT_COLOR);
    }

    public void removeHighlight() {
        setFill(getFill().equals(LIGHT_COLOR) ? LIGHT_COLOR : DARK_COLOR);
    }
}