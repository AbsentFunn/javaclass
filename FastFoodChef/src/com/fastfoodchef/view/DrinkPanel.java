package com.fastfoodchef.view;

import com.fastfoodchef.model.DrinkStation;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Random;

public class DrinkPanel extends JPanel {
    private JButton smallBtn, mediumBtn, largeBtn;
    private JButton iceBtn;
    private JButton sodaBtn, dietBtn, waterBtn;
    private JButton dispenseBtn;
    private JButton inventoryBtn;
    private JPanel visualizationPanel;
    private JPanel minigamePanel;
    private JLabel statusLabel;
    private JButton scrubBtn;
    private DrinkStation currentStation;

    public DrinkPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(40, 44, 52));

        // Left: Controls
        JPanel controlPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        controlPanel.setOpaque(false);
        controlPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        smallBtn = createStyledButton("Small Cup");
        mediumBtn = createStyledButton("Medium Cup");
        largeBtn = createStyledButton("Large Cup");
        iceBtn = createStyledButton("Add Ice");
        sodaBtn = createStyledButton("Soda");
        dietBtn = createStyledButton("Diet Soda");
        waterBtn = createStyledButton("Water");
        dispenseBtn = createStyledButton("DISPENSE (Hold)");
        inventoryBtn = createStyledButton("MOVE TO INVENTORY");
        
        controlPanel.add(smallBtn);
        controlPanel.add(mediumBtn);
        controlPanel.add(largeBtn);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(iceBtn);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(sodaBtn);
        controlPanel.add(dietBtn);
        controlPanel.add(waterBtn);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(dispenseBtn);
        controlPanel.add(inventoryBtn);

        // Center: Visualization
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        
        visualizationPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawSodaMachine(g);
            }
        };
        visualizationPanel.setOpaque(false);

        statusLabel = new JLabel("Choose a cup size", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        centerPanel.add(statusLabel, BorderLayout.NORTH);
        centerPanel.add(visualizationPanel, BorderLayout.CENTER);

        // Right: Cleanup Minigame
        minigamePanel = new JPanel(new GridBagLayout());
        minigamePanel.setPreferredSize(new Dimension(250, 0));
        minigamePanel.setBackground(new Color(33, 37, 43));
        minigamePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(244, 67, 54), 2), 
            " CLEANUP! ", 0, 0, 
            new Font("Segoe UI", Font.BOLD, 14), Color.WHITE));
        
        scrubBtn = new JButton("SCRUB!");
        scrubBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        scrubBtn.setBackground(new Color(255, 193, 7));
        scrubBtn.setForeground(Color.BLACK);
        scrubBtn.setFocusPainted(false);
        scrubBtn.setPreferredSize(new Dimension(120, 60));
        
        minigamePanel.add(scrubBtn);
        minigamePanel.setVisible(false);

        add(controlPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(minigamePanel, BorderLayout.EAST);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(new Color(60, 60, 70));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 110)),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void drawSodaMachine(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = visualizationPanel.getWidth();
        int h = visualizationPanel.getHeight();

        // 1. Draw Machine Head
        g2.setColor(new Color(80, 80, 90));
        g2.fillRoundRect(w/2 - 100, 20, 200, 60, 15, 15);
        g2.setColor(new Color(50, 50, 60));
        g2.fillRect(w/2 - 30, 80, 60, 20); // Nozzle

        if (currentStation == null) return;

        // 2. Draw Stream if dispensing
        if (currentStation.isDispensing() && currentStation.getDrinkType() != null) {
            g2.setColor(getDrinkColor(currentStation.getDrinkType()));
            g2.fillRect(w/2 - 8, 100, 16, h - 200);
        }

        // 3. Draw Cup
        DrinkStation.CupSize size = currentStation.getCurrentSize();
        if (size != null) {
            int cupW = 100 + (size == DrinkStation.CupSize.LARGE ? 40 : (size == DrinkStation.CupSize.MEDIUM ? 20 : 0));
            int cupH = 150 + (size == DrinkStation.CupSize.LARGE ? 50 : (size == DrinkStation.CupSize.MEDIUM ? 25 : 0));
            int cupX = w/2 - cupW/2;
            int cupY = h - cupH - 50;

            // Liquid
            if (currentStation.getFillLevel() > 0 && currentStation.getDrinkType() != null) {
                g2.setColor(getDrinkColor(currentStation.getDrinkType()));
                int fillH = (int)((currentStation.getFillLevel() / 120.0) * cupH);
                fillH = Math.min(fillH, cupH);
                g2.fillRect(cupX + 5, cupY + cupH - fillH, cupW - 10, fillH);
                
                if (currentStation.hasIce()) {
                    g2.setColor(new Color(255, 255, 255, 150));
                    for (int i = 0; i < 5; i++) {
                        g2.fillRect(cupX + 20 + i*15, cupY + cupH - 30, 10, 10);
                    }
                }
            }

            // Target Line (Green Range)
            int targetMinY = cupY + cupH - (int)((size.minFill / 120.0) * cupH);
            int targetMaxY = cupY + cupH - (int)((size.maxFill / 120.0) * cupH);
            g2.setColor(new Color(76, 175, 80, 150));
            g2.fillRect(cupX - 10, targetMaxY, cupW + 20, targetMinY - targetMaxY);
            g2.setColor(new Color(76, 175, 80));
            g2.setStroke(new BasicStroke(2));
            g2.drawRect(cupX - 10, targetMaxY, cupW + 20, targetMinY - targetMaxY);

            // Cup Outline
            g2.setColor(new Color(255, 255, 255, 200));
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(cupX, cupY, cupW, cupH, 10, 10);
            
            // Label on cup
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2.drawString(size.label.toUpperCase(), cupX + 10, cupY + 20);
        }
    }

    private Color getDrinkColor(String type) {
        if (type == null) return Color.GRAY;
        switch (type) {
            case "Soda": return new Color(60, 30, 10); // Cola brown
            case "Diet Soda": return new Color(80, 40, 20); // Lighter brown
            case "Water": return new Color(150, 200, 255, 180); // Translucent blue
            default: return Color.GRAY;
        }
    }

    public void update(DrinkStation station) {
        this.currentStation = station;
        if (station.isSpilled()) {
            statusLabel.setText("SPILL! CLEAN IT UP!");
            statusLabel.setForeground(new Color(244, 67, 54));
            minigamePanel.setVisible(true);
            dispenseBtn.setEnabled(false);
            smallBtn.setEnabled(false);
            mediumBtn.setEnabled(false);
            largeBtn.setEnabled(false);
        } else {
            minigamePanel.setVisible(false);
            smallBtn.setEnabled(true);
            mediumBtn.setEnabled(true);
            largeBtn.setEnabled(true);
            
            dispenseBtn.setEnabled(station.getDrinkType() != null);
            if (station.getCurrentSize() == null) {
                statusLabel.setText("Choose a cup size");
                statusLabel.setForeground(Color.WHITE);
            } else if (station.getDrinkType() == null) {
                statusLabel.setText("Choose a drink type");
                statusLabel.setForeground(Color.WHITE);
            } else {
                statusLabel.setText("Filling " + station.getCurrentSize().label + " " + station.getDrinkType());
                statusLabel.setForeground(station.isPerfectlyFilled() ? new Color(76, 175, 80) : Color.WHITE);
            }
        }

        inventoryBtn.setEnabled(station.isPerfectlyFilled() && !station.isSpilled());
        repaint();
    }

    public void setSizeListeners(ActionListener l) {
        smallBtn.addActionListener(l);
        mediumBtn.addActionListener(l);
        largeBtn.addActionListener(l);
    }

    public void setIceListener(ActionListener l) { iceBtn.addActionListener(l); }
    public void setDrinkTypeListeners(ActionListener l) {
        sodaBtn.addActionListener(l);
        dietBtn.addActionListener(l);
        waterBtn.addActionListener(l);
    }
    public void setDispenseListener(ActionListener l) { dispenseBtn.addActionListener(l); }
    public void setInventoryListener(ActionListener l) { inventoryBtn.addActionListener(l); }
    public void setScrubListener(ActionListener l) { scrubBtn.addActionListener(l); }
    
    public JButton getDispenseBtn() { return dispenseBtn; }
    public void setScrubButtonRandomLocation() {
        Random rand = new Random();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = rand.nextInt(3);
        gbc.gridy = rand.nextInt(3);
        minigamePanel.removeAll();
        minigamePanel.add(scrubBtn, gbc);
        minigamePanel.revalidate();
        minigamePanel.repaint();
    }
}
