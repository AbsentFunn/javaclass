1: I'm building Space Invaders in Java using Swing, split into three files: GameModel.java, GameView.java, and GameController.java. GameView should extend JPanel and be hosted in a JFrame. GameController should have the main method and wire the three classes together. GameModel must have no Swing imports. For now, just create the three class shells with placeholder comments describing what each class will do. The program should compile and open a blank window.

2: Fill in GameModel.java. The model should track: the player's horizontal position, the alien formation (5 rows of 11), the player's bullet (one at a time), alien bullets, the score, and lives remaining (start with 3). Add logic to: move the player left and right, fire a player bullet if one isn't already in flight, advance the player's bullet each tick, move the alien formation right until the edge then down and reverse, fire alien bullets at random intervals, and detect collisions between bullets and aliens or the player. No Swing imports.

3: Fill in GameView.java. It should take a reference to the model and draw everything the player sees: the player, the alien formation, both sets of bullets, the score, and remaining lives. Show a centered game-over message when the game ends. The view should only read from the model — it must never change game state.

4: Fill in GameController.java. Add keyboard controls so the player can move left and right with the arrow keys and fire with the spacebar. Add a game loop using a Swing timer that updates the model each tick and redraws the view. Stop the loop when the game is over.

5: Create a separate file called ModelTester.java with a main method. It should create a GameModel, call its methods directly, and print PASS or FAIL for each check. Write tests for at least five behaviors: the player cannot move past the left or right edge, firing while a bullet is already in flight does nothing, a bullet that reaches the top is removed, destroying an alien increases the score, and losing all lives triggers the game-over state. No testing libraries — just plain Java.

6: add powerups that occassionally fall from the sky. when you pickup a powerup, make the gameplay pause and a cool animated translucent screen pop up saying "POWERUP! You got: ..." and then after 2 seconds resume and animate away

7: powerups fall too often. also, when the popup menu comes up, it holds the player movement from whatever keys they had down until they press that key again. fix that as well. it should also only be like 1 powerup at a time 

8: add an aura that appears around the ship when you have a powerup active. make them last for 15 seconds and have no powerups spawn while they are active. it should be every minute a powerup spawns.

9: make the first powerup spawn after 15 seconds, then the rest 45 seconds after that one

10: make it so some of the bullets the aliens shoot can morph into a homing bullet. they will grow a little bigger and turn into a dark red color. they will aim at the player until they are within a certain amount of distance and then continue in a straight line from where they were and whatever angle they were at. make it so the user is possible to dodge them

11: make the game prettier with mathematical equations. make a cool space background and better animations for the bullets, getting hit, and killing the aliens, etc..