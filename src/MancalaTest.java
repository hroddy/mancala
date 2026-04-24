import javax.swing.JFrame;

/**
 * MancalaTest.java
 * 
 * Main entry point for the Mancala game application.
 * 
 * @author Hannah Roddy
 * @author Johnny Tsai
 * @author Nishan Bhattarai
 */

/**
 * Creates and initializes the Mancala application.
 */
public class MancalaTest {
    /**
     * Launches the Mancala game and connects the model, view/controller, and setup screen.
     * 
     * @param args unused command line arguments.
     */
    public static void main(String[] args) {
        int pitsPerSide = 6;
        int maxPitStart = 4;
        MancalaModel model = new MancalaModel(maxPitStart, pitsPerSide);
        MancalaViewController viewAndController = new MancalaViewController(model);
        model.addListener(viewAndController);
        
        JFrame gameFrame = new JFrame("Mancala");
        gameFrame.add(viewAndController);
        gameFrame.setSize(1200, 400);
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ConfigurationScreen setup = new ConfigurationScreen(
            viewAndController, model, gameFrame, model.getMaxPitStart(), model.getPitsPerSide()
        );
        
        setup.setVisible(true);
    }
}
