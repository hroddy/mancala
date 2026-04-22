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
    private static final int RANDOM_RETRIES = 100;
    private static final double MIN_DIST = 0.15;
    private static final double MIN_SQUARE_DIST = MIN_DIST * MIN_DIST;
    private static final double STONE_SIZE_RATIO = 0.15;
    private static final double OUTLINE_RATIO = 0.03;
    private static final double BOLD_OUTLINE_RATIO = 0.05;
    private static final double GAP_RATIO = 0.02;
    private static final float[] GRAD_RATIOS = {0f, 0.75f};
    
    private final int numHoles;
    private final int maxStones;
    private final int numHoriGaps;

    private final Color outlineColor;
    private final double pitFocusFactor;
    private final Color[] boardGradientColors;
    private final Color[] holeGradientColors; 
    private final Color[] stoneGradientColors;

    private final double[][] horiStoneDistRatios;
    private final double[][] vertStoneDistRatios;

    private int lastBoardWidth;
    private int lastBoardHeight;
    private double pitDiameter;

    private final RoundRectangle2D[] holeShapes;
    private final RadialGradientPaint[] holePaints;
    private BasicStroke defaultHoleStroke;
    private BasicStroke boldHoleStroke;
    
    private final double[][] stoneX;
    private final double[][] stoneY;
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
     * @param outlineColor color of outline around the pit (and store).
     * @param pitFocusFactor relative position of radial gradient focus point within a hole;
     *                       expressed as a fraction of the hole's width and height.
     * @param maxPitStart max number of stones per pit at start of Mancala game.
     * @param pitsPerSide number of pits for each of the pair of players.
     */
    protected GradientBoardStyle(
        Color materialLight, 
        Color materialDark, 
        Color pitLight, 
        Color pitDark, 
        Color outlineColor, 
        double pitFocusFactor,
        int maxPitStart,
        int pitsPerSide
    ) { 
        this.pitFocusFactor = pitFocusFactor;
        this.outlineColor = outlineColor; 

        boardGradientColors = (pitFocusFactor < 0.5) ? new Color[]{materialLight, materialDark} : new Color[]{materialDark, materialLight};
        holeGradientColors = new Color[]{pitDark, pitLight};
        stoneGradientColors = new Color[]{materialLight, materialDark};
        
        numHoles = 2 * pitsPerSide + 2;
        maxStones = maxPitStart * pitsPerSide * 2;
        numHoriGaps = pitsPerSide + 3;

        horiStoneDistRatios = new double[numHoles][maxStones];
        vertStoneDistRatios = new double[numHoles][maxStones];

        holeShapes = new RoundRectangle2D[numHoles];
        holePaints = new RadialGradientPaint[numHoles];
        
        stoneX = new double[numHoles][maxStones];
        stoneY = new double[numHoles][maxStones];

        initStoneDistRatios();
    }

    /**
     * {@inheritDoc}
     */
    public void drawBoard(Graphics2D g2, int boardWidth, int boardHeight, int[] gameState, boolean isPlayerATurn, boolean isGameOver) {
        g2.setPaint(new LinearGradientPaint(0, 0, boardWidth, boardHeight, GRAD_RATIOS, boardGradientColors));
        g2.fillRect(0, 0, boardWidth, boardHeight);
        
        if(lastBoardHeight != boardHeight || lastBoardWidth != boardWidth) {
            computeHoleShapes(boardWidth, boardHeight);
            computeHoleStroke();
            computeHolePaints();
            computeMasterStoneShape();
            computeStoneLocations();
            computeStoneStroke();
            computeMasterStonePaint();
            this.lastBoardWidth = boardWidth;
            this.lastBoardHeight = boardHeight;
        }

        renderHoles(g2, gameState, isPlayerATurn, isGameOver);
    }

    /**
     * {@inheritDoc}
     */
    public int getPitAt(int clickX, int clickY) {
        for (int i = 0; i < numHoles; i++) {
            if (holeShapes[i].contains(clickX, clickY)) {
                if (i != numHoles - 1 && i != numHoles / 2 -1) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Based on current board dimensions, computes hole shapes with locations incorporated.
     * Updates cache of hole shapes.
     * 
     * @param boardWidth total width of the board panel.
     * @param boardHeight total height of the board panel.
     */
    private void computeHoleShapes(int boardWidth, int boardHeight) {
        double gap = boardWidth * GAP_RATIO;
        double cols = numHoriGaps - 1;
        double gridWidth = (boardWidth - (gap * numHoriGaps)) / cols;
        double gridHeight = (boardHeight - (gap * 3)) / 2;
        pitDiameter = Math.min(gridWidth, gridHeight);  
        double horiOffset = (gridWidth - pitDiameter) / 2;
        double vertOffset = (gridHeight - pitDiameter) / 2;
        double storeHeight = 2 * (pitDiameter + vertOffset) + gap;

        // Left store — Player B
        holeShapes[numHoles - 1] = new RoundRectangle2D.Double(
            gap + horiOffset, gap + vertOffset, pitDiameter, storeHeight, pitDiameter, pitDiameter
        );

        // Right store — Player A
        holeShapes[numHoles / 2 - 1] = new RoundRectangle2D.Double(
            boardWidth - gap - gridWidth + horiOffset, gap + vertOffset, pitDiameter, storeHeight, pitDiameter, pitDiameter
        );
        
        // Columns of pits
        int numPits = numHoles - 2;
        int pitsPerSide = numPits / 2;
        for (int pit = 0; pit < pitsPerSide; pit++) {
            double pitX = (gap * (pit + 2)) + (gridWidth * (pit + 1)) + horiOffset;
            double pitBotY = gridHeight + 2 * gap + vertOffset;
            double pitTopY = gap + vertOffset;
            holeShapes[pit] = new RoundRectangle2D.Double(
                pitX, pitBotY, pitDiameter, pitDiameter, pitDiameter, pitDiameter
            );
            holeShapes[numPits - pit] = new RoundRectangle2D.Double(
                pitX, pitTopY, pitDiameter, pitDiameter, pitDiameter, pitDiameter
            );
        }
    }

    /**
     * Based on current pit dimensions, computes the outline stroke for holes.
     * Updates cached hole stroke.
     */
    private void computeHoleStroke() {
        defaultHoleStroke = new BasicStroke((float)(pitDiameter * OUTLINE_RATIO));
        boldHoleStroke = new BasicStroke((float)(pitDiameter * BOLD_OUTLINE_RATIO));
    }

    /**
     * For each hole, compute its paint based on its dimensions and the pit focus factor.
     * Updates cache of hole paints.
     */
    private void computeHolePaints() {
        for(int hole = 0; hole < numHoles; hole++){
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

    /**
     * For each hole, compute fixed stone locations based on hole dimensions and cached ratios
     */
    private void computeStoneLocations() {
        double stoneDiameter = masterStoneShape.getWidth();
        double stoneRadius = stoneDiameter / 2.0;

        for (int hole = 0; hole < numHoles; hole++) {
            RoundRectangle2D holeShape = holeShapes[hole];
        
            double maxHori = (holeShape.getWidth() - 2 * stoneDiameter) / 2.0;
            double maxVert = (holeShape.getHeight() - 2 * stoneDiameter) / 2.0 - maxHori;

            for (int stone = 0; stone < maxStones; stone++) {
                double xRatio = horiStoneDistRatios[hole][stone];
                double yRatio = vertStoneDistRatios[hole][stone];

                double xDisplace = xRatio * maxHori;
                double yLimit = maxVert + Math.sqrt((maxHori * maxHori) - (xDisplace * xDisplace));
                double yDisplace = yRatio * yLimit;

                stoneX[hole][stone] = holeShape.getCenterX() + xDisplace - stoneRadius;
                stoneY[hole][stone] = holeShape.getCenterY() + yDisplace - stoneRadius;
            }
        }
    }

    /**
     * Based on the width of the holes, compute stone shape template.
     * Update cached stone shape template.
     */
    private void computeMasterStoneShape() {
        double stoneDiameter = pitDiameter * STONE_SIZE_RATIO;
        masterStoneShape = new Ellipse2D.Double(0, 0, stoneDiameter, stoneDiameter);
    }

    /**
     * Based on current stone template dimension, compute its outline stroke.
     * Updates cached stone stroke.
     */
    private void computeStoneStroke() {
        double stoneDiameter = masterStoneShape.getWidth();
        stoneStroke = new BasicStroke((float)(stoneDiameter * OUTLINE_RATIO));
    }

    /**
     * Based on current stone template dimension and the pit focus factor, compute its paint.
     * Updated cached stone paint.
     */
    private void computeMasterStonePaint() {
        double stoneDiameter = masterStoneShape.getWidth();
        double radius = stoneDiameter / 2.0;

        Point2D center = new Point2D.Double(radius, radius);
        
        double offset = stoneDiameter * (1 - pitFocusFactor);
        Point2D focus = new Point2D.Double(offset, offset);

        float gradRadius = (float)(center.distance(focus) + radius);

        this.masterStonePaint = new RadialGradientPaint(center, gradRadius, focus, GRAD_RATIOS, stoneGradientColors, CycleMethod.NO_CYCLE);
    }

    /**
     * Renders all holes with lighting effects.
     * 
     * @param g2 graphics context used to draw the hole.
     * @param gameState array containing stone counts for all pits and stores.
     * @param isPlayerATurn if true player A's pit outlines will be bolded, if false player B's pit outlines will be bolded.
     * @param isGameOver if true no player's pit outlines will be bolded, if false one player's pit outlines will be bolded.
     */
    private void renderHoles(Graphics2D g2, int[] gameState, boolean isPlayerATurn, boolean isGameOver) {
        int storeA = numHoles / 2 - 1;
        
        for(int hole = 0; hole < numHoles; hole++){
            RoundRectangle2D holeShape = holeShapes[hole];
            
            g2.setPaint(holePaints[hole]);
            g2.fill(holeShape);

            if(isGameOver) {
                g2.setStroke(defaultHoleStroke);
            }
            else if (hole < storeA) {
                g2.setStroke(isPlayerATurn ? boldHoleStroke : defaultHoleStroke);
            }
            else if (hole == storeA) {
                g2.setStroke(defaultHoleStroke);
            }
            else if (hole > storeA && hole < numHoles - 1) {
                g2.setStroke(isPlayerATurn ? defaultHoleStroke : boldHoleStroke);
            }
            else {
                g2.setStroke(defaultHoleStroke);
            }
            
            g2.setColor(outlineColor);              
            g2.draw(holeShape);

            int stones = gameState[hole];
            if (stones > 0) {
                renderStonesWithinHole(g2, stones, hole);
            }
        }
    }

    /**
     * Renders the specified number of stones with lighting effects within the specified hole.
     * 
     * @param g2 graphics context used to draw the stones.
     * @param stones number of stones to draw.
     * @param holeID unique ID for hole, so stone placement remains stable when the hole is resized.
     */
    private void renderStonesWithinHole(Graphics2D g2, int stones, int holeID) {
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
     * Initialize distance ratios for the x and y of all possible stones.
     */
    private void initStoneDistRatios() {
        Random random = new Random();
        for (int hole = 0; hole < numHoles; hole++) {
            for (int stone = 0; stone < maxStones; stone++) {
                int tries = 0;
                double x, y;
                do {
                    x = random.nextDouble() * 2 - 1;
                    y = random.nextDouble() * 2 - 1;
                    tries++;
                } while(overlaps(hole, stone, x, y) && tries < RANDOM_RETRIES);

                horiStoneDistRatios[hole][stone] = x;
                vertStoneDistRatios[hole][stone] = y;
            }
        }
    }

    /**
     * Marks stone as overlap if distance with another stone less than predefined minimum.
     * 
     * @return true if overlap, false if not.
     */
    private boolean overlaps(int hole, int stone, double x, double y){
        for (int i = 0; i < stone; i++) {
            double xDist = x - horiStoneDistRatios[hole][i];
            double yDist = y - vertStoneDistRatios[hole][i];
            if (xDist * xDist + yDist * yDist < MIN_SQUARE_DIST) {
                return true;
            }
        }
        return false;
    }
}