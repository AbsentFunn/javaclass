// GameModel.java
// This class will manage the game state, logic, and data for Space Invaders.
// It will not import or use any Swing classes.
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameModel {
    // Board Constants
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    // Player Constants
    public static final int PLAYER_WIDTH = 40;
    public static final int PLAYER_HEIGHT = 20;
    public static final int PLAYER_Y = HEIGHT - 50;
    private static final int PLAYER_SPEED = 7;
    private static final int BULLET_SPEED = 10;

    // Alien Constants
    public static final int ALIEN_ROWS = 5;
    public static final int ALIEN_COLS = 11;
    public static final int ALIEN_WIDTH = 30;
    public static final int ALIEN_HEIGHT = 20;
    public static final int ALIEN_SPACING_X = 15;
    public static final int ALIEN_SPACING_Y = 15;
    private static final double ALIEN_INITIAL_SPEED_X = 2.0;
    private static final int ALIEN_DROP_Y = 20;

    // Player State
    private int playerX;
    private int lives;
    private int score;
    private boolean gameOver;

    // Player Bullet State (one at a time)
    private int playerBulletX;
    private int playerBulletY;
    private boolean playerBulletActive;

    // Alien State
    private boolean[][] aliensAlive;
    private double alienX; // Use double for smoother formation movement logic
    private double alienY;
    private int alienDirection = 1; // 1 for right, -1 for left
    private double currentAlienSpeedX = ALIEN_INITIAL_SPEED_X;

    // Alien Projectiles
    private final List<Point> alienBullets;
    private final Random random = new Random();

    public GameModel() {
        initGame();
        alienBullets = new ArrayList<>();
    }

    private void initGame() {
        playerX = WIDTH / 2 - PLAYER_WIDTH / 2;
        lives = 3;
        score = 0;
        gameOver = false;
        playerBulletActive = false;

        aliensAlive = new boolean[ALIEN_ROWS][ALIEN_COLS];
        for (int r = 0; r < ALIEN_ROWS; r++) {
            for (int c = 0; c < ALIEN_COLS; c++) {
                aliensAlive[r][c] = true;
            }
        }

        alienX = 50;
        alienY = 50;
    }

    public void movePlayerLeft() {
        if (gameOver) return;
        playerX -= PLAYER_SPEED;
        if (playerX < 0) playerX = 0;
    }

    public void movePlayerRight() {
        if (gameOver) return;
        playerX += PLAYER_SPEED;
        if (playerX > WIDTH - PLAYER_WIDTH) playerX = WIDTH - PLAYER_WIDTH;
    }

    public void firePlayerBullet() {
        if (gameOver) return;
        if (!playerBulletActive) {
            playerBulletX = playerX + PLAYER_WIDTH / 2 - 2;
            playerBulletY = PLAYER_Y;
            playerBulletActive = true;
        }
    }

    /**
     * Advances the game state by one tick.
     */
    public void update() {
        if (gameOver) return;

        // 1. Advance Player Bullet
        if (playerBulletActive) {
            playerBulletY -= BULLET_SPEED;
            if (playerBulletY < 0) {
                playerBulletActive = false;
            } else {
                checkPlayerBulletCollisions();
            }
        }

        // 2. Move Alien Formation
        updateAlienMovement();

        // 3. Fire Alien Bullets
        if (random.nextDouble() < 0.02) {
            fireAlienBullet();
        }

        // 4. Update Alien Bullets and Detect Player Collisions
        updateAlienBullets();
    }

    private void updateAlienMovement() {
        double moveStep = currentAlienSpeedX * alienDirection;
        boolean hitEdge = false;

        // Determine boundaries based on remaining aliens
        double minX = WIDTH, maxX = 0;
        boolean anyAliens = false;
        for (int r = 0; r < ALIEN_ROWS; r++) {
            for (int c = 0; c < ALIEN_COLS; c++) {
                if (aliensAlive[r][c]) {
                    anyAliens = true;
                    double ax = alienX + c * (ALIEN_WIDTH + ALIEN_SPACING_X);
                    if (ax < minX) minX = ax;
                    if (ax + ALIEN_WIDTH > maxX) maxX = ax;
                }
            }
        }

        if (!anyAliens) return; // Level cleared

        // Check if movement hits edge
        if (alienDirection == 1 && maxX + moveStep > WIDTH - 10) {
            hitEdge = true;
        } else if (alienDirection == -1 && minX + moveStep < 10) {
            hitEdge = true;
        }

        if (hitEdge) {
            alienDirection *= -1;
            alienY += ALIEN_DROP_Y;
            // Check if aliens reach the player
            if (alienY + ALIEN_ROWS * (ALIEN_HEIGHT + ALIEN_SPACING_Y) > PLAYER_Y) {
                gameOver = true;
            }
        } else {
            alienX += moveStep;
        }
    }

    private void fireAlienBullet() {
        // Pick a random column that has at least one alien
        List<Integer> activeCols = new ArrayList<>();
        for (int c = 0; c < ALIEN_COLS; c++) {
            for (int r = 0; r < ALIEN_ROWS; r++) {
                if (aliensAlive[r][c]) {
                    activeCols.add(c);
                    break;
                }
            }
        }
        if (activeCols.isEmpty()) return;

        int col = activeCols.get(random.nextInt(activeCols.size()));
        // Find the lowest alien in that column to fire from
        for (int r = ALIEN_ROWS - 1; r >= 0; r--) {
            if (aliensAlive[r][c]) {
                int bx = (int) (alienX + col * (ALIEN_WIDTH + ALIEN_SPACING_X) + ALIEN_WIDTH / 2);
                int by = (int) (alienY + r * (ALIEN_HEIGHT + ALIEN_SPACING_Y) + ALIEN_HEIGHT);
                alienBullets.add(new Point(bx, by));
                break;
            }
        }
    }

    private void updateAlienBullets() {
        for (int i = 0; i < alienBullets.size(); i++) {
            Point b = alienBullets.get(i);
            b.y += 5;

            if (b.y > HEIGHT) {
                alienBullets.remove(i--);
            } else if (intersects(b.x, b.y, 4, 10, playerX, PLAYER_Y, PLAYER_WIDTH, PLAYER_HEIGHT)) {
                lives--;
                alienBullets.remove(i--);
                if (lives <= 0) {
                    gameOver = true;
                }
            }
        }
    }

    private void checkPlayerBulletCollisions() {
        for (int r = 0; r < ALIEN_ROWS; r++) {
            for (int c = 0; c < ALIEN_COLS; c++) {
                if (aliensAlive[r][c]) {
                    int ax = (int) (alienX + c * (ALIEN_WIDTH + ALIEN_SPACING_X));
                    int ay = (int) (alienY + r * (ALIEN_HEIGHT + ALIEN_SPACING_Y));
                    if (intersects(playerBulletX, playerBulletY, 4, 10, ax, ay, ALIEN_WIDTH, ALIEN_HEIGHT)) {
                        aliensAlive[r][c] = false;
                        playerBulletActive = false;
                        score += 10;
                        return; // One hit per bullet
                    }
                }
            }
        }
    }

    private boolean intersects(int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2) {
        return x1 < x2 + w2 && x1 + w1 > x2 && y1 < y2 + h2 && y1 + h1 > y2;
    }

    // --- Getters for View Rendering ---
    public int getPlayerX() { return playerX; }
    public int getLives() { return lives; }
    public int getScore() { return score; }
    public boolean isGameOver() { return gameOver; }
    public boolean[][] getAliensAlive() { return aliensAlive; }
    public int getAlienX() { return (int) alienX; }
    public int getAlienY() { return (int) alienY; }
    public int getPlayerBulletX() { return playerBulletX; }
    public int getPlayerBulletY() { return playerBulletY; }
    public boolean isPlayerBulletActive() { return playerBulletActive; }
    public List<Point> getAlienBullets() { return alienBullets; }

    // Simple Point helper class
    public static class Point {
        public int x, y;
        public Point(int x, int y) { this.x = x; this.y = y; }
    }
}
