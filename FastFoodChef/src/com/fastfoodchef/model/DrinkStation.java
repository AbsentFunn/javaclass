package com.fastfoodchef.model;

public class DrinkStation {
    public enum CupSize {
        SMALL(40, 60, "Small"), 
        MEDIUM(65, 85, "Medium"), 
        LARGE(90, 110, "Large");

        public final int minFill;
        public final int maxFill;
        public final String label;

        CupSize(int min, int max, String label) {
            this.minFill = min;
            this.maxFill = max;
            this.label = label;
        }
    }

    private CupSize currentSize;
    private boolean hasIce;
    private double fillLevel;
    private boolean spilled;
    private String drinkType;
    private int scrubCount;
    private long lastScrubTime;
    private boolean dispensing;

    public DrinkStation() {
        reset();
    }

    public void reset() {
        currentSize = null;
        hasIce = false;
        fillLevel = 0;
        spilled = false;
        drinkType = null;
        scrubCount = 0;
        dispensing = false;
    }

    public void startNewDrink(CupSize size) {
        reset();
        this.currentSize = size;
    }

    public boolean isDispensing() { return dispensing; }
    public void setDispensing(boolean dispensing) { this.dispensing = dispensing; }
    public CupSize getCurrentSize() { return currentSize; }
    public void setHasIce(boolean hasIce) { this.hasIce = hasIce; }
    public boolean hasIce() { return hasIce; }
    public double getFillLevel() { return fillLevel; }
    public void setFillLevel(double fillLevel) { this.fillLevel = fillLevel; }
    public boolean isSpilled() { return spilled; }
    public void setSpilled(boolean spilled) { this.spilled = spilled; }
    public String getDrinkType() { return drinkType; }
    public void setDrinkType(String drinkType) { this.drinkType = drinkType; }
    public int getScrubCount() { return scrubCount; }
    public void setScrubCount(int scrubCount) { this.scrubCount = scrubCount; }
    public long getLastScrubTime() { return lastScrubTime; }
    public void setLastScrubTime(long lastScrubTime) { this.lastScrubTime = lastScrubTime; }

    public boolean isPerfectlyFilled() {
        if (currentSize == null) return false;
        return fillLevel >= currentSize.minFill && fillLevel <= currentSize.maxFill;
    }
}
