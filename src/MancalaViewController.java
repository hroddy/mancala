/**
 * MancalaViewController.java
 *
 * Renders the Mancala game board and handles user interactions.
 * Serves as both the view and controller in the MVC architecture.
 * Uses the BoardStyle interface to determine how to draw the board and its components.
 *
 * @author Hannah Roddy
 * @author Johnny Tsai
 * @author Nishan Bhattarai
 */


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Displays the Mancala board and routes user input to the model.
 * Implements MancalaListener to repaint whenever the model's state changes.
 */
public class MancalaViewController extends JPanel implements MancalaListener {
    /** The game model this controller reads from and writes to. */
    private MancalaModel model;

    /** The pluggable style strategy used to draw the board. */
    private BoardStyle style;

    /** Displays whose turn it currently is. */
    private JLabel turnLabel;

    /** Allows the current player to undo their last move. */
    private JButton undoButton;

    /**
     * Commits the current player's move and advances the turn to the other player.
     * Enabled only when a move is pending confirmation.
     * Automatically invoked when the current player exhausts their undo allowance.
     */
    private JButton confirmButton;

    /**
     * Constructs the view/controller, wires up the undo button, turn label,
     * and mouse listener.
     *
     * @param model the MancalaModel driving the game logic.
     * @precondition model is not null.
     * @postcondition a board panel, turn label, and undo button are initialized and laid out. Style is null until setStyle() is called.
     */
    public MancalaViewController(MancalaModel model) {
        this.model = model;
        setLayout(new BorderLayout());

        JPanel boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (style != null) {
                    style.drawBoard(g2, getWidth(), getHeight(), model.getBoard().getBoardCopy(), model.isPlayerATurn(), model.isGameOver());
                }
            }
        };
        boardPanel.setBackground(Color.DARK_GRAY);

        // Map a mouse click to a pit index and forward it to the model.
        boardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (style == null || model.isGameOver()) return;
                if (model.isPendingTurnSwitch()) return;
                int pitIndex = style.getPitAt(e.getX(), e.getY());
                if (pitIndex != -1) {
                    model.makeMove(pitIndex);
                }
            }
        });

        add(boardPanel, BorderLayout.CENTER);

        JPanel controlBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));

        turnLabel = new JLabel("Player A's Turn");
        turnLabel.setFont(new Font("Arial", Font.BOLD, 16));

        undoButton = new JButton("Undo");
        undoButton.setPreferredSize(new Dimension(100, 35));
        undoButton.addActionListener(e -> model.undo());

        confirmButton = new JButton("Confirm Move");
        confirmButton.setPreferredSize(new Dimension(130, 35));
        confirmButton.setEnabled(false);
        confirmButton.addActionListener(e -> model.confirmMove());

        controlBar.add(turnLabel);
        controlBar.add(undoButton);
        controlBar.add(confirmButton);
        add(controlBar, BorderLayout.SOUTH);
    }

    /**
     * Sets the visual style strategy used to paint the board.
     *
     * @param style the BoardStyle implementation to use.
     * @precondition style is not null.
     * @postcondition all subsequent paintComponent calls will use the given style.
     */
    public void setStyle(BoardStyle style){
        this.style = style;
    }

    /**
     * Called by the model when the game state changes.
     * Refreshes the turn label, repaints the board, and shows a game over dialog if the game has ended.
     *
     * @precondition none.
     * @postcondition turn label reflects the current player. Board is repainted. If the game is over, a winner dialog is displayed.
     */
    @Override
    public void boardChanged() {
        if (model.isPendingTurnSwitch() && !model.canUndo()) {
            model.confirmMove();
            return;
        }
 
        boolean pending = model.isPendingTurnSwitch();
        undoButton.setEnabled(model.canUndo());
        confirmButton.setEnabled(pending);
        
        turnLabel.setText(model.isPlayerATurn() ? "Player A's Turn" : "Player B's Turn");
        repaint();

        if (model.isGameOver()) {
            undoButton.setEnabled(false);
            confirmButton.setEnabled(false);
            JOptionPane.showMessageDialog(this, "Game Over! Winner: " + model.getWinner());
        }
    }

    /**
     * Prompts players to select a starting stone count of 3 or 4.
     * Called once after the game frame becomes visible and a style has been selected.
     * Initializes the board via the model using the chosen count.
     *
     * @precondition setStyle() has been called and the game frame is visible.
     * @postcondition the model's board is initialized with the chosen number of stones per pit. Defaults to 3 if the dialog is closed without a selection.
     */
    public void promptStoneCount() {
        String[] options = {"3", "4"};
        int choice = JOptionPane.showOptionDialog(
            this,
            "How many stones per pit?",
            "Game Setup",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        int stonesPerPit = (choice == 1) ? 4 : 3;
        model.setUpBoard(stonesPerPit);
    }
}
