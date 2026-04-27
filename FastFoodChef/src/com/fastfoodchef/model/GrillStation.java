package com.fastfoodchef.model;

public class GrillStation {
    private GrillSlot[][] slots;

    public GrillStation() {
        slots = new GrillSlot[2][2];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                slots[i][j] = new GrillSlot();
            }
        }
    }

    public GrillSlot[][] getSlots() {
        return slots;
    }

    public static class GrillSlot {
        private FoodItem item;
        private double progress; // 0 to 100
        private boolean flipped;
        private boolean burned;
        private boolean notificationSent;

        public GrillSlot() {
            this.item = null;
            this.progress = 0;
            this.flipped = false;
            this.burned = false;
            this.notificationSent = false;
        }

        public void addItem(FoodItem item) {
            this.item = item;
            this.progress = 0;
            this.flipped = false;
            this.burned = false;
            this.notificationSent = false;
        }

        public FoodItem removeItem() {
            FoodItem temp = item;
            item = null;
            progress = 0;
            flipped = false;
            burned = false;
            notificationSent = false;
            return temp;
        }

        public void update() {
            if (item != null && !burned) {
                progress += 4.0; // Cook speed (45s to reach 180)
                
                // Flip window check
                if (progress >= 45 && !flipped && !notificationSent) {
                    com.fastfoodchef.view.ToastManager.showToast("Grill Patty needs flipping!", 2);
                    notificationSent = true;
                }

                // If we pass the window (65%) and haven't flipped, it burns
                // Window is 20 points wide. At 4.0 speed, this is exactly 5 seconds.
                if (progress > 65 && !flipped) {
                    burned = true;
                }

                if (progress > 200) {
                    burned = true; // Burned if left too long even after flip
                }
            }
        }

        public void flip() {
            if (item != null && !burned && progress >= 45 && progress <= 65 && !flipped) {
                flipped = true;
                notificationSent = false;
            }
        }

        public boolean isReady() {
            return flipped && progress >= 180 && !burned;
        }

        public FoodItem getItem() { return item; }
        public double getProgress() { return progress; }
        public boolean isFlipped() { return flipped; }
        public boolean isBurned() { return burned; }
    }
}
