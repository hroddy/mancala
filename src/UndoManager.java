/**
 * UndoManager.java
 * 
 * Manage the undo functionality for the Mancala game, allowing players to undo their last move.
 * Keeps track of board states and the number of undos used by each player.
 * Players can make up to three undos per game.
 * 
 * @author Hannah Roddy
 * @author Johnny Tsai
 * @author Nishan Bhattarai
 */
public class UndoManager {

    /** Snapshot of the board before the last move. */
    private int[] savedBoard;

    /** Whose turn it was before the last move. */
    private boolean savedTurn;

    /** Whether an undo is currently available. False after an undo until a new move is made. */
    private boolean canUndo;

    /** Number of undos used this turn. Resets when the turn changes. */
    private int undoCount;

    private static final int MAX_UNDOS = 3;

    /**
     * Constructs an UndoManager with no saved state.
     *
     * @precondition none.
     * @postcondition savedBoard is null, canUndo is false, undoCount is 0.
     */
    public UndoManager() {
        savedBoard = null;
        canUndo = false;
        undoCount = 0;
    }

    /**
     * Saves the current board state and turn before a move is made.
     * Resets canUndo to true so the player may undo this move.
     *
     * @param boardSnapshot deep copy of the board before the move.
     * @param isPlayerATurn whose turn it is before the move.
     * @precondition boardSnapshot is a valid 14-element array.
     * @postcondition savedBoard and savedTurn hold the pre-move state. canUndo is true.
     */
    public void saveState(int[] boardSnapshot, boolean isPlayerATurn) {
        savedBoard = boardSnapshot;
        savedTurn = isPlayerATurn;
        canUndo = true;
    }

    /**
     * Returns whether an undo is currently allowed.
     * False if no move has been made, after an undo, or if the max undo count has been reached.
     *
     * @return true if undo is available.
     * @precondition none.
     * @postcondition none.
     */
    public boolean canUndo() {
        return canUndo && undoCount < MAX_UNDOS;
    }

    /**
     * Restores the saved board snapshot and marks undo as unavailable until the next move.
     *
     * @return the saved board snapshot, or null if no snapshot is available.
     * @precondition canUndo() returns true.
     * @postcondition canUndo is false. undoCount is incremented.
     */
    public int[] undo() {
        if (!canUndo()) return null;
        canUndo = false;
        undoCount++;
        return savedBoard;
    }

    /**
     * Returns whose turn it was before the last saved move.
     * Called by MancalaModel after undo() to restore the correct turn.
     *
     * @return true if it was Player A's turn before the last move.
     * @precondition saveState() has been called at least once.
     * @postcondition none.
     */
    public boolean getSavedTurn() {
        return savedTurn;
    }

    /**
     * Resets all undo state. Called when a new game starts.
     *
     * @precondition none.
     * @postcondition savedBoard is null, canUndo is false, undoCount is 0.
     */
    public void reset() {
        savedBoard = null;
        canUndo = false;
        undoCount = 0;
    }

    /**
     * Resets the undo count for the current turn.
     * Clears both the undo count and the canUndo flag so the incoming player cannot undo the outgoing player's last move.
     * Called by MancalaModel when the turn changes.
     *
     * @precondition none.
     * @postcondition undoCount is 0.
     */
    public void resetUndoCount() {
        undoCount = 0;
        canUndo = false;
    }
}
