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
    private int mirrorTicks = 0;
    private int freezeTicks = 0;
    private boolean magnetActive = false;

    private int direction = KeyEvent.VK_RIGHT;
    private int nextDirection = KeyEvent.VK_RIGHT;

    private boolean gameOver = false;
    private int score = 0;
    private Timer timer;
    private int baseDelay = 120;
    private int currentDelay = 120;
    // Remove old powerup timers

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
        mirrorTicks = 0;
        freezeTicks = 0;
        magnetActive = false;
        currentDelay = baseDelay;
        powerups.clear();

        spawnFood();
        spawnPowerups();

        if (timer != null) timer.stop();
        timer = new Timer(currentDelay, this);
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
        int moveDir = direction;
        if (mirrorTicks > 0) {
            // Reverse controls
            if (moveDir == KeyEvent.VK_LEFT) moveDir = KeyEvent.VK_RIGHT;
            else if (moveDir == KeyEvent.VK_RIGHT) moveDir = KeyEvent.VK_LEFT;
            else if (moveDir == KeyEvent.VK_UP) moveDir = KeyEvent.VK_DOWN;
            else if (moveDir == KeyEvent.VK_DOWN) moveDir = KeyEvent.VK_UP;
        }
        direction = nextDirection;
        Point head = snake.getFirst();
        Point newHead = new Point(head);

        switch (moveDir) {
            case KeyEvent.VK_LEFT:  newHead.x--; break;
            case KeyEvent.VK_RIGHT: newHead.x++; break;
            case KeyEvent.VK_UP:    newHead.y--; break;
            case KeyEvent.VK_DOWN:  newHead.y++; break;
        }

        newHead.x = (newHead.x + GRID_SIZE) % GRID_SIZE;
        newHead.y = (newHead.y + GRID_SIZE) % GRID_SIZE;

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
        if (selfHit && ghostTicks == 0) {
            gameOver = true;
            snake.addFirst(newHead);
            return;
        }

        snake.addFirst(newHead);

        if (ateFood) {
            score += 10;
            if (magnetActive) magnetActive = false;
            spawnFood();
            spawnPowerups();
        }

        if (hitPowerup != null) {
            applyPowerup(hitPowerup);
            powerups.remove(hitPowerup);
        }

        // Powerup timers
        if (ghostTicks > 0) ghostTicks--;
        if (mirrorTicks > 0) mirrorTicks--;
        if (freezeTicks > 0) freezeTicks--;
    }

    private void spawnFood() {
        if (magnetActive) {
            // Move fruit to snake head
            Point head = snake.getFirst();
            food = new Point(head.x, head.y);
            return;
        }
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
        if (freezeTicks > 0) return; // Freeze disables new powerups
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
            case GHOST:
                ghostTicks = 40;
                break;
            case PORTAL:
                // Teleport head to random empty cell
                java.util.Random rand = new java.util.Random();
                Point newPos;
                do {
                    int x = rand.nextInt(GRID_SIZE);
                    int y = rand.nextInt(GRID_SIZE);
                    newPos = new Point(x, y);
                } while (snake.contains(newPos) || newPos.equals(food));
                snake.addFirst(newPos);
                snake.removeLast();
                break;
            case MAGNET:
                magnetActive = true;
                break;
            case MIRROR:
                mirrorTicks = 40;
                break;
            case FREEZE:
                freezeTicks = 40;
                break;
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
                case GHOST:
                    g2.setColor(new Color(180, 255, 255)); // Cyan
                    break;
                case PORTAL:
                    g2.setColor(new Color(255, 120, 255)); // Magenta
                    break;
                case MAGNET:
                    g2.setColor(new Color(255, 200, 80)); // Yellow
                    break;
                case MIRROR:
                    g2.setColor(new Color(255, 120, 80)); // Orange
                    break;
                case FREEZE:
                    g2.setColor(new Color(120, 120, 255)); // Blue
                    break;
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
        int y = 55;
        if (ghostTicks > 0) {
            g2.setColor(new Color(180, 255, 255));
            g2.drawString("Ghost!", 15, y); y += 25;
        }
        if (magnetActive) {
            g2.setColor(new Color(255, 200, 80));
            g2.drawString("Magnet!", 15, y); y += 25;
        }
        if (mirrorTicks > 0) {
            g2.setColor(new Color(255, 120, 80));
            g2.drawString("Mirror Controls!", 15, y); y += 25;
        }
        if (freezeTicks > 0) {
            g2.setColor(new Color(120, 120, 255));
            g2.drawString("Freeze!", 15, y); y += 25;
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
        GHOST, PORTAL, MAGNET, MIRROR, FREEZE;
        public String shortName() {
            switch (this) {
                case GHOST: return "GHO";
                case PORTAL: return "POR";
                case MAGNET: return "MAG";
                case MIRROR: return "MIR";
                case FREEZE: return "FRZ";
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

    // (removed duplicate PowerupType and Powerup classes)
}