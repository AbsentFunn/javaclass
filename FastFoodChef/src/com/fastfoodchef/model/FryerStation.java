package com.fastfoodchef.model;

import java.util.ArrayList;
import java.util.List;

public class FryerStation {
    private List<FryerBasket> baskets;
    private static final int MAX_BASKETS = 3;

    public FryerStation() {
        baskets = new ArrayList<>();
        for (int i = 0; i < MAX_BASKETS; i++) {
            baskets.add(new FryerBasket(i + 1));
        }
    }

    public List<FryerBasket> getBaskets() {
        return baskets;
    }

    public static class FryerBasket {
        private FoodItem item;
        private double progress;
        private int shakesPerformed;
        private double[] shakeTargets;
        private boolean ruined;
        private boolean needsShake;
        private boolean notificationSent;
        private int id;

        private static final double MAX_PROGRESS = 100.0;
        private static final double SHAKE_WINDOW = 10.0; // 10% of MAX_PROGRESS

        public FryerBasket(int id) {
            this.id = id;
            this.item = null;
            reset();
        }

        private void reset() {
            this.progress = 0;
            this.shakesPerformed = 0;
            this.ruined = false;
            this.needsShake = false;
            this.notificationSent = false;
            this.shakeTargets = new double[3];
        }

        public void addItem(FoodItem item) {
            this.item = item;
            reset();
            // Generate 3 shake targets: 15-30%, 45-60%, 75-90%
            java.util.Random rand = new java.util.Random();
            shakeTargets[0] = 15 + rand.nextDouble() * 15;
            shakeTargets[1] = 45 + rand.nextDouble() * 15;
            shakeTargets[2] = 75 + rand.nextDouble() * 15;
        }

        public FoodItem removeItem() {
            FoodItem temp = item;
            item = null;
            reset();
            return temp;
        }

        public void update() {
            if (item != null && !ruined) {
                progress += 2.0; // Adjusted speed

                // Check if a shake is currently required
                boolean previouslyNeededShake = needsShake;
                needsShake = false;
                for (int i = 0; i < 3; i++) {
                    if (shakesPerformed <= i && progress >= shakeTargets[i]) {
                        if (progress > shakeTargets[i] + SHAKE_WINDOW) {
                            ruined = true; // Missed the window
                        } else {
                            needsShake = true;
                            if (!notificationSent) {
                                com.fastfoodchef.view.ToastManager.showToast("Fryer #" + id + " needs shaken!", 3);
                                notificationSent = true;
                            }
                        }
                    }
                }

                if (!needsShake) {
                    notificationSent = false;
                }

                if (progress > MAX_PROGRESS + 20) {
                    ruined = true; // Burned
                }
            }
        }

        public void shake() {
            if (item != null && !ruined && needsShake) {
                shakesPerformed++;
                needsShake = false;
                notificationSent = false;
            }
        }

        public boolean isReady() {
            return item != null && progress >= MAX_PROGRESS && shakesPerformed >= 3 && !ruined;
        }

        public int getId() { return id; }
        public FoodItem getItem() { return item; }
        public double getProgress() { return progress; }
        public int getShakes() { return shakesPerformed; }
        public boolean isRuined() { return ruined; }
        public boolean needsShake() { return needsShake; }
        public double getMaxProgress() { return MAX_PROGRESS; }
    }
}
