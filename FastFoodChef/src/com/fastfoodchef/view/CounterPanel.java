package com.fastfoodchef.view;

import com.fastfoodchef.model.Customer;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CounterPanel extends JPanel {
    private JPanel customerDisplay;
    private JPanel queueDisplay;
    private JLabel orderLabel;
    private JProgressBar patienceBar;
    private JLabel customerNameLabel;
    private JButton acceptButton;
    private JButton denyButton;
    private JPanel buttonPanel;

    public CounterPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 230, 210)); // Warm background

        // Main Counter Area (Current Customer)
        customerDisplay = new JPanel();
        customerDisplay.setOpaque(false);
        customerDisplay.setLayout(new BoxLayout(customerDisplay, BoxLayout.Y_AXIS));
        customerDisplay.setBorder(BorderFactory.createTitledBorder("Current Customer"));

        customerNameLabel = new JLabel("No Customer");
        customerNameLabel.setFont(new Font("Arial", Font.BOLD, 24));
        customerNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        patienceBar = new JProgressBar(0, 100);
        patienceBar.setValue(100);
        patienceBar.setForeground(Color.GREEN);
        patienceBar.setMaximumSize(new Dimension(300, 20));
        patienceBar.setAlignmentX(Component.CENTER_ALIGNMENT);

        orderLabel = new JLabel("<html><center>Order will appear here</center></html>");
        orderLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        orderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        orderLabel.setPreferredSize(new Dimension(400, 100));

        buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        acceptButton = new JButton("ACCEPT ORDER");
        acceptButton.setBackground(new Color(100, 255, 100));
        denyButton = new JButton("DENY ORDER");
        denyButton.setBackground(new Color(255, 100, 100));
        buttonPanel.add(acceptButton);
        buttonPanel.add(denyButton);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.setVisible(false);

        customerDisplay.add(Box.createVerticalStrut(20));
        customerDisplay.add(customerNameLabel);
        customerDisplay.add(Box.createVerticalStrut(10));
        customerDisplay.add(patienceBar);
        customerDisplay.add(Box.createVerticalStrut(20));
        customerDisplay.add(orderLabel);
        customerDisplay.add(Box.createVerticalStrut(20));
        customerDisplay.add(buttonPanel);

        // Queue Area (People waiting)
        queueDisplay = new JPanel();
        queueDisplay.setPreferredSize(new Dimension(200, 0));
        queueDisplay.setBorder(BorderFactory.createTitledBorder("Queue"));
        queueDisplay.setLayout(new FlowLayout());

        add(customerDisplay, BorderLayout.CENTER);
        add(queueDisplay, BorderLayout.EAST);
    }

    public void update(Customer current, List<Customer> queue) {
        if (current == null) {
            customerNameLabel.setText("Waiting for customer...");
            orderLabel.setText("");
            patienceBar.setValue(0);
            buttonPanel.setVisible(false);
        } else {
            customerNameLabel.setText(current.getName());
            patienceBar.setValue(current.getPatience());
            buttonPanel.setVisible(!current.isAccepted());
            // Update patience color
            if (current.getPatience() > 60) patienceBar.setForeground(Color.GREEN);
            else if (current.getPatience() > 30) patienceBar.setForeground(Color.ORANGE);
            else patienceBar.setForeground(Color.RED);

            StringBuilder orderText = new StringBuilder("<html><center><b>Order:</b><br>");
            for (String ingredient : current.getOrderIngredients()) {
                orderText.append("- ").append(ingredient).append("<br>");
            }
            orderText.append("</center></html>");
            orderLabel.setText(orderText.toString());
        }

        // Update queue visual
        queueDisplay.removeAll();
        for (int i = 1; i < queue.size(); i++) {
            JLabel person = new JLabel("👤");
            person.setFont(new Font("Arial", Font.PLAIN, 30));
            queueDisplay.add(person);
        }
        queueDisplay.revalidate();
        queueDisplay.repaint();
    }

    public JButton getAcceptButton() { return acceptButton; }
    public JButton getDenyButton() { return denyButton; }
}
