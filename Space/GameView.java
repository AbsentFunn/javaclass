package Space;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class GameView extends JPanel {
    private final GameModel model;
    private final Random random = new Random();

    // Galaxy Background State
    private static class Star {
        float x, y, size, speed;
        Color color;
        Star(float x, float y, float size, float speed, Color color) {
            this.x = x; this.y = y; this.size = size; this.speed = speed; this.color = color;
        }
    }
    private final Star[] stars = new Star[100];

    public GameView(GameModel model) {
        this.model = model;
        setPreferredSize(new Dimension(GameModel.WIDTH, GameModel.HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);

        // Initialize stars
        for (int i = 0; i < stars.length; i++) {
            stars[i] = new Star(
                random.nextFloat() * GameModel.WIDTH,
                random.nextFloat() * GameModel.HEIGHT,
                1 + random.nextFloat() * 2,
                0.2f + random.nextFloat() * 0.8f,
                new Color(200 + random.nextInt(56), 200 + random.nextInt(56), 255)
            );
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawGalaxy(g2);

        // --- Screen Shake FX ---
        if (model.getShakeTicks() > 0) {
            int intensity = model.getShakeTicks() / 2;
            g2.translate(random.nextInt(intensity * 2 + 1) - intensity, 
                         random.nextInt(intensity * 2 + 1) - intensity);
        }

        if (model.isGameOver()) {
            drawGameOver(g2);
        } else {
            if (model.isPowerupEffectActive()) {
                drawAura(g2);
            }
            drawPlayer(g2);
            drawAliens(g2);
            drawBullets(g2);
            drawExplosions(g2);
            drawPowerups(g2);
            drawUI(g2);
            if (model.isPowerupActive()) {
                drawPowerupPopup(g2);
            }
        }
    }

    private void drawGalaxy(Graphics2D g2) {
        long time = System.currentTimeMillis();
        
        // Use a composite to set global 70% opacity for the galaxy layer
        Composite oldComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));

        // 1. Draw Nebula (translucent colorful blobs)
        for (int i = 0; i < 5; i++) {
            float phase = (time / 5000f) + (i * 1.5f);
            int nx = (int) (GameModel.WIDTH / 2 + Math.cos(phase) * 200 - 300);
            int ny = (int) (GameModel.HEIGHT / 2 + Math.sin(phase * 0.7) * 150 - 300);
            int size = 600;
            
            Color nebulaColor;
            switch(i % 3) {
                case 0: nebulaColor = new Color(100, 50, 150, 40); break; // Purple
                case 1: nebulaColor = new Color(50, 80, 150, 40); break;  // Deep Blue
                default: nebulaColor = new Color(150, 50, 80, 40); break; // Magenta
            }
            
            g2.setPaint(new RadialGradientPaint(nx + 300, ny + 300, 300, 
                new float[]{0f, 1f}, new Color[]{nebulaColor, new Color(0,0,0,0)}));
            g2.fillOval(nx, ny, size, size);
        }

        // 2. Draw and Update Stars (Parallax)
        for (Star s : stars) {
            // Update position (moving downwards)
            s.y += s.speed;
            if (s.y > GameModel.HEIGHT) {
                s.y = -s.size;
                s.x = random.nextFloat() * GameModel.WIDTH;
            }

            // Twinkle effect
            float twinkle = (float) Math.abs(Math.sin(time / 500.0 + s.x));
            int alpha = 150 + (int) (105 * twinkle);
            g2.setColor(new Color(s.color.getRed(), s.color.getGreen(), s.color.getBlue(), alpha));
            
            g2.fillOval((int) s.x, (int) s.y, (int) s.size, (int) s.size);
            
            // Subtle glow for larger stars
            if (s.size > 2) {
                g2.setColor(new Color(s.color.getRed(), s.color.getGreen(), s.color.getBlue(), alpha / 4));
                g2.fillOval((int) s.x - 2, (int) s.y - 2, (int) s.size + 4, (int) s.size + 4);
            }
        }

        g2.setComposite(oldComposite);
    }

    // Draw aura around player when powerup is active
    private void drawAura(Graphics2D g2) {
        int px = model.getPlayerX();
        int py = GameModel.PLAYER_Y;
        int auraSize = 70;
        int centerX = px + GameModel.PLAYER_WIDTH / 2;
        int centerY = py + GameModel.PLAYER_HEIGHT / 2;
        float phase = (System.currentTimeMillis() % 1000) / 1000f;
        int alpha = 100 + (int)(60 * Math.abs(Math.sin(phase * 2 * Math.PI)));
        
        Color auraColor;
        GameModel.Powerup last = model.getLastCollectedPowerup();
        if (last != null) {
            switch (last.type) {
                case 0: auraColor = new Color(0, 255, 0, alpha); break; // Green
                case 1: auraColor = new Color(255, 215, 0, alpha); break; // Gold
                case 2: auraColor = new Color(0, 200, 255, alpha); break; // Blue
                default: auraColor = new Color(100, 200, 255, alpha); break;
            }
        } else {
            auraColor = new Color(100, 200, 255, alpha);
        }
        
        g2.setColor(auraColor);
        g2.setStroke(new BasicStroke(8f));
        g2.drawOval(centerX - auraSize / 2, centerY - auraSize / 2, auraSize, auraSize);
        g2.setStroke(new BasicStroke(1f));
    }

    // Draw falling powerups
    private void drawPowerups(Graphics2D g2) {
        for (GameModel.Powerup p : model.getPowerups()) {
            // Animate: pulse size and color
            int base = 30;
            int size = base + (int)(5 * Math.sin(System.currentTimeMillis() / 100.0 + p.x));
            Color c;
            switch (p.type) {
                case 0: c = new Color(0, 255, 0, 200); break; // Green
                case 1: c = new Color(255, 215, 0, 200); break; // Gold
                case 2: c = new Color(0, 200, 255, 200); break; // Blue
                default: c = new Color(255, 255, 255, 200); break;
            }
            g2.setColor(c);
            g2.fillOval(p.x, p.y, size, size);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Monospaced", Font.BOLD, 16));
            String label = "?";
            if (p.type == 0) label = "L";
            else if (p.type == 1) label = "$";
            else if (p.type == 2) label = "S";
            FontMetrics fm = g2.getFontMetrics();
            int tx = p.x + (size - fm.stringWidth(label)) / 2;
            int ty = p.y + (size + fm.getAscent()) / 2 - 4;
            g2.drawString(label, tx, ty);
        }
    }

    // Draw animated translucent popup for powerup
    private void drawPowerupPopup(Graphics2D g2) {
        int ticks = model.getPowerupPauseTicks();
        float progress = 1.0f - (ticks / 120.0f);
        // Animate fade in/out and scale
        float alpha = (float)(Math.sin(Math.PI * Math.min(progress, 1.0)));
        int w = (int)(GameModel.WIDTH * (0.7 + 0.1 * alpha));
        int h = (int)(200 + 30 * alpha);
        int x = (GameModel.WIDTH - w) / 2;
        int y = (GameModel.HEIGHT - h) / 2;
        g2.setColor(new Color(50, 50, 200, (int)(180 * alpha)));
        g2.fillRoundRect(x, y, w, h, 40, 40);
        g2.setColor(new Color(255, 255, 255, (int)(220 * alpha)));
        g2.setFont(new Font("Monospaced", Font.BOLD, 48));
        String msg = "POWERUP!";
        FontMetrics fm = g2.getFontMetrics();
        int mx = x + (w - fm.stringWidth(msg)) / 2;
        int my = y + 70;
        g2.drawString(msg, mx, my);
        g2.setFont(new Font("Monospaced", Font.BOLD, 28));
        String got = "You got: " + model.getPowerupName();
        fm = g2.getFontMetrics();
        int gx = x + (w - fm.stringWidth(got)) / 2;
        int gy = my + 60;
        g2.drawString(got, gx, gy);
    }
    // <-- Removed the extra closing brace that was originally here

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

        for (GameModel.Point b : model.getAlienBullets()) {
            if (b.isHoming) {
                g2.setColor(new Color(150, 0, 0));
                g2.fillRect((int)b.x, (int)b.y, 8, 12);
                // Dark red aura for homing bullets
                g2.setColor(new Color(255, 0, 0, 80));
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval((int)b.x - 4, (int)b.y - 4, 16, 20);
                g2.setStroke(new BasicStroke(1f));
            } else {
                g2.setColor(Color.ORANGE);
                g2.fillRect((int)b.x, (int)b.y, 4, 10);
                // Pulse effect
                if (System.currentTimeMillis() % 200 < 100) {
                    g2.setColor(Color.RED);
                    g2.drawOval((int)b.x - 2, (int)b.y - 2, 8, 14);
                }
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