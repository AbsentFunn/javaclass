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
        setBackground(new Color(40, 44, 52));

        // Top: Item Selection
        JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        itemPanel.setOpaque(false);
        itemPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(60, 60, 60)));
        
        friesBtn = createSelectionButton("ADD FRIES");
        ringsBtn = createSelectionButton("ADD ONION RINGS");
        curdsBtn = createSelectionButton("ADD CHEESE CURDS");
        
        itemPanel.add(friesBtn);
        itemPanel.add(ringsBtn);
        itemPanel.add(curdsBtn);

        // Center: Baskets
        basketContainer = new JPanel(new GridLayout(1, 3, 20, 20));
        basketContainer.setOpaque(false);
        basketContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        baskets = new BasketUI[3];
        for (int i = 0; i < 3; i++) {
            baskets[i] = new BasketUI();
            basketContainer.add(baskets[i]);
        }

        add(itemPanel, BorderLayout.NORTH);
        add(basketContainer, BorderLayout.CENTER);
    }

    private JButton createSelectionButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(new Color(60, 60, 70));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 110)),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
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
            setBackground(new Color(33, 37, 43));
            setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 2));
            setPreferredSize(new Dimension(200, 300));

            nameLabel = new JLabel("EMPTY");
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            nameLabel.setForeground(new Color(100, 100, 100));

            progressBar = new JProgressBar(0, 100);
            progressBar.setMaximumSize(new Dimension(180, 25));
            progressBar.setStringPainted(true);
            progressBar.setBackground(new Color(60, 60, 70));
            progressBar.setForeground(new Color(76, 175, 80));
            progressBar.setFont(new Font("Segoe UI", Font.BOLD, 12));
            progressBar.setBorder(BorderFactory.createEmptyBorder());

            shakeLabel = new JLabel("SHAKES: 0/3");
            shakeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            shakeLabel.setForeground(Color.WHITE);
            shakeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            actionButton = createActionButton("READY");
            actionButton.setEnabled(false);

            add(Box.createVerticalStrut(30));
            add(nameLabel);
            add(Box.createVerticalStrut(30));
            add(progressBar);
            add(Box.createVerticalStrut(15));
            add(shakeLabel);
            add(Box.createVerticalGlue());
            add(actionButton);
        }

        private JButton createActionButton(String text) {
            JButton btn = new JButton(text);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            btn.setBackground(new Color(60, 60, 70));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setMaximumSize(new Dimension(200, 60));
            btn.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(80, 80, 90)));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return btn;
        }

        public void update(FryerStation.FryerBasket model) {
            if (model.getItem() == null) {
                nameLabel.setText("EMPTY");
                nameLabel.setForeground(new Color(100, 100, 100));
                progressBar.setValue(0);
                shakeLabel.setText("SHAKES: 0/3");
                actionButton.setText("READY");
                actionButton.setBackground(new Color(60, 60, 70));
                actionButton.setEnabled(false);
                setBackground(new Color(33, 37, 43));
            } else {
                nameLabel.setText(model.getItem().getName().toUpperCase());
                nameLabel.setForeground(Color.WHITE);
                progressBar.setValue((int)Math.min(model.getProgress(), 100));
                shakeLabel.setText("SHAKES: " + model.getShakes() + "/3");
                actionButton.setEnabled(true);

                if (model.isRuined()) {
                    nameLabel.setText(model.getItem().getName().toUpperCase() + " (RUINED)");
                    nameLabel.setForeground(new Color(244, 67, 54));
                    setBackground(new Color(60, 30, 30));
                    actionButton.setText("TRASH");
                    actionButton.setBackground(new Color(244, 67, 54));
                } else if (model.isReady()) {
                    setBackground(new Color(30, 60, 30));
                    actionButton.setText("PULL TO WARMER");
                    actionButton.setBackground(new Color(76, 175, 80));
                } else {
                    if (model.needsShake()) {
                        setBackground(new Color(80, 60, 0));
                        actionButton.setText("SHAKE!");
                        actionButton.setBackground(new Color(255, 193, 7));
                        actionButton.setForeground(Color.BLACK);
                    } else {
                        setBackground(new Color(33, 37, 43));
                        actionButton.setText("WAIT...");
                        actionButton.setBackground(new Color(60, 60, 70));
                        actionButton.setForeground(Color.WHITE);
                        actionButton.setEnabled(false);
                    }
                }
            }
        }

        public JButton getActionButton() { return actionButton; }
    }
}
