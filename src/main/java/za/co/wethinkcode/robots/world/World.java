package za.co.wethinkcode.robots.world;


import za.co.wethinkcode.robots.OperationalStatus;
import za.co.wethinkcode.robots.Position;
import za.co.wethinkcode.robots.UpdateResponse;
import za.co.wethinkcode.robots.maze.Maze;
import za.co.wethinkcode.robots.obstacle.*;
import za.co.wethinkcode.robots.robot.Robot;
import java.util.ArrayList;
import java.util.List;

import static za.co.wethinkcode.robots.Direction.*;
import static za.co.wethinkcode.robots.config.Config.*;
import static za.co.wethinkcode.robots.UpdateResponse.*;


/**
 * The World class represents the game world where robots and obstacles exist.
 * It contains methods to manage robots, check positions, and update their states.
 */
public class World {

    private final Position TOP_LEFT;
    private final Position BOTTOM_RIGHT;
    private final Maze maze;
    private final List<Robot> robots;
    private Robot currentRobot;
    private final List<Obstacle> obstacleList;
    private WorldGUI gui;
    private final boolean GUI;

    public Position getTOP_LEFT() {
        return TOP_LEFT;
    }

    public Position getBOTTOM_RIGHT() {
        return BOTTOM_RIGHT;
    }

    /**
     * Constructor for the World class.
     * Initializes the maze and sets the boundaries of the world.
     */
    public World(boolean GUI) {
        this.maze = new Maze("");
        this.TOP_LEFT = new Position(0, 0);
        this.BOTTOM_RIGHT = new Position(HEIGHT - 1, WIDTH - 1);
        obstacleList = maze.getObstacles();
        this.GUI = GUI;
        robots = new ArrayList<>();
        if (GUI) gui = new WorldGUI(this);
    }

    /**
     * Sets the current robot by its name.
     * @param name The name of the robot to set as current.
     */
    public void setCurrentRobotByName(String name) {
        for (Robot robot : robots) {
            if (robot.getName().equals(name)) {
                currentRobot = robot;
                break;
            }
        }
    }

    /**
     * Sets the current robot directly.
     * @param robot The robot to set as current.
     */
    public void setCurrentRobot(Robot robot) {
        currentRobot = robot;
    }

    /**
     * Returns the list of obstacles in the maze.
     * @return A list of obstacles.
     */
    public List<Obstacle> getObstacles() {
        return maze.getObstacles();
    }

    /**
     * Returns the current robot.
     * @return The current robot.
     */
    public Robot getCurrentRobot() {
        return currentRobot;
    }

    /**
     * Returns the list of robots in the world.
     * @return A list of robots.
     */
    public List<Robot> getBots() {
        return robots;
    }

    /**
     * Adds a robot to the world.
     * @param robot The robot to add.
     */
    public void addRobot(Robot robot) {
        robots.add(robot);
    }

    /**
     * Checks if a new position is allowed based on the current robot's position and obstacles.
     * @param newPosition The new position to check.
     * @return true if the new position is allowed, false otherwise.
     */
    public boolean isNewPositionAllowed(Position newPosition) {
        for (Robot robot : robots) {
            if (robot.getName().equals(currentRobot.getName())) continue;
            if (robot.getPosition().equals(newPosition)) return false;

            if (robot.getPosition().getY() == currentRobot.getPosition().getY()) {
                if (currentRobot.getPosition().getX() > robot.getPosition().getX()) {
                    if (currentRobot.getCurrentDirection() == WEST)
                        return newPosition.getX() > robot.getPosition().getX();
                }
                else if (currentRobot.getCurrentDirection() == EAST)
                    return newPosition.getX() < robot.getPosition().getX();
            } else if (robot.getPosition().getX() == currentRobot.getPosition().getX()) {
                if (currentRobot.getPosition().getY() > robot.getPosition().getY()) {
                    if (currentRobot.getCurrentDirection() == SOUTH)
                        return newPosition.getY() > robot.getPosition().getY();
                } else if (currentRobot.getCurrentDirection() == NORTH)
                    return newPosition.getY() < robot.getPosition().getY();
            }
        }

        for (Obstacle obstacle : obstacleList) {
            if ((newPosition.isIn(obstacle.getTopLeft(), obstacle.getBottomRight())
                    || obstacle.blocksPath(currentRobot.getPosition(), newPosition))
                    && obstacle.getType()!= ObstacleType.BOTTOMLESS_PIT) return false;
        }
        return true;
    }

    /**
     * Checks if a new position is allowed for launching based on the current robot's positions and obstacles.
     * @param newPosition The new position to check.
     * @return true if the new position is allowed, false otherwise.
     */
    public boolean isLaunchAllowed(Position newPosition) {
        for (Robot robot : robots) {
            if (robot.getName().equals(currentRobot.getName())) continue;
            if (robot.getPosition().equals(newPosition)) return false;
        }
        for (Obstacle obstacle : obstacleList) {
            if (newPosition.isIn(obstacle.getTopLeft(), obstacle.getBottomRight())) return false;
        }
        return true;
    }


    /**
     *checks if robot can move from current position to the new without being obstructed
     * @param newPos - new position that the bot is attempting to move to
     * @return boolean that states if bot can move or not.
     */
    public boolean isMovementObstructed(Position newPos){
        for(Obstacle o : obstacleList){
            if (o.getType() == ObstacleType.MOUNTAIN){
                return newPos.isIn(o.getTopLeft(), o.getBottomRight());
            }
        }
        return true;
    }

    /**
     * Updates the position of the current robot based on the number of steps.
     * @param nrSteps The number of steps to move.
     * @return An UpdateResponse indicating the result of the update.
     */
    public UpdateResponse updatePosition(int nrSteps) {

        Position oldPos = getCurrentRobot().getPosition();
        Position pos = oldPos.newPos(currentRobot.getCurrentDirection(), nrSteps);

        if(pos.isIn(TOP_LEFT, BOTTOM_RIGHT) && isNewPositionAllowed(pos)){
            getCurrentRobot().setPosition(pos);
            if (GUI) gui.update();
            for (Obstacle o : getObstacles()){
                if (o.getType() == ObstacleType.BOTTOMLESS_PIT && (o.blocksPosition(pos) || o.blocksPath(oldPos, pos))){
                    getCurrentRobot().setStatus(OperationalStatus.DEAD);

                    return DIED_FELL_IN_PIT;
                }
            }
            return SUCCESS;
        } else if (!pos.isIn(TOP_LEFT, BOTTOM_RIGHT) && isNewPositionAllowed(pos)) {
            getCurrentRobot().setStatus(OperationalStatus.DEAD);
            if (GUI)gui.update();
            return FAILURE_OUT_OF_BOUNDS;
        }
        if (GUI) gui.update();
        return FAILURE_OBSTRUCTED;

    }

    /**
     * deletes robots with a DEAD status
     */
    public void deleteDeadBots(){
        robots.removeIf(r -> r.getStatus() == OperationalStatus.DEAD);
        if (GUI) gui.update();
    }

    /**
     * Returns the maze object.
     * @return The maze object.
     */
    public List<Robot> getRobots(){
        return robots;
    }
}
