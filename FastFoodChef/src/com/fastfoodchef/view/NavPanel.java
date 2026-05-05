package com.fastfoodchef.view;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class NavPanel extends JPanel {
    private Map<String, JButton> buttons;

    public NavPanel() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        setBackground(new Color(33, 37, 43));
        setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(60, 60, 60)));
        buttons = new HashMap<>();

        String[] screens = {"Counter", "Grill", "Fryer", "Drink", "Assembly"};
        for (String screen : screens) {
            JButton button = createNavButton(screen);
            buttons.put(screen, button);
            add(button);
        }
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(new Color(60, 60, 70));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 90)),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public JButton getButton(String screenName) {
        return buttons.get(screenName);
    }
}
