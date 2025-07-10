package za.co.wethinkcode.robots.acceptanceTests.serverAcceptanceTests.testCommands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

import za.co.wethinkcode.robots.acceptanceTests.client.RobotWorldClient;
import za.co.wethinkcode.robots.acceptanceTests.serverAcceptanceTests.RobotWorldServerTestFixture;

@Tag("AcceptanceTest")
class LaunchRobotBaseFunctionalityTests {
  @BeforeAll
  static void setUpServers() throws IOException, InterruptedException {
    RobotWorldServerTestFixture.setUpServers(1, null, null, null, null);
  }

  @AfterAll
  static void tearDownServers() {
    RobotWorldServerTestFixture.tearDownServers();
  }

  RobotWorldClient serverClient;

  @BeforeEach
  void connectToServer() {
    RobotWorldServerTestFixture.connectToServers();
    serverClient = RobotWorldServerTestFixture.brownFieldsClient;
  }

  @AfterEach
  void disconnectFromServers() {
    RobotWorldServerTestFixture.disconnectFromServers();
  }

  @Test
  void correctlySpelledLaunchCommandCausesSuccess() {

    /* Test valid launch on both servers */
    /* Given that I am connected to a running Robot Worlds server */
    /*
     * And the world is of size 1x1 (The world is configured or hardcoded to this
     * size)
     */
    assertTrue(serverClient.isConnected(), "Brown Fields server should be connected");
    /* When I send a valid launch request to the server */
    final String request = "{" +
        "\"robot\": \"Ayanda\"," +
        "\"command\": \"launch\"," +
        "\"arguments\": [\"shooter\",\"5\",\"5\"]" +
        "}";

    final JsonNode response = serverClient.sendRequest(request);
    /* Then I should get a valid response from the server */
    assertNotNull(response.get("result"));
    assertEquals("OK", response.get("result").asText());
    /* And the position should be (x:0, y:0) */
    assertNotNull(response.get("data"));
    assertNotNull(response.get("data").get("position"));
    assertEquals(0, response.get("data").get("position").get(0).asInt());
    assertEquals(0, response.get("data").get("position").get(1).asInt());
    /* And I should also get the state of the robot */
    assertNotNull(response.get("state"));
  }

  @Test
  void misspelledLaunchCommandCausesFailure() {
    /* Test invalid launch on both servers */
    /* Given that I am connected to a running Robot Worlds server */
    assertTrue(serverClient.isConnected(), "Brown Fields server should be connected");
    /*
     * When I send an invalid launch request with the command "luanch" instead of
     * "launch"
     */
    final String request = "{" +
        "\"robot\": \"Ayanda\"," +
        "\"command\": \"luanch\"," +
        "\"arguments\": [\"shooter\",\"5\",\"5\"]" +
        "}";

    final JsonNode response = serverClient.sendRequest(request);
    /* Then I should get an error response */
    assertNotNull(response.get("result"));
    assertEquals("ERROR", response.get("result").asText());
    /* And the message "Unsupported command" */
    assertNotNull(response.get("data"));
    assertNotNull(response.get("data").get("message"));
    assertTrue(response.get("data").get("message").asText().contains("Unsupported command"));
  }
}
