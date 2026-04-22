package Space;

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
    private int level;
    private boolean gameOver;

    // Player Bullet State (one at a time)
    private int playerBulletX;
    private int playerBulletY;
    private boolean playerBulletActive;

    // Alien State
    private boolean[][] aliensAlive;
    private double alienX;
    private double alienY;
    private int alienDirection = 1;
    private double currentAlienSpeedX = ALIEN_INITIAL_SPEED_X;

    // Effects State
    private int muzzleTicks = 0;
    private int shakeTicks = 0;
    private final List<Explosion> explosions = new ArrayList<>();

    // Alien Projectiles
    private final List<Point> alienBullets;
    private final Random random = new Random();

    // Powerup State
    public static class Powerup {
        public int x, y, type, ticks;
        public Powerup(int x, int y, int type) {
            this.x = x;
            this.y = y;
            this.type = type;
            this.ticks = 0;
        }
    }
    private final List<Powerup> powerups = new ArrayList<>();
    private Powerup lastCollectedPowerup = null;
    private boolean powerupActive = false;
    private int powerupPauseTicks = 0;
    private String powerupName = "";
    private int powerupEffectTicks = 0; // 15 seconds at 60 FPS = 900
    private long lastPowerupSpawnTime = System.currentTimeMillis();
    private boolean firstPowerupSpawned = false;

    public GameModel() {
        alienBullets = new ArrayList<>();
        level = 1;
        initGame();
    }

    public void initGame() {
        playerX = WIDTH / 2 - PLAYER_WIDTH / 2;
        lives = 3;
        score = 0;
        level = 1;
        gameOver = false;
        playerBulletActive = false;
        alienBullets.clear();
        explosions.clear();
        muzzleTicks = 0;
        shakeTicks = 0;
        powerups.clear();
        lastCollectedPowerup = null;
        powerupActive = false;
        powerupPauseTicks = 0;
        powerupName = "";
        powerupEffectTicks = 0;
        lastPowerupSpawnTime = System.currentTimeMillis();
        firstPowerupSpawned = false;
        resetLevel();
    }

    private void resetLevel() {
        aliensAlive = new boolean[ALIEN_ROWS][ALIEN_COLS];
        for (int r = 0; r < ALIEN_ROWS; r++) {
            for (int c = 0; c < ALIEN_COLS; c++) {
                aliensAlive[r][c] = true;
            }
        }
        alienX = 50;
        alienY = 50;
        alienDirection = 1;
        currentAlienSpeedX = ALIEN_INITIAL_SPEED_X + (level - 1) * 0.5;
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
            muzzleTicks = 5; 
            SoundEffect.playFire();
        }
    }

    public void update() {
        if (gameOver) return;

        // Powerup pause logic
        if (powerupPauseTicks > 0) {
            powerupPauseTicks--;
            if (powerupPauseTicks == 0) {
                powerupActive = false;
                powerupEffectTicks = 900; // 15 seconds at 60 FPS
            }
            return;
        }

        // Powerup effect logic
        if (powerupEffectTicks > 0) {
            powerupEffectTicks--;
            if (powerupEffectTicks == 0) {
                // Powerup effect ends
                lastCollectedPowerup = null;
                powerupName = "";
            }
        }

        // 1. Advance Player Bullet
        if (playerBulletActive) {
            playerBulletY -= BULLET_SPEED;
            if (playerBulletY < 0) {
                playerBulletActive = false;
            } else {
                checkPlayerBulletCollisions();
            }
        }

        // 1.5 Powerup spawn (every 60 seconds, only if no effect or popup active)
        long now = System.currentTimeMillis();
        if (powerups.isEmpty() && powerupPauseTicks == 0 && powerupEffectTicks == 0) {
            long wait = firstPowerupSpawned ? 45000 : 15000; // 45s after first, 15s for first
            if (now - lastPowerupSpawnTime >= wait) {
                int px = 40 + random.nextInt(WIDTH - 80);
                int type = random.nextInt(3); // 3 types for demo
                powerups.add(new Powerup(px, 0, type));
                lastPowerupSpawnTime = now;
                firstPowerupSpawned = true;
            }
        }
        // Move powerups
        for (int i = 0; i < powerups.size(); i++) {
            Powerup p = powerups.get(i);
            p.y += 3;
            if (p.y > HEIGHT) {
                powerups.remove(i--);
            } else if (intersects(p.x, p.y, 30, 30, playerX, PLAYER_Y, PLAYER_WIDTH, PLAYER_HEIGHT)) {
                // Collect powerup
                lastCollectedPowerup = p;
                powerupName = getPowerupName(p.type);
                powerupActive = true;
                powerupPauseTicks = 120; // 2 seconds at 60 FPS
                powerups.clear(); // Remove all powerups (only one at a time)
                
                // Apply immediate effects
                if (p.type == 0) { // Extra Life
                    lives++;
                }
                SoundEffect.playPowerup();

                // After collection, next spawn is 45s
                lastPowerupSpawnTime = System.currentTimeMillis();
                firstPowerupSpawned = true;
            }
        }

        // 2. Move Alien Formation
        updateAlienMovement();

        // 3. Fire Alien Bullets
        if (random.nextDouble() < 0.02 + (level * 0.005)) {
            fireAlienBullet();
        }

        // 4. Update Alien Bullets and Detect Player Collisions
        updateAlienBullets();

        // 5. Effects Update
        if (muzzleTicks > 0) muzzleTicks--;
        if (shakeTicks > 0) shakeTicks--;
        for (int i = 0; i < explosions.size(); i++) {
            Explosion e = explosions.get(i);
            e.ticks--;
            if (e.ticks <= 0) explosions.remove(i--);
        }

        // 6. Check if all aliens are dead
        checkLevelCleared();
    }

    private void checkLevelCleared() {
        for (int r = 0; r < ALIEN_ROWS; r++) {
            for (int c = 0; c < ALIEN_COLS; c++) {
                if (aliensAlive[r][c]) return;
            }
        }
        level++;
        playerBulletActive = false;
        alienBullets.clear();
        SoundEffect.playLevelUp();
        resetLevel();
    }

    private void updateAlienMovement() {
        double moveStep = currentAlienSpeedX * alienDirection;
        boolean hitEdge = false;
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
        if (!anyAliens) return; 

        if (alienDirection == 1 && maxX + moveStep > WIDTH - 10) hitEdge = true;
        else if (alienDirection == -1 && minX + moveStep < 10) hitEdge = true;

        if (hitEdge) {
            alienDirection *= -1;
            alienY += ALIEN_DROP_Y;
            if (alienY + ALIEN_ROWS * (ALIEN_HEIGHT + ALIEN_SPACING_Y) > PLAYER_Y) {
                gameOver = true;
            }
        } else {
            alienX += moveStep;
        }
    }

    private void fireAlienBullet() {
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
        for (int r = ALIEN_ROWS - 1; r >= 0; r--) {
            if (aliensAlive[r][col]) {
                double bx = alienX + col * (ALIEN_WIDTH + ALIEN_SPACING_X) + ALIEN_WIDTH / 2.0;
                double by = alienY + r * (ALIEN_HEIGHT + ALIEN_SPACING_Y) + ALIEN_HEIGHT;
                
                // 15% chance for a homing bullet
                boolean homing = random.nextDouble() < 0.15;
                alienBullets.add(new Point(bx, by, homing));
                break;
            }
        }
    }

    private void updateAlienBullets() {
        for (int i = 0; i < alienBullets.size(); i++) {
            Point b = alienBullets.get(i);
            
            if (b.isHoming && !b.isLocked) {
                double targetX = playerX + PLAYER_WIDTH / 2.0;
                double targetY = PLAYER_Y + PLAYER_HEIGHT / 2.0;
                double dx = targetX - b.x;
                double dy = targetY - b.y;
                double dist = Math.sqrt(dx * dx + dy * dy);
                
                if (dist < 180) { // Lock direction when close
                    b.isLocked = true;
                } else if (dist > 0) {
                    double speed = 3.5; // Slightly slower than regular bullets for fairness
                    b.vx = (dx / dist) * speed;
                    b.vy = (dy / dist) * speed;
                }
            }

            b.x += b.vx;
            b.y += b.vy;

            if (b.y > HEIGHT || b.y < -50 || b.x < -50 || b.x > WIDTH + 50) {
                alienBullets.remove(i--);
            } else if (intersects((int)b.x, (int)b.y, b.isHoming ? 8 : 4, b.isHoming ? 12 : 10, playerX, PLAYER_Y, PLAYER_WIDTH, PLAYER_HEIGHT)) {
                boolean shielded = isPowerupEffectActive() && lastCollectedPowerup != null && lastCollectedPowerup.type == 2;
                if (!shielded) {
                    lives--;
                    if (lives <= 0) gameOver = true;
                }
                shakeTicks = 15; 
                explosions.add(new Explosion(playerX + PLAYER_WIDTH/2, PLAYER_Y + PLAYER_HEIGHT/2, 30));
                SoundEffect.playPlayerHit();
                alienBullets.remove(i--);
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
                        boolean doubleScore = isPowerupEffectActive() && lastCollectedPowerup != null && lastCollectedPowerup.type == 1;
                        score += doubleScore ? 20 : 10;
                        shakeTicks = 5; 
                        explosions.add(new Explosion(ax + ALIEN_WIDTH/2, ay + ALIEN_HEIGHT/2, 15));
                        SoundEffect.playExplosion();
                        return;
                    }
                }
            }
        }
    }

    private boolean intersects(int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2) {
        return x1 < x2 + w2 && x1 + w1 > x2 && y1 < y2 + h2 && y1 + h1 > y2;
    }

    public int getMuzzleTicks() { return muzzleTicks; }
    public int getShakeTicks() { return shakeTicks; }
    public List<Explosion> getExplosions() { return explosions; }

    public int getPlayerX() { return playerX; }
            public boolean isPowerupEffectActive() { return powerupEffectTicks > 0; }
        public List<Powerup> getPowerups() { return powerups; }
        public boolean isPowerupActive() { return powerupActive; }
        public int getPowerupPauseTicks() { return powerupPauseTicks; }
        public String getPowerupName() { return powerupName; }
        public Powerup getLastCollectedPowerup() { return lastCollectedPowerup; }

        private String getPowerupName(int type) {
            switch (type) {
                case 0: return "Extra Life";
                case 1: return "Double Score";
                case 2: return "Shield";
                default: return "Mystery";
            }
        }
    public int getLives() { return lives; }
    public int getScore() { return score; }
    public int getLevel() { return level; }
    public boolean isGameOver() { return gameOver; }
    public boolean[][] getAliensAlive() { return aliensAlive; }
    public int getAlienX() { return (int) alienX; }
    public int getAlienY() { return (int) alienY; }
    public int getPlayerBulletX() { return playerBulletX; }
    public int getPlayerBulletY() { return playerBulletY; }
    public boolean isPlayerBulletActive() { return playerBulletActive; }
    public List<Point> getAlienBullets() { return alienBullets; }

    public static class Point {
        public double x, y, vx, vy;
        public boolean isHoming, isLocked;
        public Point(double x, double y, boolean homing) { 
            this.x = x; this.y = y; 
            this.isHoming = homing;
            this.vx = 0;
            this.vy = 5; // Regular bullet speed
            this.isLocked = false;
        }
    }

    public static class Explosion {
        public int x, y, ticks, maxTicks;
        public Explosion(int x, int y, int duration) {
            this.x = x; this.y = y; this.ticks = duration; this.maxTicks = duration;
        }
    }
}
