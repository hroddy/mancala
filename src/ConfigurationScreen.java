/**
 * ConfigurationScreen.java
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
     * Sets up one button for each available style.
     * Clicking a button sets the style strategy of the view and launches the game setup.
     * MancalaTest launches this screen at the start of the application.
     * 
     * Precondition: maxPitStart >= 3, pitsPerSide > 0
     * Postcondition: A configuration screen is built with a prompt label and buttons for each available board style.
     *                Each button is active and updates the view/controller with its associated style when clicked.
     *                The frame is packed, centered on the screen, non-resizable, and configured to exit on close.
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

    /**
     * Prompts players to select a starting stone count from 3 to maxPitStart.
     *
     * Precondition: maxPitStart >= 3.
     * Postcondition: The selected number of starting stones per pit is returned, 
     *                or -1 if the user exits the dialog.
     *  
     * @param maxPitStart maximum allowed starting stones per pit.
     * @return number of stones players choose to start with in each pit.
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

        if (choice < 0){
            return choice;
        }

        return Integer.parseInt(options[choice]);
    }

    /**
     * Creates a button for the specified board style.
     * Attaches a listener to the button so that when clicked,
     * the game launches with the specified style and number of stones in each pit.
     * 
     * Precondition:  styleName is non-empty and maxPitStart >= 3.
     * Postcondition: A JButton with the specified styleName is created and returned.
     *                When clicked, it assigns the corresponding style to the view/controller,
     *                closes the style selection screen, and prompts the user to choose a
     *                starting stone count from 3 to maxPitStart. If the user selects a valid
     *                count, the model is initialized with that count and the game frame is made visible.
     *                
     * @param styleName text displayed on the button.
     * @param style board style associated with the button.
     * @param viewAndController view/controller whose style is updated.
     * @param model game model to initialize starting stones per pit for.
     * @param gameFrame frame that displays the game.
     * @param maxPitStart maximum allowed starting stones per pit.
     * @return a configured JButton for selecting the given board style.
     */
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
}
