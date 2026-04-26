/**
 * MancalaBoard.java
 * 
 * The data structure of the board and its manipulation methods.
 * 
 * @author Hannah Roddy
 * @author Johnny Tsai
 * @author Nishan Bhattarai
 */

/**
 * Supports moving and adding stones, and retrieving current state of pits and stores.
 */
public class MancalaBoard {
    private int[] holes;

    /**
     * Creates an array representing all holes on the board
     * (two stores and the remaining pits).
     * 
     * Precondition: numHoles is an even number >= 4.
     * Postcondition: holes is initialized with size numHoles.     
     * 
     * @param numHoles size of array representing all the holes.
     *                 - Indices 0 to (numHoles/2 - 2) represent Player A's pits.
     *                 - Index (numHoles/2 - 1) is Player A's store.
     *                 - Indices (numHoles/2) to (numHoles - 2) represent Player B's pits.
     *                 - Index (numHoles - 1) is Player B's store.
     */
    public MancalaBoard(int numHoles){
        holes = new int[numHoles];
    }

    /**
     * Initializes all pits to stonesPerPit and sets both stores to 0.
     * 
     * Precondition:  stonesPerPit >= 3.
     * Postcondition: Postcondition: Each index representing a pit is populated with 
     *                stonesPerPit stones, and both stores are set to 0.
     * 
     * @param stonesPerPit How many stones to put in each pit.
     */
    public void setStonesPerPit(int stonesPerPit) {
        for (int i = 0; i < holes.length; i++) {
            holes[i] = (i == storeAIndex() || i == storeBIndex()) ? 0 : stonesPerPit;
        }
    }
    
    /**
     * Precondition: none.
     * Postcondition: The number of stones total in player A's pits is returned.
     * 
     * @return total number of stones in player A's pits.
     */
    public int getPlayerAPitStoneCount() {
        int total = 0;
        for (int i = 0; i < storeAIndex(); i++) {
            total += holes[i];
        }
        return total;
    }

    /**
     * Precondition: none.
     * Postcondition: The number of stones total in player B's pits is returned.
     * 
     * @return total number of stones in player B's pits.
     */
    public int getPlayerBPitStoneCount() {
        int total = 0;
        for (int i = storeAIndex() + 1; i < storeBIndex(); i++) {
            total += holes[i];
        }
        return total;
    }

    /**
     * Precondition: none.
     * Postcondition: The stone count in Player A's store is returned.
     * 
     * @return the stone count in Player A's mancala / store.
     */
    public int getStoreA() {
        return holes[storeAIndex()];
    }

    /**
     * Precondition: none.
     * Postcondition: The stone count in Player B's store is returned.
     * 
     * @return the stone count in Player B's mancala / store.
     */
    public int getStoreB() {
        return holes[storeBIndex()];
    }

    /**
     * Returns the number of stones in the given hole.
     * 
     * Precondition: 0 <= holeIndex < numHoles
     * Postcondition: The number of stones in the specified hole is returned.
     * 
     * @param holeIndex index of given hole.
     * @return number of stones in given hole.
     */
    public int getStonesInHole(int holeIndex) {
        return holes[holeIndex];
    }

    /**
     * Returns index of the pit directly opposite the given pit.
     * The opposite pit is the mirrored position across the board.
     * Used for the capture rule, so stores are not valid inputs.
     * 
     * Precondition: pitIndex is an index that refers to a valid pit.
     * Postcondition: The index of the pit directly opposite the given pit is returned.
     *
     * @param pitIndex index of the given pit.
     * @return index of the pit directly opposite the given pit.
     * @throws IllegalArgumentException if pitIndex refers to a store.
     */
    public int getOppositePitIndex(int pitIndex) {
        if (pitIndex == storeAIndex() || pitIndex == storeBIndex()) {
            throw new IllegalArgumentException("Given index is for a store not a pit.");
        }
        return storeBIndex() - 1 - pitIndex;
    }

    /**
     * Removes all stones from the given pit and returns the number removed.
     * Stores are not valid inputs.
     * 
     * Precondition: pitIndex is an index that refers to a valid pit.
     * Postcondition: The number of stones that used to be in specified pit is returned.
     *                The number of stones in the pit is reset to 0.
     * 
     * @param pitIndex index of given pit.
     * @return number of stones that used to be in given pit.
     * @throws IllegalArgumentException if pitIndex refers to a store.
     */
    public int moveStonesOut(int pitIndex) {
        if (pitIndex == storeAIndex() || pitIndex == storeBIndex()) {
            throw new IllegalArgumentException("Given index is for a store not a pit.");
        }
        int numStones = getStonesInHole(pitIndex);
        holes[pitIndex] = 0;
        return numStones;
    }

    /**
     * Increments the stone count in the specified hole by one.
     * The hole may be either a pit or a store.
     *
     * Precondition: 0 <= holeIndex < numHoles
     * Postcondition: The stone count in the specified hole is incremented by one.
     * 
     * @param holeIndex index of the hole to update.
     */
    public void addStoneToHole(int holeIndex) {
        holes[holeIndex]++;
    }

    /**
     * Increments the stone count in the specified hole by the given amount.
     * The hole may be either a pit or a store.
     *
     * Precondition: 0 <= holeIndex < numHoles, count >= 0.
     * Postcondition: The stone count in the specified hole is increased by count.
     * 
     * @param holeIndex index of the hole to update.
     * @param count number of stones to add.
     */
    public void addStonesToHole(int holeIndex, int count) {
        holes[holeIndex] += count;
    }

    /**
     * Returns a copy of the board state array.
     * Used by UndoManager to save snapshots.
     * 
     * Precondition: none.
     * Postcondition: A copy of the holes array representing the current game state is returned.
     * 
     * @return a copy of the holes array.
     */ 
    public int[] getBoardCopy() {
        return holes.clone();
    }

    /**
     * Overwrites the holes array with a previously saved snapshot.
     * Used by UndoManager to restore state.
     * 
     * Precondition: The snapshot of the given game state and the current game state must be the same length.
     * Postcondition: The current game state is replaced by the snapshot of the previous game state.
     * 
     * @param snapshot previous board state to restore.
     * @throws IllegalArgumentException if the snapshot has a different size than the board.
     */
    public void restoreBoard(int[] snapshot) {
        if (snapshot.length != holes.length) {
            throw new IllegalArgumentException("Snapshot size mismatch!");
        }
        holes = snapshot.clone();
    }

    /**
     * @return index of player A's store in the holes array.
     * 
     * Precondition: none.
     * Postcondition: Index of player A's store returned.
     */
    private int storeAIndex() {
        return holes.length / 2 - 1;
    }

    /**
     * @return index of player B's store in the holes array.
     * 
     * Precondition: none.
     * Postcondition: Index of player A's store returned.
     */
    private int storeBIndex() {
        return holes.length - 1;
    }
}
