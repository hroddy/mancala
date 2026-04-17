/**
 * MancalaModel.java
 *
 * Model containing game logic and state of the Mancala game.
 *
 * @author Hannah Roddy
 * @author Johnny Tsai
 * @author Nishan Bhattarai
 */
import java.util.ArrayList;
import java.util.List;

public class MancalaModel {
    /**
     * The underlying board holding stone counts for all 14 pits.
     */
    private MancalaBoard board;
    /**
     * List of views that want to be notified when the board changes.
     */
    private List<MancalaListener> listeners;
    /**
     * It is true if it is currently Player A's turn, false if it is Player B's turn.
     */
    private boolean isPlayerATurn;
    /**
     * True once game has ended and no more moves will be proceed furthur.
     */
    private boolean gameOver;
    /**
     * Handles the saving and restoring board snapshots for the undo feature
     */
    private UndoManager undoManager;

    /**
     * Construct a new MancalaModel with an empty board
     * Player A will start the game by default and since it a start the game is not over.
     */
    public MancalaModel() {
        board = new MancalaBoard();
        listeners = new ArrayList<>();
        isPlayerATurn = true;
        gameOver = false;
        undoManager = new UndoManager();

    }

    /**
     * It will register the listeners that will be notified whenever the board changes
     *
     * @param listener the lister to add
     */

    public void addListener(MancalaListener listener) {
        listeners.add(listener);
    }

    /**
     * It will notify all the listeners that the board state has changed.
     * I will call internally after every move, undo or game setup
     */
    private void notifyListeners() {
        // loop through every registered listener and call its callback
        for (MancalaListener listener : listeners) {
            listener.boardChanged();
        }
    }

    /**
     * Initializes all 12 pits with the given number of stones and leave the both stores on the side empty
     * Player will agree on this number before the game starts it will be either 3 or 4.
     *
     * @param stonesPerPit the number of stones to place in each pit
     */
    public void setUpBoard(int stonesPerPit) {
        board.setStonesPerPit(stonesPerPit);
        isPlayerATurn = true;    // new game it will reset turn state and notify the view to repaint
        gameOver = false;
        undoManager.reset();
        notifyListeners();
    }

    /**
     * @return the MancalaBoard so views can read pit and store counts
     */
    public MancalaBoard getBoard() {
        return board;
    }

    /**
     * @return true if its Player A's turn
     */
    public boolean isPlayerATurn() {
        return isPlayerATurn;
    }

    /**
     * @return true if the game is ended
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * It will return the winner of the game or a tie message once the game is over.
     * It will only be call after the isGameOver() returns true
     *
     * @return Player A if A has a more stones, Player B if B has more, otherwise return tie.
     */
    public String getWinner() {
        int a = board.getStoreA();
        int b = board.getStoreB();
        if (a > b)
            return "Player A";
        if (b > a)
            return "Player B";
        return "Tie";
    }

    /**
     * Makes a move from the pit the player clicked on.
     * It picks up all the stones from that pit and drops them one by one going counterclockwise around the board.
     * Skip the other player's store when dropping the stones.
     * If your last stone lands in your own store you will go again.
     * The board is saved before the move so that the player can undo it.
     *
     * @param pitIndex which pit to move from
     */
    public void makeMove(int pitIndex) {
        if (gameOver) return;      // reject the move after the game is over
        if (pitIndex < 0 || pitIndex > 12 || pitIndex == 6)
            return;
        if (isPlayerATurn && pitIndex > 5)  //Make sure that the player picks from their own side
            return;
        if (!isPlayerATurn && pitIndex < 7)
            return;
        if (board.getStonesInPit(pitIndex) == 0)         //can't pick an empty pit
            return;
        undoManager.saveState(board.getBoardCopy(), isPlayerATurn);     // Save the board so we can undo later if needed
        int stones = board.moveStonesOut(pitIndex);
        int currentIndex = pitIndex;

        while (stones > 0) {
            currentIndex = (currentIndex + 1) % 14;
            if (isPlayerATurn && currentIndex == 13)
                continue;
            if (!isPlayerATurn && currentIndex == 6)
                continue;

            board.addStoneToPit(currentIndex);
            stones--;
        }
        boolean landedInOwnStore = (isPlayerATurn && currentIndex == 6) || (!isPlayerATurn && currentIndex == 13);
        if (!landedInOwnStore) {
            isPlayerATurn = !isPlayerATurn;
        }
        checkGameOver();
        notifyListeners();
    }
    /**
     * Checks if one side of the board is completely empty.
     * If it is true the game is over. Any Stones left on the other side get moved into that player's store.
     * Then we mark the game as finished
     */
    private void checkGameOver() {
        int sideA = 0;
        int sideB = 0;
        for (int i = 0; i <= 5; i++) sideA += board.getStonesInPit(i);
        for (int i = 7; i <= 12; i++) sideB += board.getStonesInPit(i);

        if (sideA == 0 || sideB == 0) {          // Move any leftover stones on A's side into A's store.
            if (sideA > 0) {
                for (int i = 0; i <= 5; i++) {
                    int n = board.moveStonesOut(i);
                    for (int j = 0; j < n; j++) board.addStoneToPit(6);
                }
            }
            if (sideB > 0) {
                for (int i = 7; i <= 12; i++) {
                    int n = board.moveStonesOut(i);
                    for (int j = 0; j < n; j++)
                        board.addStoneToPit(13);
                }
            }
            gameOver = true;
        }
    }

    /**
     * Undo the last move and put the board back to how it was before the player made their move.
     * The player can only undo upto 3 times per turn and cn't undo twice in a row without making a move in between.
     */
    public void undo() {
        if (!undoManager.canUndo())
            return;
        int[] snapshot = undoManager.undo();
        if (snapshot != null) {
            board.restoreBoard(snapshot);
            isPlayerATurn = undoManager.getSavedTurn();
            notifyListeners();
        }
    }
}
