import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.awt.Point;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for {@link GameState}.
 *
 * Test categories:
 *   1. Initial state
 *   2. Basic movement
 *   3. Grid wrap-around
 *   4. Direction change rules
 *   5. Food consumption and scoring
 *   6. Self-collision / game-over behaviour
 *   7. Re-initialisation
 *   8. Food spawn constraints
 */
class GameStateTest {

    // -----------------------------------------------------------------------
    // 1. Initial state
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Snake starts with three segments")
    void initialSnakeHasThreeSegments() {
        GameState gs = new GameState(20);
        assertEquals(3, gs.getSnake().size());
    }

    @Test
    @DisplayName("Snake head is at (5, 10) after initialisation")
    void initialSnakeHeadPosition() {
        GameState gs = new GameState(20);
        assertEquals(new Point(5, 10), gs.getSnake().getFirst());
    }

    @Test
    @DisplayName("Snake body is laid out horizontally to the left of the head")
    void initialSnakeBodyLayout() {
        GameState gs = new GameState(20);
        LinkedList<Point> snake = gs.getSnake();
        assertEquals(new Point(5, 10), snake.get(0)); // head
        assertEquals(new Point(4, 10), snake.get(1)); // body
        assertEquals(new Point(3, 10), snake.get(2)); // tail
    }

    @Test
    @DisplayName("Score is 0 at the start")
    void initialScoreIsZero() {
        GameState gs = new GameState(20);
        assertEquals(0, gs.getScore());
    }

    @Test
    @DisplayName("Game is not over at the start")
    void initialGameNotOver() {
        GameState gs = new GameState(20);
        assertFalse(gs.isGameOver());
    }

    @Test
    @DisplayName("Initial direction is RIGHT")
    void initialDirectionIsRight() {
        GameState gs = new GameState(20);
        assertEquals(GameState.DIR_RIGHT, gs.getDirection());
    }

    @Test
    @DisplayName("Food is placed within grid bounds after initialisation")
    void initialFoodWithinGridBounds() {
        GameState gs = new GameState(20);
        Point food = gs.getFood();
        assertTrue(food.x >= 0 && food.x < 20, "food.x out of bounds: " + food.x);
        assertTrue(food.y >= 0 && food.y < 20, "food.y out of bounds: " + food.y);
    }

    @Test
    @DisplayName("Food is not placed on the snake at initialisation")
    void initialFoodNotOnSnake() {
        GameState gs = new GameState(20);
        assertFalse(gs.getSnake().contains(gs.getFood()),
                "Food must not overlap with the snake");
    }

    // -----------------------------------------------------------------------
    // 2. Basic movement
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Snake moves right by one cell per tick")
    void movesRightByDefault() {
        GameState gs = new GameState(20);
        gs.setFood(new Point(19, 0)); // keep food away from path
        Point headBefore = gs.getSnake().getFirst();
        gs.tick();
        Point headAfter = gs.getSnake().getFirst();
        assertEquals(headBefore.x + 1, headAfter.x);
        assertEquals(headBefore.y,     headAfter.y);
    }

    @Test
    @DisplayName("Snake length stays the same after moving without eating")
    void snakeLengthUnchangedWhenNotEating() {
        GameState gs = new GameState(20);
        gs.setFood(new Point(19, 0));
        int lengthBefore = gs.getSnake().size();
        gs.tick();
        assertEquals(lengthBefore, gs.getSnake().size());
    }

    @Test
    @DisplayName("Snake moves left after direction is set to LEFT")
    void movesLeft() {
        GameState gs = new GameState(20);
        gs.setFood(new Point(19, 0));
        // first move up so we can then turn left
        gs.setNextDirection(GameState.DIR_UP);
        gs.tick();
        gs.setNextDirection(GameState.DIR_LEFT);
        gs.tick();
        Point head = gs.getSnake().getFirst();
        // after UP tick head was at (6,9), after LEFT tick head should be at (5,9)
        assertEquals(GameState.DIR_LEFT, gs.getDirection());
    }

    @Test
    @DisplayName("Snake moves up after direction is set to UP")
    void movesUp() {
        GameState gs = new GameState(20);
        gs.setFood(new Point(19, 0));
        gs.setNextDirection(GameState.DIR_UP);
        gs.tick();
        assertEquals(GameState.DIR_UP, gs.getDirection());
        Point head = gs.getSnake().getFirst();
        assertEquals(9, head.y); // started at y=10, moved up
    }

    @Test
    @DisplayName("Snake moves down after direction is set to DOWN")
    void movesDown() {
        GameState gs = new GameState(20);
        gs.setFood(new Point(19, 0));
        gs.setNextDirection(GameState.DIR_DOWN);
        gs.tick();
        assertEquals(GameState.DIR_DOWN, gs.getDirection());
        Point head = gs.getSnake().getFirst();
        assertEquals(11, head.y); // started at y=10, moved down
    }

    @Test
    @DisplayName("Body segments follow the head correctly")
    void bodyFollowsHead() {
        GameState gs = new GameState(20);
        gs.setFood(new Point(19, 0));
        Point originalHead = gs.getSnake().getFirst();
        gs.tick();
        LinkedList<Point> snake = gs.getSnake();
        // what was the head is now body[1]
        assertEquals(originalHead, snake.get(1));
    }

    // -----------------------------------------------------------------------
    // 3. Grid wrap-around
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Snake wraps from the right edge to the left edge")
    void wrapsRightToLeft() {
        GameState gs = new GameState(20);
        // Place head at the rightmost column
        LinkedList<Point> snake = new LinkedList<>();
        snake.add(new Point(19, 5));
        snake.add(new Point(18, 5));
        gs.setSnake(snake);
        gs.setFood(new Point(0, 19));
        gs.tick();
        assertEquals(0, gs.getSnake().getFirst().x);
    }

    @Test
    @DisplayName("Snake wraps from the left edge to the right edge")
    void wrapsLeftToRight() {
        GameState gs = new GameState(20);
        LinkedList<Point> snake = new LinkedList<>();
        snake.add(new Point(0, 5));
        snake.add(new Point(1, 5));
        gs.setSnake(snake);
        gs.setDirection(GameState.DIR_LEFT);
        gs.setFood(new Point(19, 19));
        gs.tick();
        assertEquals(19, gs.getSnake().getFirst().x);
    }

    @Test
    @DisplayName("Snake wraps from the top edge to the bottom edge")
    void wrapsTopToBottom() {
        GameState gs = new GameState(20);
        LinkedList<Point> snake = new LinkedList<>();
        snake.add(new Point(5, 0));
        snake.add(new Point(5, 1));
        gs.setSnake(snake);
        gs.setDirection(GameState.DIR_UP);
        gs.setFood(new Point(0, 19));
        gs.tick();
        assertEquals(19, gs.getSnake().getFirst().y);
    }

    @Test
    @DisplayName("Snake wraps from the bottom edge to the top edge")
    void wrapsBottomToTop() {
        GameState gs = new GameState(20);
        LinkedList<Point> snake = new LinkedList<>();
        snake.add(new Point(5, 19));
        snake.add(new Point(5, 18));
        gs.setSnake(snake);
        gs.setDirection(GameState.DIR_DOWN);
        gs.setFood(new Point(0, 0));
        gs.tick();
        assertEquals(0, gs.getSnake().getFirst().y);
    }

    // -----------------------------------------------------------------------
    // 4. Direction change rules
    // -----------------------------------------------------------------------

    @ParameterizedTest(name = "Cannot reverse from {0} to {1}")
    @CsvSource({
        "1, 0",   // RIGHT -> LEFT  blocked
        "0, 1",   // LEFT  -> RIGHT blocked
        "2, 3",   // UP    -> DOWN  blocked
        "3, 2"    // DOWN  -> UP    blocked
    })
    void cannotReverseDirection(int current, int opposite) {
        GameState gs = new GameState(20);
        gs.setFood(new Point(19, 0));

        // Manoeuvre the snake so `current` is the active direction
        if (current == GameState.DIR_LEFT) {
            gs.setNextDirection(GameState.DIR_UP);
            gs.tick();
            gs.setNextDirection(GameState.DIR_LEFT);
            gs.tick();
        } else if (current == GameState.DIR_UP) {
            gs.setNextDirection(GameState.DIR_UP);
            gs.tick();
        } else if (current == GameState.DIR_DOWN) {
            gs.setNextDirection(GameState.DIR_DOWN);
            gs.tick();
        }
        // DIR_RIGHT is the default

        boolean accepted = gs.setNextDirection(opposite);
        assertFalse(accepted, "Reversing direction should be rejected");
        assertNotEquals(opposite, gs.getNextDirection(),
                "nextDirection must not be set to the opposite direction");
    }

    @Test
    @DisplayName("Perpendicular direction change is accepted")
    void perpendicularDirectionIsAccepted() {
        GameState gs = new GameState(20);
        boolean accepted = gs.setNextDirection(GameState.DIR_UP);
        assertTrue(accepted);
        assertEquals(GameState.DIR_UP, gs.getNextDirection());
    }

    @Test
    @DisplayName("Direction change takes effect on the next tick")
    void directionChangeAppliedOnNextTick() {
        GameState gs = new GameState(20);
        gs.setFood(new Point(19, 0));
        gs.setNextDirection(GameState.DIR_UP);
        assertEquals(GameState.DIR_RIGHT, gs.getDirection()); // not yet applied
        gs.tick();
        assertEquals(GameState.DIR_UP, gs.getDirection());    // applied after tick
    }

    @Test
    @DisplayName("Only the most recently accepted direction is used on the next tick")
    void lastAcceptedDirectionWins() {
        GameState gs = new GameState(20);
        gs.setFood(new Point(19, 0));
        gs.setNextDirection(GameState.DIR_UP);
        gs.setNextDirection(GameState.DIR_LEFT); // ignored: opposite of RIGHT
        gs.setNextDirection(GameState.DIR_DOWN); // overrides UP
        gs.tick();
        assertEquals(GameState.DIR_DOWN, gs.getDirection());
    }

    // -----------------------------------------------------------------------
    // 5. Food consumption and scoring
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Score increases by 10 when food is eaten")
    void scoreIncreasesBy10WhenFoodEaten() {
        GameState gs = new GameState(20);
        // Place food directly in front of the snake head (head is at 5,10, moving right)
        gs.setFood(new Point(6, 10));
        gs.tick();
        assertEquals(10, gs.getScore());
    }

    @Test
    @DisplayName("Snake grows by one when food is eaten")
    void snakeGrowsOnFoodEaten() {
        GameState gs = new GameState(20);
        gs.setFood(new Point(6, 10));
        int lengthBefore = gs.getSnake().size();
        gs.tick();
        assertEquals(lengthBefore + 1, gs.getSnake().size());
    }

    @Test
    @DisplayName("New food spawns after the current food is eaten")
    void newFoodSpawnsAfterEating() {
        GameState gs = new GameState(20);
        Point originalFood = new Point(6, 10);
        gs.setFood(originalFood);
        gs.tick(); // eat food
        assertNotEquals(originalFood, gs.getFood(),
                "Food position should change after it is eaten");
    }

    @Test
    @DisplayName("Score accumulates correctly after eating multiple pieces of food")
    void scoreAccumulatesOverMultipleEats() {
        GameState gs = new GameState(20);
        for (int i = 1; i <= 3; i++) {
            // Always place food one step ahead of the current head
            Point head = gs.getSnake().getFirst();
            gs.setFood(new Point(head.x + 1, head.y));
            gs.tick();
        }
        assertEquals(30, gs.getScore());
    }

    @Test
    @DisplayName("New food is not placed on the snake after eating")
    void newFoodNotOnSnakeAfterEating() {
        GameState gs = new GameState(20, 42L);
        gs.setFood(new Point(6, 10));
        gs.tick();
        assertFalse(gs.getSnake().contains(gs.getFood()),
                "Newly spawned food must not overlap with the snake");
    }

    // -----------------------------------------------------------------------
    // 6. Self-collision / game-over behaviour
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Game over is triggered when the snake runs into itself")
    void gameOverOnSelfCollision() {
        GameState gs = new GameState(20);
        // Build a snake that is about to run into its own body
        // Head at (5,5) moving right, body arranged so (6,5) is occupied
        LinkedList<Point> snake = new LinkedList<>();
        snake.add(new Point(5, 5)); // head
        snake.add(new Point(6, 5)); // body – next cell to the right
        snake.add(new Point(6, 6));
        snake.add(new Point(5, 6));
        snake.add(new Point(4, 6));
        snake.add(new Point(4, 5));
        snake.add(new Point(4, 4));
        snake.add(new Point(5, 4));
        snake.add(new Point(6, 4));
        gs.setSnake(snake);
        gs.setFood(new Point(0, 19)); // keep food out of the way

        gs.tick(); // head moves to (6,5) – occupied by body
        assertTrue(gs.isGameOver());
    }

    @Test
    @DisplayName("tick() is a no-op after the game is over")
    void tickIsNoOpAfterGameOver() {
        GameState gs = new GameState(20);
        LinkedList<Point> snake = new LinkedList<>();
        snake.add(new Point(5, 5));
        snake.add(new Point(6, 5));
        snake.add(new Point(6, 6));
        snake.add(new Point(5, 6));
        snake.add(new Point(4, 6));
        snake.add(new Point(4, 5));
        snake.add(new Point(4, 4));
        snake.add(new Point(5, 4));
        snake.add(new Point(6, 4));
        gs.setSnake(snake);
        gs.setFood(new Point(0, 19));
        gs.tick(); // triggers game over

        Point headAfterGameOver = gs.getSnake().getFirst();
        int scoreAfterGameOver  = gs.getScore();

        gs.tick(); // should be a no-op
        assertEquals(headAfterGameOver, gs.getSnake().getFirst());
        assertEquals(scoreAfterGameOver, gs.getScore());
    }

    @Test
    @DisplayName("Score does not change when the game is over")
    void scoreDoesNotChangeAfterGameOver() {
        GameState gs = new GameState(20);
        LinkedList<Point> snake = new LinkedList<>();
        snake.add(new Point(5, 5));
        snake.add(new Point(6, 5));
        snake.add(new Point(6, 6));
        snake.add(new Point(5, 6));
        snake.add(new Point(4, 6));
        snake.add(new Point(4, 5));
        snake.add(new Point(4, 4));
        snake.add(new Point(5, 4));
        snake.add(new Point(6, 4));
        gs.setSnake(snake);
        gs.setFood(new Point(0, 19));
        gs.tick(); // game over

        int scoreFrozen = gs.getScore();
        gs.tick();
        gs.tick();
        assertEquals(scoreFrozen, gs.getScore());
    }

    // -----------------------------------------------------------------------
    // 7. Re-initialisation
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("init() resets score to 0")
    void initResetsScore() {
        GameState gs = new GameState(20);
        gs.setFood(new Point(6, 10));
        gs.tick(); // score = 10
        gs.init();
        assertEquals(0, gs.getScore());
    }

    @Test
    @DisplayName("init() clears the game-over flag")
    void initClearsGameOver() {
        GameState gs = new GameState(20);
        LinkedList<Point> snake = new LinkedList<>();
        snake.add(new Point(5, 5));
        snake.add(new Point(6, 5));
        snake.add(new Point(6, 6));
        snake.add(new Point(5, 6));
        snake.add(new Point(4, 6));
        snake.add(new Point(4, 5));
        snake.add(new Point(4, 4));
        snake.add(new Point(5, 4));
        snake.add(new Point(6, 4));
        gs.setSnake(snake);
        gs.setFood(new Point(0, 19));
        gs.tick(); // game over
        assertTrue(gs.isGameOver());

        gs.init();
        assertFalse(gs.isGameOver());
    }

    @Test
    @DisplayName("init() restores the snake to three segments")
    void initRestoresSnakeLength() {
        GameState gs = new GameState(20);
        gs.setFood(new Point(6, 10));
        gs.tick(); // grow
        gs.init();
        assertEquals(3, gs.getSnake().size());
    }

    @Test
    @DisplayName("init() restores the snake head to (5, 10)")
    void initRestoresSnakeHeadPosition() {
        GameState gs = new GameState(20);
        gs.setFood(new Point(19, 0));
        gs.tick();
        gs.init();
        assertEquals(new Point(5, 10), gs.getSnake().getFirst());
    }

    @Test
    @DisplayName("init() sets direction back to RIGHT")
    void initRestoresDirection() {
        GameState gs = new GameState(20);
        gs.setFood(new Point(19, 0));
        gs.setNextDirection(GameState.DIR_UP);
        gs.tick();
        gs.init();
        assertEquals(GameState.DIR_RIGHT, gs.getDirection());
    }

    // -----------------------------------------------------------------------
    // 8. Food spawn constraints
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("spawnFood() places food within grid bounds")
    void spawnFoodWithinGridBounds() {
        GameState gs = new GameState(20);
        for (int i = 0; i < 50; i++) {
            gs.spawnFood();
            Point food = gs.getFood();
            assertTrue(food.x >= 0 && food.x < 20, "food.x out of bounds");
            assertTrue(food.y >= 0 && food.y < 20, "food.y out of bounds");
        }
    }

    @Test
    @DisplayName("spawnFood() never places food on the snake")
    void spawnFoodNeverOnSnake() {
        GameState gs = new GameState(20);
        for (int i = 0; i < 50; i++) {
            gs.spawnFood();
            assertFalse(gs.getSnake().contains(gs.getFood()),
                    "Food must not overlap with the snake (iteration " + i + ")");
        }
    }

    @Test
    @DisplayName("getSnake() returns a defensive copy – mutating it does not affect state")
    void getSnakeReturnsDefensiveCopy() {
        GameState gs = new GameState(20);
        LinkedList<Point> copy = gs.getSnake();
        copy.clear();
        assertEquals(3, gs.getSnake().size(), "Internal state must not be mutated via getSnake()");
    }

    @Test
    @DisplayName("getFood() returns a defensive copy – mutating it does not affect state")
    void getFoodReturnsDefensiveCopy() {
        GameState gs = new GameState(20);
        Point foodCopy = gs.getFood();
        Point originalFood = new Point(foodCopy);
        foodCopy.x = -999;
        assertEquals(originalFood, gs.getFood(), "Internal state must not be mutated via getFood()");
    }

    @Test
    @DisplayName("gridSize is reported correctly")
    void gridSizeReported() {
        GameState gs = new GameState(15);
        assertEquals(15, gs.getGridSize());
    }
}
