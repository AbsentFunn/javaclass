package com.fastfoodchef.model;

import java.time.LocalTime;

public class GameState {
    private double revenue;
    private double dailyRating;
    private LocalTime gameTime;
    private String activeScreen;
    private GrillStation grill;
    private FryerStation fryer;
    private DrinkStation drink;
    private java.util.LinkedList<Customer> customerQueue;
    private java.util.List<FoodItem> warmer;
    private java.util.List<String> currentBuild;

    public GameState() {
        this.revenue = 0.0;
        this.dailyRating = 5.0; // Start with a perfect rating
        this.gameTime = LocalTime.of(8, 0); // 8:00 AM
        this.activeScreen = "Counter";
        this.grill = new GrillStation();
        this.fryer = new FryerStation();
        this.drink = new DrinkStation();
        this.customerQueue = new java.util.LinkedList<>();
        this.warmer = new java.util.ArrayList<>();
        this.currentBuild = new java.util.ArrayList<>();
    }

    public java.util.List<FoodItem> getWarmer() {
        return warmer;
    }

    public void addToWarmer(FoodItem item) {
        if (warmer.size() < 30) {
            warmer.add(item);
        }
    }

    public java.util.List<String> getCurrentBuild() {
        return currentBuild;
    }

    public void addToBuild(String ingredient) {
        currentBuild.add(ingredient);
    }

    public void clearBuild() {
        currentBuild.clear();
    }

    public java.util.LinkedList<Customer> getCustomerQueue() {
        return customerQueue;
    }

    public void addCustomer(Customer customer) {
        if (customerQueue.size() < 5) { // Limit queue size for visual simplicity
            customerQueue.add(customer);
            com.fastfoodchef.view.ToastManager.showToast("New Customer: " + customer.getName(), 3);
        }
    }

    public Customer getCurrentCustomer() {
        return customerQueue.isEmpty() ? null : customerQueue.peek();
    }

    public void serveCustomer() {
        if (!customerQueue.isEmpty()) {
            customerQueue.poll();
        }
    }

    public GrillStation getGrill() { return grill; }
    public FryerStation getFryer() { return fryer; }
    public DrinkStation getDrink() { return drink; }

    public double getRevenue() {
        return revenue;
    }

    public void addRevenue(double amount) {
        this.revenue += amount;
    }

    public double getDailyRating() {
        return dailyRating;
    }

    public void setDailyRating(double dailyRating) {
        this.dailyRating = dailyRating;
    }

    public LocalTime getGameTime() {
        return gameTime;
    }

    public void advanceTime(int minutes) {
        this.gameTime = this.gameTime.plusMinutes(minutes);
    }

    public String getActiveScreen() {
        return activeScreen;
    }

    public void setActiveScreen(String activeScreen) {
        this.activeScreen = activeScreen;
    }
}
