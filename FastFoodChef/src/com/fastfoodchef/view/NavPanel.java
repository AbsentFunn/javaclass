package com.fastfoodchef.view;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class NavPanel extends JPanel {
    private Map<String, JButton> buttons;

    public NavPanel() {
        setLayout(new FlowLayout(FlowLayout.CENTER));
        buttons = new HashMap<>();

        String[] screens = {"Counter", "Grill", "Fryer", "Drink", "Assembly"};
        for (String screen : screens) {
            JButton button = new JButton(screen);
            buttons.put(screen, button);
            add(button);
        }
    }

    public JButton getButton(String screenName) {
        return buttons.get(screenName);
    }
}
