import javax.swing.JFrame;

/**
 * MancalaTest.java
 * 
 * Main entry point for mancala game app.
 * 
 * @author Hannah Roddy
 * @author Johnny Tsai
 * @author Nishan Bhattarai
 */

/**
 * Client class for the Mancala game model.
 */
public class MancalaTest {
    /**
     * Initializes and connects model, view and controller.
     * 
     * @param args command line arguments not used.
     */
    public static void main(String[] args) {
        MancalaModel model = new MancalaModel();
        MancalaViewController viewAndController = new MancalaViewController(model);
        model.addListener(viewAndController);
        
        JFrame gameFrame = new JFrame("Mancala");
        gameFrame.add(viewAndController);
        gameFrame.setSize(1200, 400);
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        StyleSelectionScreen setup = new StyleSelectionScreen(
            viewAndController, gameFrame, model.getMaxPitStart(), model.getPitsPerSide()
        );
        
        setup.setVisible(true);
    }
}
