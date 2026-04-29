1: I'm building a Snake game in Java using Swing. Create a single file called SnakeGame.java. It should have a main method that opens a JFrame window that is 600 by 600 pixels and titled Snake. Inside the frame, add a JPanel subclass called GamePanel. Do not add any game logic yet. Just get the window to open correctly.

2: Now extend SnakeGame.java. Keep it as one file. Add a dark background grid and draw a starting snake that is three segments long near the center of the board, facing right. Each cell should be a 30x30 pixel square. Draw the snake in green and the background in dark gray. Do not add movement yet.

3: Make the snake move automatically using a Swing timer that ticks every 150 milliseconds. Add arrow key controls so the player can steer, but don't allow the snake to reverse direction. For now, have the snake wrap around the edges instead of dying. Make sure the panel can receive keyboard input.

4: Add a food pellet that spawns at a random empty cell. When the snake eats it, grow by one segment and spawn new food. Add collision detection: hitting a wall or the snake's own body should end the game, stop movement, and show a "Game Over" message with the final score. Display the current score in the top-left corner during play. When the game is over, let the player press R to reset everything and play again.

5: add powerups to the game. make them not increase speed or prevent lengthening the snake. they should be good.

6: okay i was a little vague on the last one, i want you to make the powerups GHOST, MAGNET, 2X SCORE, SLOWMO, and SHIELD.

7: the powerups spawn too frequently, make it so only 3 spawn per round and you can pick up all of them, and then when you eat an apple it resets all of them and spawns 3 more randomly around with the new apple.

8: the grid allows for the snake to go off the screen and for stuff to spawn off the screen. can you fix the grid to match the resolution of the window and wrap around when the snake goes off the window?

9: make the game look more matte, i want small gradients on the objects and have it dark mode with smooth colors.

10: the double score power up doesn't work, neither does the magnet. review the logic on just those powerups.