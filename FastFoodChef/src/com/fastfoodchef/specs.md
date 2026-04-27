## **Project Spec: Fast Food Maker (Java/Swing)**

### **1. Model (Data & Logic)**
The Model is the "brain" of the game. It handles state, math, and time without any knowledge of the UI.
* **Time System:** Tracks game time from 8:00 AM to 10:00 PM. (1 hour game time = 5 minutes real time).
* **GameState:** Tracks the current daily revenue, overall rating, and current "active" screen.
* **Inventory/Warmer:** A list or collection of `FoodItem` objects.
    * Limit: 30 items.
    * Expiration: Each item has a `timestampCreated`. If `currentTime - timestampCreated > 2 hours`, the item is flagged as "spoiled."
* **Cooking Stations Logic:**
    * **Grill:** A 2x2 array of slots. Each slot manages an `ItemProgress` and a `needsFlip` boolean.
    * **Fryer:** Tracks item count (max 10) and "shake" requirements (3 beeps).
    * **Oven:** Tracks two rack objects with independent progress bars.
* **Customer Logic:** Generates random intervals for arrivals. Each `Customer` object contains a `RequiredIngredients` list, a `PatienceTimer`, and logic to calculate a `Rating` and `Tip` based on accuracy.

### **2. View (Interface)**
The View renders the state of the Model and provides the visual layout.
* **Main Frame:** A `JFrame` with a `CardLayout` to switch between the five main screens instantly.
* **Navigation Bar:** A persistent `JPanel` at the bottom containing five `JButtons`: `[Counter]`, `[Grill]`, `[Fryer]`, `[Oven]`, and `[Assembly]`.
* **Screens:**
    * **Counter:** Displays current customer and their specific ingredient order.
    * **Grill/Fryer/Oven:** Visual representations of stations with `JProgressBar` components for every active item.
    * **Assembly:** A drag-and-drop workspace where the player moves components onto a tray.
* **Heads-Up Display (HUD):** Shows the current Game Time, Daily Rating, and Bank Balance.

### **3. Controller (Input & Updates)**
The Controller bridges the Model and the View, handling user input and the game loop.
* **Game Loop:** A `javax.swing.Timer` that ticks (e.g., every 100ms) to update progress bars, age the food in the warmer, and move game time forward.
* **Action Listeners:**
    * Handles button clicks for flipping items on the grill or shaking the fryer.
    * Manages the "Trash Can" logic (removing items from the Model's arrays).
* **Drag-and-Drop Handler:** Listens for mouse events in the Assembly screen to update the "Current Build" list in the Model.
* **Navigation Controller:** Swaps the active card in the `CardLayout` when a nav-bar button is clicked.

---

### **Win/Loss & Balancing Summary**
| Feature | Rule |
| :--- | :--- |
| **Shift Hours** | 08:00 to 22:00 (70 minutes real-time per day). |
| **Penalty** | 50% revenue reduction if Daily Rating is $\le$ 3.0 stars. |
| **Warmer Logic** | FIFO (First-In, First-Out) recommended to avoid spoilage. |
| **Cooking Station** | Grill requires 1 flip; Fryer requires 3 shakes. |