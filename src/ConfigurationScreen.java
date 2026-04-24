/**
 * StyleSelectionScreen.java
 * 
 * Context program for strategy pattern; assign the chosen BoardStyle to the game view.
 * 
 * @author Hannah Roddy
 * @author Johnny Tsai
 * @author Nishan Bhattarai
 */

import java.awt.*;
import javax.swing.*;

/**
 * Provides UI for players to select the style of the Mancala board (e.g., wood, metal).
 */
public class ConfigurationScreen extends JFrame {
    private static final Dimension BUTTON_SIZE = new Dimension(150, 50);

    /**
     * Set up one button per available style.
     * Clicking a button sets the style strategy of view and launches the game.
     * MancalaTest will launch this screen at the start of the application.
     */
    public ConfigurationScreen(
        MancalaViewController viewAndController, 
        MancalaModel model, 
        JFrame gameFrame, 
        int maxPitStart, 
        int pitsPerSide
    ) {
        setLayout(new BorderLayout());

        JLabel promptLabel = new JLabel("Choose a Board Style", SwingConstants.CENTER);
        promptLabel.setFont(new Font("Arial", Font.BOLD, 25));
        promptLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        BoardStyle woodStyle = new WoodStyle(maxPitStart, pitsPerSide);
        JButton woodButton = createActiveStyleButton("Wood Style", woodStyle, viewAndController, model, gameFrame, maxPitStart);

        BoardStyle metalStyle = new MetalStyle(maxPitStart, pitsPerSide);
        JButton metalButton = createActiveStyleButton("Metal Style", metalStyle, viewAndController, model, gameFrame, maxPitStart);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        buttonPanel.add(woodButton);
        buttonPanel.add(metalButton);

        add(promptLabel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);  
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private JButton createActiveStyleButton(
        String styleName,
        BoardStyle style,
        MancalaViewController viewAndController,
        MancalaModel model,
        JFrame gameFrame,
        int maxPitStart
    ) {
        JButton button = new JButton(styleName);
        button.setPreferredSize(BUTTON_SIZE);

        button.addActionListener(e -> {
            viewAndController.setStyle(style);
            dispose();

            int stonesPerPit = promptStoneCount(maxPitStart);
            if (stonesPerPit < 0) return;

            model.setUpBoard(stonesPerPit);
            gameFrame.setVisible(true);
        });

        return button;
    }

    /**
     * Prompts players to select a starting stone count of 3 to .
     * Called once after the game frame becomes visible and a style has been selected.
     * Initializes the board via the model using the chosen count.
     *
     * @precondition setStyle() has been called and the game frame is visible.
     * @postcondition the model's board is initialized with the chosen number of stones per pit. Defaults to 3 if the dialog is closed without a selection.
     */
    public int promptStoneCount(int maxPitStart) {
        String[] options = new String[maxPitStart - 2];
        for(int i = 3; i <= maxPitStart; i++) {
            options[i - 3] = String.valueOf(i);
        }

        int choice = JOptionPane.showOptionDialog(
            this,
            "How many stones per pit?",
            null,
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );

        return Integer.parseInt(options[choice]);
    }
}
