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
import javax.swing.*;

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
    private  JPanel boardWithLabels;

    /**Side panel containing the vertical MANCALA B label. */
    private JPanel leftLabelPanel;
    /**Side panel containing the vertical MANCALA A label. */
    private JPanel rightLabelPanel;

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
                    style.drawBoard(g2, getWidth(), getHeight(), model.getBoardCopy(), model.isPlayerATurn(), model.isGameOver());
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

        boardWithLabels = new JPanel(new BorderLayout());
        leftLabelPanel = createMancalaLabel("B");
        rightLabelPanel = createMancalaLabel("A");
        boardWithLabels.add(leftLabelPanel, BorderLayout.WEST);
        boardWithLabels.add(boardPanel, BorderLayout.CENTER);
        boardWithLabels.add(rightLabelPanel,BorderLayout.EAST);
        add(boardWithLabels, BorderLayout.CENTER);


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
     * Creates a vertical label panel for the two stores
     * Letters of the word MANCALA are stacked from top to bottom and a small gap and a player's letter A and B respectively.
     * @param playerLetter A or B the letters shown at the bottom of the each label
     * @return a JPanel containing the vertically stacked and centered label
     */
    private JPanel createMancalaLabel(String playerLetter) {
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new javax.swing.BoxLayout(labelPanel, javax.swing.BoxLayout.Y_AXIS));
        labelPanel.setOpaque(true);

        Font font = new Font("Arial", Font.BOLD, 18);
        labelPanel.add(javax.swing.Box.createVerticalGlue());

        String[]mancalaLetters = {"M", "A", "N", "C", "A", "L", "A"};
        for(String letter : mancalaLetters){
            JLabel jL = new JLabel(letter);
            jL.setFont(font);
            jL.setForeground(Color.black);
            jL.setAlignmentX(JLabel.CENTER_ALIGNMENT);
            labelPanel.add(jL);
        }
        labelPanel.add(javax.swing.Box.createVerticalStrut(15));
        JLabel playerLabel = new JLabel(playerLetter);
        playerLabel.setFont(font);
        playerLabel.setForeground(Color.BLACK);
        playerLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        labelPanel.add(playerLabel);

        labelPanel.add(javax.swing.Box.createVerticalGlue());
        labelPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 15, 20, 15));

        return labelPanel;
    }

    /**
     * Sets the visual style strategy used to paint the board and updates the label panel background
     * to match the board color so that MANCALA labels visually blend with the board
     *
     * @param style the BoardStyle implementation to use.
     * @precondition style is not null.
     * @postcondition all subsequent paintComponent calls will use the given style.
     */
    public void setStyle(BoardStyle style){

        this.style = style;
        Color boardColor = style.getBoardColor();
        boardWithLabels.setBackground(boardColor);
        leftLabelPanel.setBackground(boardColor);
        rightLabelPanel.setBackground(boardColor);
        repaint();
    }

    /**
     * {@inheritDoc}
     * Updates the turn label, repaints the board, and shows a game over dialog if the game has ended.
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
            JOptionPane.showMessageDialog(this, "Game Over! " + model.getWinner());
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
