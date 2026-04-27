package com.fastfoodchef.model;

import java.time.LocalTime;

public class FoodItem {
    private String name;
    private LocalTime timestampCreated;
    private boolean isSpoiled;

    public FoodItem(String name, LocalTime gameTime) {
        this.name = name;
        this.timestampCreated = gameTime;
        this.isSpoiled = false;
    }

    public String getName() {
        return name;
    }

    public LocalTime getTimestampCreated() {
        return timestampCreated;
    }

    public boolean isSpoiled() {
        return isSpoiled;
    }

    public void setSpoiled(boolean spoiled) {
        isSpoiled = spoiled;
    }
}
