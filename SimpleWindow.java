import java.awt.*;
import javax.swing.*;

public class SimpleWindow extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLUE);
        g.fillRect(50, 50, 200, 100);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Simple Window");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.add(new SimpleWindow());
        frame.setVisible(true);
    }
}