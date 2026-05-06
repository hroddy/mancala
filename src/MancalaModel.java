/**
 * MancalaModel.java
 *
 * Represents the model in the MVC architecture for a Mancala game.
 * 
 * @author Hannah Roddy
 * @author Johnny Tsai
 * @author Nishan Bhattarai
 */
import java.util.ArrayList;
import java.util.List;

/**
 * Maintains game state, enforces rules, and coordinates turn flow.
 */
public class MancalaModel {
    private final int maxPitStart;
    private final int pitsPerSide;
    
    private final MancalaBoard board; // The underlying board holding stone counts for all holes.
    private final List<MancalaListener> listeners; // List of views that want to be notified when the board changes.
    private final UndoManager undoManager; // Handles the saving and restoring board snapshots for the undo feature.
    
    private boolean isPlayerATurn; // True if it is currently Player A's turn, false if it is Player B's turn.
    private boolean gameOver; // True once game has ended and no more moves will be proceed furthur.
    private boolean pendingGameOver;
    private boolean pendingTurnSwitch; // True when current player made a move that would switch turn but has not yet confirmed it, false otherwise.

    private int storeA;
    private int storeB;

    /**
     * Constructs a new MancalaModel with an empty board.
     * Player A will start the game by default.
     */
    public MancalaModel(int maxPitStart, int pitsPerSide) {
        this.maxPitStart = maxPitStart;
        this.pitsPerSide = pitsPerSide;
        
        int numHoles = pitsPerSide * 2 + 2;
        board = new MancalaBoard(numHoles);
        listeners = new ArrayList<>();
        undoManager = new UndoManager();

        isPlayerATurn = true;

        storeA = numHoles / 2 - 1;
        storeB = numHoles - 1;
    }

    /**
     * Adds a listener that will be notified whenever the board changes.
     * 
     * Precondition: none.
     * Postcondition: Specified MancalaListener is added to listener list.
     *
     * @param listener the listener to add.
     */
    public void addListener(MancalaListener listener) {
        listeners.add(listener);
    }

    /**
     * Initializes all pits with the specified number of stones and leave the stores empty.
     * Players choose this number before the game starts.
     * 
     * Precondition: stonesPerPit <= maxPitStart
     * Postcondition: Each pit is populated with stonesPerPit stones.
     *
     * @param stonesPerPit the number of stones to place in each pit.
     * @throws IllegalArgumentException if stonesPerPit > maxPitStart.
     */
    public void setUpBoard(int stonesPerPit) {
        if (stonesPerPit > maxPitStart) {
            throw new IllegalArgumentException("stonesPerPit > maxPitStart");
        }
        board.setStonesPerPit(stonesPerPit);
        isPlayerATurn = true;
        undoManager.reset();
        notifyListeners();
    }

    /**
     * Precondition: none.
     * Postcondition: A copy of the underlying board state array is returned.
     * 
     * @return a copy of the current board state.
     */
    public int[] getBoardCopy() {
        return board.getBoardCopy();
    }

    /**
     * Precondition: none.
     * Postcondition: True is returned if player A's turn, false is returned if player B's turn.
     * 
     * @return true if it's Player A's turn, false if it's Player B's turn.
     */
    public boolean isPlayerATurn() {
        return isPlayerATurn;
    }

    /**
     * Precondition: none.
     * Postcondition: True is returned if game has ended, false is returned if game is still ongoing.
     * 
     * @return true if the game has ended, false if the game is still ongoing.
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Precondition: none.
     * Postcondition: Integer representing max pit start returned.
     * 
     * @return maximum number of stones a pit can start with in Mancala.
     */
    public int getMaxPitStart() {
        return maxPitStart;
    }

    /**
     * Precondition: none.
     * Postcondition: Integer representing number of pits on each side of the board returned.
     * 
     * @return number of pits on each side of the mancala board.
     */
    public int getPitsPerSide() {
        return pitsPerSide;
    }

    /**
     * Precondition: none.
     * Postcondition: True returned if current player made a move that is not a free move 
     *                or move ends the game, false returned otherwise.
     * 
     * @return true if current player made a move that must be confirmed before turn switches; false otherwise.
     */
    public boolean isPendingTurnSwitch() {
        return pendingTurnSwitch;
    }
 
    /**
     * Precondition: none.
     * Postcondition: True returned if current player can undo last move, false if undo disabled.
     * 
     * @return true if the current player is allowed to undo their last move.
     */
    public boolean canUndo() {
        return undoManager.canUndo();
    }

    /**
     * Returns the winner of the game, or "Tie" if scores are equal.
     * 
     * Precondition: Game must be over.
     * Postcondition: A string representing game result is returned.
     *
     * @return "Winner: Player A" if Player A has more stones,
     *         "Winner: Player B" if Player B has more stones,
     *         or "Tie" if both have the same number of stones.
     */
    public String getWinner() {
        int a = board.getStoreA();
        int b = board.getStoreB();
        if (a > b)
            return "Winner: Player A";
        if (b > a)
            return "Winner: Player B";
        return "Tie";
    }

    /**
     * Makes a move from the pit the player clicked on.
     * Stones are picked up and distributed counterclockwise, skipping the opponent's store.
     * 
     * If the last stone lands in the current player's own store, they get a free turn
     * (no confirmation required — they may move again immediately).
     * Otherwise, pendingTurnSwitch is set to true and the player must confirm before the turn advances.
     *
     * If the last stone lands in the current player's own empty pit, and the opposite pit contains stones,
     * all stones from the opposite pit and the landing pit are captured into the player's store.
     *
     * If the move causes one side of the board to become empty, all remaining stones
     * on the other side are swept into that player's store, and pendingTurnSwitch is set to true.
     * The player must confirm whether they want to make that move that ends the game.
     *
     * Precondition: pitIndex corresponds to a valid pit on the current player's side containing at least one stone.
     * Postcondition: Stones from the selected pit are distributed according to Mancala rules,
     *                captures are performed if applicable, and the game state is updated accordingly.
     *
     * @param pitIndex index of the pit from which stones are moved.
     */
    public void makeMove(int pitIndex) {
        if (pendingTurnSwitch ||
            pitIndex == storeA || 
            pitIndex == storeB || 
            (isPlayerATurn && pitIndex > storeA) || 
            (!isPlayerATurn && pitIndex < storeA) || 
            board.getStonesInHole(pitIndex) == 0
        ) return;

        undoManager.saveState(board.getBoardCopy());

        int stones = board.moveStonesOut(pitIndex);
        int currentIndex = pitIndex;

        while (stones > 0) {
            currentIndex = (currentIndex + 1) % (pitsPerSide * 2 + 2);
            if (isPlayerATurn && currentIndex == storeB) continue;
            if (!isPlayerATurn && currentIndex == storeA) continue;
            board.addStoneToHole(currentIndex);
            stones--;
        }

        boolean landedInOwnStore = (isPlayerATurn && currentIndex == storeA) || 
                                    (!isPlayerATurn && currentIndex == storeB);

        boolean landedOnOwnSide = (isPlayerATurn && currentIndex < storeA) ||
                                (!isPlayerATurn && currentIndex > storeA && currentIndex < storeB);

        if (landedOnOwnSide && board.getStonesInHole(currentIndex) == 1) {
            int oppositeIndex = board.getOppositePitIndex(currentIndex);
            int oppositeStones = board.moveStonesOut(oppositeIndex);
            
            if (oppositeStones > 0) {
                int store = isPlayerATurn ? storeA : storeB;
                board.addStonesToHole(store, oppositeStones + board.moveStonesOut(currentIndex));
            }
        }

        pendingTurnSwitch = !landedInOwnStore;

        if(checkAndSweep()){
            pendingTurnSwitch = true;
        }

        notifyListeners();
    }

    /**
     * Confirms the current player's move and advances the turn to the other player.
     * Has no effect if there is no pending turn switch
     * (e.g. during a free turn or before any move has been made).
     *
     * Precondition: pendingTurnSwitch is true for the method to have an effect.
     * Postcondition: Turn is switched, pendingTurnSwitch is set to false.
     *                If the confirmed move ended the game, gameOver is set to true.
     */
    public void confirmMove() {
        if (!pendingTurnSwitch) return;
        if (pendingGameOver) {
            gameOver = true;
        }
        isPlayerATurn = !isPlayerATurn;
        pendingTurnSwitch = false;
        undoManager.resetUndoCount();
        notifyListeners();
    }

    /**
     * Checks whether one side of the board is empty and, if so, 
     * sweeps the remaining stones on the other side into that player's store.
     *
     * Precondition: none.
     * Postcondition: If one side of the board is empty, all stones on the other side are
     *                transferred to the corresponding player's store, pendingGameOver is set
     *                to true, and true is returned. Otherwise, false is returned.
     *
     * @return true if a sweep occurred because one side was empty; false otherwise.
     */
    private boolean checkAndSweep() {
        int sideA = board.getPlayerAPitStoneCount();
        int sideB = board.getPlayerBPitStoneCount();

        if (sideA == 0 || sideB == 0) {
            for (int i = 0; i < storeA; i++) {
                board.addStonesToHole(storeA, board.moveStonesOut(i));
            }
            for (int i = storeA + 1; i < storeB; i++) {
                board.addStonesToHole(storeB, board.moveStonesOut(i));
            }
            pendingGameOver = true;
            return true;
        }

        return false;
    }


    /**
     * Undoes the last move and restores the board to its previous state (before the player made their move).
     * The player can only undo up to 3 times per turn and cannot undo twice in a row without making a move in between.
     * 
     * Precondition: Player can undo (not confirmed yet) for method to have an effect.
     * Postcondition: Board is restored to its previous state,
     *                pendingTurnSwitch and pendingGameOver are set to false.
     */
    public void undo() {
        if (!undoManager.canUndo()) return;
        int[] snapshot = undoManager.undo();
        if (snapshot != null) {
            board.restoreBoard(snapshot);
            pendingGameOver = false;
            pendingTurnSwitch = false;
            notifyListeners();
        }
    }

    /**
     * Notifies all added listeners that the board state has changed.
     * Changes include player move, undo operations, and game setup.
     * 
     * Precondition: none.
     * Postcondition: All attached listeners notified of board state update.
     */
    private void notifyListeners() {
        for (MancalaListener listener : listeners) {
            listener.boardChanged();
        }
    }
}
