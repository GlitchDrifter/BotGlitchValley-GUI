package za.co.wethinkcode.robots.server;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import za.co.wethinkcode.flow.Recorder;
import za.co.wethinkcode.robots.command.DumpCommand;
import za.co.wethinkcode.robots.command.RobotsCommand;
import za.co.wethinkcode.robots.config.Config;
import za.co.wethinkcode.robots.world.World;

/**
 * MultiServers class to handle multiple client connections and server commands.
 * It initializes the server, accepts client connections, and processes
 * commands.
 */
public class MultiServers {
  public static final ConcurrentHashMap<String, Server> clientHandlerMap = new ConcurrentHashMap<>();

  private static MultiServerEngine server;

  static {
    new Recorder().logRun();
  }

  /**
   * Gets the server instance.
   * 
   * @return The server instance
   */
  public static MultiServerEngine getServer() {
    return server;
  }

  /**
   * Main method to start the server and handle commands.
   *
   * @param args Command line arguments
   */
  public static void main(final String[] args) {
    Config.loadConfig("config.properties");
    World worldInstance;
    if (args.length > 0) {
      worldInstance = new World(false);
    } else {
      worldInstance = new World(true);
    }
    server = new MultiServerEngine(worldInstance);

    try {
      server.start(Config.PORT);
    } catch (final IOException e) {
      System.err.println("Failed to bind to port: " + e.getMessage());
      return;
    }

    try (Scanner scanner = new Scanner(System.in)) {
      while (true) {
        System.out.print("Server Command> ");
        final String command = scanner.nextLine().toLowerCase().trim();

        switch (command) {
          case "quit":
          case "shutdown":
            System.out.println("Shutting down server...");
            server.broadcastMessage("quit");
            try {
              server.shutdown();
            } catch (final IOException e) {
              System.out.println("Error shutting down: " + e.getMessage());
            }
            System.exit(0);
            break;
          case "dump":
            DumpCommand.getInstance().dump(worldInstance);
            break;
          case "robots":
            RobotsCommand.getInstance().printRobots(worldInstance);
            break;
          default:
            System.out.println("Unknown command: " + command);
            break;
        }

      }
    }
  }

  /**
   * Prints the server command prompt.
   */
  public static void printServerPrompt() {
    System.out.print("\nServer Command> ");
  }
}
