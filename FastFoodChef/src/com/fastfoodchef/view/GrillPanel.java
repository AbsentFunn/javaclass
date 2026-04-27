package com.fastfoodchef.view;

import com.fastfoodchef.model.GrillStation;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class GrillPanel extends JPanel {
    private SlotUI[][] slotUIs;

    public GrillPanel() {
        setLayout(new GridLayout(2, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.DARK_GRAY);

        slotUIs = new SlotUI[2][2];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                slotUIs[i][j] = new SlotUI();
                add(slotUIs[i][j]);
            }
        }
    }

    public void update(GrillStation station) {
        GrillStation.GrillSlot[][] slots = station.getSlots();
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                slotUIs[i][j].update(slots[i][j]);
            }
        }
    }

    public void triggerFlip(int r, int c) {
        slotUIs[r][c].triggerFlip();
    }

    public void setSlotActionListener(int r, int c, ActionListener listener) {
        slotUIs[r][c].button.addActionListener(listener);
    }

    private static class SlotUI extends JPanel {
        JButton button;
        JProgressBar progress;
        JLabel statusLabel;
        PattyIcon pattyIcon;

        SlotUI() {
            setLayout(new BorderLayout());
            setBackground(new Color(60, 60, 60));
            setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

            statusLabel = new JLabel("Empty", SwingConstants.CENTER);
            statusLabel.setForeground(Color.WHITE);

            pattyIcon = new PattyIcon();
            JLabel iconLabel = new JLabel(pattyIcon);
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

            progress = new JProgressBar(0, 200);
            progress.setStringPainted(true);

            button = new JButton("Add Patty");

            add(statusLabel, BorderLayout.NORTH);
            
            JPanel centerPanel = new JPanel(new BorderLayout());
            centerPanel.setOpaque(false);
            centerPanel.add(iconLabel, BorderLayout.CENTER);
            centerPanel.add(progress, BorderLayout.SOUTH);
            
            add(centerPanel, BorderLayout.CENTER);
            add(button, BorderLayout.SOUTH);
        }

        void update(GrillStation.GrillSlot slot) {
            if (slot.getItem() == null) {
                statusLabel.setText("Empty");
                statusLabel.setForeground(Color.WHITE);
                progress.setValue(0);
                progress.setString("");
                button.setText("Add Patty");
                button.setEnabled(true);
                pattyIcon.setVisible(false);
                pattyIcon.setProgress(0, false);
                pattyIcon.setFlipped(false);
            } else {
                pattyIcon.setVisible(true);
                pattyIcon.setProgress(slot.getProgress(), slot.isBurned());
                progress.setValue((int)slot.getProgress());
                
                if (slot.isBurned()) {
                    statusLabel.setText("BURNED!");
                    statusLabel.setForeground(Color.RED);
                    button.setText("Trash");
                    button.setEnabled(true);
                } else if (slot.isReady()) {
                    statusLabel.setText("READY!");
                    statusLabel.setForeground(Color.GREEN);
                    button.setText("Remove");
                    button.setEnabled(true);
                } else if (!slot.isFlipped()) {
                    statusLabel.setText("Cooking Side 1...");
                    statusLabel.setForeground(Color.ORANGE);
                    button.setText("Flip");
                    // Enable button ONLY within the window
                    button.setEnabled(slot.getProgress() >= 45 && slot.getProgress() <= 65);
                } else {
                    statusLabel.setText("Cooking Side 2...");
                    statusLabel.setForeground(Color.YELLOW);
                    button.setText("Wait...");
                    button.setEnabled(false);
                }
                progress.setString((int)(slot.getProgress()/2) + "%");
            }
            repaint();
        }

        public void triggerFlip() {
            pattyIcon.startFlipAnimation();
        }
    }

    private static class PattyIcon implements Icon {
        private double progress = 0;
        private boolean burned = false;
        private boolean visible = false;
        private boolean flipped = false;
        private double flipScale = 1.0;
        private Timer flipTimer;

        public void setProgress(double progress, boolean burned) {
            this.progress = progress;
            this.burned = burned;
        }

        public void setVisible(boolean visible) { this.visible = visible; }
        public void setFlipped(boolean flipped) { this.flipped = flipped; }

        public void startFlipAnimation() {
            if (flipTimer != null && flipTimer.isRunning()) return;
            
            flipTimer = new Timer(16, null); // ~60 FPS
            final long startTime = System.currentTimeMillis();
            final long duration = 300; // ms

            flipTimer.addActionListener(e -> {
                long elapsed = System.currentTimeMillis() - startTime;
                if (elapsed >= duration) {
                    flipScale = 1.0;
                    flipped = true;
                    flipTimer.stop();
                } else {
                    // Cosine from 0 to PI makes a nice 1.0 -> -1.0 -> 1.0 transition
                    // We'll just do a scale 1.0 -> 0.0 -> 1.0
                    double phase = (elapsed / (double)duration) * Math.PI;
                    flipScale = Math.abs(Math.cos(phase));
                }
            });
            flipTimer.start();
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            if (!visible) return;
            Graphics2D g2 = (Graphics2D) g;
            int size = 10; // Pixel size
            
            // Apply scale for flip
            int totalWidth = 8 * size;
            int scaledWidth = (int)(totalWidth * flipScale);
            int startX = x + (getIconWidth() - scaledWidth) / 2;
            int startY = y + (getIconHeight() - (8 * size)) / 2;

            Color pattyColor;
            if (burned) {
                pattyColor = Color.BLACK;
            } else if (progress < 180) {
                // Phase 1: Raw pink (255,180,180) to Perfect Brown (101,67,33)
                double factor = progress / 180.0;
                int r = (int) (255 - (factor * (255 - 101)));
                int gr = (int) (180 - (factor * (180 - 67)));
                int b = (int) (180 - (factor * (180 - 33)));
                pattyColor = new Color(r, gr, b);
            } else {
                // Phase 2: Perfect Brown to Black (Burning)
                double factor = (progress - 180) / 20.0; 
                int r = (int) Math.max(0, 101 - (factor * 101));
                int gr = (int) Math.max(0, 67 - (factor * 67));
                int b = (int) Math.max(0, 33 - (factor * 33));
                pattyColor = new Color(r, gr, b);
            }

            g2.setColor(pattyColor);
            int[][] mask = {
                {0,0,1,1,1,1,0,0},
                {0,1,1,1,1,1,1,0},
                {1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1},
                {0,1,1,1,1,1,1,0},
                {0,0,1,1,1,1,0,0}
            };

            double pixelScale = flipScale;
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (mask[i][j] == 1) {
                        int px = startX + (int)(j * size * pixelScale);
                        int pW = (int)Math.ceil(size * pixelScale);
                        g2.fillRect(px, startY + (i * size), pW, size);
                    }
                }
            }
        }

        @Override public int getIconWidth() { return 100; }
        @Override public int getIconHeight() { return 100; }
    }
}
