package com.fastfoodchef.model;

import java.awt.Color;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Customer {
    private List<String> orderIngredients;
    private int patience; // 0 to 100
    private String name;
    private boolean accepted;
    private String arrivalTime = "";
    private LocalTime arrivalLocalTime;

    // Visual attributes
    private Color skinColor;
    private Color hairColor;
    private Color shirtColor;
    private int hairStyle; // 0: bald, 1: short, 2: long

    private static final Color[] SKIN_TONES = {
        new Color(255, 219, 172), new Color(241, 194, 125),
        new Color(224, 172, 105), new Color(198, 134, 66),
        new Color(141, 85, 36)
    };
    private static final Color[] HAIR_COLORS = {
        new Color(43, 29, 20), new Color(102, 51, 0),
        new Color(190, 150, 80), new Color(165, 42, 42),
        new Color(50, 50, 50)
    };
    private static final Color[] SHIRT_COLORS = {
        Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE,
        Color.MAGENTA, Color.CYAN, new Color(100, 100, 255)
    };

    public Customer(String name) {
        this.name = name;
        this.patience = 100;
        this.accepted = false;
        this.orderIngredients = generateRandomOrder();

        Random rand = new Random();
        this.skinColor = SKIN_TONES[rand.nextInt(SKIN_TONES.length)];
        this.hairColor = HAIR_COLORS[rand.nextInt(HAIR_COLORS.length)];
        this.shirtColor = SHIRT_COLORS[rand.nextInt(SHIRT_COLORS.length)];
        this.hairStyle = rand.nextInt(3);
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
        String[] possibleSides = {"Fries", "Onion Rings", "Cheese Curds"};
        String[] drinkSizes = {"Small", "Medium", "Large"};
        String[] drinkTypes = {"Soda", "Diet Soda", "Water"};
        Random rand = new Random();
        
        // 1. Bottom Bun
        order.add("Bun");
        
        // 2. Patty
        order.add("Patty");
        
        // 3. 1-3 Random Toppings
        int toppingCount = 1 + rand.nextInt(3);
        java.util.Collections.addAll(order, getRandomToppings(possibleToppings, toppingCount));
        
        // 4. Top Bun
        order.add("Bun");

        // 5. Optional Side (50% chance)
        if (rand.nextBoolean()) {
            order.add(possibleSides[rand.nextInt(possibleSides.length)]);
        }

        // 6. Optional Drink (70% chance)
        if (rand.nextDouble() < 0.7) {
            String drink = drinkSizes[rand.nextInt(drinkSizes.length)] + " " + drinkTypes[rand.nextInt(drinkTypes.length)];
            if (rand.nextBoolean()) drink += " (Ice)";
            order.add(drink);
        }
        
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
    public String getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(String time) { this.arrivalTime = time; }
    public LocalTime getArrivalLocalTime() { return arrivalLocalTime; }
    public void setArrivalLocalTime(LocalTime time) { this.arrivalLocalTime = time; }
    public Color getSkinColor() { return skinColor; }
    public Color getHairColor() { return hairColor; }
    public Color getShirtColor() { return shirtColor; }
    public int getHairStyle() { return hairStyle; }
}
