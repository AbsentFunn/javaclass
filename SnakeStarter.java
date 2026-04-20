import java.awt.*;
import javax.swing.*;

public class SnakeStarter extends JPanel {
    private final int gridSize = 20;
    private final int cellSize = 25;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, gridSize * cellSize, gridSize * cellSize);

        // draw a green 3-segment snake near the center
        g.setColor(Color.GREEN);
        int centerX = (gridSize / 2) * cellSize;
        int centerY = (gridSize / 2) * cellSize;
        g.fillRect(centerX, centerY, cellSize, cellSize); // head
        g.fillRect(centerX - cellSize, centerY, cellSize, cellSize); // body
        g.fillRect(centerX - 2 * cellSize, centerY, cellSize, cellSize); // tail

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake STarter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.add(new SnakeStarter());
        frame.setVisible(true);
    }
}