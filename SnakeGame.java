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
    private java.util.List<Powerup> powerups = new java.util.ArrayList<>();

    // Unique powerup state
    private int ghostTicks = 0;
    private int magnetTicks = 0;
    private int slowTicks = 0;
    private boolean shieldActive = false;

    private int direction = KeyEvent.VK_RIGHT;
    private int nextDirection = KeyEvent.VK_RIGHT;

    private boolean gameOver = false;
    private int score = 0;
    private Timer timer;
    private final int baseDelay = 120;

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
        ghostTicks = 0;
        magnetTicks = 0;
        slowTicks = 0;
        shieldActive = false;
        
        powerups.clear();
        spawnFood();
        spawnPowerups();

        if (timer != null) timer.stop();
        timer = new Timer(baseDelay, this);
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

        // Magnet logic: move food towards head
        if (magnetTicks > 0) {
            if (food.x < newHead.x) food.x++;
            else if (food.x > newHead.x) food.x--;
            if (food.y < newHead.y) food.y++;
            else if (food.y > newHead.y) food.y--;
        }

        boolean ateFood = newHead.equals(food);
        Powerup hitPowerup = null;
        for (Powerup p : powerups) {
            if (p.pos.equals(newHead)) {
                hitPowerup = p;
                break;
            }
        }

        if (!ateFood) {
            snake.removeLast();
        }

        boolean selfHit = snake.contains(newHead);
        if (selfHit) {
            if (ghostTicks > 0) {
                // Safe in ghost mode
            } else if (shieldActive) {
                shieldActive = false;
                ghostTicks = 20; // Brief invincibility after shield break
            } else {
                gameOver = true;
                snake.addFirst(newHead);
                return;
            }
        }

        snake.addFirst(newHead);

        if (ateFood) {
            score += 10;
            spawnFood();
            spawnPowerups();
        }

        if (hitPowerup != null) {
            applyPowerup(hitPowerup);
            powerups.remove(hitPowerup);
        }

        // Powerup timers
        if (ghostTicks > 0) ghostTicks--;
        if (magnetTicks > 0) magnetTicks--;
        if (slowTicks > 0) {
            slowTicks--;
            if (slowTicks == 0) updateTimer();
        }
    }

    private void spawnFood() {
        int x, y;
        boolean onSnakeOrPowerup;
        do {
            x = (int) (Math.random() * GRID_SIZE);
            y = (int) (Math.random() * GRID_SIZE);
            Point p = new Point(x, y);
            onSnakeOrPowerup = snake.contains(p);
            for (Powerup pow : powerups) {
                if (pow.pos.equals(p)) onSnakeOrPowerup = true;
            }
        } while (onSnakeOrPowerup);
        food = new Point(x, y);
    }

    private void spawnPowerups() {
        powerups.clear();
        java.util.Random rand = new java.util.Random();
        int numPowerups = 2 + rand.nextInt(2); // 2 or 3 powerups
        java.util.List<PowerupType> types = new java.util.ArrayList<>();
        for (PowerupType t : PowerupType.values()) types.add(t);
        java.util.Collections.shuffle(types);
        for (int i = 0; i < numPowerups && i < types.size(); i++) {
            Point p;
            boolean valid;
            do {
                int x = rand.nextInt(GRID_SIZE);
                int y = rand.nextInt(GRID_SIZE);
                p = new Point(x, y);
                valid = !snake.contains(p) && !p.equals(food);
                for (Powerup pow : powerups) if (pow.pos.equals(p)) valid = false;
            } while (!valid);
            powerups.add(new Powerup(types.get(i), p));
        }
    }

    private void applyPowerup(Powerup p) {
        switch (p.type) {
            case GHOST:  ghostTicks = 60; break;
            case MAGNET: magnetTicks = 60; break;
            case SCORE:  score += 50; break;
            case SLOW:   slowTicks = 60; updateTimer(); break;
            case SHIELD: shieldActive = true; break;
        }
    }

    private void updateTimer() {
        if (timer != null) {
            timer.setDelay(slowTicks > 0 ? (int)(baseDelay * 1.5) : baseDelay);
        }
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

        // Draw Powerups
        for (Powerup p : powerups) {
            switch (p.type) {
                case GHOST:  g2.setColor(new Color(180, 255, 255)); break; // Cyan
                case MAGNET: g2.setColor(new Color(255, 255, 100)); break; // Yellow
                case SCORE:  g2.setColor(new Color(100, 255, 100)); break; // Green
                case SLOW:   g2.setColor(new Color(120, 120, 255)); break; // Blue
                case SHIELD: g2.setColor(new Color(255, 100, 255)); break; // Magenta
            }
            int px = p.pos.x * CELL_SIZE + padding;
            int py = p.pos.y * CELL_SIZE + padding;
            g2.fillRect(px, py, CELL_SIZE - padding * 2, CELL_SIZE - padding * 2);
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            String label = p.type.shortName();
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(label, px + (CELL_SIZE - padding * 2 - fm.stringWidth(label)) / 2, py + (CELL_SIZE - padding * 2) / 2 + fm.getAscent() / 2);
        }

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

        // Draw Score and Powerup Status
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 18));
        g2.drawString("Score: " + score, 15, 30);
        int yStatus = 55;
        if (ghostTicks > 0) {
            g2.setColor(new Color(180, 255, 255));
            g2.drawString("Ghost!", 15, yStatus); yStatus += 25;
        }
        if (magnetTicks > 0) {
            g2.setColor(new Color(255, 255, 100));
            g2.drawString("Magnet!", 15, yStatus); yStatus += 25;
        }
        if (slowTicks > 0) {
            g2.setColor(new Color(120, 120, 255));
            g2.drawString("Slow Motion!", 15, yStatus); yStatus += 25;
        }
        if (shieldActive) {
            g2.setColor(new Color(255, 100, 255));
            g2.drawString("Shield Active!", 15, yStatus); yStatus += 25;
        }

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

    // Powerup types and class
    enum PowerupType {
        GHOST, MAGNET, SCORE, SLOW, SHIELD;
        public String shortName() {
            switch (this) {
                case GHOST:  return "GHO";
                case MAGNET: return "MAG";
                case SCORE:  return "SCR";
                case SLOW:   return "SLO";
                case SHIELD: return "SHD";
            }
            return "?";
        }
    }
    static class Powerup {
        PowerupType type;
        Point pos;
        Powerup(PowerupType type, Point pos) {
            this.type = type;
            this.pos = pos;
        }
    }
}