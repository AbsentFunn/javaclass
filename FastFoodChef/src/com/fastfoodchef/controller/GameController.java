package com.fastfoodchef.controller;

import com.fastfoodchef.model.Customer;
import com.fastfoodchef.model.FoodItem;
import com.fastfoodchef.model.GameState;
import com.fastfoodchef.model.GrillStation;
import com.fastfoodchef.model.FryerStation;
import com.fastfoodchef.view.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
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
        initGameLoop();
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
                view.getCounterPanel().update(current, model.getCustomerQueue());
            }
        });

        view.getCounterPanel().getDenyButton().addActionListener(e -> {
            Customer current = model.getCurrentCustomer();
            if (current != null) {
                model.serveCustomer(); // Remove from queue
                model.setDailyRating(Math.max(0, model.getDailyRating() - 0.3)); // Penalty
                view.getCounterPanel().update(model.getCurrentCustomer(), model.getCustomerQueue());
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
            model.clearBuild();
            updateAssemblyView();
        });

        view.getAssemblyPanel().getServeButton().addActionListener(e -> {
            handleServe();
        });
    }

    private void handleServe() {
        Customer current = model.getCurrentCustomer();
        if (current == null || !current.isAccepted()) return;

        java.util.List<String> build = model.getCurrentBuild();
        java.util.List<String> order = current.getOrderIngredients();

        // Basic comparison (exact match for now, ignoring order of ingredients)
        boolean correct = build.size() == order.size();
        if (correct) {
            java.util.List<String> buildCopy = new java.util.ArrayList<>(build);
            for (String ing : order) {
                if (!buildCopy.remove(ing)) {
                    correct = false;
                    break;
                }
            }
        }
        
        if (correct) {
            double basePay = 10.0;
            double tip = current.getPatience() / 10.0;
            model.addRevenue(basePay + tip);
            model.setDailyRating(Math.min(5.0, model.getDailyRating() + 0.1));
            JOptionPane.showMessageDialog(view, "Order Correct! +$" + String.format("%.2f", basePay + tip));
        } else {
            model.setDailyRating(Math.max(0.0, model.getDailyRating() - 0.5));
            JOptionPane.showMessageDialog(view, "Wrong Order! Rating decreased.");
        }

        model.serveCustomer();
        model.clearBuild();
        updateAssemblyView();
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
        String[] screens = {"Counter", "Grill", "Fryer", "Oven", "Assembly"};
        for (String screen : screens) {
            view.getNavPanel().getButton(screen).addActionListener(e -> {
                model.setActiveScreen(screen);
                view.showScreen(screen);
            });
        }
    }

    private void initGameLoop() {
        // Tick every 1 second (simulating time passing)
        gameLoop = new Timer(1000, (ActionEvent e) -> {
            updateGame();
        });
        gameLoop.start();
    }

    private void updateGame() {
        tickCount++;
        model.advanceTime(1);
        
        // Customer Logic
        handleCustomers();
        
        // Grill Logic
        handleGrill();
        
        // Fryer Logic
        handleFryer();
        
        // Update View
        view.getHudPanel().updateTime(model.getGameTime().format(TIME_FORMATTER));
        view.getHudPanel().updateRating(model.getDailyRating());
        view.getHudPanel().updateRevenue(model.getRevenue());
        
        // Update Screens
        if (model.getActiveScreen().equals("Counter")) {
            view.getCounterPanel().update(model.getCurrentCustomer(), model.getCustomerQueue());
        } else if (model.getActiveScreen().equals("Grill")) {
            view.getGrillPanel().update(model.getGrill());
        } else if (model.getActiveScreen().equals("Fryer")) {
            view.getFryerPanel().update(model.getFryer());
        } else if (model.getActiveScreen().equals("Assembly")) {
            updateAssemblyView();
        }
        
        // End of day check
        if (model.getGameTime().getHour() >= 22) {
            gameLoop.stop();
            JOptionPane.showMessageDialog(view, "Shift ended! Final Revenue: $" + model.getRevenue());
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
            model.addCustomer(new Customer(names[random.nextInt(names.length)]));
        }

        // Reduce patience of the current customer
        Customer current = model.getCurrentCustomer();
        if (current != null && current.isAccepted()) {
            current.reducePatience(1);
            if (current.getPatience() <= 0) {
                // Customer leaves in anger
                model.serveCustomer(); // Remove them
                model.setDailyRating(Math.max(0, model.getDailyRating() - 0.2));
            }
        }
    }
}
