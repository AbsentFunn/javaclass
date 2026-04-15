import javax.swing.*;
import java.awt.*;

/**
 * Main application window for the Snake game.
 * Creates the {@link GamePanel} and sets up the JFrame.
 */
public class SnakeGame extends JFrame {

    public SnakeGame() {
        add(new GamePanel());
        setResizable(false);
        pack();
        setTitle("Simple Snake");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new SnakeGame().setVisible(true));
    }
}
