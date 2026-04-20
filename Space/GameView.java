package Space;

import javax.swing.*;
import java.awt.*;

public class GameView extends JPanel {
    private final GameModel model;

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

        if (model.isGameOver()) {
            drawGameOver(g2);
        } else {
            drawPlayer(g2);
            drawAliens(g2);
            drawBullets(g2);
            drawUI(g2);
        }
    }

    private void drawPlayer(Graphics2D g2) {
        g2.setColor(Color.GREEN);
        g2.fillRect(model.getPlayerX(), GameModel.PLAYER_Y, GameModel.PLAYER_WIDTH, GameModel.PLAYER_HEIGHT);
        // Add a small "cannon" on top
        g2.fillRect(model.getPlayerX() + GameModel.PLAYER_WIDTH / 2 - 2, GameModel.PLAYER_Y - 5, 4, 5);
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
                    
                    // Different colors per row
                    if (r == 0) g2.setColor(Color.RED);
                    else if (r < 3) g2.setColor(Color.YELLOW);
                    else g2.setColor(Color.CYAN);
                    
                    g2.fillRect(ax, ay, GameModel.ALIEN_WIDTH, GameModel.ALIEN_HEIGHT);
                    
                    // Eyes
                    g2.setColor(Color.BLACK);
                    g2.fillRect(ax + 5, ay + 5, 4, 4);
                    g2.fillRect(ax + GameModel.ALIEN_WIDTH - 9, ay + 5, 4, 4);
                }
            }
        }
    }

    private void drawBullets(Graphics2D g2) {
        // Player Bullet
        if (model.isPlayerBulletActive()) {
            g2.setColor(Color.WHITE);
            g2.fillRect(model.getPlayerBulletX(), model.getPlayerBulletY(), 4, 10);
        }

        // Alien Bullets
        g2.setColor(Color.ORANGE);
        for (GameModel.Point b : model.getAlienBullets()) {
            g2.fillRect(b.x, b.y, 4, 10);
        }
    }

    private void drawUI(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Monospaced", Font.BOLD, 20));
        g2.drawString("SCORE: " + model.getScore(), 20, 30);
        g2.drawString("LEVEL: " + model.getLevel(), GameModel.WIDTH / 2 - 50, 30);
        g2.drawString("LIVES: " + model.getLives(), GameModel.WIDTH - 130, 30);
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
