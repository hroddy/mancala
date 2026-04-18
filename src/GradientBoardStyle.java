/**
 * GradientBoardStyle.java
 * 
 * Abstract class to factor out common variables and methods for gradient board styles.
 * 
 * @author Hannah Roddy
 * @author Johnny Tsai
 * @author Nishan Bhattarai
 */

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MultipleGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Random;

/**
 * Provides shared fields and helper methods for drawing a gradient board, its holes (pits or stores), and the stones within them.
 */
public abstract class GradientBoardStyle implements BoardStyle {
    private static final double STONE_SIZE_RATIO = 0.15;

    private final Color materialLight;
    private final Color materialDark;

    private final Color pitLight;
    private final Color pitDark;
    private final Color outline;

    private final double pitFocusFactor;

    private Color startColor;
    private Color endColor;

    /**
     * Constructor for subclasses to call.
     * Allows concrete subclasses to define their own gradient colors in line with their style.
     * 
     * @param materialLight lighter color of the board gradient.
     * @param materialDark darker color of the board gradient.
     * @param pitLight lighter color of the pit (and store) gradient.
     * @param pitDark darker color of the pit (and store) gradient.
     * @param outline color of outline around the pit (and store).
     * @param pitFocusFactor relative position of radial gradient focus point within a hole;
     *                       expressed as a fraction of the hole's width and height.
     */
    protected GradientBoardStyle(
        Color materialLight, 
        Color materialDark, 
        Color pitLight, 
        Color pitDark, 
        Color outline, 
        double pitFocusFactor
    ) {
        this.materialLight = materialLight;
        this.materialDark = materialDark;

        this.pitLight = pitLight;
        this.pitDark = pitDark;
        this.outline = outline;
        this.pitFocusFactor = pitFocusFactor;

        if (pitFocusFactor < 0.5) {
            this.startColor = materialLight;
            this.endColor = materialDark;
        } 
        else {
            this.startColor = materialDark;
            this.endColor = materialLight;
        }
    }

    /**
     * Computes the pixel boundaries of all 14 holes based on current board dimensions.
     * Single source of truth for layout math — shared by drawBoard and getPitAt.
     * 
     * @param boardWidth total width of the board panel.
     * @param boardHeight total height of the board panel.
     * @return array of {x, y, width, height} for each of the 14 pit indices.
     */
    private double[][] computeHoleBounds(int boardWidth, int boardHeight) {
        double[][] bounds = new double[14][4];

        double gap = boardWidth / 50.0;
        double gridWidth = (boardWidth - (gap * 9)) / 8;
        double gridHeight = (boardHeight - (gap * 3)) / 2;
        double pitDiameter = Math.min(gridWidth, gridHeight);
        double horiOffset = (gridWidth - pitDiameter) / 2;
        double vertOffset = (gridHeight - pitDiameter) / 2;
        double storeHeight = 2 * (pitDiameter + vertOffset) + gap;

        // Left store — Player B (index 13)
        bounds[13] = new double[]{gap + horiOffset, gap + vertOffset, pitDiameter, storeHeight};

        // Right store — Player A (index 6)
        bounds[6] = new double[]{boardWidth - gap - gridWidth + horiOffset, gap + vertOffset, pitDiameter, storeHeight};

        // 6 columns of pits
        for (int i = 0; i < 6; i++) {
            double pitX = (gap * (i + 2)) + (gridWidth * (i + 1)) + horiOffset;
            bounds[i]      = new double[]{pitX, gridHeight + 2 * gap + vertOffset, pitDiameter, pitDiameter};
            bounds[12 - i] = new double[]{pitX, gap + vertOffset, pitDiameter, pitDiameter};
        }

        return bounds;
    }

    /**
     * {@inheritDoc}
     */
    public void drawBoard(Graphics g, int boardX, int boardY, int boardWidth, int boardHeight, int[] gameState) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setPaint(new GradientPaint(boardX, boardY, startColor, boardX + boardWidth, boardY + boardHeight, endColor));
        g2.fillRect(boardX, boardY, boardWidth, boardHeight);

        double[][] bounds = computeHoleBounds(boardWidth, boardHeight);

        // Draw stores
        drawHole(g2, bounds[13][0], bounds[13][1], bounds[13][2], bounds[13][3], gameState[13], 13);
        drawHole(g2, bounds[6][0],  bounds[6][1],  bounds[6][2],  bounds[6][3],  gameState[6],  6);

        // Draw pits
        for (int i = 0; i < 6; i++) {
            drawHole(g2, bounds[i][0],      bounds[i][1],      bounds[i][2],      bounds[i][3],      gameState[i],      i);
            drawHole(g2, bounds[12-i][0],   bounds[12-i][1],   bounds[12-i][2],   bounds[12-i][3],   gameState[12-i],   12-i);
        }
    }

    /**
     * {@inheritDoc}
     * Uses computeHoleBounds to map click coordinates to a pit index.
     */
    public int getPitAt(int clickX, int clickY, int boardWidth, int boardHeight) {
        double[][] bounds = computeHoleBounds(boardWidth, boardHeight);
        for (int i = 0; i < 14; i++) {
            if (clickX >= bounds[i][0] && clickX <= bounds[i][0] + bounds[i][2] &&
                clickY >= bounds[i][1] && clickY <= bounds[i][1] + bounds[i][3])
                return i;
        }
        return -1;
    }

    /**
     * Draws a hole with lighting effects at the specified location with the specified dimensions.
     * 
     * @param g graphics context used to draw the hole.
     * @param holeX x-coordinate of top-left corner of the hole's bounding box.
     * @param holeY y-coordinate of top-left corner of the hole's bounding box.
     * @param holeWidth width of the hole.
     * @param holeHeight height of the hole.
     * @param stones number of stones inside the hole.
     * @param holeID unique ID for hole, so stone placement remains stable when the hole is resized.
     */
    protected void drawHole(Graphics g, double holeX, double holeY, double holeWidth, double holeHeight, int stones, int holeID) {
        Graphics2D g2 = (Graphics2D) g;

        double minRadius = holeWidth * 0.5;
        double maxRadius = holeHeight * 0.5;

        Point2D center = new Point2D.Double(holeX + minRadius, holeY + maxRadius);
        Point2D focus = new Point2D.Double(holeX + (holeWidth * pitFocusFactor), holeY + (holeHeight * pitFocusFactor));

        double gradRadius = center.distance(focus) + maxRadius;

        Color[] colors = {pitDark, pitLight};

        RadialGradientPaint radialGrad = new RadialGradientPaint(
            center, (float) gradRadius, focus, new float[]{0.0f, 0.75f}, colors, MultipleGradientPaint.CycleMethod.NO_CYCLE
        );

        double arcWidth = holeWidth;
        double arcHeight = holeWidth;
        RoundRectangle2D holeShape = new RoundRectangle2D.Double(holeX, holeY, holeWidth, holeHeight, arcWidth, arcHeight);

        g2.setPaint(radialGrad);
        g2.fill(holeShape);

        g2.setStroke(new BasicStroke((float)(holeWidth * 0.03))); 
        g2.setColor(outline);              
        g2.draw(holeShape);

        if (stones > 0) {
            drawStonesWithinHole(g2, holeShape, stones, holeID);
        }
    }

    /**
     * Draws the specified number of stones within the specified hole.
     * 
     * @param g2 graphics context used to draw the stones.
     * @param holeShape the hole in which to draw stones.
     * @param stones number of stones to draw.
     * @param holeID unique ID for hole, so stone placement remains stable when the hole is resized.
     */
    protected void drawStonesWithinHole(Graphics2D g2, Shape holeShape, int stones, int holeID) {
        Rectangle2D bounds = holeShape.getBounds2D();
        double stoneDiameter = bounds.getWidth() * STONE_SIZE_RATIO;

        Random seed = new Random(holeID);

        for (int i = 0; i < stones; i++) {
            double stoneX, stoneY;
            do {
            stoneX = bounds.getX() + (bounds.getWidth() - stoneDiameter) * seed.nextDouble();
            stoneY = bounds.getY() + (bounds.getHeight() - stoneDiameter) * seed.nextDouble();
            } while (!holeShape.contains(stoneX, stoneY, stoneDiameter, stoneDiameter));

            drawStone(g2, stoneX, stoneY, stoneDiameter);
        }
    }

    /**
     * Draws the stone with lighting effect at the specified location with the specified dimensions.
     * 
     * @param g2 graphics context used to draw the stones.
     * @param stoneX the x-coordinate of top-left corner of stone's bounding box.
     * @param stoneY the y-coordinate of top-left corner of stone's bounding box
     * @param stoneDiameter the diameter of the stone
     */
    protected void drawStone(Graphics2D g2, double stoneX, double stoneY, double stoneDiameter){
        double radius = stoneDiameter * 0.5;

        Point2D center = new Point2D.Double(stoneX + radius, stoneY + radius);
        Point2D focus = new Point2D.Double(stoneX + (stoneDiameter * (1 - pitFocusFactor)), stoneY + (stoneDiameter * (1 - pitFocusFactor)));

        double gradRadius = center.distance(focus) + radius;

        Color[] colors = {materialLight, materialDark};

        RadialGradientPaint radialGrad = new RadialGradientPaint(
            center, (float) gradRadius, focus, new float[]{0.0f, 0.75f}, colors, MultipleGradientPaint.CycleMethod.NO_CYCLE
        );

        Ellipse2D stoneShape = new Ellipse2D.Double(stoneX, stoneY, stoneDiameter, stoneDiameter);

        g2.setPaint(radialGrad);
        g2.fill(stoneShape);

        g2.setStroke(new BasicStroke((float)(stoneDiameter * 0.05))); 
        g2.setColor(outline);              
        g2.draw(stoneShape);
    }

}