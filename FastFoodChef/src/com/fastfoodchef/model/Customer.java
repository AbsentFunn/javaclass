package com.fastfoodchef.model;

import java.util.ArrayList;
import java.util.List;

public class Customer {
    private List<String> orderIngredients;
    private int patience; // 0 to 100
    private String name;
    private boolean accepted;

    public Customer(String name) {
        this.name = name;
        this.patience = 100;
        this.accepted = false;
        this.orderIngredients = generateRandomOrder();
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    private List<String> generateRandomOrder() {
        List<String> order = new ArrayList<>();
        String[] possibleToppings = {"Cheese", "Lettuce", "Tomato", "Onion"};
        
        // 1. Bottom Bun
        order.add("Bun");
        
        // 2. Patty
        order.add("Patty");
        
        // 3. 1-3 Random Toppings
        int toppingCount = 1 + (int)(Math.random() * 3);
        java.util.Collections.addAll(order, getRandomToppings(possibleToppings, toppingCount));
        
        // 4. Top Bun
        order.add("Bun");
        
        return order;
    }

    private String[] getRandomToppings(String[] pool, int count) {
        String[] selected = new String[count];
        for (int i = 0; i < count; i++) {
            selected[i] = pool[(int)(Math.random() * pool.length)];
        }
        return selected;
    }

    public void reducePatience(int amount) {
        this.patience -= amount;
        if (this.patience < 0) this.patience = 0;
    }

    public List<String> getOrderIngredients() { return orderIngredients; }
    public int getPatience() { return patience; }
    public String getName() { return name; }
}
