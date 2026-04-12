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


public abstract class GradientBoardStyle implements BoardStyle {
    private final Color boardLight;
    private final Color boardDark;

    private final Color pitLight;
    private final Color pitDark;
    private final Color pitOutline;

    private final float pitFocusFactor;

    private Color startColor;
    private Color endColor;

    /**
     * Constructor called by concrete class.
     * Allows concrete class to define their own colors in line with their style.
     * 
     * @param boardLight the lighter color of the board gradient.
     * @param boardDark the darker color of the board gradient.
     * @param pitLight the lighter color of the pit (and store) gradient.
     * @param pitDark the darker color of the pit (and store) gradient.
     * @param pitOutline the color of the outline around the pit (and store).
     * @param pitFocusFactor offset from top left of pit (and store) in terms of width and height.
     */
    protected GradientBoardStyle(
        Color boardLight, 
        Color boardDark, 
        Color pitLight, 
        Color pitDark, 
        Color pitOutline, 
        float pitFocusFactor
    ) {
        this.boardLight = boardLight;
        this.boardDark = boardDark;

        this.pitLight = pitLight;
        this.pitDark = pitDark;
        this.pitOutline = pitOutline;
        this.pitFocusFactor = pitFocusFactor;

        if (pitFocusFactor < 0.5f) {
            this.startColor = boardDark;
            this.endColor = boardLight;
        } else {
            this.startColor = boardLight;
            this.endColor = boardDark;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Color getBoardColor() {
        return new Color(
            (boardLight.getRed() + boardDark.getRed()) / 2,
            (boardLight.getGreen() + boardDark.getGreen()) / 2,
            (boardLight.getBlue() + boardDark.getBlue()) / 2
        );
    }

    /**
     * {@inheritDoc}
     */
    public void drawBoard(Graphics g, int boardX, int boardY, int boardWidth, int boardHeight, int[] gameState) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setPaint(new GradientPaint(boardX, boardY, startColor, boardX + boardWidth, boardY + boardHeight, endColor));
        g2.fillRect(boardX, boardY, boardWidth, boardHeight);

        int gap = boardWidth / 50;
        int gridWidth = (boardWidth - (gap * 9)) / 8;
        int gridHeight = (boardHeight - (gap * 3) / 2);

        int pitDiameter = Math.min(gridWidth, gridHeight);
        int storeWidth = pitDiameter;
        int storeHeight = boardHeight - (gap * 2);

        int horiOffset = (gridWidth - pitDiameter) / 2;
        int vertOffset = (gridHeight - pitDiameter) / 2;

        int leftStoreX = boardX + gap + horiOffset;
        int leftStoreY = boardY + gap + vertOffset;
        
        drawHole(g2, leftStoreX, leftStoreY, storeWidth, storeHeight, gameState[13]);

        for (int i = 0; i < 6; i++) {
            int pitX = boardX + (gap * (i + 2)) + (gridWidth * (i + 1)) + horiOffset;
            int topPitY = boardY + gap + vertOffset;
            int botPitY = boardY + gridHeight + gap + vertOffset; 

            drawCircularPit(g2, pitX, topPitY, pitDiameter, gameState[12 - i]);
            drawCircularPit(g2, pitX, botPitY, pitDiameter, gameState[i]);
        }

        int rightStoreX = boardX + boardWidth - gap - gridWidth + horiOffset;
        int rightStoreY = leftStoreY;

        drawHole(g2, rightStoreX, rightStoreY, storeWidth, storeHeight, gameState[6]);
    }

    protected void drawCircularPit(Graphics g, int pitX, int pitY, int pitDiameter, int stones) {
        drawHole(g, pitX, pitY, pitDiameter, pitDiameter, stones);
    }

    protected void drawHole(Graphics g, int holeX, int holeY, int holeWidth, int holeHeight, int stones) {
        Graphics2D g2 = (Graphics2D) g;

        float minRadius = holeWidth * 0.5f;
        float maxRadius = holeHeight * 0.5f;

        Point2D center = new Point2D.Float(holeX + minRadius, holeY + maxRadius);
        Point2D focus = new Point2D.Float(holeX + (holeWidth * pitFocusFactor), holeY + (holeHeight * pitFocusFactor));

        float gradRadius = (float) center.distance(focus) + maxRadius;

        float[] dist = {0.0f, 1.0f};
        Color[] colors = {pitLight, pitDark};

        RadialGradientPaint radialGrad = new RadialGradientPaint(
            center, gradRadius, focus, dist, colors, MultipleGradientPaint.CycleMethod.NO_CYCLE
        );

        float arcWidth = holeWidth;
        float arcHeight = holeWidth;
        RoundRectangle2D holeShape = new RoundRectangle2D.Float(holeX, holeY, holeWidth, holeHeight, arcWidth, arcHeight);

        g2.setPaint(radialGrad);
        g2.fill(holeShape);

        g2.setStroke(new BasicStroke(holeWidth * 0.02f)); 
        g2.setColor(pitOutline);              
        g2.draw(holeShape);

        if (stones > 0) {
            drawStonesWithinHole(g2, holeShape, stones);
        }
    }

    protected void drawStonesWithinHole(Graphics2D g2, Shape holeShape, int stones) {

    }
}