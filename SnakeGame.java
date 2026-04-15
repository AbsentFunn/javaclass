import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;

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

class GamePanel extends JPanel implements ActionListener {
    private final int GRID_SIZE = 20;
    private final int CELL_SIZE = 30;
    private final int BOARD_SIZE = GRID_SIZE * CELL_SIZE;

    private LinkedList<Point> snake;
    private Point food;
    
    private int direction = KeyEvent.VK_RIGHT;
    private int nextDirection = KeyEvent.VK_RIGHT;
    
    private boolean gameOver = false;
    private int score = 0;
    private Timer timer;

    public GamePanel() {
        setPreferredSize(new Dimension(BOARD_SIZE, BOARD_SIZE));
        setBackground(new Color(30, 30, 30));
        setFocusable(true);
        setupInput();
        initGame();
    }

    private void initGame() {
        snake = new LinkedList<>();
        snake.add(new Point(5, 10));
        snake.add(new Point(4, 10));
        snake.add(new Point(3, 10));
        
        direction = KeyEvent.VK_RIGHT;
        nextDirection = KeyEvent.VK_RIGHT;
        score = 0;
        gameOver = false;
        
        spawnFood();
        
        if (timer != null) timer.stop();
        timer = new Timer(120, this);
        timer.start();
        repaint();
    }

    private void setupInput() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (gameOver && key == KeyEvent.VK_SPACE) {
                    initGame();
                    return;
                }
                
                if (key == KeyEvent.VK_LEFT && direction != KeyEvent.VK_RIGHT) {
                    nextDirection = key;
                } else if (key == KeyEvent.VK_RIGHT && direction != KeyEvent.VK_LEFT) {
                    nextDirection = key;
                } else if (key == KeyEvent.VK_UP && direction != KeyEvent.VK_DOWN) {
                    nextDirection = key;
                } else if (key == KeyEvent.VK_DOWN && direction != KeyEvent.VK_UP) {
                    nextDirection = key;
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            move();
        }
        repaint();
    }

    private void move() {
        direction = nextDirection;
        Point head = snake.getFirst();
        Point newHead = new Point(head);

        switch (direction) {
            case KeyEvent.VK_LEFT:  newHead.x--; break;
            case KeyEvent.VK_RIGHT: newHead.x++; break;
            case KeyEvent.VK_UP:    newHead.y--; break;
            case KeyEvent.VK_DOWN:  newHead.y++; break;
        }

        newHead.x = (newHead.x + GRID_SIZE) % GRID_SIZE;
        newHead.y = (newHead.y + GRID_SIZE) % GRID_SIZE;

        boolean ateFood = newHead.equals(food);
        
        if (!ateFood) {
            snake.removeLast();
        }

        if (snake.contains(newHead)) {
            gameOver = true;
            snake.addFirst(newHead);
            return;
        }

        snake.addFirst(newHead);

        if (ateFood) {
            score += 10;
            spawnFood();
        }
    }

    private void spawnFood() {
        int x, y;
        boolean onSnake;
        do {
            x = (int) (Math.random() * GRID_SIZE);
            y = (int) (Math.random() * GRID_SIZE);
            Point p = new Point(x, y);
            onSnake = snake.contains(p);
        } while (onSnake);
        food = new Point(x, y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw Food
        g2.setColor(new Color(255, 80, 80));
        int padding = 4;
        g2.fillOval(food.x * CELL_SIZE + padding, food.y * CELL_SIZE + padding, 
                    CELL_SIZE - padding * 2, CELL_SIZE - padding * 2);

        // Draw Snake
        for (int i = 0; i < snake.size(); i++) {
            Point p = snake.get(i);
            
            if (i == 0) {
                g2.setColor(new Color(100, 255, 100)); // Head
            } else {
                g2.setColor(new Color(80, 200, 80)); // Body
            }
            
            int segPadding = 1;
            g2.fillRoundRect(p.x * CELL_SIZE + segPadding, p.y * CELL_SIZE + segPadding, 
                             CELL_SIZE - segPadding * 2, CELL_SIZE - segPadding * 2, 
                             12, 12);
        }

        // Draw Score
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 18));
        g2.drawString("Score: " + score, 15, 30);

        if (gameOver) {
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