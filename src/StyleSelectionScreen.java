/**
 * StyleSelectionScreen.java
 * 
 * Represent the screen where players can select the style of the Mancala board (e.g., wood, metal).
 * Implements the Strategy pattern by passing the chosen BoardStyle to the game view.
 * 
 * @author Hannah Roddy
 * @author Johnny Tsai
 * @author Nishan Bhattarai
 */

import java.awt.*;
import javax.swing.*;

public class StyleSelectionScreen extends JFrame{
    private BoardStyle selectedStyle;

    /**
     * Constructor for the style selection screen with a title label and one button per available style.
     * Clicking a button sets the style strategy and launches the game.
     * MancalaTest will launch this screen atthe start of the application.
     */
    public StyleSelectionScreen() {
        setTitle("Mancala - Select Board Style");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Label
        JLabel label = new JLabel("Choose a Board Style", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        label.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(label, BorderLayout.NORTH);

        // Style buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));

        // Wood
        JButton woodButton = new JButton("Wood Style");
        woodButton.setPreferredSize(new Dimension(150, 50));
        woodButton.addActionListener(e -> launchGame(new WoodStyle()));
        buttonPanel.add(woodButton);

        // Metal
        JButton metalButton = new JButton("Metal Style");
        metalButton.setPreferredSize(new Dimension(150, 50));
        metalButton.addActionListener(e -> launchGame(new MetalStyle()));
        buttonPanel.add(metalButton);
        
        add(buttonPanel, BorderLayout.CENTER);
    }

    /**
     * Closes this screen and launches the main game window with the chosen style strategy.
     * 
     * @param style The BoardStyle implementation selected by the player.
     */
    private void launchGame(BoardStyle style) {
        this.selectedStyle = style;
        dispose();
        // TO DO: MancalaView
        // new MancalaView(selectedStyle);
    }
}
