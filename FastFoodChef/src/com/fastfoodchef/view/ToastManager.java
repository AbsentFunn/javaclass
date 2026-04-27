package com.fastfoodchef.view;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class ToastManager {
    private static JFrame mainFrame;
    private static JLayeredPane layeredPane;
    private static JPanel toastContainer;

    public static void init(JFrame frame) {
        mainFrame = frame;
        layeredPane = frame.getLayeredPane();
        
        // Initialize the container for toasts
        toastContainer = new JPanel();
        toastContainer.setLayout(new BoxLayout(toastContainer, BoxLayout.Y_AXIS));
        toastContainer.setOpaque(false);
        
        // Position at the top center
        int width = 400;
        int height = 600; // Large enough to hold many toasts
        int x = (mainFrame.getWidth() - width) / 2;
        int y = 20;
        toastContainer.setBounds(x, y, width, height);
        
        layeredPane.add(toastContainer, JLayeredPane.POPUP_LAYER);
    }

    public static void showToast(String message, int durationSeconds) {
        SwingUtilities.invokeLater(() -> {
            if (toastContainer == null) return;

            JPanel toastPanel = new JPanel();
            toastPanel.setBackground(new Color(50, 50, 50, 230));
            toastPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
            toastPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
            toastPanel.setMaximumSize(new Dimension(350, 45));
            toastPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel label = new JLabel(message);
            label.setForeground(Color.WHITE);
            label.setFont(new Font("Arial", Font.BOLD, 14));
            toastPanel.add(label);

            toastContainer.add(toastPanel);
            toastContainer.add(Box.createVerticalStrut(5)); // Gap between toasts
            
            toastContainer.revalidate();
            toastContainer.repaint();

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    SwingUtilities.invokeLater(() -> {
                        toastContainer.remove(toastPanel);
                        // We also want to remove the strut, but for simplicity, 
                        // we can just re-render or manage a list. 
                        // A better way is to remove the specific panel and the next component (the strut)
                        // but revalidate is usually enough if we don't mind tiny gaps growing.
                        // Let's do it properly:
                        Component[] comps = toastContainer.getComponents();
                        for (int i = 0; i < comps.length; i++) {
                            if (comps[i] == toastPanel) {
                                toastContainer.remove(comps[i]); // Remove panel
                                if (i + 1 < comps.length && comps[i+1] instanceof Box.Filler) {
                                    toastContainer.remove(comps[i+1]); // Remove strut
                                }
                                break;
                            }
                        }
                        
                        toastContainer.revalidate();
                        toastContainer.repaint();
                    });
                }
            }, durationSeconds * 1000L);
        });
    }
}
