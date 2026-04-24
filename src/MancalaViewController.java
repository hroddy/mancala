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
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;

/**
 * Displays the Mancala board and routes user input to the model.
 * Implements MancalaListener to repaint whenever the model's state changes.
 */
public class MancalaViewController extends JPanel implements MancalaListener {
    private static final Font TEXT_FONT = new Font("Arial", Font.BOLD, 20);
    private static final Dimension BUTTON_SIZE = new Dimension(130, 35);

    private MancalaModel model; // The game model this controller reads from and writes to.
    private BoardStyle style; // The pluggable style strategy used to draw the board.
    
    /*
     * Commits the current player's move and advances the turn to the other player.
     * Enabled only when a move is pending confirmation.
     * Automatically invoked when the current player exhausts their undo allowance.
     */
    private JButton confirmButton; 
    private JButton undoButton; // Allows the current player to undo their last move.
    private JLabel turnLabel; // Displays whose turn it currently is.
    
    private final JPanel boardWithLabels;
    private final JLabel storeALabel;
    private final JLabel storeBLabel;

    /**
     * Constructs the view/controller and initializes UI components,
     * including the board panel, controls, and event listeners.
     *
     * @param model the MancalaModel driving the game logic; cannot be null.
     */
    public MancalaViewController(MancalaModel model) {
        this.model = model;
        setLayout(new BorderLayout());

        // Delegate drawing of the board to the BoardStyle.
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

        storeALabel = createLabel();
        storeALabel.setText("<html>M<br>A<br>N<br>C<br>A<br>L<br>A<br><br>A</html>");

        storeBLabel = createLabel();
        storeBLabel.setText("<html>M<br>A<br>N<br>C<br>A<br>L<br>A<br><br>B</html>");

        
        boardWithLabels = new JPanel(new BorderLayout());
        boardWithLabels.add(boardPanel, BorderLayout.CENTER);
        boardWithLabels.add(storeALabel,BorderLayout.EAST);
        boardWithLabels.add(storeBLabel, BorderLayout.WEST);

        JPanel controlBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));

        turnLabel = new JLabel("Player A's Turn");
        turnLabel.setFont(TEXT_FONT);

        undoButton = new JButton("Undo");
        undoButton.setPreferredSize(BUTTON_SIZE);
        undoButton.addActionListener(e -> model.undo());

        confirmButton = new JButton("Confirm Move");
        confirmButton.setPreferredSize(BUTTON_SIZE);
        confirmButton.setEnabled(false);
        confirmButton.addActionListener(e -> model.confirmMove());

        controlBar.add(turnLabel);
        controlBar.add(undoButton);
        controlBar.add(confirmButton);
        
        add(boardWithLabels, BorderLayout.CENTER);
        add(controlBar, BorderLayout.SOUTH);
    }

    /**
     * Sets the board style strategy used to paint the board
     * Updates the label panel colors to match with the style colors.
     *
     * @param style the BoardStyle implementation to use; cannot be null
     */
    public void setStyle(BoardStyle style){
        this.style = style;
        
        Color boardColor = style.getBoardColor();
        boardWithLabels.setBackground(boardColor);
        storeALabel.setForeground(style.getBoardContrastColor());
        storeBLabel.setForeground(style.getBoardContrastColor());

        repaint();
    }

    /**
     * {@inheritDoc}
     * Updates UI components to reflect the current game state,
     * including the turn label, control buttons, and board display.
     * Displays a dialog if the game has ended.
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

    private JLabel createLabel() {
        JLabel label = new JLabel();
        label.setOpaque(false);
        label.setFont(TEXT_FONT);
        label.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return label;
    }
}
