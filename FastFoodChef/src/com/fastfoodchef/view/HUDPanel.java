package com.fastfoodchef.view;

import javax.swing.*;
import java.awt.*;

public class HUDPanel extends JPanel {
    private JLabel timeLabel;
    private JLabel ratingLabel;
    private JLabel revenueLabel;

    public HUDPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));
        
        timeLabel = new JLabel("Time: 08:00 AM");
        ratingLabel = new JLabel("Rating: 5.0 ★");
        revenueLabel = new JLabel("Bank: $0.00");

        add(timeLabel);
        add(ratingLabel);
        add(revenueLabel);
    }

    public void updateTime(String time) {
        timeLabel.setText("Time: " + time);
    }

    public void updateRating(double rating) {
        ratingLabel.setText(String.format("Rating: %.1f ★", rating));
    }

    public void updateRevenue(double revenue) {
        revenueLabel.setText(String.format("Bank: $%.2f", revenue));
    }
}
