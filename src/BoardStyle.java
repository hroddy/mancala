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
     *                  Let n be length of gameState, representing the number of holes total. 
     *                  - Indices 0 to (n/2 - 2) represent Player A's pits.
     *                  - Index (n/2 - 1) is Player A's store.
     *                  - Indices (n/2) to (n - 2) represent Player B's pits.
     *                  - Index (n - 1) is Player B's store.
     * @param isPlayerATurn true if it is player A's turn; false if it is player B's turn
     * @param isGameOver true if the game has ended; false otherwise.
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