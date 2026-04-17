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

private int[] savedBoard;
private boolean canUndo;
private int undoCount;



public UndoManager(){
    savedBoard = null;
    canUndo = false;
    undoCount = 0;
}

}
