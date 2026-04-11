/**
 * WoodStyle.java
 * 
 * Rustic wood board style with weathered textures and a warm color palette.
 * 
 * @author Hannah Roddy
 * @author Johnny Tsai
 * @author Nishan Bhattarai
 */

import java.awt.Color;
import java.awt.Graphics;

public class WoodStyle implements BoardStyle {
    Color color;
    
    /**
     * {@inheritDoc}
     */
    public Color getBoardColor() {
        return color;
    }

    /**
     * {@inheritDoc}
     */
    public void drawBoard(Graphics g, int x, int y, int width, int height) {

    }

    /**
     * {@inheritDoc}
     */
    public void drawPit(Graphics g, int x, int y, int size, int stones) {

    }

    /**
     * {@inheritDoc}
     */
    public void drawStore(Graphics g, int x, int y, int width, int height, int stones) {

    }
}
