/**
 * StyleUtils.java
 * 
 * Utility class responsible for blending colors.
 * 
 * @author Hannah Roddy
 * @author Johnny Tsai
 * @author Nishan Bhattarai
 */

import java.awt.Color;

public class StyleUtils {
    /**
     * Blends two colors by averaging their RGB values.
     *
     * @param c1 the first color
     * @param c2 the second color
     * @return the blended color
     */
    public static Color blend(Color c1, Color c2) {
        return new Color(
            (c1.getRed() + c2.getRed()) / 2,
            (c1.getGreen() + c2.getGreen()) / 2,
            (c1.getBlue() + c2.getBlue()) / 2
        );
    }
}