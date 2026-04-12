/**
 * MetalStyle.java
 * 
 * Sleek, futuristic board style with metallic textures and a cool color palette.
 * 
 * @author Hannah Roddy
 * @author Johnny Tsai
 * @author Nishan Bhattarai
 */

import java.awt.Color;
import java.awt.Graphics;

public class MetalStyle implements BoardStyle {
    private static final Color LIGHT_METAL = new Color(200, 200, 200);
    private static final Color DARK_METAL  = new Color(130, 130, 130);
    
    /**
     * {@inheritDoc}
     */
    public Color getBoardColor() {
        return StyleUtils.blend(LIGHT_METAL, DARK_METAL);
    }

    /**
     * {@inheritDoc}
     */
    public void drawBoard(Graphics g, int x, int y, int width, int height, int[] stones) {

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
