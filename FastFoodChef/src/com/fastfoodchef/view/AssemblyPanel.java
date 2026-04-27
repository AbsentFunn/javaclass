package com.fastfoodchef.view;

import com.fastfoodchef.model.FoodItem;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class AssemblyPanel extends JPanel {
    private DefaultListModel<String> buildListModel;
    private JList<String> buildList;
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

        JPanel buildContainer = new JPanel(new BorderLayout());
        buildContainer.setBorder(BorderFactory.createTitledBorder("Current Build"));
        buildListModel = new DefaultListModel<>();
        buildList = new JList<>(buildListModel);
        buildContainer.add(new JScrollPane(buildList), BorderLayout.CENTER);

        JPanel controls = new JPanel(new GridLayout(2, 1, 5, 5));
        serveButton = new JButton("SERVE CUSTOMER");
        serveButton.setBackground(new Color(100, 200, 100));
        serveButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        trashButton = new JButton("TRASH BUILD");
        trashButton.setBackground(new Color(255, 100, 100));
        
        controls.add(serveButton);
        controls.add(trashButton);
        buildContainer.add(controls, BorderLayout.SOUTH);
        
        rightPanel.add(buildContainer, BorderLayout.CENTER);

        add(pantryPanel, BorderLayout.WEST);
        add(warmerContainer, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
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
        buildListModel.clear();
        for (String s : build) {
            buildListModel.addElement(s);
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
