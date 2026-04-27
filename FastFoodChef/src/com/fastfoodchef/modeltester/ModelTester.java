package com.fastfoodchef.modeltester;

import com.fastfoodchef.model.*;
import java.time.LocalTime;
import java.util.List;

public class ModelTester {
    private static void testFryerMissedShake() {
        System.out.print("Testing FryerStation (Missed Shake Logic): ");
        FryerStation station = new FryerStation();
        FryerStation.FryerBasket basket = station.getBaskets().get(0);
        
        basket.addItem(new FoodItem("Fries", LocalTime.of(8, 0)));
        
        // Cook until a shake is needed, then wait too long
        int maxTicks = 100;
        int ticks = 0;
        boolean shakeNeededFound = false;
        while (ticks < maxTicks) {
            basket.update();
            if (basket.needsShake()) {
                shakeNeededFound = true;
                // Wait for more than 10% (5 ticks at 2.0 progress per tick = 10% of 100)
                for (int j = 0; j < 6; j++) basket.update();
                break;
            }
            ticks++;
        }
        
        boolean ruined = basket.isRuined();

        if (shakeNeededFound && ruined) {
            System.out.println("PASSED");
        } else {
            System.out.println("FAILED (ShakeNeeded: " + shakeNeededFound + ", Ruined: " + ruined + ")");
        }
    }

    private static void testFryerBasketIDs() {
        System.out.print("Testing FryerBasket IDs: ");
        FryerStation station = new FryerStation();
        boolean id1 = station.getBaskets().get(0).getProgress() == 0; // Just checking existence
        // Accessing private field id via reflection or just adding a getter. 
        // I'll add a getter to FryerBasket for id.
        int idA = station.getBaskets().get(0).getId();
        int idB = station.getBaskets().get(1).getId();
        int idC = station.getBaskets().get(2).getId();

        if (idA == 1 && idB == 2 && idC == 3) {
            System.out.println("PASSED");
        } else {
            System.out.println("FAILED (IDs: " + idA + ", " + idB + ", " + idC + ")");
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Starting Model Tests ===");

        testFoodItem();
        testGameState();
        testCustomer();
        testGrillStation();
        testFryerStation();
        testFryerMissedShake();
        testFryerBasketIDs();
        testAssemblyLogic();

        System.out.println("\n=== All Tests Completed ===");
    }

    private static void testFryerStation() {
        System.out.print("Testing FryerStation (Randomized Shakes & Ready Logic): ");
        FryerStation station = new FryerStation();
        FryerStation.FryerBasket basket = station.getBaskets().get(0);
        
        basket.addItem(new FoodItem("Fries", LocalTime.of(8, 0)));
        
        // Cook and perform shakes when needed
        int maxTicks = 100;
        int ticks = 0;
        while (!basket.isReady() && !basket.isRuined() && ticks < maxTicks) {
            basket.update();
            if (basket.needsShake()) {
                basket.shake();
            }
            ticks++;
        }
        
        boolean ready = basket.isReady();
        boolean shakesDone = basket.getShakes() == 3;
        boolean notRuined = !basket.isRuined();

        if (ready && shakesDone && notRuined) {
            System.out.println("PASSED (" + ticks + " ticks)");
        } else {
            System.out.println("FAILED (Ready: " + ready + ", Shakes: " + basket.getShakes() + ", Ruined: " + basket.isRuined() + ", Ticks: " + ticks + ")");
        }
    }

    private static void testFoodItem() {
        System.out.print("Testing FoodItem: ");
        FoodItem item = new FoodItem("Patty", LocalTime.of(8, 0));
        if ("Patty".equals(item.getName()) && !item.isSpoiled()) {
            System.out.println("PASSED");
        } else {
            System.out.println("FAILED");
        }
    }

    private static void testGameState() {
        System.out.print("Testing GameState: ");
        GameState state = new GameState();
        state.advanceTime(60);
        state.addRevenue(10.50);
        
        if (state.getGameTime().equals(LocalTime.of(9, 0)) && state.getRevenue() == 10.50) {
            System.out.println("PASSED");
        } else {
            System.out.println("FAILED (Time: " + state.getGameTime() + ", Bank: " + state.getRevenue() + ")");
        }
    }

    private static void testCustomer() {
        System.out.print("Testing Customer (Logical Order & Acceptance): ");
        Customer c = new Customer("TestBot");
        List<String> order = c.getOrderIngredients();
        
        boolean startsWithBun = order.get(0).equals("Bun");
        boolean endsWithBun = order.get(order.size() - 1).equals("Bun");
        boolean hasPatty = order.contains("Patty");
        boolean notAcceptedByDefault = !c.isAccepted();
        
        c.setAccepted(true);
        boolean acceptedWorks = c.isAccepted();
        
        if (startsWithBun && endsWithBun && hasPatty && notAcceptedByDefault && acceptedWorks) {
            System.out.println("PASSED");
        } else {
            System.out.println("FAILED");
        }
    }

    private static void testGrillStation() {
        System.out.print("Testing GrillStation (45-65% Flip Window): ");
        GrillStation station = new GrillStation();
        GrillStation.GrillSlot slot = station.getSlots()[0][0];
        
        slot.addItem(new FoodItem("Patty", LocalTime.of(8, 0)));
        
        // Simulating cooking until window starts (45 / 4.0 = 11.25 ticks)
        for (int i = 0; i < 11; i++) slot.update();
        boolean notYetReadyToFlip = !slot.isFlipped();
        
        slot.update(); // Now at 12 ticks * 4.0 = 48 progress
        slot.flip();
        boolean flippedInWindow = slot.isFlipped();

        if (notYetReadyToFlip && flippedInWindow) {
            System.out.println("PASSED");
        } else {
            System.out.println("FAILED (NotYet: " + notYetReadyToFlip + ", FlippedInWindow: " + flippedInWindow + ")");
        }
    }

    private static void testAssemblyLogic() {
        System.out.print("Testing Assembly Logic (Warmer & Build): ");
        GameState state = new GameState();
        
        // Test Warmer
        state.addToWarmer(new FoodItem("Patty", state.getGameTime()));
        boolean warmerOk = state.getWarmer().size() == 1 && state.getWarmer().get(0).getName().equals("Patty");
        
        // Test Build
        state.addToBuild("Bun");
        state.addToBuild("Patty");
        boolean buildOk = state.getCurrentBuild().size() == 2 && state.getCurrentBuild().contains("Bun");
        
        state.clearBuild();
        boolean clearOk = state.getCurrentBuild().isEmpty();

        if (warmerOk && buildOk && clearOk) {
            System.out.println("PASSED");
        } else {
            System.out.println("FAILED (Warmer: " + warmerOk + ", Build: " + buildOk + ", Clear: " + clearOk + ")");
        }
    }
}
