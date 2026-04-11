/**
 * MancalaModel.java
 * 
 * Model containing game logic and state of the Mancala game.
 * 
 * @author Hannah Roddy
 * @author Johnny Tsai
 * @author Nishan Bhattarai
 */

import java.util.List;

public class MancalaModel {
    private MancalaBoard board;
    private List<MancalaListener> listeners;

    public MancalaModel() {
        board = new MancalaBoard();
    }

    public void addListener(MancalaListener listener) {
        listeners.add(listener);
    }

    public void setUpBoard(int stonesPerPit) {
        board.setStonesPerPit(stonesPerPit);
    }
}
