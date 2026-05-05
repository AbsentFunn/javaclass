package com.fastfoodchef.view;

import javax.swing.*;
import java.awt.*;

public class HUDPanel extends JPanel {
    private JLabel timeLabel;
    private JLabel ratingLabel;
    private JLabel revenueLabel;

    public HUDPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 30, 15));
        setBackground(new Color(33, 37, 43));
        setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(60, 60, 60)));
        
        timeLabel = createHUDLabel("Time: 08:00 AM");
        ratingLabel = createHUDLabel("Rating: 5.0 ★");
        revenueLabel = createHUDLabel("Bank: $0.00");

        add(timeLabel);
        add(ratingLabel);
        add(revenueLabel);
    }

    private JLabel createHUDLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(Color.WHITE);
        return label;
    }

    public void updateTime(String time) {
        timeLabel.setText("Time: " + time);
    }

    public void updateRating(double rating) {
        ratingLabel.setText(String.format("Rating: %.1f ★", rating));
        if (rating < 3.0) ratingLabel.setForeground(new Color(244, 67, 54));
        else if (rating < 4.0) ratingLabel.setForeground(new Color(255, 193, 7));
        else ratingLabel.setForeground(new Color(76, 175, 80));
    }

    public void updateRevenue(double revenue) {
        revenueLabel.setText(String.format("Bank: $%.2f", revenue));
    }
}
