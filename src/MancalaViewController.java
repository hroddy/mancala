/**
 * MancalaViewController.java
 * 
 * Renders the Mancala game board and handles user interactions.
 * Use the BoardStyle interface to determine how to draw the board and its components.
 * 
 * @author Hannah Roddy
 * @author Johnny Tsai
 * @author Nishan Bhattarai
 */

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * 
 */
public class MancalaViewController extends JPanel implements MancalaListener {
    private MancalaModel model;
    private BoardStyle style;

    public MancalaViewController(MancalaModel model) {
        this.model = model;
    }

    /**
     * Set look and feel of Mancala board to selected style.
     * 
     * @param selectedStyle selected board style.
     */
    public void setStyle(BoardStyle style){
        this.style = style;
    }

    /**
     * Called by the model when the game state changes.
     * Update the view to reflect the new game state.
     */
    public void boardChanged() {
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        style.drawBoard(g2, this.getX(), this.getY(), this.getWidth(), this.getHeight(), model.getGameState());
    }
}
