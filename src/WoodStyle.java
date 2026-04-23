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
    private static final Color MATERIAL_LIGHT = new Color(150, 110, 70);
    private static final Color MATERIAL_DARK = new Color(90, 55, 30);

    private static final Color PIT_LIGHT = new Color(140, 95, 60);
    private static final Color PIT_DARK  = new Color(65, 35, 20);

    private static final Color OUTLINE = new Color(30, 15, 5);
    private static final Color OUTLINE_SELECTED = new Color(120, 180, 60);
    private static final double PIT_FOCUS_FACTOR = 0.2;

    /**
     * Initialize gradient board with wood themed colors and specified focus.
     * @param maxPitStart max number of stones per pit at start of Mancala game.
     * @param pitsPerSide number of pits for each of the pair of players.
     */
    public WoodStyle(int maxPitStart, int pitsPerSide) {
        super(MATERIAL_LIGHT, MATERIAL_DARK, PIT_LIGHT, PIT_DARK, OUTLINE, OUTLINE_SELECTED, PIT_FOCUS_FACTOR, maxPitStart, pitsPerSide);
    }

    /**
     * {@inheritDoc}
     * Blends gradient colors into a primary color using arithmetic mean to maintain the warm, earthy quality of the theme.
     */
    public Color getBoardColor() {
        return new Color(
            (MATERIAL_LIGHT.getRed() + MATERIAL_DARK.getRed()) / 2,
            (MATERIAL_LIGHT.getGreen() + MATERIAL_DARK.getGreen()) / 2,
            (MATERIAL_LIGHT.getBlue() + MATERIAL_DARK.getBlue()) / 2
        );
    }
}
