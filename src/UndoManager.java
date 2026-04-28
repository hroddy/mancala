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
    private static final int MAX_UNDOS = 3;
    
    private int[] savedBoard; // Snapshot of the board before the last move.
    private boolean canUndo; // Whether an undo is available. False after an undo until a new move is made.
    private int undoCount; // Number of undos used this turn. Resets when the turn changes.

    /**
     * Constructs an UndoManager with no saved state.
     *
     * Precondition: none.
     * Postcondition: No undo is available and undo count is 0.
     */
    public UndoManager() {
        savedBoard = null;
        canUndo = false;
        undoCount = 0;
    }

    /**
     * Saves the current board state before a move is made.
     * Resets canUndo to true so the player may undo this move.
     * 
     * Precondition: none.
     * Postcondition: savedBoard hold the pre-move state. canUndo is true.
     *
     * @param boardSnapshot deep copy of the board before the move.
     */
    public void saveState(int[] boardSnapshot) {
        savedBoard = boardSnapshot;
        canUndo = true;
    }

    /**
     * Returns whether an undo is currently allowed.
     *
     * Precondition: none.
     * Postcondition: Returns true if canUndo is true and 
     *                undoCount is less than MAX_UNDOS;
     *                returns false otherwise.
     * 
     * @return true if undo is available.
     */
    public boolean canUndo() {
        return canUndo && undoCount < MAX_UNDOS;
    }

    /**
     * Returns the saved board snapshot and marks undo as unavailable until the next move.
     * 
     * Precondition: canUndo() returns true for the method to have an effect.
     * Postcondition: canUndo is false and undoCount is incremented.
     *
     * @return the saved board snapshot, or null if no snapshot is available.
     */
    public int[] undo() {
        if (!canUndo()) return null;
        canUndo = false;
        undoCount++;
        return savedBoard;
    }

    /**
     * Resets all undo state. Called when a new game starts.
     *
     * Precondition: none.
     * Postcondition: savedBoard is null, canUndo is false, undoCount is 0.
     */
    public void reset() {
        savedBoard = null;
        canUndo = false;
        undoCount = 0;
    }

    /**
     * Resets the undo count for the current turn.
     * Clears both the undo count and the canUndo flag so 
     * the incoming player cannot undo the outgoing player's last move.
     * Called by MancalaModel when the turn changes.
     *
     * Precondition: none.
     * Postcondition: undoCount is 0 and canUndo is false.
     */
    public void resetUndoCount() {
        undoCount = 0;
        canUndo = false;
    }
}
