/**
 * BoardStyle.java
 * 
 * This file defines the interface for different board styles in the Mancala game.
 * Implementations of this interface will provide the rendering logic for various board designs.
 * Possible styles include rustic wood, earthy toned stones, etc.
 * 
 * @author Hannah Roddy
 * @author Johnny Tsai
 * @author Nishan Bhattarai
 */

import java.awt.Color;
import java.awt.Graphics;

public interface BoardStyle {
    /**
     * Gets the primary color of the board.
     * 
     * @return background color of board. If board uses gradient as background, returns blend as proxy.
     */
    Color getBoardColor();

    /**
     * Paints board, including pits, stores, and stones.
     * Paints within specified drawing window based on current game state.
     * 
     * @param g the graphics context used to draw the board.
     * @param x the x-coordinate of top left corner of window.
     * @param y the y-coordinate of top left corner of window.
     * @param width the width of the window.
     * @param height the height of the window.
     * @param gameState array containing stone counts for all pits and stores.
     */
    void drawBoard(Graphics g, int x, int y, int width, int height, int[] gameState);
}
