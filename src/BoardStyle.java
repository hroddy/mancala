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
     * @return background color of board. If board uses gradient as background, returns blend as proxy.
     */
    Color getBoardColor();

    /**
     * Draws the outer board shape at the given position and size.
     */
    void drawBoard(Graphics g, int x, int y, int width, int height, int[] stones);
}
