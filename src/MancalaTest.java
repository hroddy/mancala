/**
 * MancalaTest.java
 * 
 * Main entry point for mancala game app.
 * 
 * @author Hannah Roddy
 * @author Johnny Tsai
 * @author Nishan Bhattarai
 */

public class MancalaTest {
    public static void main(String[] args) {
        MancalaModel model = new MancalaModel();
        MancalaViewController viewController = new MancalaViewController();
        model.addListener(viewController);
    }
}
