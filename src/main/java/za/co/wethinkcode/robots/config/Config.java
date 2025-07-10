package za.co.wethinkcode.robots.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * The Config class is responsible for loading and storing configuration
 * settings for the robot simulation.
 * It reads properties from a configuration file and provides static variables
 * to access these settings.
 */
public class Config {

  public static int HEIGHT;
  public static int WIDTH;
  public static String HOST;
  public static int PORT;
  public static int VISIBILITY;
  public static int REPAIR_DURATION; // seconds
  public static int RELOAD_DURATION; // seconds
  public static int MAX_SHIELD;
  public static int MAX_SHOTS;
  public static String OBSTACLE_MODE;

  /**
   * Loads configuration settings from a properties file.
   * The properties file should contain key-value pairs for various settings.
   *
   * @param configFile The path to the configuration file.
   */
  public static void loadConfig(final String configFile) {
    final Properties properties = new Properties();
    final File rawFile = new File(configFile);

    final File file = new File(rawFile.getAbsolutePath().replace("config.properties",
        "src/main/java/za/co/wethinkcode/robots/config/config.properties"));

    try (FileInputStream fileInputStream = new FileInputStream(file)) {
      properties.load(fileInputStream);
      HEIGHT = Integer.parseInt(properties.getProperty("HEIGHT"));
      WIDTH = Integer.parseInt(properties.getProperty("WIDTH"));
      HOST = properties.getProperty("HOST");
      PORT = Integer.parseInt(properties.getProperty("PORT"));
      VISIBILITY = Integer.parseInt(properties.getProperty("VISIBILITY"));
      REPAIR_DURATION = Integer.parseInt(properties.getProperty("REPAIR_DURATION"));
      RELOAD_DURATION = Integer.parseInt(properties.getProperty("RELOAD_DURATION"));
      MAX_SHIELD = Integer.parseInt(properties.getProperty("MAX_SHIELD"));
      MAX_SHOTS = Integer.parseInt(properties.getProperty("MAX_SHOTS"));
      OBSTACLE_MODE = properties.getProperty("OBSTACLE_MODE");
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }
}
