package com.fastfoodchef.view;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private NavPanel navPanel;
    private HUDPanel hudPanel;
    private CounterPanel counterPanel;
    private GrillPanel grillPanel;
    private AssemblyPanel assemblyPanel;
    private FryerPanel fryerPanel;
    private DrinkPanel drinkPanel;

    public MainFrame() {
        setTitle("Fast Food Maker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        // Screens
        counterPanel = new CounterPanel();
        grillPanel = new GrillPanel();
        assemblyPanel = new AssemblyPanel();
        fryerPanel = new FryerPanel();
        drinkPanel = new DrinkPanel();
        
        mainContainer.add(counterPanel, "Counter");
        mainContainer.add(grillPanel, "Grill");
        mainContainer.add(fryerPanel, "Fryer");
        mainContainer.add(drinkPanel, "Drink");
        mainContainer.add(assemblyPanel, "Assembly");

        hudPanel = new HUDPanel();
        navPanel = new NavPanel();

        add(hudPanel, BorderLayout.NORTH);
        add(mainContainer, BorderLayout.CENTER);
        add(navPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }

    private JPanel createPlaceholderPanel(String text) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.add(new JLabel(text));
        return panel;
    }

    public void showScreen(String screenName) {
        cardLayout.show(mainContainer, screenName);
    }

    public NavPanel getNavPanel() {
        return navPanel;
    }

    public HUDPanel getHudPanel() {
        return hudPanel;
    }

    public CounterPanel getCounterPanel() {
        return counterPanel;
    }

    public GrillPanel getGrillPanel() {
        return grillPanel;
    }

    public AssemblyPanel getAssemblyPanel() {
        return assemblyPanel;
    }

    public FryerPanel getFryerPanel() {
        return fryerPanel;
    }

    public DrinkPanel getDrinkPanel() {
        return drinkPanel;
    }
}
