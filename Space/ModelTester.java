// ModelTester.java
// Manual test suite for GameModel logic.
package Space;

import java.util.List;

public class ModelTester {
    public static void main(String[] args) {
        System.out.println("Running GameModel Tests...\n");

        testLeftEdge();
        testRightEdge();
        testDoubleFire();
        testBulletRemoval();
        testScoreIncrease();
        testGameOver();

        System.out.println("\nTests Complete.");
    }

    private static void testLeftEdge() {
        GameModel model = new GameModel();
        // Move left many times
        for (int i = 0; i < 200; i++) {
            model.movePlayerLeft();
        }
        if (model.getPlayerX() == 0) {
            System.out.println("PASS: Player cannot move past left edge.");
        } else {
            System.out.println("FAIL: Player moved past left edge! X=" + model.getPlayerX());
        }
    }

    private static void testRightEdge() {
        GameModel model = new GameModel();
        // Move right many times
        for (int i = 0; i < 200; i++) {
            model.movePlayerRight();
        }
        int expected = GameModel.WIDTH - GameModel.PLAYER_WIDTH;
        if (model.getPlayerX() == expected) {
            System.out.println("PASS: Player cannot move past right edge.");
        } else {
            System.out.println("FAIL: Player moved past right edge! X=" + model.getPlayerX() + " Expected=" + expected);
        }
    }

    private static void testDoubleFire() {
        GameModel model = new GameModel();
        model.firePlayerBullet();
        int initialY = model.getPlayerBulletY();
        
        // Advance one tick so bullet moves up
        model.update();
        int updatedY = model.getPlayerBulletY();
        
        // Try to fire again
        model.firePlayerBullet();
        
        if (model.getPlayerBulletY() == updatedY && model.getPlayerBulletY() != initialY) {
            System.out.println("PASS: Firing while bullet is in flight does nothing.");
        } else {
            System.out.println("FAIL: Bullet reset or second bullet fired! Y=" + model.getPlayerBulletY());
        }
    }

    private static void testBulletRemoval() {
        GameModel model = new GameModel();
        model.firePlayerBullet();
        
        // Update until bullet should be off-screen
        for (int i = 0; i < 100; i++) {
            model.update();
        }
        
        if (!model.isPlayerBulletActive()) {
            System.out.println("PASS: Bullet reaching top is removed.");
        } else {
            System.out.println("FAIL: Bullet still active after leaving screen! Y=" + model.getPlayerBulletY());
        }
    }

    private static void testScoreIncrease() {
        GameModel model = new GameModel();
        int initialScore = model.getScore();
        
        // This test assumes an alien exists at the starting formation.
        // We will move the player to be roughly under the first column and fire.
        // Alien at c=0 is at alienX (50).
        // To hit it, playerBulletX should be inside [50, 50+30].
        // playerBulletX = playerX + 20 - 2 = playerX + 18.
        // So playerX = 50 - 18 = 32.
        
        // Reset player position for deterministic test
        // Since we can't set it directly, we move it.
        while (model.getPlayerX() > 32) model.movePlayerLeft();
        while (model.getPlayerX() < 32) model.movePlayerRight();
        
        model.firePlayerBullet();
        
        // Update until collision should occur
        for (int i = 0; i < 100; i++) {
            model.update();
            if (model.getScore() > initialScore) break;
        }
        
        if (model.getScore() > initialScore) {
            System.out.println("PASS: Destroying an alien increases score.");
        } else {
            System.out.println("FAIL: Score did not increase after shooting towards alien formation.");
        }
    }

    private static void testGameOver() {
        GameModel model = new GameModel();
        
        // Simulate 3 hits by manually adding alien bullets to the list
        List<GameModel.Point> bullets = model.getAlienBullets();
        int px = model.getPlayerX();
        int py = GameModel.PLAYER_Y;
        
        // Add bullets at player position and update for each hit
        for (int i = 0; i < 3; i++) {
            bullets.add(new GameModel.Point(px + 10, py));
            model.update();
        }
        
        if (model.getLives() <= 0 && model.isGameOver()) {
            System.out.println("PASS: Losing all lives triggers game-over state.");
        } else {
            System.out.println("FAIL: Game not over after 3 hits! Lives=" + model.getLives());
        }
    }
}
