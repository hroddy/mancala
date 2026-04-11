/**
 * MancalaBoard.java
 * 
 * This file holds the data structure of the board and its manipulation methods.
 * Supports moving and adding stones, and retrieving current state of pits and stores.
 * This class is part of the model in our MVC architecture.
 * 
 * @author Hannah Roddy
 * @author Johnny Tsai
 * @author Nishan Bhattarai
 */

public class MancalaBoard {
    /**
     * Holds raw int array representing the 14 pits (6 for each player and 2 for the stores).
     */
    private int[] pits = new int[14];

    /**
     * Initializes all 12 pits to stonesPerPit and sets both stores to 0.
     * 
     * @param stonesPerPit How many stones to put in each pit.
     */
    public void setStonesPerPit(int stonesPerPit) {
        for (int i = 0; i < 14; i++) {
            if (i == 6 || i == 13) pits[i] = 0;
            else pits[i] = stonesPerPit;
        }
    }

    /**
     * @return a copy of the 6 pit stone counts belonging to player A: 0-5.
     */
    public int[] getPlayerAPits() {
        return new int[]{pits[0], pits[1], pits[2], pits[3], pits[4], pits[5]};
    }

    /**
     * @return a copy of the 6 pit stone counts belonging to player B: 7-12.
     */
    public int[] getPlayerBPits() {
        return new int[]{pits[7], pits[8], pits[9], pits[10], pits[11], pits[12]};
    }

    /**
     * @return the stone count in Player A's mancala (index 6).
     */
    public int getStoreA() {
        return pits[6];
    }

    /**
     * @return the stone count in Player B's mancala store (index 13).
     */
    public int getStoreB() {
        return pits[13];
    }

    /**
     * Returns the number of stones in the given pit.
     * 
     * @param pitIndex index of given pit.
     * @return number of stones in given pit.
     */
    public int getStonesInPit(int pitIndex) {
        if (pitIndex < 0 || pitIndex >= pits.length) {
            throw new IllegalArgumentException("Invalid pit index");
        }
        return pits[pitIndex];
    }

    /**
     * Returns the index of the pit directly opposite the given pit.
     * Pit 0 is opposite of pit 12, pit 1 is opposite of pit 11, etc.
     * Used for capture rule. 
     * Cannot capture stores, so calling on index 6 or 13 is invalid.
     * 
     * @param pitIndex index of given pit.
     * @return index of pit directly opposite the given pit.
     * @throws IllegalArgumentException if pit index is invalid
     */
    public int getOppositeIndex(int pitIndex) {
        if (pitIndex < 0 || pitIndex == 6 || pitIndex > pits.length - 2) {
            throw new IllegalArgumentException("Invalid pit index");
        }
        return 12 - pitIndex;
    }

    /**
     * Returns the number of stones that used to be in given pit.
     * Sets the stones in given pit to 0.
     * Called when a player selects a pit to move stones from.
     * Cannot move pits out of stores, so calling on index 6 or 13 is invalid.
     * 
     * @param pitIndex index of given pit.
     * @return number of stones that used to be in given pit.
     * @throws IllegalArgumentException if pit index is invalid
     */
    public int moveStonesOut(int pitIndex) {
        if (pitIndex < 0 || pitIndex == 6 || pitIndex > pits.length - 2) {
            throw new IllegalArgumentException("Invalid pit index");
        }
        int numStones = getStonesInPit(pitIndex);
        pits[pitIndex] = 0;
        return numStones;
    }

    /**
     * As the player moves stones, we will need to add them to the pits or stores.
     * This method adds a stone to the given pit or store.
     * This method should be called in a for loop traversing over pits and stores on the board.
     * 
     * @param pitIndex index of given pit or store.
     * @throws IllegalArgumentException if pit or store index is invalid.
     */
    public void addStoneToPit(int pitIndex) {
        if (pitIndex < 0 || pitIndex >= pits.length) {
            throw new IllegalArgumentException("Invalid pit index");
        }
        pits[pitIndex]++;
    }

    /**
     * Returns a full deep copy of the pits array.
     * Used by UndoManager to save snapshots.
     * 
     * @return full deep copy of pits array.
     */ 
    public int[] getBoardCopy() {
        return pits.clone();
    }

    /**
     * Overwrites the pits array with a previously saved snapshot.
     * Used by UndoManager to restore state.
     * 
     * @param snapshot Previous state of board to restore.
     * @throws IllegalArgumentException If snapshot different size than board.
     */
    public void restoreBoard(int[] snapshot) {
        if (snapshot.length != pits.length)
            throw new IllegalArgumentException("Snapshot size mismatch");
        pits = snapshot.clone();
    }
}
