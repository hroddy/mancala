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
import java.awt.LinearGradientPaint;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.geom.RoundRectangle2D;
import java.util.Random;

/**
 * Provides shared fields and helper methods for drawing a gradient board, its holes (pits or stores), and the stones within them.
 */
public abstract class GradientBoardStyle implements BoardStyle {
    private static final int NUM_HOLES = MancalaModel.TOTAL_PITS + 2;
    private static final int MAX_STONES = MancalaModel.TOTAL_PITS * MancalaModel.MAX_PIT_START_STONES;
    private static final int NUM_HORI_GAPS = MancalaModel.TOTAL_PITS / 2 + 3;
    private static final double SIN_45 = Math.sin(Math.PI / 4);
    private static final double STONE_SIZE_RATIO = 0.15;
    private static final double OUTLINE_RATIO = 0.03;
    private static final double GAP_RATIO = 0.01;
    private static final float[] GRAD_RATIOS = {0f, 0.75f};

    private final Color outlineColor;
    private final double pitFocusFactor;

    private final Color[] boardGradientColors;
    private final Color[] holeGradientColors; 
    private final Color[] stoneGradientColors;

    private final double[][] horiStoneDistRatios = new double[NUM_HOLES][MAX_STONES];
    private final double[][] vertStoneDistRatios = new double[NUM_HOLES][MAX_STONES];

    private int lastBoardWidth;
    private int lastBoardHeight;

    private double pitDiameter;

    private final RoundRectangle2D[] holeShapes = new RoundRectangle2D[NUM_HOLES];
    private final RadialGradientPaint[] holePaints = new RadialGradientPaint[NUM_HOLES];
    private BasicStroke holeStroke;
    
    private final double[][] stoneX = new double[NUM_HOLES][MAX_STONES];
    private final double[][] stoneY = new double[NUM_HOLES][MAX_STONES];
    private Ellipse2D masterStoneShape;
    private RadialGradientPaint masterStonePaint;
    private BasicStroke stoneStroke;
    
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
        Color outlineColor, 
        double pitFocusFactor
    ) { 
        this.pitFocusFactor = pitFocusFactor;
        this.outlineColor = outlineColor; 

        boardGradientColors = (pitFocusFactor < 0.5) ? new Color[]{materialLight, materialDark} : new Color[]{materialDark, materialLight};
        holeGradientColors = new Color[]{pitDark, pitLight};
        stoneGradientColors = new Color[]{materialLight, materialDark};
        
        setStoneDistRatios();
    }

    private void setStoneDistRatios() {
        Random random = new Random();
        for (int hole = 0; hole < NUM_HOLES; hole++) {
            for (int stone = 0; stone < MAX_STONES; stone++) {
                horiStoneDistRatios[hole][stone] = random.nextDouble();
                vertStoneDistRatios[hole][stone] = random.nextDouble();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void drawBoard(Graphics2D g2, int boardWidth, int boardHeight, int[] gameState) {
        g2.setPaint(new LinearGradientPaint(0, 0, boardWidth, boardHeight, GRAD_RATIOS, boardGradientColors));
        g2.fillRect(0, 0, boardWidth, boardHeight);

        if(lastBoardHeight != boardHeight || lastBoardWidth != boardWidth) {
            setHoleShapes(boardWidth, boardHeight);
            setHoleStroke();
            setHolePaints();
            setStoneLocations();
            setMasterStoneShape();
            setStoneStroke();
            setMasterStonePaint();
            this.lastBoardWidth = boardWidth;
            this.lastBoardHeight = boardHeight;
        }

        drawHoles(g2, gameState);
    }

    /**
     * Computes the pixel boundaries of all holes based on current board dimensions.
     * Single source of truth for layout math — shared by drawBoard and getPitAt.
     * 
     * @param boardWidth total width of the board panel.
     * @param boardHeight total height of the board panel.
     */
    private void setHoleShapes(int boardWidth, int boardHeight) {
        double gap = boardWidth * GAP_RATIO;
        double cols = NUM_HORI_GAPS - 1;
        double gridWidth = (boardWidth - (gap * NUM_HORI_GAPS)) / cols;
        double gridHeight = (boardHeight - (gap * 3)) / 2;
        pitDiameter = Math.min(gridWidth, gridHeight);  
        double horiOffset = (gridWidth - pitDiameter) / 2;
        double vertOffset = (gridHeight - pitDiameter) / 2;
        double storeHeight = 2 * (pitDiameter + vertOffset) + gap;

        // Left store — Player B
        holeShapes[NUM_HOLES - 1] = new RoundRectangle2D.Double(
            gap + horiOffset, gap + vertOffset, pitDiameter, storeHeight, pitDiameter, pitDiameter
        );

        // Right store — Player A
        holeShapes[NUM_HOLES / 2 - 1] = new RoundRectangle2D.Double(
            boardWidth - gap - gridWidth + horiOffset, gap + vertOffset, pitDiameter, storeHeight, pitDiameter, pitDiameter
        );
        
        // 6 columns of pits
        for (int pit = 0; pit < MancalaModel.TOTAL_PITS / 2; pit++) {
            double pitX = (gap * (pit + 2)) + (gridWidth * (pit + 1)) + horiOffset;
            double pitBotY = gridHeight + 2 * gap + vertOffset;
            double pitTopY = gap + vertOffset;
            holeShapes[pit] = new RoundRectangle2D.Double(
                pitX, pitBotY, pitDiameter, pitDiameter, pitDiameter, pitDiameter
            );
            holeShapes[MancalaModel.TOTAL_PITS - pit] = new RoundRectangle2D.Double(
                pitX, pitTopY, pitDiameter, pitDiameter, pitDiameter, pitDiameter
            );
        }
    }

    private void setHoleStroke() {
        holeStroke = new BasicStroke((float)(pitDiameter * OUTLINE_RATIO));
    }

    private void setHolePaints() {
        for(int hole = 0; hole < NUM_HOLES; hole++){
            RoundRectangle2D holeShape = holeShapes[hole];

            Point2D center = new Point2D.Double(holeShape.getCenterX(), holeShape.getCenterY());
            Point2D focus = new Point2D.Double(
                holeShape.getX() + (holeShape.getWidth() * pitFocusFactor), 
                holeShape.getY() + (holeShape.getHeight() * pitFocusFactor)
            );

            float gradRadius = (float)(center.distance(focus) + (holeShape.getHeight() / 2));
            holePaints[hole] = new RadialGradientPaint(center, gradRadius, focus, GRAD_RATIOS, holeGradientColors, CycleMethod.NO_CYCLE);
        }
    }


    private void setStoneLocations() {
        double zoneOffset = pitDiameter - (pitDiameter * SIN_45);
        double outOfZone = (2 * zoneOffset) + (pitDiameter * STONE_SIZE_RATIO);

        for (int hole = 0; hole < NUM_HOLES; hole++) {
            RoundRectangle2D holeShape = holeShapes[hole];
            
            double holeHeight = holeShape.getHeight();
            double holeWidth = holeShape.getWidth();
            
            for (int stone = 0; stone < MAX_STONES; stone++) {
                stoneX[hole][stone] = holeShape.getX() + zoneOffset + (holeWidth - outOfZone) * horiStoneDistRatios[hole][stone];
                stoneY[hole][stone] = holeShape.getY() + zoneOffset + (holeHeight - outOfZone) * vertStoneDistRatios[hole][stone];
            }
        }
    }

    private void setMasterStoneShape() {
        double stoneDiameter = pitDiameter * STONE_SIZE_RATIO;
        masterStoneShape = new Ellipse2D.Double(0, 0, stoneDiameter, stoneDiameter);
    }

    private void setStoneStroke() {
        double stoneDiameter = masterStoneShape.getWidth();
        stoneStroke = new BasicStroke((float)(stoneDiameter * OUTLINE_RATIO));
    }

    private void setMasterStonePaint() {
        double stoneDiameter = masterStoneShape.getWidth();
        double radius = stoneDiameter / 2.0;

        Point2D center = new Point2D.Double(radius, radius);
        
        double offset = stoneDiameter * (1 - pitFocusFactor);
        Point2D focus = new Point2D.Double(offset, offset);

        float gradRadius = (float)(center.distance(focus) + radius);

        this.masterStonePaint = new RadialGradientPaint(center, gradRadius, focus, GRAD_RATIOS, stoneGradientColors, CycleMethod.NO_CYCLE);
    }

    /**
     * Draws all holes with lighting effects.
     * 
     * @param g2 graphics context used to draw the hole.
     */
    private void drawHoles(Graphics2D g2, int[] gameState) {
        for(int hole = 0; hole < NUM_HOLES; hole++){
            RoundRectangle2D holeShape = holeShapes[hole];
            
            g2.setPaint(holePaints[hole]);
            g2.fill(holeShape);

            g2.setStroke(holeStroke); 
            g2.setColor(outlineColor);              
            g2.draw(holeShape);

            int stones = gameState[hole];
            if (stones > 0) {
                drawStonesWithinHole(g2, stones, hole);
            }
        }
    }

    /**
     * Draws the specified number of stones within the specified hole.
     * 
     * @param g2 graphics context used to draw the stones.
     * @param stones number of stones to draw.
     * @param holeID unique ID for hole, so stone placement remains stable when the hole is resized.
     */
    protected void drawStonesWithinHole(Graphics2D g2, int stones, int holeID) {
        for (int stone = 0; stone < stones; stone++) {
            double x = stoneX[holeID][stone];
            double y = stoneY[holeID][stone];
            g2.translate(x, y);

            g2.setPaint(masterStonePaint);
            g2.fill(masterStoneShape);

            g2.setStroke(stoneStroke); 
            g2.setColor(outlineColor);              
            g2.draw(masterStoneShape);

            g2.translate(-x, -y);
        }
    }

    /**
     * {@inheritDoc}
     * Checks which hole contains the click point and returns the corresponding index.
     */
    public int getPitAt(int clickX, int clickY) {
        for (int i = 0; i < NUM_HOLES; i++) {
            if (holeShapes[i].contains(clickX, clickY)) {
                return i;
            }
        }
        return -1;
    }
}