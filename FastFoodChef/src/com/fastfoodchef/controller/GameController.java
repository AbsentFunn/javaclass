package com.fastfoodchef.controller;

import com.fastfoodchef.model.Customer;
import com.fastfoodchef.model.FoodItem;
import com.fastfoodchef.model.GameState;
import com.fastfoodchef.model.GrillStation;
import com.fastfoodchef.model.FryerStation;
import com.fastfoodchef.view.MainFrame;

import com.fastfoodchef.model.DrinkStation;
import com.fastfoodchef.view.DrinkPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class GameController {
    private GameState model;
    private MainFrame view;
    private Timer gameLoop;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");
    private Random random = new Random();
    private int tickCount = 0;

    public GameController(GameState model, MainFrame view) {
        this.model = model;
        this.view = view;

        initNavigation();
        initGrillActions();
        initAssemblyActions();
        initCounterActions();
        initFryerActions();
        initDrinkActions();
        initGameLoop();
    }

    private void initDrinkActions() {
        DrinkStation ds = model.getDrink();
        DrinkPanel dp = view.getDrinkPanel();

        dp.setSizeListeners(e -> {
            String cmd = e.getActionCommand();
            if (cmd.contains("Small")) ds.startNewDrink(DrinkStation.CupSize.SMALL);
            else if (cmd.contains("Medium")) ds.startNewDrink(DrinkStation.CupSize.MEDIUM);
            else if (cmd.contains("Large")) ds.startNewDrink(DrinkStation.CupSize.LARGE);
            dp.update(ds);
        });

        dp.setIceListener(e -> {
            if (ds.getCurrentSize() != null) {
                ds.setHasIce(true);
                dp.update(ds);
            }
        });

        dp.setDrinkTypeListeners(e -> {
            if (ds.getCurrentSize() != null) {
                ds.setDrinkType(e.getActionCommand());
                dp.update(ds);
            }
        });

        dp.getDispenseBtn().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (ds.getDrinkType() != null && !ds.isSpilled()) ds.setDispensing(true);
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                ds.setDispensing(false);
            }
        });

        dp.setInventoryListener(e -> {
            if (ds.isPerfectlyFilled()) {
                String name = ds.getCurrentSize().label + " " + ds.getDrinkType() + (ds.hasIce() ? " (Ice)" : "");
                model.addToWarmer(new FoodItem(name, model.getGameTime()));
                ds.reset();
                dp.update(ds);
            }
        });

        dp.setScrubListener(e -> {
            long now = System.currentTimeMillis();
            if (ds.getScrubCount() == 0) {
                ds.setLastScrubTime(now);
            }
            
            if (now - ds.getLastScrubTime() > 2000) {
                ds.setScrubCount(0); // Failed speed check
            } else {
                ds.setScrubCount(ds.getScrubCount() + 1);
            }
            ds.setLastScrubTime(now);

            if (ds.getScrubCount() >= 5) {
                ds.reset();
            } else {
                dp.setScrubButtonRandomLocation();
            }
            dp.update(ds);
        });
    }

    private void initFryerActions() {
        view.getFryerPanel().setIngredientListener(e -> {
            String cmd = e.getActionCommand();
            String itemName = "";
            if (cmd.contains("Fries")) itemName = "Fries";
            else if (cmd.contains("Rings")) itemName = "Onion Rings";
            else if (cmd.contains("Curds")) itemName = "Cheese Curds";

            if (!itemName.isEmpty()) {
                // Find empty basket
                for (FryerStation.FryerBasket basket : model.getFryer().getBaskets()) {
                    if (basket.getItem() == null) {
                        basket.addItem(new FoodItem(itemName, model.getGameTime()));
                        break;
                    }
                }
                view.getFryerPanel().update(model.getFryer());
            }
        });

        for (int i = 0; i < 3; i++) {
            final int index = i;
            view.getFryerPanel().setBasketListener(i, e -> {
                FryerStation.FryerBasket basket = model.getFryer().getBaskets().get(index);
                if (basket.getItem() != null) {
                    if (basket.isRuined()) {
                        basket.removeItem();
                    } else if (basket.isReady()) {
                        model.addToWarmer(basket.removeItem());
                    } else {
                        basket.shake();
                    }
                    view.getFryerPanel().update(model.getFryer());
                }
            });
        }
    }

    private void initCounterActions() {
        view.getCounterPanel().getAcceptButton().addActionListener(e -> {
            Customer current = model.getCurrentCustomer();
            if (current != null) {
                current.setAccepted(true);
                view.getCounterPanel().update(current, model.getCustomerQueue(), current.getArrivalTime());
            }
        });

        view.getCounterPanel().getDenyButton().addActionListener(e -> {
            Customer current = model.getCurrentCustomer();
            if (current != null) {
                model.serveCustomer(); // Remove from queue
                model.setDailyRating(Math.max(0, model.getDailyRating() - 0.3)); // Penalty
                Customer next = model.getCurrentCustomer();
                view.getCounterPanel().update(next, model.getCustomerQueue(), (next != null) ? next.getArrivalTime() : "");
            }
        });
    }

    private void initAssemblyActions() {
        view.getAssemblyPanel().setPantryActionListener(e -> {
            String command = e.getActionCommand();
            if (command.startsWith("PANTRY:")) {
                String ingredient = command.substring(7);
                model.addToBuild(ingredient);
                updateAssemblyView();
            }
        });

        view.getAssemblyPanel().getTrashButton().addActionListener(e -> {
            java.util.List<String> build = model.getCurrentBuild();
            for (String item : build) {
                // Return cooked items and drinks to warmer, trash pantry items
                if (isSide(item) || isDrink(item) || item.equals("Patty")) {
                    model.addToWarmer(new FoodItem(item, model.getGameTime()));
                }
            }
            model.clearBuild();
            updateAssemblyView();
        });

        view.getAssemblyPanel().getServeButton().addActionListener(e -> {
            handleServe();
        });
    }

    private boolean isSide(String item) {
        return item.equals("Fries") || item.equals("Onion Rings") || item.equals("Cheese Curds");
    }

    private boolean isDrink(String item) {
        return item.contains("Small") || item.contains("Medium") || item.contains("Large");
    }

    private void handleServe() {
        Customer current = model.getCurrentCustomer();
        if (current == null || !current.isAccepted()) return;

        java.util.List<String> build = model.getCurrentBuild();
        java.util.List<String> order = current.getOrderIngredients();

        // Split order and build
        java.util.List<String> orderBurger = new java.util.ArrayList<>();
        java.util.List<String> orderSides = new java.util.ArrayList<>();
        java.util.List<String> orderDrinks = new java.util.ArrayList<>();
        for (String s : order) {
            if (isDrink(s)) orderDrinks.add(s);
            else if (isSide(s)) orderSides.add(s);
            else orderBurger.add(s);
        }

        java.util.List<String> buildBurger = new java.util.ArrayList<>();
        java.util.List<String> buildSides = new java.util.ArrayList<>();
        java.util.List<String> buildDrinks = new java.util.ArrayList<>();
        for (String s : build) {
            if (isDrink(s)) buildDrinks.add(s);
            else if (isSide(s)) buildSides.add(s);
            else buildBurger.add(s);
        }

        // Validation
        boolean burgerCorrect = validateSection(orderBurger, buildBurger);
        boolean sidesCorrect = validateSection(orderSides, buildSides);
        boolean drinksCorrect = validateSection(orderDrinks, buildDrinks);
        
        if (burgerCorrect && sidesCorrect && drinksCorrect) {
            double basePay = 10.0 + (orderSides.size() * 5.0) + (orderDrinks.size() * 3.0);
            double tip = current.getPatience() / 10.0;
            model.addRevenue(basePay + tip);
            model.setDailyRating(Math.min(5.0, model.getDailyRating() + 0.1));
            JOptionPane.showMessageDialog(view, "Order Correct! +$" + String.format("%.2f", basePay + tip));
        } else {
            model.setDailyRating(Math.max(0.0, model.getDailyRating() - 0.5));
            String msg = "Wrong Order!";
            if (!burgerCorrect) msg += " (Burger incorrect)";
            if (!sidesCorrect) msg += " (Sides incorrect)";
            if (!drinksCorrect) msg += " (Drinks incorrect)";
            JOptionPane.showMessageDialog(view, msg + " Rating decreased.");
        }

        model.serveCustomer();
        model.clearBuild();
        updateAssemblyView();
    }

    private boolean validateSection(java.util.List<String> order, java.util.List<String> build) {
        if (order.size() != build.size()) return false;
        java.util.List<String> temp = new java.util.ArrayList<>(build);
        for (String s : order) {
            if (!temp.remove(s)) return false;
        }
        return true;
    }

    private void updateAssemblyView() {
        view.getAssemblyPanel().update(model.getWarmer(), model.getCurrentBuild(), model.getCurrentCustomer(), e -> {
            String command = e.getActionCommand();
            if (command.startsWith("WARMER:")) {
                int index = Integer.parseInt(command.substring(7));
                if (index >= 0 && index < model.getWarmer().size()) {
                    FoodItem item = model.getWarmer().remove(index);
                    model.addToBuild(item.getName());
                    updateAssemblyView();
                }
            }
        });
    }

    private void initGrillActions() {
        GrillStation.GrillSlot[][] slots = model.getGrill().getSlots();
        for (int i = 0; i < 2; i++) {
            final int r = i;
            for (int j = 0; j < 2; j++) {
                final int c = j;
                view.getGrillPanel().setSlotActionListener(r, c, e -> {
                    GrillStation.GrillSlot slot = slots[r][c];
                    if (slot.getItem() == null) {
                        slot.addItem(new FoodItem("Patty", model.getGameTime()));
                    } else if (slot.isBurned()) {
                        slot.removeItem(); // Trash it
                    } else if (slot.isReady()) {
                        FoodItem item = slot.removeItem();
                        model.addToWarmer(item);
                    } else if (!slot.isFlipped()) {
                        slot.flip();
                        if (slot.isFlipped()) {
                            view.getGrillPanel().triggerFlip(r, c);
                        }
                    }
                    view.getGrillPanel().update(model.getGrill());
                });
            }
        }
    }

    private void initNavigation() {
        String[] screens = {"Counter", "Grill", "Fryer", "Drink", "Assembly"};
        for (String screen : screens) {
            view.getNavPanel().getButton(screen).addActionListener(e -> {
                model.setActiveScreen(screen);
                view.showScreen(screen);
            });
        }
    }

    private void initGameLoop() {
        // Tick every 100ms for smoother dispensing
        gameLoop = new Timer(100, (ActionEvent e) -> {
            updateGame();
        });
        gameLoop.start();
    }

    private void updateGame() {
        tickCount++;
        if (tickCount % 10 == 0) model.advanceTime(1);
        
        // Drink Dispensing
        DrinkStation ds = model.getDrink();
        if (ds.isDispensing() && ds.getCurrentSize() != null && !ds.isSpilled()) {
            ds.setFillLevel(ds.getFillLevel() + 2.5);
            if (ds.getFillLevel() > 120) {
                ds.setSpilled(true);
                ds.setDispensing(false);
            }
            view.getDrinkPanel().update(ds);
        }

        // Cleanup Timeout
        if (ds.isSpilled() && ds.getScrubCount() > 0) {
            if (System.currentTimeMillis() - ds.getLastScrubTime() > 10000) {
                ds.setScrubCount(0);
                view.getDrinkPanel().update(ds);
            }
        }
        
        // Customer Logic (Spawn every 1s, Reduce patience every 2s)
        if (tickCount % 10 == 0) handleCustomerSpawning();
        if (tickCount % 20 == 0) handleCustomerPatience();
        
        // Grill Logic
        if (tickCount % 10 == 0) handleGrill();
        
        // Fryer Logic
        if (tickCount % 10 == 0) handleFryer();
        
        // Update View
        view.getHudPanel().updateTime(model.getGameTime().format(TIME_FORMATTER));
        view.getHudPanel().updateRating(model.getDailyRating());
        view.getHudPanel().updateRevenue(model.getRevenue());
        
        // Update Screens
        if (model.getActiveScreen().equals("Counter")) {
            Customer c = model.getCurrentCustomer();
            view.getCounterPanel().update(c, model.getCustomerQueue(), (c != null) ? c.getArrivalTime() : "");
        } else if (model.getActiveScreen().equals("Grill")) {
            view.getGrillPanel().update(model.getGrill());
        } else if (model.getActiveScreen().equals("Fryer")) {
            view.getFryerPanel().update(model.getFryer());
        } else if (model.getActiveScreen().equals("Drink")) {
            view.getDrinkPanel().update(model.getDrink());
        } else if (model.getActiveScreen().equals("Assembly")) {
            updateAssemblyView();
        }
        
        // End of day check
        if (model.getGameTime().getHour() >= 22) {
            gameLoop.stop();
            JOptionPane.showMessageDialog(view, "Shift ended! Final Revenue: $" + model.getRevenue());
        }
    }

    private void handleCustomerSpawning() {
        // Spawn customer every ~10-20 seconds
        if (random.nextInt(15) == 0) {
            String[] names = {"Alice", "Bob", "Charlie", "Diana", "Eve"};
            Customer c = new Customer(names[random.nextInt(names.length)]);
            c.setArrivalTime(model.getGameTime().format(TIME_FORMATTER));
            c.setArrivalLocalTime(model.getGameTime());
            model.addCustomer(c);
        }
    }

    private void handleCustomerPatience() {
        Customer current = model.getCurrentCustomer();
        if (current != null) {
            // Initialize counter arrival time if not set
            if (current.getCounterArrivalTime() == null) {
                current.setCounterArrivalTime(model.getGameTime());
            }

            current.reducePatience(1);
            
            if (current.getPatience() <= 0) {
                if (!current.isAccepted()) {
                    com.fastfoodchef.view.ToastManager.showToast(current.getName() + ": \"I waited way too long, I'm leaving!\"", 4);
                    model.setDailyRating(Math.max(0, model.getDailyRating() - 0.5));
                } else {
                    com.fastfoodchef.view.ToastManager.showToast(current.getName() + " left in anger!", 3);
                    model.setDailyRating(Math.max(0, model.getDailyRating() - 0.2));
                }
                
                model.serveCustomer(); // Remove them
                
                // Update view immediately if on Counter screen
                if (model.getActiveScreen().equals("Counter")) {
                    Customer next = model.getCurrentCustomer();
                    view.getCounterPanel().update(next, model.getCustomerQueue(), (next != null) ? next.getArrivalTime() : "");
                }
            }
        }
    }

    private void handleGrill() {
        GrillStation.GrillSlot[][] slots = model.getGrill().getSlots();
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                slots[i][j].update();
            }
        }
    }

    private void handleFryer() {
        for (com.fastfoodchef.model.FryerStation.FryerBasket basket : model.getFryer().getBaskets()) {
            basket.update();
        }
    }

    private void handleCustomers() {
        // Spawn customer every ~10-20 ticks
        if (tickCount % (10 + random.nextInt(10)) == 0) {
            String[] names = {"Alice", "Bob", "Charlie", "Diana", "Eve"};
            Customer c = new Customer(names[random.nextInt(names.length)]);
            c.setArrivalTime(model.getGameTime().format(TIME_FORMATTER));
            c.setArrivalLocalTime(model.getGameTime());
            model.addCustomer(c);
        }

        // Reduce patience of the current customer
        Customer current = model.getCurrentCustomer();
        if (current != null) {
            if (current.isAccepted()) {
                current.reducePatience(1);
                if (current.getPatience() <= 0) {
                    // Customer leaves in anger
                    model.serveCustomer(); // Remove them
                    model.setDailyRating(Math.max(0, model.getDailyRating() - 0.2));
                }
            } else {
                // Initialize counter arrival time if not set (just reached front of queue)
                if (current.getCounterArrivalTime() == null) {
                    current.setCounterArrivalTime(model.getGameTime());
                }

                // Check if they've been waiting too long to be accepted (90 min limit)
                long minutesWaiting = Duration.between(current.getCounterArrivalTime(), model.getGameTime()).toMinutes();
                if (minutesWaiting >= 90) {
                    com.fastfoodchef.view.ToastManager.showToast(current.getName() + ": \"I waited way too long, I'm leaving!\"", 4);
                    model.serveCustomer(); // Remove them
                    model.setDailyRating(Math.max(0, model.getDailyRating() - 0.5)); // Penalty
                    
                    // Update view immediately if on Counter screen
                    if (model.getActiveScreen().equals("Counter")) {
                        Customer next = model.getCurrentCustomer();
                        view.getCounterPanel().update(next, model.getCustomerQueue(), (next != null) ? next.getArrivalTime() : "");
                    }
                }
            }
        }
    }
}
