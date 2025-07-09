package za.co.wethinkcode.robots.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.*;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * MultiServerTest.java
 * This class contains unit tests for the Server class.
 * It tests the server's ability to handle client requests and respond correctly.
 */
class MultiServerTest {
    private static final int TEST_PORT = 4242;
    private Thread serverThread;

    /**
     * This method sets up the server before each test.
     * It starts the server in a separate thread to allow for concurrent testing.
     */
    @BeforeEach
    void setupServer() {
        serverThread = new Thread(() -> {
            try {
                MultiServers.main(new String[]{"test"});
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        serverThread.start();

        waitForServerToBeReady();
    }

    /**
     * This method tears down the server after each test.
     * It properly shuts down the server and interrupts the server thread.
     */
    @AfterEach
    void tearDownServer() throws IOException {
        MultiServerEngine server = MultiServers.getServer();
        if (server != null) {
            server.shutdown();
        }

        if (serverThread != null) {
            serverThread.interrupt();
        }
    }

    /**
     * This test method checks if the server is set up correctly and can respond to a client request.
     * It sends a JSON request to the server and verifies the response.
     */
    @Test
    @DisplayName("Test server setup and response")
    void testServerSetupAndResponse() throws IOException {
        try (Socket clientSocket = new Socket("localhost", TEST_PORT);
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            JsonObject launchRequest = new JsonObject();
            launchRequest.addProperty("robot", "TestBot10");
            launchRequest.addProperty("command", "launch");
            JsonArray args = new JsonArray();
            args.add("sniper");
            launchRequest.add("arguments", args);

            out.println(launchRequest);

            String response = in.readLine();
            JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();

            assertEquals("OK", jsonResponse.get("result").getAsString());
        }
    }


    /**
     * This test method checks if the server is running and we can connect to it.
     */
    private void waitForServerToBeReady() {
        int retries = 10;
        int delay = 500; // milliseconds
        while (retries > 0) {
            try (Socket socket = new Socket("localhost", TEST_PORT)) {
                return;
            } catch (UnknownHostException | SocketException e) {
                retries--;
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ignored) {
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        throw new IllegalStateException("Server did not start within the expected time.");
    }
}
