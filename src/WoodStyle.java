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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.RadialGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.BasicStroke;

public class WoodStyle implements BoardStyle {
    private static final Color BOARD_LIGHT = new Color(170, 120, 80);
    private static final Color BOARD_DARK = new Color(90, 55, 30);

    private static final Color PIT_LIGHT = new Color(130, 85, 50);
    private static final Color PIT_DARK = new Color(70, 40, 20);
    private static final Color PIT_OUTLINE = new Color(45, 25, 10);

    private static final float PIT_FOCUS_FACTOR = 0.35f;

    private static final Color START_COLOR;
    private static final Color END_COLOR;

    static {
        boolean isTopLeft = PIT_FOCUS_FACTOR < 0.5f;
        if (isTopLeft) {
            START_COLOR = BOARD_DARK;
            END_COLOR = BOARD_LIGHT;
        } else {
            START_COLOR = BOARD_LIGHT;
            END_COLOR = BOARD_DARK;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Color getBoardColor() {
        return StyleUtils.blend(BOARD_LIGHT, BOARD_DARK);
    }

    /**
     * {@inheritDoc}
     */
    public void drawBoard(Graphics g, int boardX, int boardY, int width, int height, int[] stones) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setPaint(new GradientPaint(boardX, boardY, START_COLOR, boardX + width, boardY + height, END_COLOR));
        g2.fillRect(boardX, boardY, width, height);

        int gap = width / 50;
        int gridWidth = (width - (gap * 9)) / 8;
        int gridHeight = (height - (gap * 3) / 2);

        int pitSize = Math.min(gridWidth, gridHeight);
        int storeWidth = pitSize;
        int storeHeight = height - (gap * 2);

        int horiOffset = (gridWidth - pitSize) / 2;
        int vertOffset = (gridHeight - pitSize) / 2;

        int leftStoreX = boardX + gap + horiOffset;
        int leftStoreY = boardY + gap + vertOffset;
        
        drawStore(g2, leftStoreX, leftStoreY, storeWidth, storeHeight, stones[13]);

        for (int i = 0; i < 6; i++) {
            int pitX = boardX + (gap * (i + 2)) + (gridWidth * (i + 1)) + horiOffset;
            int topPitY = boardY + gap + vertOffset;
            int botPitY = boardY + gridHeight + gap + vertOffset; 

            drawPit(g2, pitX, topPitY, pitSize, stones[12 - i]);
            drawPit(g2, pitX, botPitY, pitSize, stones[i]);
        }

        int rightStoreX = boardX + width - gap - gridWidth + horiOffset;
        int rightStoreY = leftStoreY;

        drawStore(g2, rightStoreX, rightStoreY, storeWidth, storeHeight, stones[6]);
    }

    /**
     * {@inheritDoc}
     */
    public void drawPit(Graphics g, int x, int y, int size, int stones) {
        Graphics2D g2 = (Graphics2D) g;

        float pitRadius = size / 2f;

        Point2D center = new Point2D.Float(x + pitRadius, y + pitRadius);
        Point2D focus = new Point2D.Float(x + (size * PIT_FOCUS_FACTOR), y + (size * PIT_FOCUS_FACTOR));

        float gradRadius = (float) center.distance(focus) + pitRadius;

        float[] dist = {0.0f, 1.0f};
        Color[] colors = {PIT_LIGHT, PIT_DARK};

        RadialGradientPaint radialGrad = new RadialGradientPaint(
            center, gradRadius, focus, dist, colors, MultipleGradientPaint.CycleMethod.NO_CYCLE
        );

        g2.setPaint(radialGrad);
        g2.fillOval(x, y, size, size);

        g2.setStroke(new BasicStroke(size * 0.02f)); 
        g2.setColor(PIT_OUTLINE);              
        g2.drawOval(x, y, size, size);

        if (stones > 0) {
            drawStonesWithin(g2, x, y, size, stones);
        }
    }

    private void drawStore(Graphics g, int x, int y, int width, int height, int stones) {

    }

    private void drawStonesWithin(Graphics2D g2, int x, int y, int pitSize, int stones) {

    }
}
