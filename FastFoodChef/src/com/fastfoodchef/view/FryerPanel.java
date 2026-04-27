package com.fastfoodchef.view;

import com.fastfoodchef.model.FryerStation;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class FryerPanel extends JPanel {
    private JPanel basketContainer;
    private JButton friesBtn, ringsBtn, curdsBtn;
    private BasketUI[] baskets;

    public FryerPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(60, 60, 60)); // Darker for industrial look

        // Top: Item Selection
        JPanel itemPanel = new JPanel(new FlowLayout());
        itemPanel.setBorder(BorderFactory.createTitledBorder("Raw Ingredients"));
        friesBtn = new JButton("Add Fries");
        ringsBtn = new JButton("Add Onion Rings");
        curdsBtn = new JButton("Add Cheese Curds");
        itemPanel.add(friesBtn);
        itemPanel.add(ringsBtn);
        itemPanel.add(curdsBtn);

        // Center: Baskets
        basketContainer = new JPanel(new GridLayout(1, 3, 10, 10));
        basketContainer.setOpaque(false);
        baskets = new BasketUI[3];
        for (int i = 0; i < 3; i++) {
            baskets[i] = new BasketUI();
            basketContainer.add(baskets[i]);
        }

        add(itemPanel, BorderLayout.NORTH);
        add(basketContainer, BorderLayout.CENTER);
    }

    public void update(FryerStation model) {
        List<FryerStation.FryerBasket> modelBaskets = model.getBaskets();
        for (int i = 0; i < 3; i++) {
            baskets[i].update(modelBaskets.get(i));
        }
    }

    public void setIngredientListener(ActionListener listener) {
        friesBtn.addActionListener(listener);
        ringsBtn.addActionListener(listener);
        curdsBtn.addActionListener(listener);
    }

    public void setBasketListener(int index, ActionListener listener) {
        baskets[index].getActionButton().addActionListener(listener);
    }

    private static class BasketUI extends JPanel {
        private JLabel nameLabel;
        private JProgressBar progressBar;
        private JLabel shakeLabel;
        private JButton actionButton;

        public BasketUI() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
            setPreferredSize(new Dimension(200, 300));

            nameLabel = new JLabel("Empty");
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 16));

            progressBar = new JProgressBar(0, 100);
            progressBar.setMaximumSize(new Dimension(150, 20));

            shakeLabel = new JLabel("Shakes: 0/3");
            shakeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            actionButton = new JButton("Shake/Pull");
            actionButton.setAlignmentX(Component.CENTER_ALIGNMENT);

            add(Box.createVerticalStrut(20));
            add(nameLabel);
            add(Box.createVerticalStrut(20));
            add(progressBar);
            add(Box.createVerticalStrut(10));
            add(shakeLabel);
            add(Box.createVerticalGlue());
            add(actionButton);
            add(Box.createVerticalStrut(20));
        }

        public void update(FryerStation.FryerBasket model) {
            if (model.getItem() == null) {
                nameLabel.setText("Empty");
                progressBar.setValue(0);
                shakeLabel.setText("Shakes: 0/3");
                actionButton.setText("Ready");
                actionButton.setEnabled(false);
                setBackground(Color.LIGHT_GRAY);
            } else {
                nameLabel.setText(model.getItem().getName());
                progressBar.setValue((int)Math.min(model.getProgress(), 100));
                shakeLabel.setText("Shakes: " + model.getShakes() + "/3");
                actionButton.setEnabled(true);

                if (model.isRuined()) {
                    nameLabel.setText(model.getItem().getName() + " (STUCK/RUINED)");
                    setBackground(new Color(150, 75, 75));
                    actionButton.setText("Trash");
                } else if (model.isReady()) {
                    setBackground(new Color(75, 150, 75));
                    actionButton.setText("Pull to Warmer");
                } else {
                    if (model.needsShake()) {
                        setBackground(new Color(255, 200, 0)); // Bright yellow for attention
                        actionButton.setText("SHAKE!");
                    } else {
                        setBackground(new Color(200, 200, 150));
                        actionButton.setText("Wait...");
                        actionButton.setEnabled(false);
                    }
                }
            }
        }

        public JButton getActionButton() { return actionButton; }
    }
}
