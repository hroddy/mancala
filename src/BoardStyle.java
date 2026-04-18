/**
 * BoardStyle.java
 * 
 * Defines the interface for different board styles in Mancala.
 * Possible styles include rustic wood, earthy-toned stones, etc.
 * 
 * @author Hannah Roddy
 * @author Johnny Tsai
 * @author Nishan Bhattarai
 */

import java.awt.Color;
import java.awt.Graphics;

/**
 * Implementations provide rendering logic for a specific board design.
 */
public interface BoardStyle {
    /**
     * @return the board's primary color.
     */
    Color getBoardColor();

    /**
     * Paints board, including pits, stores, and stones, within the specified drawing window.
     * 
     * @param g graphics context used to draw the board.
     * @param x x-coordinate of top-left corner of window.
     * @param y y-coordinate of top-left corner of window.
     * @param width width of the window.
     * @param height height of the window.
     * @param gameState array containing stone counts for all pits and stores.
     */
    void drawBoard(Graphics g, int x, int y, int width, int height, int[] gameState);

    /**
     * Returns the pit index at the given coordinates, or -1 if no pit was clicked.
     * Allows the controller to map a mouse click to a pit without duplicating layout math.
     *
     * @param clickX x-coordinate of mouse click relative to the board panel.
     * @param clickY y-coordinate of mouse click relative to the board panel.
     * @param boardWidth total width of the board panel.
     * @param boardHeight total height of the board panel.
     * @return pit index (0-13) if a pit was clicked, -1 otherwise.
     */
    int getPitAt(int clickX, int clickY, int boardWidth, int boardHeight);
}
