package com.fastfoodchef.view;

import com.fastfoodchef.model.FoodItem;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class AssemblyPanel extends JPanel {
    private DefaultListModel<String> burgerListModel;
    private DefaultListModel<String> sideListModel;
    private DefaultListModel<String> drinkListModel;
    private JPanel pantryPanel;
    private JPanel warmerPanel;
    private JButton serveButton;
    private JButton trashButton;
    private JLabel currentOrderLabel;

    public AssemblyPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(40, 44, 52));

        // Left: Pantry (Unlimited Items)
        pantryPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        pantryPanel.setBackground(new Color(33, 37, 43));
        pantryPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)), 
            " PANTRY ", 0, 0, 
            new Font("Segoe UI", Font.BOLD, 12), Color.WHITE));
        
        String[] ingredients = {"Bun", "Cheese", "Lettuce", "Tomato", "Onion"};
        for (String ing : ingredients) {
            pantryPanel.add(createPantryButton(ing));
        }

        // Center: Warmer (Cooked Items)
        JPanel warmerContainer = new JPanel(new BorderLayout());
        warmerContainer.setOpaque(false);
        warmerContainer.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)), 
            " WARMER (CLICK TO USE) ", 0, 0, 
            new Font("Segoe UI", Font.BOLD, 12), Color.WHITE));
        
        warmerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        warmerPanel.setBackground(new Color(40, 44, 52));
        
        JScrollPane warmerScroll = new JScrollPane(warmerPanel);
        warmerScroll.setBorder(null);
        warmerScroll.setOpaque(false);
        warmerScroll.getViewport().setOpaque(false);
        warmerContainer.add(warmerScroll, BorderLayout.CENTER);

        // Right: Current Build & Order Info
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(300, 0));
        rightPanel.setBackground(new Color(33, 37, 43));
        rightPanel.setBorder(BorderFactory.createMatteBorder(0, 2, 0, 0, new Color(60, 60, 60)));

        currentOrderLabel = new JLabel("<html><b>No active order</b></html>");
        currentOrderLabel.setForeground(Color.WHITE);
        currentOrderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        currentOrderLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        rightPanel.add(currentOrderLabel, BorderLayout.NORTH);

        // Split build view
        JPanel buildContainer = new JPanel(new GridLayout(3, 1, 0, 10));
        buildContainer.setOpaque(false);
        buildContainer.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        
        // Burger Section
        burgerListModel = new DefaultListModel<>();
        buildContainer.add(createBuildSection("BURGER", burgerListModel));
        
        // Side Section
        sideListModel = new DefaultListModel<>();
        buildContainer.add(createBuildSection("SIDES", sideListModel));

        // Drink Section
        drinkListModel = new DefaultListModel<>();
        buildContainer.add(createBuildSection("DRINKS", drinkListModel));
        
        JPanel controls = new JPanel(new GridLayout(2, 1, 5, 5));
        controls.setOpaque(false);
        controls.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        serveButton = createStyledButton("SERVE CUSTOMER", new Color(76, 175, 80));
        trashButton = createStyledButton("RETURN BUILD", new Color(244, 67, 54));
        
        controls.add(serveButton);
        controls.add(trashButton);
        
        JPanel rightBottomPanel = new JPanel(new BorderLayout());
        rightBottomPanel.setOpaque(false);
        rightBottomPanel.add(buildContainer, BorderLayout.CENTER);
        rightBottomPanel.add(controls, BorderLayout.SOUTH);
        
        rightPanel.add(rightBottomPanel, BorderLayout.CENTER);

        add(pantryPanel, BorderLayout.WEST);
        add(warmerContainer, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }

    private JButton createPantryButton(String text) {
        JButton btn = new JButton(text);
        btn.setActionCommand("PANTRY:" + text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(new Color(60, 60, 70));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel createBuildSection(String title, DefaultListModel<String> model) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)), 
            " " + title + " ", 0, 0, 
            new Font("Segoe UI", Font.BOLD, 10), new Color(150, 150, 150)));
        
        JList<String> list = new JList<>(model);
        list.setBackground(new Color(40, 44, 52));
        list.setForeground(Color.WHITE);
        list.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(null);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private boolean isSide(String item) {
        return item.equals("Fries") || item.equals("Onion Rings") || item.equals("Cheese Curds");
    }

    private boolean isDrink(String item) {
        return item.contains("Small") || item.contains("Medium") || item.contains("Large");
    }

    public void update(List<FoodItem> warmer, List<String> build, com.fastfoodchef.model.Customer currentCustomer, ActionListener warmerListener) {
        // Update Warmer
        warmerPanel.removeAll();
        for (int i = 0; i < warmer.size(); i++) {
            FoodItem item = warmer.get(i);
            JButton btn = new JButton(item.getName());
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            btn.setBackground(new Color(60, 60, 70));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 90)),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
            ));
            btn.setActionCommand("WARMER:" + i);
            btn.addActionListener(warmerListener);
            warmerPanel.add(btn);
        }
        warmerPanel.revalidate();
        warmerPanel.repaint();

        // Update Build
        burgerListModel.clear();
        sideListModel.clear();
        drinkListModel.clear();
        for (String s : build) {
            if (isDrink(s)) {
                drinkListModel.addElement(s);
            } else if (isSide(s)) {
                sideListModel.addElement(s);
            } else {
                burgerListModel.addElement(s);
            }
        }

        // Update Order Info
        if (currentCustomer != null) {
            if (currentCustomer.isAccepted()) {
                StringBuilder orderText = new StringBuilder("<html><b>CUSTOMER: " + currentCustomer.getName().toUpperCase() + "</b><br><br>ORDER:<br>");
                for (String ing : currentCustomer.getOrderIngredients()) {
                    orderText.append("- ").append(ing.toUpperCase()).append("<br>");
                }
                orderText.append("</html>");
                currentOrderLabel.setText(orderText.toString());
            } else {
                currentOrderLabel.setText("<html><b style='color: #FFC107'>CUSTOMER WAITING AT COUNTER</b><br><br>(Accept order at Counter)</html>");
            }
        } else {
            currentOrderLabel.setText("<html><b>NO ACTIVE ORDER</b></html>");
        }
    }

    public void setPantryActionListener(ActionListener listener) {
        for (Component c : pantryPanel.getComponents()) {
            if (c instanceof JButton) {
                ((JButton) c).addActionListener(listener);
            }
        }
    }

    public JButton getServeButton() { return serveButton; }
    public JButton getTrashButton() { return trashButton; }
}
