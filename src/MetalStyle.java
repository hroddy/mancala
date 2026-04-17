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

/**
 * Gradient metallic implementation of board style.
 */
public class MetalStyle extends GradientBoardStyle {
    private static final Color MATERIAL_LIGHT = new Color(155, 165, 180);
    private static final Color MATERIAL_DARK = new Color(55, 60, 70);

    private static final Color PIT_LIGHT = new Color(125, 130, 135);
    private static final Color PIT_DARK = new Color(45, 50, 60);

    private static final Color OUTLINE = new Color(165, 170, 180);
    private static final double PIT_FOCUS_FACTOR = 0.25;
    
    /**
     * Initialize gradient board with metal themed colors and specified focus.
     */
    public MetalStyle() {
        super(MATERIAL_LIGHT, MATERIAL_DARK, PIT_LIGHT, PIT_DARK, OUTLINE, PIT_FOCUS_FACTOR);
    }

    /**
     * {@inheritDoc}
     * Blends gradient colors into a primary color using root-mean-square to maintain the luminous quality of the theme.
     */
    public Color getBoardColor() {
        int r = (int) Math.sqrt((Math.pow(MATERIAL_LIGHT.getRed(), 2) + Math.pow(MATERIAL_DARK.getRed(), 2)) / 2);
        int g = (int) Math.sqrt((Math.pow(MATERIAL_LIGHT.getGreen(), 2) + Math.pow(MATERIAL_DARK.getGreen(), 2)) / 2);
        int b = (int) Math.sqrt((Math.pow(MATERIAL_LIGHT.getBlue(), 2) + Math.pow(MATERIAL_DARK.getBlue(), 2)) / 2);
        
        return new Color(r, g, b);
    }
}
