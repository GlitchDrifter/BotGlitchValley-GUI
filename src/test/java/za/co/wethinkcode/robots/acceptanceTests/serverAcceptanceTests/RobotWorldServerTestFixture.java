package za.co.wethinkcode.robots.acceptanceTests.serverAcceptanceTests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import za.co.wethinkcode.robots.acceptanceTests.client.RobotWorldClient;
import za.co.wethinkcode.robots.acceptanceTests.client.StandardRobotWorldClient;

public class RobotWorldServerTestFixture {

  public static final int SERVER_PORT = 5000;
  public static final String DEFAULT_IP = "localhost";

  /* Paths to server JAR files */
  public static final String JAR_PATH = "target/out/server.jar";

  /* Clients for interacting with the servers */
  public static final RobotWorldClient brownFieldsClient = new StandardRobotWorldClient();

  /* Processes for running the servers */
  public static Process brownFieldsServerProcess;

  public static void setUpServers(final Integer worldSize, final String obstacle, final String pit,
      final String mountain, final String lake)
      throws IOException, InterruptedException {

    final List<String> brownFieldsCommand = new ArrayList<>(Arrays.asList(
        "java", "-jar", JAR_PATH,
        "-p", String.valueOf(SERVER_PORT)));

    if (worldSize != null) {
      brownFieldsCommand.addAll(Arrays.asList("-s", worldSize.toString()));
    }

    if (obstacle != null && !obstacle.isEmpty()) {
      brownFieldsCommand.addAll(Arrays.asList("-o", obstacle));
    }

    if (pit != null && !pit.isEmpty()) {
      brownFieldsCommand.addAll(Arrays.asList("-pt", pit));
    }

    if (lake != null && !lake.isEmpty()) {
      brownFieldsCommand.addAll(Arrays.asList("-l", lake));
    }

    if (mountain != null && !mountain.isEmpty()) {
      brownFieldsCommand.addAll(Arrays.asList("-m", mountain));
    }

    final ProcessBuilder pbBrownFields = new ProcessBuilder(brownFieldsCommand);
    brownFieldsServerProcess = pbBrownFields.start();

    /* Wait for servers to start */
    TimeUnit.SECONDS.sleep(3);
  }

  public static void connectToServers() {
    /* Connect to both servers before each test */
    brownFieldsClient.connect(DEFAULT_IP, SERVER_PORT);
  }

  public static void disconnectFromServers() {
    /* Disconnect from both servers after each test */
    brownFieldsClient.disconnect();
  }

  public static void tearDownServers() {
    /* Stop both server processes after all tests */
    stopServer(brownFieldsServerProcess);
  }

  private static void stopServer(final Process serverProcess) {
    /* Stop a server process, forcibly if necessary */
    if (serverProcess != null && serverProcess.isAlive()) {
      serverProcess.destroy();
      try {
        if (!serverProcess.waitFor(1, TimeUnit.SECONDS)) {
          serverProcess.destroyForcibly();
        }
      } catch (final InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
