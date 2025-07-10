package za.co.wethinkcode.robots.command;

import java.util.List;

import com.google.gson.JsonObject;

import za.co.wethinkcode.robots.obstacle.Obstacle;
import za.co.wethinkcode.robots.world.AsciiWorld;
import za.co.wethinkcode.robots.world.World;

/**
 * The Dump class represents a command to dump the current state of the world.
 * It extends the Command class and implements the execute method to perform the
 * dump action.
 * 
 */
public class DumpCommand extends Command {
  // Singleton instance
  private static DumpCommand instance;

  /**
   * Gets the singleton instance of DumpCommand.
   *
   * @return The singleton instance of DumpCommand
   */
  public static synchronized DumpCommand getInstance() {
    if (instance == null) {
      instance = new DumpCommand();
    }
    return instance;
  }

  /**
   * Private constructor for DumpCommand.
   * Sets the command name to "dump".
   */
  private DumpCommand() {
    super("dump");
  }

  /**
   * Prints the current state of the obstacles in the world.
   *
   * @param obstacleList the list of obstacles to be printed
   */
  public void listObstacles(final List<Obstacle> obstacleList) {
    System.out.println("Obstacles currently in world:");
    for (int i = 0; i < obstacleList.size(); i++) {
      System.out.println("Obstacle " + (i + 1) + ": " + obstacleList.get(i));
    }
  }

  @Override
  public JsonObject execute(final World world) {
    return new JsonObject();
  }

  /**
   * Prints the current state of the world, including the robots and obstacles.
   *
   * @param world the world to be dumped
   */
  public void dump(final World world) {
    System.out.println("Dumping world...");
    final StringBuilder names = new StringBuilder("[");
    if (!world.getBots().isEmpty()) {
      names.deleteCharAt(names.length() - 1);
    }
    new RobotsCommand().printRobots(world);
    listObstacles(world.getObstacles());
    final AsciiWorld txtWorld = new AsciiWorld(world);
    txtWorld.printToTxt();
    System.out.println(txtWorld.getContent().toString());
  }

}
