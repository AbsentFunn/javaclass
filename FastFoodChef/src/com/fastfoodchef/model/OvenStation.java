package com.fastfoodchef.model;

public class OvenStation {
    private Rack[] racks;

    public OvenStation() {
        racks = new Rack[2];
        racks[0] = new Rack();
        racks[1] = new Rack();
    }

    public static class Rack {
        private FoodItem item;
        private double progress;

        public Rack() {
            this.item = null;
            this.progress = 0;
        }

        public void addItem(FoodItem item) {
            this.item = item;
            this.progress = 0;
        }
    }
}
