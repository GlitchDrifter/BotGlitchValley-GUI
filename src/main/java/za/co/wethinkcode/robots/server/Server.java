package za.co.wethinkcode.robots.server;

import com.google.gson.*;
import java.io.*;
import java.net.*;

import za.co.wethinkcode.robots.OperationalStatus;
import za.co.wethinkcode.robots.command.*;
import za.co.wethinkcode.robots.command.look.LookCommand;
import za.co.wethinkcode.robots.robot.Robot;
import za.co.wethinkcode.robots.world.World;
import static za.co.wethinkcode.robots.client.Client.formatServerResponse;

/**
 * Server class to handle client connections and process commands.
 */
public class Server implements Runnable {

    private static World world;
    private String robotName;
    private final Socket socket;

    /**
     * Constructor to initialize the server with a socket and world instance.
     *
     * @param socket The socket for client connection.
     * @param worldInstance The world instance to interact with.
     * @throws IOException If an I/O error occurs when creating the input/output streams.
     */
    public Server(Socket socket, World worldInstance) throws IOException {
        String clientMachine = socket.getInetAddress().getHostName();
        System.out.println("Connection from " + clientMachine);
        MultiServers.printServerPrompt();
        world = worldInstance;
        this.socket = socket;
    }

    /**
     * The main method that runs the server and handles client requests.
     */
    public void run() {
        try(PrintStream out = new PrintStream(socket.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String messageFromClient;
            while((messageFromClient = in.readLine()) != null) {
                JsonObject request = JsonParser.parseString(messageFromClient).getAsJsonObject();
                robotName = request.get("robot").getAsString();
                if (!MultiServers.clientHandlerMap.containsKey(robotName)) {
                    MultiServers.clientHandlerMap.put(robotName, this);
                }
                String commandName = request.get("command").getAsString();
                JsonArray args = request.get("arguments").getAsJsonArray();

                world.setCurrentRobotByName(robotName);
                JsonObject response = handleCommand(commandName, args);
                MultiServers.printServerPrompt();
                world.deleteDeadBots();
                System.out.println(formatServerResponse(response));
                out.println(response);
            }
        } catch (SocketException e){
            System.out.println("Client Socket has been closed " + e.getMessage());
        } catch(IOException ex) {
            System.out.println("Error with taking input " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    /**
     * Sends a reload message to the client indicating that the reload command was successful.
     * It includes the current state of the robot in the response.
     */
    public void sendRepairMessage() {
        JsonObject response = new JsonObject();
        JsonObject data = new JsonObject();
        response.addProperty("result", "OK");
        data.addProperty("message", "Done");
        response.add("data", data );
        response.add("state", world.getCurrentRobot().state() );

        try {
            PrintStream out = new PrintStream(socket.getOutputStream());
            out.println(response);

        } catch (IOException e) {
            System.out.println("Repair message unable to send" + e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends a reload message to the client indicating that the robot has been reloaded.
     * It includes the current state of the robot in the response.
     */
    public void sendReloadMessage() {
        JsonObject response = new JsonObject();
        JsonObject data = new JsonObject();
        response.addProperty("result", "OK");
        data.addProperty("message", "Done");
        response.add("data", data );
        response.add("state", world.getCurrentRobot().state() );

        try {
            PrintStream out = new PrintStream(socket.getOutputStream());
            out.println(response);

        } catch (IOException e) {
            System.out.println("Reload message unable to send" + e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends a quit message to the client and closes the socket connection.
     * It also removes the robot from the client handler map.
     */
    public void sendQuit() {
        JsonObject response = new JsonObject();
        JsonObject data = new JsonObject();
        response.addProperty("result", "OK");
        data.addProperty("message", "QUIT");
        response.add("data", data );

        try {
            PrintStream out = new PrintStream(socket.getOutputStream());
            out.println(response);
            socket.close();  // Close the socket to terminate client
            MultiServers.clientHandlerMap.remove(robotName); // Clean up map
        } catch (IOException e) {
            System.out.println("Quit message unable to send" + e);
            throw new RuntimeException(e);
        }
    }



    /**
     * Handles the command received from the client.
     *
     * @param commandName The name of the command to execute.
     * @param args       The arguments for the command.
     * @return A JsonObject containing the response to be sent back to the client.
     */
    private JsonObject handleCommand(String commandName, JsonArray args) {
        JsonObject response = new JsonObject();
        JsonObject data = new JsonObject();
        System.out.println("\nName: " + robotName +" , Command: " + commandName +" , Arguments: " + args );
        Command command;

        try {
            switch (commandName) {
                case "launch" -> {
                    for (Robot robot: world.getRobots()){
                        if (robot.getName().equals(robotName)){
                            response.addProperty("result", "ERROR");
                            data.addProperty("message", "Too many of you in this world");
                            response.add("data", data);
                            return response;
                        }
                    }
                    command = LaunchCommand.getInstance(robotName, args);
                }
                case "forward" -> command = MoveCommand.getInstance("forward", String.valueOf(args.get(0).getAsInt()));
                case "back" -> command = MoveCommand.getInstance("back", String.valueOf(args.get(0).getAsInt()));
                case "turn" -> command = TurnCommand.getInstance(String.valueOf(args.get(0)));
                case "state" -> command = StateCommand.getInstance();
                case "look" -> command = LookCommand.getInstance();
                case "help" -> command = HelpCommand.getInstance();
                case "fire" -> command = FireCommand.getInstance();
                case "reload" -> command = ReloadCommand.getInstance();
                case "repair" -> command = RepairCommand.getInstance();
                case "orientation" -> {
                    response.addProperty("result", "OK");
                    data.addProperty("message", "Done");
                    data.addProperty("orientation", world.getCurrentRobot().getCurrentDirection().toString());
                    response.add("data", data);
                    return response;
                }
                default -> throw new IllegalArgumentException("Unsupported command");
            }

        } catch (Exception e) {
            response.addProperty("result", "ERROR");
            data.addProperty("message", "Invalid command arguments");
            response.add("data", data);
            return response;
        }
        response = command.execute(world);

        Robot currentRobot = world.getCurrentRobot();


        if (response.has("result") && "OK".equals(response.get("result").getAsString())) {
            response.add("state", currentRobot.state());
            if (world.getCurrentRobot().getStatus() != OperationalStatus.DEAD) {
                currentRobot.setStatus(OperationalStatus.NORMAL);
            }
        }

        if (!commandName.equals("reload") && !commandName.equals("repair") && world.getCurrentRobot().getStatus()!= OperationalStatus.DEAD) {
            assert currentRobot != null;
            if(currentRobot.getStatus()!= OperationalStatus.DEAD) currentRobot.setStatus(OperationalStatus.NORMAL);
        }

        return response;
    }

}
