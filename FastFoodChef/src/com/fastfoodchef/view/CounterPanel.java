package com.fastfoodchef.view;

import com.fastfoodchef.model.Customer;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class CounterPanel extends JPanel {
    private CustomerVisual currentCustomerVisual;
    private JPanel queueDisplay;
    private TicketPanel ticketPanel;
    private JProgressBar patienceBar;
    private JLabel customerNameLabel;
    private JButton acceptButton;
    private JButton denyButton;
    private JPanel buttonPanel;

    public CounterPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(40, 44, 52)); // Darker, modern background

        // Main Counter Area (Current Customer)
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.VERTICAL;

        ticketPanel = new TicketPanel();
        
        currentCustomerVisual = new CustomerVisual(250, 300);
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        customerNameLabel = new JLabel("No Customer");
        customerNameLabel.setForeground(Color.WHITE);
        customerNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        customerNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        patienceBar = new JProgressBar(0, 100);
        patienceBar.setValue(100);
        patienceBar.setStringPainted(true);
        patienceBar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        patienceBar.setForeground(Color.GREEN);
        patienceBar.setMaximumSize(new Dimension(300, 25));
        patienceBar.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        acceptButton = createStyledButton("ACCEPT", new Color(76, 175, 80));
        denyButton = createStyledButton("DENY", new Color(244, 67, 54));
        buttonPanel.add(acceptButton);
        buttonPanel.add(denyButton);
        buttonPanel.setVisible(false);

        infoPanel.add(customerNameLabel);
        infoPanel.add(Box.createVerticalStrut(15));
        infoPanel.add(patienceBar);
        infoPanel.add(Box.createVerticalStrut(30));
        infoPanel.add(buttonPanel);

        // Ticket on the left
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridheight = 2;
        centerPanel.add(ticketPanel, gbc);

        // Customer and info on the right
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.gridheight = 1;
        centerPanel.add(currentCustomerVisual, gbc);

        gbc.gridy = 1;
        centerPanel.add(infoPanel, gbc);

        // Queue Area (People waiting)
        queueDisplay = new JPanel();
        queueDisplay.setPreferredSize(new Dimension(220, 0));
        queueDisplay.setBackground(new Color(33, 37, 43));
        queueDisplay.setBorder(BorderFactory.createMatteBorder(0, 2, 0, 0, new Color(60, 60, 60)));
        queueDisplay.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));

        JLabel queueHeader = new JLabel("QUEUE");
        queueHeader.setForeground(new Color(100, 100, 100));
        queueHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        queueHeader.setPreferredSize(new Dimension(200, 30));
        queueHeader.setHorizontalAlignment(SwingConstants.CENTER);
        queueDisplay.add(queueHeader);

        add(centerPanel, BorderLayout.CENTER);
        add(queueDisplay, BorderLayout.EAST);
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public void update(Customer current, List<Customer> queue, String time) {
        if (current == null) {
            customerNameLabel.setText("Waiting for customer...");
            ticketPanel.setData(null, "");
            patienceBar.setValue(0);
            buttonPanel.setVisible(false);
            currentCustomerVisual.setCustomer(null);
        } else {
            currentCustomerVisual.setCustomer(current);
            customerNameLabel.setText(current.getName());
            patienceBar.setValue(current.getPatience());
            buttonPanel.setVisible(!current.isAccepted());
            
            if (current.getPatience() > 60) patienceBar.setForeground(new Color(76, 175, 80));
            else if (current.getPatience() > 30) patienceBar.setForeground(new Color(255, 193, 7));
            else patienceBar.setForeground(new Color(244, 67, 54));

            ticketPanel.setData(current.getOrderIngredients(), time);
        }

        // Update queue visual
        queueDisplay.removeAll();
        for (int i = 1; i < queue.size(); i++) {
            Customer c = queue.get(i);
            CustomerVisual mini = new CustomerVisual(60, 80);
            mini.setCustomer(c);
            queueDisplay.add(mini);
        }
        queueDisplay.revalidate();
        queueDisplay.repaint();
    }

    // Ticket visualization
    private static class TicketPanel extends JPanel {
        private List<String> items;
        private String orderTime = "";
        private final Color TICKET_COLOR = new Color(252, 252, 240);
        private final Color TEXT_COLOR = new Color(30, 30, 30);

        public TicketPanel() {
            setPreferredSize(new Dimension(180, 320));
            setOpaque(false);
        }

        public void setData(List<String> items, String time) {
            this.items = items;
            this.orderTime = time;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (items == null || items.isEmpty()) return;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Shadow
            g2.setColor(new Color(0, 0, 0, 60));
            g2.fillRoundRect(15, 15, getWidth() - 20, getHeight() - 20, 5, 5);

            // Paper
            g2.setColor(TICKET_COLOR);
            g2.fillRoundRect(10, 10, getWidth() - 20, getHeight() - 20, 5, 5);

            // Header bar
            g2.setColor(new Color(220, 60, 60));
            g2.fillRoundRect(10, 10, getWidth() - 20, 40, 5, 5);
            g2.fillRect(10, 30, getWidth() - 20, 20); // Square bottom for header

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
            g2.drawString("ORDER", 25, 38);

            // Date/Time mockup
            g2.setFont(new Font("Consolas", Font.PLAIN, 10));
            g2.setColor(new Color(255, 255, 255, 180));
            g2.drawString(orderTime, getWidth() - 85, 35);

            // Items
            g2.setColor(TEXT_COLOR);
            int y = 70;
            
            // Separate into Burger, Sides, Drinks for grouped display
            List<String> burgerParts = new ArrayList<>();
            List<String> sides = new ArrayList<>();
            List<String> drinks = new ArrayList<>();
            
            for (String item : items) {
                if (item.contains("Small") || item.contains("Medium") || item.contains("Large")) drinks.add(item);
                else if (item.equals("Fries") || item.equals("Onion Rings") || item.equals("Cheese Curds")) sides.add(item);
                else burgerParts.add(item);
            }

            g2.setFont(new Font("Consolas", Font.BOLD, 12));
            
            // Burger Header
            if (!burgerParts.isEmpty()) {
                g2.drawString("BURGER:", 20, y);
                y += 18;
                g2.setFont(new Font("Consolas", Font.PLAIN, 11));
                for (String p : burgerParts) {
                    g2.drawString(" \u2022 " + p.toUpperCase(), 25, y);
                    y += 15;
                }
                y += 5;
            }

            // Sides Header
            if (!sides.isEmpty()) {
                g2.setFont(new Font("Consolas", Font.BOLD, 12));
                g2.drawString("SIDES:", 20, y);
                y += 18;
                g2.setFont(new Font("Consolas", Font.PLAIN, 11));
                for (String s : sides) {
                    g2.drawString(" \u2022 " + s.toUpperCase(), 25, y);
                    y += 15;
                }
                y += 5;
            }

            // Drinks Header
            if (!drinks.isEmpty()) {
                g2.setFont(new Font("Consolas", Font.BOLD, 12));
                g2.drawString("DRINK:", 20, y);
                y += 18;
                g2.setFont(new Font("Consolas", Font.PLAIN, 11));
                for (String d : drinks) {
                    // "Large Soda (Ice)" -> parse
                    String size = d.contains("Small") ? "SMALL" : (d.contains("Medium") ? "MEDIUM" : "LARGE");
                    String ice = d.contains("(Ice)") ? "YES" : "NO";
                    String type = d.replace(size, "").replace("(Ice)", "").trim().replace("Small", "").replace("Medium", "").replace("Large", "");
                    if (type.isEmpty()) type = "SODA"; // Fallback

                    g2.drawString(" \u2022 TYPE: " + type.toUpperCase(), 25, y); y += 15;
                    g2.drawString("   SIZE: " + size, 25, y); y += 15;
                    g2.drawString("   ICE:  " + ice, 25, y); y += 15;
                }
            }

            // Footer
            g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4}, 0));
            g2.setColor(new Color(0, 0, 0, 50));
            g2.drawLine(20, getHeight() - 40, getWidth() - 20, getHeight() - 40);
            
            g2.setFont(new Font("Consolas", Font.ITALIC, 12));
            g2.drawString("GRUB'S UP!", 30, getHeight() - 20);
        }
    }

    // Inner class for drawing the customer
    private static class CustomerVisual extends JPanel {
        private Customer customer;
        private int width, height;

        public CustomerVisual(int w, int h) {
            this.width = w;
            this.height = h;
            setPreferredSize(new Dimension(w, h));
            setOpaque(false);
        }

        public void setCustomer(Customer c) {
            this.customer = c;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (customer == null) return;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            float scaleX = getWidth() / 200.0f;
            float scaleY = getHeight() / 250.0f;
            float scale = Math.min(scaleX, scaleY);

            int centerX = getWidth() / 2;
            int baseY = getHeight() - (int)(10 * scale);
            int headSize = (int)(80 * scale);
            int bodyW = (int)(120 * scale);
            int bodyH = (int)(100 * scale);
            int headY = baseY - bodyH - headSize + (int)(5 * scale);

            // 1. Hair Back (for long hair)
            if (customer.getHairStyle() == 2) {
                g2.setColor(customer.getHairColor());
                int hairW = headSize + (int)(20 * scale);
                int hairH = headSize + (int)(40 * scale);
                g2.fillRoundRect(centerX - hairW/2, headY, hairW, hairH, (int)(30 * scale), (int)(30 * scale));
            }

            // 2. Shirt/Body
            g2.setColor(customer.getShirtColor());
            g2.fillRoundRect(centerX - bodyW/2, baseY - bodyH, bodyW, bodyH, (int)(20 * scale), (int)(20 * scale));

            // 3. Neck
            g2.setColor(customer.getSkinColor().darker());
            int neckW = (int)(30 * scale);
            int neckH = (int)(20 * scale);
            g2.fillRect(centerX - neckW/2, baseY - bodyH - neckH/2, neckW, neckH);

            // 4. Head (Skin)
            g2.setColor(customer.getSkinColor());
            g2.fillOval(centerX - headSize/2, headY, headSize, headSize);

            // 5. Facial Features
            // Eyes
            g2.setColor(Color.WHITE);
            int eyeSize = (int)(12 * scale);
            int eyeXOffset = (int)(18 * scale);
            int eyeY = headY + (int)(30 * scale);
            g2.fillOval(centerX - eyeXOffset - eyeSize/2, eyeY, eyeSize, eyeSize);
            g2.fillOval(centerX + eyeXOffset - eyeSize/2, eyeY, eyeSize, eyeSize);
            
            g2.setColor(Color.BLACK);
            int pupilSize = (int)(5 * scale);
            int pupilY = eyeY + (int)(4 * scale);
            g2.fillOval(centerX - eyeXOffset - pupilSize/2, pupilY, pupilSize, pupilSize);
            g2.fillOval(centerX + eyeXOffset - pupilSize/2, pupilY, pupilSize, pupilSize);

            // Mouth
            g2.setColor(new Color(150, 50, 50));
            int mouthW = (int)(25 * scale);
            int mouthH = (int)(5 * scale);
            int mouthY = headY + (int)(50 * scale);
            if (customer.getPatience() > 50) {
                g2.drawArc(centerX - mouthW/2, mouthY, mouthW, (int)(mouthH * 2 * scale), 0, -180);
            } else {
                g2.drawLine(centerX - mouthW/2, mouthY + (int)(5 * scale), centerX + mouthW/2, mouthY + (int)(5 * scale));
            }

            // 6. Hair Top/Bangs
            g2.setColor(customer.getHairColor());
            int style = customer.getHairStyle();
            if (style == 1 || style == 2) { // Short or Bangs for long hair
                g2.fillArc(centerX - headSize/2 - (int)(2 * scale), headY - (int)(5 * scale), headSize + (int)(4 * scale), (int)(40 * scale), 0, 180);
                
                // Extra bit for style 1 to make it look like "hair" rather than a cap
                if (style == 1) {
                    g2.fillRoundRect(centerX - headSize/2, headY, headSize, (int)(15 * scale), (int)(10 * scale), (int)(10 * scale));
                }
            }
        }
    }

    public JButton getAcceptButton() { return acceptButton; }
    public JButton getDenyButton() { return denyButton; }
}
