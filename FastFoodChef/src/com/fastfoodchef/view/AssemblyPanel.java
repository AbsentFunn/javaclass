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

        // Left: Pantry (Unlimited Items)
        pantryPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        pantryPanel.setBorder(BorderFactory.createTitledBorder("Pantry"));
        String[] ingredients = {"Bun", "Cheese", "Lettuce", "Tomato", "Onion"};
        for (String ing : ingredients) {
            JButton btn = new JButton(ing);
            btn.setActionCommand("PANTRY:" + ing);
            pantryPanel.add(btn);
        }

        // Center: Warmer (Cooked Items)
        JPanel warmerContainer = new JPanel(new BorderLayout());
        warmerContainer.setBorder(BorderFactory.createTitledBorder("Warmer (Click to use)"));
        warmerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        warmerContainer.add(new JScrollPane(warmerPanel), BorderLayout.CENTER);

        // Right: Current Build & Order Info
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(300, 0));

        currentOrderLabel = new JLabel("<html><b>No active order</b></html>");
        currentOrderLabel.setBorder(BorderFactory.createTitledBorder("Current Order"));
        rightPanel.add(currentOrderLabel, BorderLayout.NORTH);

        // Split build view
        JPanel buildContainer = new JPanel(new GridLayout(3, 1));
        buildContainer.setBorder(BorderFactory.createTitledBorder("Current Build"));
        
        // Burger Section
        JPanel burgerSection = new JPanel(new BorderLayout());
        burgerSection.setBorder(BorderFactory.createTitledBorder("Burger"));
        burgerListModel = new DefaultListModel<>();
        burgerSection.add(new JScrollPane(new JList<>(burgerListModel)), BorderLayout.CENTER);
        
        // Side Section
        JPanel sideSection = new JPanel(new BorderLayout());
        sideSection.setBorder(BorderFactory.createTitledBorder("Sides"));
        sideListModel = new DefaultListModel<>();
        sideSection.add(new JScrollPane(new JList<>(sideListModel)), BorderLayout.CENTER);

        // Drink Section
        JPanel drinkSection = new JPanel(new BorderLayout());
        drinkSection.setBorder(BorderFactory.createTitledBorder("Drinks"));
        drinkListModel = new DefaultListModel<>();
        drinkSection.add(new JScrollPane(new JList<>(drinkListModel)), BorderLayout.CENTER);
        
        buildContainer.add(burgerSection);
        buildContainer.add(sideSection);
        buildContainer.add(drinkSection);

        JPanel controls = new JPanel(new GridLayout(2, 1, 5, 5));
        serveButton = new JButton("SERVE CUSTOMER");
        serveButton.setBackground(new Color(100, 200, 100));
        serveButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        trashButton = new JButton("RETURN BUILD");
        trashButton.setBackground(new Color(255, 100, 100));
        
        controls.add(serveButton);
        controls.add(trashButton);
        
        JPanel rightBottomPanel = new JPanel(new BorderLayout());
        rightBottomPanel.add(buildContainer, BorderLayout.CENTER);
        rightBottomPanel.add(controls, BorderLayout.SOUTH);
        
        rightPanel.add(rightBottomPanel, BorderLayout.CENTER);

        add(pantryPanel, BorderLayout.WEST);
        add(warmerContainer, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
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
                StringBuilder orderText = new StringBuilder("<html><b>Customer: " + currentCustomer.getName() + "</b><br>Order:<br>");
                for (String ing : currentCustomer.getOrderIngredients()) {
                    orderText.append("- ").append(ing).append("<br>");
                }
                orderText.append("</html>");
                currentOrderLabel.setText(orderText.toString());
            } else {
                currentOrderLabel.setText("<html><b>Customer waiting at counter...</b><br>(Accept order at Counter)</html>");
            }
        } else {
            currentOrderLabel.setText("<html><b>No active order</b></html>");
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
