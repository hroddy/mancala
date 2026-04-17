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
     * Returns primary color of the board.
     * 
     * @return background color of the board; if board uses a gradient, returns blended proxy color.
     */
    Color getBoardColor();

    /**
     * Paints board, including pits, stores, and stones, within specified drawing window.
     * 
     * @param g graphics context used to draw the board.
     * @param x x-coordinate of top-left corner of window.
     * @param y y-coordinate of top-left corner of window.
     * @param width width of the window.
     * @param height height of the window.
     * @param gameState array containing stone counts for all pits and stores.
     */
    void drawBoard(Graphics g, int x, int y, int width, int height, int[] gameState);
}
