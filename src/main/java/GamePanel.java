import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;

/**
 * The Swing panel that renders the snake game and handles keyboard input.
 * All game logic is delegated to {@link GameState}.
 */
public class GamePanel extends JPanel implements ActionListener {

    private static final int GRID_SIZE  = 20;
    private static final int CELL_SIZE  = 30;
    private static final int BOARD_SIZE = GRID_SIZE * CELL_SIZE;

    private GameState gameState;
    private Timer timer;

    public GamePanel() {
        gameState = new GameState(GRID_SIZE);
        setPreferredSize(new Dimension(BOARD_SIZE, BOARD_SIZE));
        setBackground(new Color(30, 30, 30));
        setFocusable(true);
        setupInput();
        startTimer();
    }

    private void startTimer() {
        if (timer != null) timer.stop();
        timer = new Timer(120, this);
        timer.start();
    }

    private void setupInput() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();

                if (gameState.isGameOver() && key == KeyEvent.VK_SPACE) {
                    gameState.init();
                    startTimer();
                    repaint();
                    return;
                }

                switch (key) {
                    case KeyEvent.VK_LEFT:  gameState.setNextDirection(GameState.DIR_LEFT);  break;
                    case KeyEvent.VK_RIGHT: gameState.setNextDirection(GameState.DIR_RIGHT); break;
                    case KeyEvent.VK_UP:    gameState.setNextDirection(GameState.DIR_UP);    break;
                    case KeyEvent.VK_DOWN:  gameState.setNextDirection(GameState.DIR_DOWN);  break;
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        gameState.tick();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw food
        Point food = gameState.getFood();
        g2.setColor(new Color(255, 80, 80));
        int padding = 4;
        g2.fillOval(food.x * CELL_SIZE + padding, food.y * CELL_SIZE + padding,
                    CELL_SIZE - padding * 2, CELL_SIZE - padding * 2);

        // Draw snake
        LinkedList<Point> snake = gameState.getSnake();
        for (int i = 0; i < snake.size(); i++) {
            Point p = snake.get(i);
            g2.setColor(i == 0 ? new Color(100, 255, 100) : new Color(80, 200, 80));
            int segPadding = 1;
            g2.fillRoundRect(p.x * CELL_SIZE + segPadding, p.y * CELL_SIZE + segPadding,
                             CELL_SIZE - segPadding * 2, CELL_SIZE - segPadding * 2, 12, 12);
        }

        // Draw score
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 18));
        g2.drawString("Score: " + gameState.getScore(), 15, 30);

        // Draw game-over overlay
        if (gameState.isGameOver()) {
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(0, 0, BOARD_SIZE, BOARD_SIZE);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 36));
            FontMetrics metrics = g2.getFontMetrics();
            String msg = "GAME OVER";
            g2.drawString(msg, (BOARD_SIZE - metrics.stringWidth(msg)) / 2, BOARD_SIZE / 2 - 20);

            g2.setFont(new Font("SansSerif", Font.PLAIN, 18));
            metrics = g2.getFontMetrics();
            String subMsg = "Press SPACE to Restart";
            g2.drawString(subMsg, (BOARD_SIZE - metrics.stringWidth(subMsg)) / 2, BOARD_SIZE / 2 + 20);
        }
    }
}
