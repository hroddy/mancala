/**
 * MancalaListener.java
 * 
 * Listener interface for receiving notifications when the Mancala model changes.
 * 
 * @author Hannah Roddy
 * @author Johnny Tsai
 * @author Nishan Bhattarai
 */

/**
 * Defines callback contract for objects that need to respond when the board state changes.
 * Decouples model from view by allowing model to notify listeners without depending on a concrete view class.
 */
public interface MancalaListener {
    /**
     * Called when the board state changes.
     */
    void boardChanged();
}
