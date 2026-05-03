## **Project Spec: Fast Food Maker (Java/Swing)**

### **1. Model (Data & Logic)**
The Model is the "brain" of the game. It handles state, math, and time without any knowledge of the UI.
* **Time System:** Tracks game time from 8:00 AM to 10:00 PM. (1 hour game time = 1.6 real minutes, ticks every 100ms).
* **GameState:** Tracks the current daily revenue, overall rating, and current "active" screen.
* **Inventory/Warmer:** A list or collection of `FoodItem` objects.
    * Limit: 30 items.
    * Expiration: Each item has a `timestampCreated`. If `currentTime - timestampCreated > 2 hours`, the item is flagged as "spoiled."
* **Cooking Stations Logic:**
    * **Grill:** A 2x2 array of slots. Each slot manages an `ItemProgress` and a `needsFlip` boolean.
    * **Fryer:** Tracks item count (max 10) and "shake" requirements (3 beeps).
    * **Drink Station:** Manages beverage preparation with cup sizes (Small, Medium, Large), Ice options, and dispensing.
        * **Filling:** Must hit target threshold (Small: 40-60%, Medium: 65-85%, Large: 90-110%).
        * **Spill:** Filling over 120% causes a spill, trashing the drink and requiring cleanup.
        * **Cleanup Minigame:** Requires 5 "scrubs" within 10 seconds, with each scrub clicked within 2 seconds of the previous.
* **Customer Logic:** Generates random intervals for arrivals. Each `Customer` object contains a `RequiredIngredients` list (including Burgers, Sides, and Drinks), a `PatienceTimer`, and an `arrivalTime` timestamp.
    * **Acceptance Timeout:** If an order is not accepted within 90 minutes of game time AFTER the customer reaches the front of the counter, the customer leaves with a penalty to the daily rating and a notification is displayed. Only the customer currently at the counter is subject to this timeout.

### **2. View (Interface)**
The View renders the state of the Model and provides the visual layout.
* **Main Frame:** A `JFrame` with a `CardLayout` to switch between the five main screens instantly.
* **Navigation Bar:** A persistent `JPanel` at the bottom containing five `JButtons`: `[Counter]`, `[Grill]`, `[Fryer]`, `[Drink]`, and `[Assembly]`.
* **Screens:**
    * **Counter:** Displays current customer and their specific order on a stylized kitchen ticket.
    * **Grill/Fryer/Drink:** Visual representations of stations with `JProgressBar` and interactive components.
    * **Assembly:** A workspace divided into Burgers, Sides, and Drinks for organized serving.
* **Heads-Up Display (HUD):** Shows the current Game Time, Daily Rating, and Bank Balance.

### **3. Controller (Input & Updates)**
The Controller bridges the Model and the View, handling user input and the game loop.
* **Game Loop:** A `javax.swing.Timer` that ticks every 100ms to update progress bars, age food, move game time, and handle real-time drink dispensing.
* **Action Listeners:**
    * Handles button clicks for flipping grill items, shaking the fryer, or scrubing spills.
    * Manages the "Trash Can" and "Move to Inventory" logic.
* **Order Validation:** Separately validates Burger, Side, and Drink components of an order for accuracy.
* **Navigation Controller:** Swaps the active card in the `CardLayout` when a nav-bar button is clicked.

---

### **Win/Loss & Balancing Summary**
| Feature | Rule |
| :--- | :--- |
| **Shift Hours** | 08:00 to 22:00 (approx. 23 real-time minutes per day). |
| **Penalty** | 50% revenue reduction if Daily Rating is $\le$ 3.0 stars. |
| **Warmer Logic** | FIFO (First-In, First-Out) recommended to avoid spoilage. |
| **Cooking Station** | Grill: 1 flip; Fryer: 3 shakes; Drink: Threshold fill. |
| **Cleanup** | Spills disable dispensing until the 5-click scrub minigame is completed. |
---
