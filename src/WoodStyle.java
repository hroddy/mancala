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

public class WoodStyle extends GradientBoardStyle {
    private static final Color BOARD_LIGHT = new Color(170, 120, 80);
    private static final Color BOARD_DARK = new Color(90, 55, 30);

    private static final Color PIT_LIGHT = new Color(130, 85, 50);
    private static final Color PIT_DARK = new Color(70, 40, 20);
    private static final Color PIT_OUTLINE = new Color(45, 25, 10);

    private static final float PIT_FOCUS_FACTOR = 0.35f;

    /**
     * Initialize gradient board with wood themed colors and specified focus.
     */
    public WoodStyle() {
        super(BOARD_LIGHT, BOARD_DARK, PIT_LIGHT, PIT_DARK, PIT_OUTLINE, PIT_FOCUS_FACTOR);
    }
}
