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

public class MetalStyle extends GradientBoardStyle {
    private static final Color BOARD_LIGHT = new Color(190, 195, 200); 
    private static final Color BOARD_DARK = new Color(45, 50, 55);

    private static final Color PIT_LIGHT = new Color(110, 115, 120);
    private static final Color PIT_DARK = new Color(20, 25, 30);
    private static final Color PIT_OUTLINE = new Color(220, 220, 230);

    private static final float PIT_FOCUS_FACTOR = 0.20f;
    
    /**
     * Initialize gradient board with metal themed colors and specified focus.
     */
    public MetalStyle() {
        super(BOARD_LIGHT, BOARD_DARK, PIT_LIGHT, PIT_DARK, PIT_OUTLINE, PIT_FOCUS_FACTOR);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Color getBoardColor() {
        int r = (int) Math.sqrt((Math.pow(BOARD_LIGHT.getRed(), 2) + Math.pow(BOARD_DARK.getRed(), 2)) / 2);
        int g = (int) Math.sqrt((Math.pow(BOARD_LIGHT.getGreen(), 2) + Math.pow(BOARD_DARK.getGreen(), 2)) / 2);
        int b = (int) Math.sqrt((Math.pow(BOARD_LIGHT.getBlue(), 2) + Math.pow(BOARD_DARK.getBlue(), 2)) / 2);
        
        return new Color(r, g, b);
    }
}
