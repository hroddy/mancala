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
import java.awt.Graphics2D;

/**
 * Implementations provide rendering logic for a specific board design.
 */
public interface BoardStyle {
    /**
     * @return the board's primary color.
     */
    Color getBoardColor();

    /**
     * Paints the board, including pits, stores, and stones, within the specified drawing window.
     * 
     * @param g2 graphics context used to draw the board.
     * @param boardWidth width of the window.
     * @param boardHeight height of the window.
     * @param gameState array containing stone counts for all pits and stores.
     * @param isPlayerATurn whether it is currently player A or player B's turn.
     * @param isGameOver whether the game is over or not.
     */
    void drawBoard(Graphics2D g2, int boardWidth, int boardHeight, int[] gameState, boolean isPlayerATurn, boolean isGameOver);

    /**
     * Returns the index of the pit at the given coordinates, or -1 if no valid pit was clicked.
     * Allows the controller to map a mouse click to a pit without duplicating layout math.
     * Stores are not considered pits.
     *
     * @param clickX x-coordinate of mouse click relative to the board panel.
     * @param clickY y-coordinate of mouse click relative to the board panel.
     * @return index corresponding to a pit in the gameState array, or -1 if none was clicked.
     */
    int getPitAt(int clickX, int clickY);
}
