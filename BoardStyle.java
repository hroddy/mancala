/**
 * author: Hannah
 * BoardStyle.java
 * 
 * This file defines the interface for different board styles in the Mancala game.
 * Implementations of this interface will provide the rendering logic for various board designs.
 */

import java.awt.Color;
import java.awt.Graphics;

public interface BoardStyle {

    // Returns the background color of the board.
    Color getBoardColor();

    // Draws the outer board shape at the given position and size.
    void drawBoard(Graphics g, int x, int y, int width, int height);

    // Draws a single pit at the given position, showing the correct number of stones inside it.
    void drawPit(Graphics g, int x, int y, int size, int stones);

    // Draws a player's store (mancala) at the given position, showing the correct stone count.
    void drawStore(Graphics g, int x, int y, int width, int height, int stones);
}
