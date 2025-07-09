package za.co.wethinkcode.robots.maze;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import za.co.wethinkcode.robots.config.Config;
import za.co.wethinkcode.robots.obstacle.Obstacle;

import java.util.List;

class MazeTest {

    @BeforeEach
    void setUp() {
        System.setProperty("OBSTACLE_MODE", "Random");
        System.setProperty("WIDTH", "10");
        Config.loadConfig("config.properties");
    }

    /**
     * Tests the Maze constructor with a specific mode.
     * Verifies that the maze is initialized correctly with the expected number of obstacles.
     */
    @Test
    @DisplayName("Test Maze Constructor with Random Mode")
    void testConstructorRandomMode() {
        Maze maze = new Maze("Random");
        List<Obstacle> obstacles = maze.getObstacles();

        // Test that the maze has between 2 and 5 obstacles (as per randomization)
        assertTrue(!obstacles.isEmpty() && obstacles.size() <= 5);
    }


    /**
     * Tests the Maze constructor with an empty mode.
     * Verifies that the maze is initialized correctly with the expected number of obstacles.
     */
    @Test
    @DisplayName("Test Maze Constructor with Empty Mode")
    void testRandomizeMethod() {
        Maze maze = new Maze("Random");
        List<Obstacle> obstacles = maze.getObstacles();

        // Verify that obstacles are randomly generated with valid coordinates and types
        assertFalse(obstacles.isEmpty());
        for (Obstacle obstacle : obstacles) {
            assertNotNull(obstacle);
            assertTrue(obstacle.getTopLeft().getX() >= 1 && obstacle.getTopLeft().getX() < Config.WIDTH);
            assertTrue(obstacle.getTopLeft().getY() >= 1 && obstacle.getTopLeft().getY() < Config.WIDTH);
            assertNotNull(obstacle.getType());
        }
    }

    /**
     * Tests that the maze does not have overlapping obstacles.
     * Verifies that each obstacle is distinct and does not overlap with others.
     */
    @Test
    @DisplayName("Test No Overlapping Obstacles")
    void testNoOverlappingObstacles() {
        Maze maze = new Maze("Random");
        List<Obstacle> obstacles = maze.getObstacles();

        // Verify that no obstacles are overlapping
        for (int i = 0; i < obstacles.size(); i++) {
            for (int j = i + 1; j < obstacles.size(); j++) {
                assertFalse(obstacles.get(i).isOverlapping(obstacles.get(j)));
            }
        }
    }
}
