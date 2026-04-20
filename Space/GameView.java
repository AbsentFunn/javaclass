package Space;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class GameView extends JPanel {
    private final GameModel model;
    private final Random random = new Random();

    public GameView(GameModel model) {
        this.model = model;
        setPreferredSize(new Dimension(GameModel.WIDTH, GameModel.HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // --- Screen Shake FX ---
        if (model.getShakeTicks() > 0) {
            int intensity = model.getShakeTicks() / 2;
            g2.translate(random.nextInt(intensity * 2 + 1) - intensity, 
                         random.nextInt(intensity * 2 + 1) - intensity);
        }

        if (model.isGameOver()) {
            drawGameOver(g2);
        } else {
            drawPlayer(g2);
            drawAliens(g2);
            drawBullets(g2);
            drawExplosions(g2);
            drawUI(g2);
        }
    }

    private void drawPlayer(Graphics2D g2) {
        int px = model.getPlayerX();
        int py = GameModel.PLAYER_Y;

        // Player Body
        g2.setColor(Color.GREEN);
        g2.fillRect(px, py, GameModel.PLAYER_WIDTH, GameModel.PLAYER_HEIGHT);
        g2.fillRect(px + GameModel.PLAYER_WIDTH / 2 - 2, py - 5, 4, 5);

        // --- Muzzle Flash FX ---
        if (model.getMuzzleTicks() > 0) {
            g2.setColor(new Color(255, 255, 100, 200));
            int size = model.getMuzzleTicks() * 4;
            g2.fillOval(px + GameModel.PLAYER_WIDTH / 2 - size / 2, py - 10 - size / 2, size, size);
        }
    }

    private void drawAliens(Graphics2D g2) {
        boolean[][] aliens = model.getAliensAlive();
        int alienX = model.getAlienX();
        int alienY = model.getAlienY();

        for (int r = 0; r < GameModel.ALIEN_ROWS; r++) {
            for (int c = 0; c < GameModel.ALIEN_COLS; c++) {
                if (aliens[r][c]) {
                    int ax = alienX + c * (GameModel.ALIEN_WIDTH + GameModel.ALIEN_SPACING_X);
                    int ay = alienY + r * (GameModel.ALIEN_HEIGHT + GameModel.ALIEN_SPACING_Y);
                    
                    if (r == 0) g2.setColor(Color.RED);
                    else if (r < 3) g2.setColor(Color.YELLOW);
                    else g2.setColor(Color.CYAN);
                    
                    g2.fillRect(ax, ay, GameModel.ALIEN_WIDTH, GameModel.ALIEN_HEIGHT);
                    
                    g2.setColor(Color.BLACK);
                    g2.fillRect(ax + 5, ay + 5, 4, 4);
                    g2.fillRect(ax + GameModel.ALIEN_WIDTH - 9, ay + 5, 4, 4);
                }
            }
        }
    }

    private void drawBullets(Graphics2D g2) {
        if (model.isPlayerBulletActive()) {
            g2.setColor(Color.WHITE);
            g2.fillRect(model.getPlayerBulletX(), model.getPlayerBulletY(), 4, 10);
            // Trail effect
            g2.setColor(new Color(255, 255, 255, 100));
            g2.fillRect(model.getPlayerBulletX(), model.getPlayerBulletY() + 10, 4, 15);
        }

        g2.setColor(Color.ORANGE);
        for (GameModel.Point b : model.getAlienBullets()) {
            g2.fillRect(b.x, b.y, 4, 10);
            // Pulse effect
            if (System.currentTimeMillis() % 200 < 100) {
                g2.setColor(Color.RED);
                g2.drawOval(b.x - 2, b.y - 2, 8, 14);
                g2.setColor(Color.ORANGE);
            }
        }
    }

    private void drawExplosions(Graphics2D g2) {
        for (GameModel.Explosion e : model.getExplosions()) {
            float progress = 1.0f - ((float) e.ticks / e.maxTicks);
            int size = (int) (progress * 60);
            int alpha = (int) ((1.0f - progress) * 255);
            
            g2.setColor(new Color(255, 150, 0, alpha));
            g2.fillOval(e.x - size / 2, e.y - size / 2, size, size);
            
            g2.setColor(new Color(255, 255, 255, alpha / 2));
            g2.drawOval(e.x - size / 3, e.y - size / 3, size / 1.5f > 0 ? (int)(size/1.5f) : 1, size / 1.5f > 0 ? (int)(size/1.5f) : 1);
        }
    }

    private void drawUI(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Monospaced", Font.BOLD, 20));
        g2.drawString("SCORE: " + model.getScore(), 20, 30);
        g2.drawString("LEVEL: " + model.getLevel(), GameModel.WIDTH / 2 - 50, 30);
        
        // Lives as icons
        g2.setColor(Color.GREEN);
        for (int i = 0; i < model.getLives(); i++) {
            g2.fillRect(GameModel.WIDTH - 150 + (i * 30), 15, 20, 10);
        }
    }

    private void drawGameOver(Graphics2D g2) {
        g2.setColor(new Color(255, 0, 0, 100));
        g2.fillRect(0, 0, GameModel.WIDTH, GameModel.HEIGHT);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Monospaced", Font.BOLD, 50));
        String msg = "GAME OVER";
        FontMetrics fm = g2.getFontMetrics();
        int x = (GameModel.WIDTH - fm.stringWidth(msg)) / 2;
        int y = GameModel.HEIGHT / 2;
        g2.drawString(msg, x, y);

        g2.setFont(new Font("Monospaced", Font.BOLD, 20));
        String scoreMsg = "FINAL SCORE: " + model.getScore();
        fm = g2.getFontMetrics();
        g2.drawString(scoreMsg, (GameModel.WIDTH - fm.stringWidth(scoreMsg)) / 2, y + 50);
        
        String restartMsg = "Press Enter to Restart";
        fm = g2.getFontMetrics();
        g2.drawString(restartMsg, (GameModel.WIDTH - fm.stringWidth(restartMsg)) / 2, y + 100);
    }
}
