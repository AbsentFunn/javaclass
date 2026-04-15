import java.awt.Point;
import java.util.LinkedList;
import java.util.Random;

/**
 * Encapsulates all game logic for the Snake game: snake movement,
 * collision detection, food spawning, and scoring. This class is
 * independent of any UI framework and is fully unit-testable.
 */
public class GameState {

    public static final int DIR_LEFT  = 0;
    public static final int DIR_RIGHT = 1;
    public static final int DIR_UP    = 2;
    public static final int DIR_DOWN  = 3;

    private static final int INITIAL_X = 5;
    private static final int INITIAL_Y = 10;

    private final int gridSize;
    private final Random random;

    private LinkedList<Point> snake;
    private Point food;
    private int direction;
    private int nextDirection;
    private boolean gameOver;
    private int score;

    /** Creates a new game state with the given grid size and a random seed. */
    public GameState(int gridSize) {
        this(gridSize, new Random());
    }

    /** Creates a deterministic game state using the given seed (useful for tests). */
    public GameState(int gridSize, long seed) {
        this(gridSize, new Random(seed));
    }

    private GameState(int gridSize, Random random) {
        this.gridSize = gridSize;
        this.random = random;
        init();
    }

    /** Resets the game to its initial state. */
    public void init() {
        snake = new LinkedList<>();
        snake.add(new Point(INITIAL_X,     INITIAL_Y));
        snake.add(new Point(INITIAL_X - 1, INITIAL_Y));
        snake.add(new Point(INITIAL_X - 2, INITIAL_Y));

        direction     = DIR_RIGHT;
        nextDirection = DIR_RIGHT;
        score         = 0;
        gameOver      = false;

        spawnFood();
    }

    /**
     * Requests a direction change. The change is ignored if it would reverse
     * the current direction of travel.
     *
     * @param dir one of {@link #DIR_LEFT}, {@link #DIR_RIGHT},
     *            {@link #DIR_UP}, {@link #DIR_DOWN}
     * @return {@code true} if the direction was accepted
     */
    public boolean setNextDirection(int dir) {
        if (dir == DIR_LEFT  && direction != DIR_RIGHT) { nextDirection = dir; return true; }
        if (dir == DIR_RIGHT && direction != DIR_LEFT)  { nextDirection = dir; return true; }
        if (dir == DIR_UP    && direction != DIR_DOWN)  { nextDirection = dir; return true; }
        if (dir == DIR_DOWN  && direction != DIR_UP)    { nextDirection = dir; return true; }
        return false;
    }

    /**
     * Advances the game by one tick: moves the snake, checks for collisions,
     * and handles food consumption. Does nothing if the game is already over.
     */
    public void tick() {
        if (gameOver) return;

        direction = nextDirection;
        Point head    = snake.getFirst();
        Point newHead = new Point(head);

        switch (direction) {
            case DIR_LEFT:  newHead.x--; break;
            case DIR_RIGHT: newHead.x++; break;
            case DIR_UP:    newHead.y--; break;
            case DIR_DOWN:  newHead.y++; break;
        }

        // Wrap around grid edges
        newHead.x = (newHead.x + gridSize) % gridSize;
        newHead.y = (newHead.y + gridSize) % gridSize;

        boolean ateFood = newHead.equals(food);

        // Remove tail before self-collision check (unless growing)
        if (!ateFood) {
            snake.removeLast();
        }

        // Self-collision check
        if (snake.contains(newHead)) {
            gameOver = true;
            snake.addFirst(newHead);
            return;
        }

        snake.addFirst(newHead);

        if (ateFood) {
            score += 10;
            spawnFood();
        }
    }

    /** Spawns food at a random cell not occupied by the snake. */
    public void spawnFood() {
        int x, y;
        do {
            x = random.nextInt(gridSize);
            y = random.nextInt(gridSize);
        } while (snake.contains(new Point(x, y)));
        food = new Point(x, y);
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    /** Returns a copy of the snake body (head at index 0). */
    public LinkedList<Point> getSnake() { return new LinkedList<>(snake); }

    /** Returns a copy of the current food position. */
    public Point getFood() { return new Point(food); }

    public int  getDirection()     { return direction; }
    public int  getNextDirection() { return nextDirection; }
    public boolean isGameOver()    { return gameOver; }
    public int  getScore()         { return score; }
    public int  getGridSize()      { return gridSize; }

    // -------------------------------------------------------------------------
    // Package-private helpers used by tests
    // -------------------------------------------------------------------------

    /** Directly sets the food position (for deterministic test setup). */
    void setFood(Point food) {
        this.food = new Point(food);
    }

    /** Directly replaces the snake body (for deterministic test setup). */
    void setSnake(LinkedList<Point> snake) {
        this.snake = new LinkedList<>(snake);
    }

    /** Directly sets both current and queued direction (for deterministic test setup). */
    void setDirection(int dir) {
        this.direction     = dir;
        this.nextDirection = dir;
    }
}
