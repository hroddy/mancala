public class MancalaView implements MancalaListener {
    // The view
    // This class will be responsible for rendering the Mancala game board and handling user interactions.
    // Per the project description, the view doubles as the controller
    // It will use the BoardStyle interface to determine how to draw the board and its components.
    @Override
    public void boardChanged() {
        // This method will be called by the model when the game state changes.
        // It should update the view to reflect the new game state.
    }
}
