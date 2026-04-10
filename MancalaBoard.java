public class MancalaBoard {
    // Part of the model
    // This class will represent the Mancala board and its state, including the pits and the stones.

    //Holds raw int array representing the 14 pits (6 for each player and 2 for the stores)
    private int[] pits = new int[14];

    // Initializes all 12 pits to stonesPerPit and sets both stores to 0.
    public void setupBoard(int stonesPerPit) {
        for (int i = 0; i < 14; i++) {
            if (i == 6 || i == 13) pits[i] = 0;
            else pits[i] = stonesPerPit;
        }
    }

    // Returns a copy of the 6 pit stone counts belonging to player A: 0-5, store: 6
    public int[] getPlayerAPits() {
        return new int[]{pits[0], pits[1], pits[2], pits[3], pits[4], pits[5], pits[6]};
    }

    // Returns a copy of the 6 pit stone counts belonging to player B: 7-12, store: 13
    public int[] getPlayerBPits() {
        return new int[]{pits[7], pits[8], pits[9], pits[10], pits[11], pits[12], pits[13]};
    }

    // Returns the stone count in Player A's mancala (index 6).
    public int getStoreA() {
        return pits[6];
    }

    // Returns the stone count in Player B's mancala store (index 13).
    public int getStoreB() {
        return pits[13];
    }

    public int getStonesInPit(int pitIndex) {
        if (pitIndex < 0 || pitIndex >= pits.length) {
            throw new IllegalArgumentException("Invalid pit index");
        }
        return pits[pitIndex];
    }

    // Returns the pit index directly opposite the given pit, used for the capture rule.
    // e.g. pit 0 is opposite pit 12, pit 1 is opposite pit 11, etc.
    public int getOppositeIndex(int pitIndex) {
        if (pitIndex < 0 || pitIndex >= pits.length) {
            throw new IllegalArgumentException("Invalid pit index");
        }
        return 12 - pitIndex;
    }

    // Called when a player selects a pit to move stones from.
    // This method will return the number of stones in the selected pit and set that pit to 0.
    public int moveStonesOut(int pitIndex, int stones) {
        if (pitIndex < 0 || pitIndex >= pits.length) {
            throw new IllegalArgumentException("Invalid pit index");
        }
        int numStones = getStonesInPit(pitIndex);
        pits[pitIndex] = 0;
        return numStones;
    }

    // As the player moves stones, we will need to add them to the pits.
    // This method will be used to add a stone to a specific pit in a for loop as the players traverses the board.
    public void addStoneToPit(int pitIndex) {
        if (pitIndex < 0 || pitIndex >= pits.length) {
            throw new IllegalArgumentException("Invalid pit index");
        }
        pits[pitIndex]++;
    }

    // Sets a specific pit to a given value, used when restoring a board snapshot.
    public void setPit(int pitIndex, int stones) {
        if (pitIndex < 0 || pitIndex >= pits.length) {
            throw new IllegalArgumentException("Invalid pit index");
        }
        pits[pitIndex] = stones;
    }

    // Returns a full deep copy of the pits array, used by UndoManager to save snapshots.
    public int[] getBoardCopy() {
        return pits.clone();
    }

    // Overwrites the pits array with a previously saved snapshot, used by UndoManager to restore state.
    public void restoreBoard(int[] snapshot) {
        if (snapshot.length != pits.length)
            throw new IllegalArgumentException("Snapshot size mismatch");
        pits = snapshot.clone();
    }
}
