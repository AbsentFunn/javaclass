// GameController.java
// This class handles user input, the game loop, and connects the model and view.
import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameController {
    private final GameModel model;
    private final GameView view;
    private Timer timer;
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    public GameController(GameModel model, GameView view) {
        this.model = model;
        this.view = view;
        setupControls();
        setupGameLoop();
    }

    private void setupControls() {
        view.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_LEFT) leftPressed = true;
                if (key == KeyEvent.VK_RIGHT) rightPressed = true;
                if (key == KeyEvent.VK_SPACE) {
                    if (model.isGameOver()) {
                        // Optional: Restart logic could go here
                    } else {
                        model.firePlayerBullet();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_LEFT) leftPressed = false;
                if (key == KeyEvent.VK_RIGHT) rightPressed = false;
            }
        });
    }

    private void setupGameLoop() {
        // approx 60 FPS (16ms delay)
        timer = new Timer(16, e -> {
            if (!model.isGameOver()) {
                if (leftPressed) model.movePlayerLeft();
                if (rightPressed) model.movePlayerRight();
                
                model.update();
                view.repaint();
            } else {
                timer.stop();
                view.repaint(); // Final render for game over screen
            }
        });
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Space Invaders");
            GameModel model = new GameModel();
            GameView view = new GameView(model);
            
            new GameController(model, view);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(view);
            frame.pack(); // Use preferred size from GameView
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
            view.requestFocusInWindow(); // Ensure view gets keyboard focus
        });
    }
}
