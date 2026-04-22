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
public class StyleSelectionScreen extends JFrame {
    
    /**
     * Set up one button per available style.
     * Clicking a button sets the style strategy of view and launches the game.
     * MancalaTest will launch this screen at the start of the application.
     */
    public StyleSelectionScreen(MancalaViewController viewAndController, JFrame gameFrame, int maxPitStart, int pitsPerSide) {
        setTitle("Mancala - Select Board Style");
        setLayout(new BorderLayout());

        JLabel promptLabel = new JLabel("Choose a Board Style", SwingConstants.CENTER);
        promptLabel.setFont(new Font("Arial", Font.BOLD, 24));
        promptLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(promptLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));

        JButton woodButton = new JButton("Wood Style");
        woodButton.setPreferredSize(new Dimension(150, 50));
        woodButton.addActionListener(
            e -> {
                viewAndController.setStyle(new WoodStyle(maxPitStart, pitsPerSide));
                gameFrame.setVisible(true);
                viewAndController.promptStoneCount();
                dispose();
            }
        );
        buttonPanel.add(woodButton);

        JButton metalButton = new JButton("Metal Style");
        metalButton.setPreferredSize(new Dimension(150, 50));
        metalButton.addActionListener(
            e -> {
                viewAndController.setStyle(new MetalStyle(maxPitStart, pitsPerSide));
                gameFrame.setVisible(true);
                viewAndController.promptStoneCount();
                dispose();
            }
        );
        buttonPanel.add(metalButton);
        
        add(buttonPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);  
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
