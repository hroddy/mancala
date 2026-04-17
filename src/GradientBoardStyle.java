/**
 * GradientBoardStyle.java
 * 
 * Abstract class to factor out common variables and methods for gradient board styles.
 * 
 * @author Hannah Roddy
 * @author Johnny Tsai
 * @author Nishan Bhattarai
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.RadialGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.BasicStroke;
import java.awt.geom.RoundRectangle2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.Random;
import java.awt.geom.Ellipse2D;

/**
 * Provides shared color fields and methods for drawing the board, its holes (pits or stores), and the stones within them.
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
     * Constructor called by concrete class.
     * Allows concrete class to define their own colors in line with their style.
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
            this.startColor = materialDark;
            this.endColor = materialLight;
        } else {
            this.startColor = materialLight;
            this.endColor = materialDark;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void drawBoard(Graphics g, int boardX, int boardY, int boardWidth, int boardHeight, int[] gameState) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setPaint(new GradientPaint(boardX, boardY, startColor, boardX + boardWidth, boardY + boardHeight, endColor));
        g2.fillRect(boardX, boardY, boardWidth, boardHeight);

        double gap = boardWidth / 50.0;
        double gridWidth = (boardWidth - (gap * 9)) / 8;
        double gridHeight = (boardHeight - (gap * 3)) / 2;

        double pitDiameter = Math.min(gridWidth, gridHeight);

        double horiOffset = (gridWidth - pitDiameter) / 2;
        double vertOffset = (gridHeight - pitDiameter) / 2;
        
        double storeWidth = pitDiameter;
        double storeHeight = 2 * (pitDiameter + vertOffset) + gap;

        double leftStoreX = boardX + gap + horiOffset;
        double leftStoreY = boardY + gap + vertOffset;

        drawHole(g2, leftStoreX, leftStoreY, storeWidth, storeHeight, gameState[13], 13);

        for (int i = 0; i < 6; i++) {
            double pitX = boardX + (gap * (i + 2)) + (gridWidth * (i + 1)) + horiOffset;
            double topPitY = boardY + gap + vertOffset;
            double botPitY = boardY + gridHeight + 2 * gap + vertOffset; 

            drawCircularPit(g2, pitX, topPitY, pitDiameter, gameState[12 - i], 12 - i);
            drawCircularPit(g2, pitX, botPitY, pitDiameter, gameState[i], i);
        }

        double rightStoreX = boardX + boardWidth - gap - gridWidth + horiOffset;
        double rightStoreY = leftStoreY;

        drawHole(g2, rightStoreX, rightStoreY, storeWidth, storeHeight, gameState[6], 6);
    }

    /**
     * Draws a circular pit at the specified location.
     * 
     * @param g graphics context used to draw the pit.
     * @param pitX x-coordinate of top-left corner of the pit's bounding box.
     * @param pitY y-coordinate of top-left corner of the pit's bounding box.
     * @param pitDiameter diameter of the pit.
     * @param stones number of stones inside the pit.
     * @param holeID unique ID for hole, even if hole resizes stones will not dance.
     */
    protected void drawCircularPit(Graphics g, double pitX, double pitY, double pitDiameter, int stones, int holeID) {
        drawHole(g, pitX, pitY, pitDiameter, pitDiameter, stones, holeID);
    }

    /**
     * Draws a hole at the specified location with the specified dimensions.
     * 
     * @param g graphics context used to draw the hole.
     * @param holeX x-coordinate of top-left corner of the hole's bounding box.
     * @param holeY y-coordinate of top-left corner of the hole's bounding box.
     * @param holeWidth width of the hole.
     * @param holeHeight height of the hole.
     * @param stones number of stones inside the hole.
     * @param holeID unique ID for hole, even if hole resizes stones will not dance.
     */
    protected void drawHole(Graphics g, double holeX, double holeY, double holeWidth, double holeHeight, int stones, int holeID) {
        Graphics2D g2 = (Graphics2D) g;

        double minRadius = holeWidth * 0.5;
        double maxRadius = holeHeight * 0.5;

        Point2D center = new Point2D.Double(holeX + minRadius, holeY + maxRadius);
        Point2D focus = new Point2D.Double(holeX + (holeWidth * pitFocusFactor), holeY + (holeHeight * pitFocusFactor));

        double gradRadius = center.distance(focus) + maxRadius;

        Color[] colors = {pitLight, pitDark};

        RadialGradientPaint radialGrad = new RadialGradientPaint(
            center, (float) gradRadius, focus, new float[]{0.0f, 1.0f}, colors, MultipleGradientPaint.CycleMethod.NO_CYCLE
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
     * @param holeID unique ID for hole, even if hole resizes stones will not dance.
     */
    protected void drawStonesWithinHole(Graphics2D g2, Shape holeShape, int stones, int holeID) {
        Rectangle2D bounds = holeShape.getBounds2D();
        double stoneSize = bounds.getWidth() * STONE_SIZE_RATIO;

        Random seed = new Random(holeID);

        for (int i = 0; i < stones; i++) {
            double stoneX, stoneY;
            do {
            stoneX = bounds.getX() + (bounds.getWidth() - stoneSize) * seed.nextDouble();
            stoneY = bounds.getY() + (bounds.getHeight() - stoneSize) * seed.nextDouble();
            } while (!holeShape.contains(stoneX, stoneY, stoneSize, stoneSize));

            drawStone(g2, stoneX, stoneY, stoneSize);
        }
    }

    /**
     * 
     */
    protected void drawStone(Graphics2D g2, double stoneX, double stoneY, double stoneSize){
        double radius = stoneSize * 0.5;

        Point2D center = new Point2D.Double(stoneX + radius, stoneY + radius);
        Point2D focus = new Point2D.Double(stoneX + (stoneSize * (1 - pitFocusFactor)), stoneY + (stoneSize * (1 - pitFocusFactor)));

        double gradRadius = center.distance(focus) + radius;

        Color[] colors = {materialLight, materialDark};

        RadialGradientPaint radialGrad = new RadialGradientPaint(
            center, (float) gradRadius, focus, new float[]{0.0f, 1.0f}, colors, MultipleGradientPaint.CycleMethod.NO_CYCLE
        );

        Ellipse2D stoneShape = new Ellipse2D.Double(stoneX, stoneY, stoneSize, stoneSize);

        g2.setPaint(radialGrad);
        g2.fill(stoneShape);

        g2.setStroke(new BasicStroke((float)(stoneSize * 0.05))); 
        g2.setColor(outline);              
        g2.draw(stoneShape);
    }
}