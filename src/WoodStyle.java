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

/**
 * Gradient wooden implementation of board style.
 */
public class WoodStyle extends GradientBoardStyle {
    private static final Color MATERIAL_LIGHT = new Color(170, 120, 80);
    private static final Color MATERIAL_DARK = new Color(90, 55, 30);

    private static final Color PIT_LIGHT = new Color(130, 85, 50);
    private static final Color PIT_DARK = new Color(70, 40, 20);
    
    private static final Color OUTLINE = new Color(45, 25, 10);
    private static final double PIT_FOCUS_FACTOR = 0.35;

    /**
     * Initialize gradient board with wood themed colors and specified focus.
     */
    public WoodStyle() {
        super(MATERIAL_LIGHT, MATERIAL_DARK, PIT_LIGHT, PIT_DARK, OUTLINE, PIT_FOCUS_FACTOR);
    }

    /**
     * Blends gradient colors into a primary color using arithmetic mean to maintain muddy quality of theme.
     * 
     * @return muddy blend of gradient colors as primary color.
     */
    public Color getBoardColor() {
        return new Color(
            (MATERIAL_LIGHT.getRed() + MATERIAL_DARK.getRed()) / 2,
            (MATERIAL_LIGHT.getGreen() + MATERIAL_DARK.getGreen()) / 2,
            (MATERIAL_LIGHT.getBlue() + MATERIAL_DARK.getBlue()) / 2
        );
    }
}
